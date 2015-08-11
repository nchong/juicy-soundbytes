/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.io.IOUtils;
import java.util.concurrent.*;

/**
 *
 * @author tom
 */
public class Transcriber implements Runnable {
  String language = "en-US";
  String key = "AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw";
  String uri = "http://www.google.com/speech-api/v2/recognize?client=chromium&lang="+language+"&key="+key;
  
  private BlockingQueue<ByteArrayOutputStream> queue;

  public Transcriber(BlockingQueue<ByteArrayOutputStream> queue) {
    this.queue = queue;
  }
  
  
  // @Override
  public void run() {
    int n = 0;
    while (true) {
      try {
        ByteArrayOutputStream wav = queue.take();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  public void process(ByteArrayOutputStream wav) throws Exception {
    // Create connection
    URL url = new URL(uri);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "audio/l16; rate=16000");
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36");

    //connection.setUseCaches(false);
    connection.setDoOutput(true);

    // Write wav to request body
    OutputStream out = connection.getOutputStream();
    //InputStream in = new FileInputStream("hello.wav");
    wav.writeTo(out);
    out.close();

    //Get Response
    InputStream is = connection.getInputStream();
    IOUtils.copy(is, System.out);
  }
}
