public class SpotifyClient {
  public static void main(String[] args) {
    Spotify spotify = new Spotify();
    spotify.authorize();
  }
}
