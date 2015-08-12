public class SpotifyConfig {
  public static final String CLIENT_ID = "6ffd5e293d954967b23aaca32886c340";
  public static final String AUTHORIZE_URL = "https://accounts.spotify.com/authorize";
  public static final String ACCESS_TOKEN_URL = "https://accounts.spotify.com/api/token";
  public static final String REDIRECT_URI = "http://localhost";
  public static final String SEARCH_URL = "https://api.spotify.com/v1/search";

  private static final String ENV_SECRET = "JUICY_SOUNDBYTES_SECRET";
  private static final String ENV_TOKEN = "JUICY_SOUNDBYTES_ACCESS_TOKEN";

  public static String getEnvSecret() {
    return getEnvVar(SpotifyConfig.ENV_SECRET);
  }

  public static String getEnvToken() {
    return getEnvVar(SpotifyConfig.ENV_TOKEN);
  }
  
  private static String getEnvVar(String key) {
    return System.getenv().get(key);
  }
}
