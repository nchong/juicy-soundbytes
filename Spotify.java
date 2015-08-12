import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.*;
import java.io.*;
import java.net.*;
import org.json.*;
import java.nio.charset.StandardCharsets;

public class Spotify {
  private String CLIENT_SECRET;

  private String accessToken;
  private String tokenType;
  private int expiresIn;
  private String refreshToken;

  public Spotify() {
    CLIENT_SECRET = SpotifyConfig.getEnvSecret();
    if (CLIENT_SECRET == null) {
      System.out.println("ERROR: expected JUICY_SOUNDBYTES_SECRET env variable!");
      throw new RuntimeException();
    }
    accessToken = SpotifyConfig.getEnvToken();
    if (hasValidAccessToken()) {
      System.out.println("Picked up accessToken from environment!");
    } else {
      authorize();
    }
  }

  private void invalidateAccessToken() {
    accessToken = null;
  }

  public boolean hasValidAccessToken() {
    return (accessToken != null);
  }

  private String SEARCH_URL = "https://api.spotify.com/v1/search";
  public void apiSearch(String query, String type) {
    if (!hasValidAccessToken()) authorize();
    System.out.println("apiSearch " + query + type);
    Map<String, String> params = new HashMap<String, String>();
    params.put("q", query);
    params.put("type", type);
    try {
      HttpURLConnection connection = getRequest(SEARCH_URL, params);
      int responseCode = connection.getResponseCode();
      System.out.println("responseCode is " + responseCode);
      if (responseCode == HttpURLConnection.HTTP_OK) {
        JSONObject json = jsonOfConnection(connection);      
      } else {
        invalidateAccessToken();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void authorize() {
    if (hasValidAccessToken()) { return; }
    while (true) {
      try {
        String state = nextSessionId();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("client_id", SpotifyConfig.CLIENT_ID);
        params.put("response_type", "code");
        params.put("state", state);
        params.put("redirect_uri", SpotifyConfig.REDIRECT_URI);
        String url = SpotifyConfig.AUTHORIZE_URL + "/?" + keyValueStringOfParamMap(params);
        System.out.println("Please visit " + url);

        Scanner scanner = new Scanner(System.in);
        System.out.println("And paste the return url> ");
        String input = scanner.nextLine();

        URL callback = new URL(input);
        Map<String, String> queryPairs = splitQuery(callback);
        if (!(
          queryPairs.containsKey("code") &&
          queryPairs.containsKey("state"))) {
          System.out.println("URL must contain code and state params!");
          continue;
        }
        String code = queryPairs.get("code");
        String returnedState = queryPairs.get("state");
        if (!state.equals(returnedState)) {
          System.out.println("Returned state does not match!");
          continue;
        }
        System.out.println("Authorized okay! Code is " + code);
        getAccessToken(code);
        return;
      } catch (UnsupportedEncodingException e) {
        System.out.println("URL has bad encoding!");
      } catch (MalformedURLException e) {
        System.out.println("Could not parse return url!");
      } catch (Exception e) {
        System.out.println("Exception");
        e.printStackTrace();
      }
    }
  }

  private void getAccessToken(String code) {
    HashMap<String, String> params = new HashMap<String, String>();
    params.put("grant_type", "authorization_code");
    params.put("code", code);
    params.put("redirect_uri", SpotifyConfig.REDIRECT_URI);
    params.put("client_id", SpotifyConfig.CLIENT_ID);
    params.put("client_secret", CLIENT_SECRET);
    try {
      URL url = new URL(SpotifyConfig.ACCESS_TOKEN_URL);
      HttpURLConnection connection = postRequest(url, params);
      InputStream is = connection.getInputStream();
      int responseCode = connection.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_OK) {
        return;
      }
      JSONObject json = jsonOfConnection(connection);      
      accessToken = json.getString("access_token");
      tokenType = json.getString("token_type");
      expiresIn = json.getInt("expires_in");
      refreshToken = json.getString("refresh_token");
    } catch (Exception e) {
      System.out.println("ERROR: Problem getting access token!");
      e.printStackTrace();
      accessToken = null;
    }
  }

  private HttpURLConnection getRequest(String urlStr, Map<String, String>params) throws IOException {
    String paramStr = keyValueStringOfParamMap(params);
    System.out.println(urlStr + "/?" + paramStr);
    URL url = new URL(urlStr + "/?" + paramStr);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty("Authorization : Bearer", accessToken);
    return connection;
  }

  private HttpURLConnection postRequest(URL url, Map<String, String>params) throws IOException {
    String paramStr = keyValueStringOfParamMap(params);
    byte[] postData = paramStr.getBytes(StandardCharsets.UTF_8);
    int postDataLength = postData.length;
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setDoOutput(true);
    connection.setInstanceFollowRedirects(false);
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
    connection.setRequestProperty("charset", "utf-8");
    connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
    connection.setUseCaches(false);
    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
    wr.write(postData);
    return connection;
  }

  private String nextSessionId() {
    SecureRandom random = new SecureRandom();
    return new BigInteger(130, random).toString(32);
  }

  private static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
    Map<String, String> result = new HashMap<String, String>();
    String query = url.getQuery();
    String[] pairs = query.split("&");
    for (String pair : pairs) {
      int idx = pair.indexOf("=");
      String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
      String val = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
      result.put(key, val);
    }
    return result;
  }

  private static String keyValueStringOfParamMap(Map<String, String> params) {
    StringBuilder paramBuilder = new StringBuilder();
    boolean first = true;
    for (Map.Entry<String, String> p : params.entrySet()) {
      String key = p.getKey();
      String val = p.getValue();
      if (first) {
        paramBuilder.append(key + "=" + val);
        first = false;
      } else {
        paramBuilder.append("&" + key + "=" + val);
      }
    }
    return paramBuilder.toString();
  }

  private static JSONObject jsonOfConnection(HttpURLConnection connection) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String line = null;
    StringBuilder responseData = new StringBuilder();
    while ((line = in.readLine()) != null) {
      responseData.append(line);
    }
    String result = responseData.toString();
    System.out.println("Response data: " + result);
    JSONObject json = new JSONObject(result);
    return json;
  }

  private static String getEnvVar(String key) {
    return System.getenv().get(key);
  }
}
