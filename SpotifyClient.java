public class SpotifyClient {
  public static void main(String[] args) {
    Spotify spotify = new Spotify();
    spotify.apiSearch("joni%20mitchell", "artist");
  }
}
