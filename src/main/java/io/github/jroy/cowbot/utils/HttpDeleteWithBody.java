package io.github.jroy.cowbot.utils;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
  private static final String METHOD_NAME = "DELETE";

  public String getMethod() {
    return METHOD_NAME;
  }

  HttpDeleteWithBody(final String uri) {
    super();
    setURI(URI.create(uri));
  }
}
