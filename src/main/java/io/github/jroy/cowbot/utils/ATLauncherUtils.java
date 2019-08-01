package io.github.jroy.cowbot.utils;

import io.github.jroy.cowbot.ProxiedCow;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ATLauncherUtils {

  private static HttpClient client = HttpClientBuilder.create().build();
  private static final String endpoint = "https://api.atlauncher.com/v1/admin/pack/SchlattPack/settings/allowedplayers/";

  public static String getPlayers() throws IOException {
    HttpGet get = new HttpGet(endpoint);
    get.addHeader("API-KEY", ProxiedCow.getInstance().getConfiguration().getString("atl-key"));
    return fetchResponse(client.execute(get));
  }

  public static void addPlayer(String player) {
    try {
      HttpPost post = new HttpPost(endpoint);
      post.addHeader("API-KEY", ProxiedCow.getInstance().getConfiguration().getString("atl-key"));
      post.addHeader("Content-Type", "application/json");
      post.setEntity(new StringEntity("[\"" + player + "\"]"));
      client.execute(post);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void removePlayer(String player) {
    try {
      HttpDeleteWithBody delete = new HttpDeleteWithBody(endpoint);
      delete.addHeader("API-KEY", ProxiedCow.getInstance().getConfiguration().getString("atl-key"));
      delete.addHeader("Content-Type", "application/json");
      delete.setEntity(new StringEntity("[\"" + player + "\"]"));
      client.execute(delete);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static String fetchResponse(HttpResponse execute) throws IOException {
    BufferedReader rd = new BufferedReader(new InputStreamReader(execute.getEntity().getContent()));
    StringBuilder res = new StringBuilder();
    String line;
    while ((line = rd.readLine()) != null) {
      res.append(line);
    }
    return res.toString();
  }
}
