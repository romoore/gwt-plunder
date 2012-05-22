package org.grailrtls.plunder.client;

public class Receiver {
  private final String uri;
  private float xOffset;
  private float yOffset;

  public Receiver(final String uri) {
    this.uri = uri;
  }

  public String getUri() {
    return uri;
  }

  public float getxOffset() {
    return xOffset;
  }

  public void setxOffset(float xOffset) {
    this.xOffset = xOffset;
  }

  public float getyOffset() {
    return yOffset;
  }

  public void setyOffset(float yOffset) {
    this.yOffset = yOffset;
  }

  public boolean equals(Object o) {
    if (o instanceof Receiver) {
      return this.equals((Receiver) o);
    }
    return super.equals(o);
  }

  public boolean equals(Receiver r) {
    if (this.uri.equals(r.uri)) {
      float diff = Math.abs(this.xOffset - r.xOffset);
      if (diff < 0.01f) {
        return true;
      }
      diff = Math.abs(this.yOffset - r.yOffset);
      if (diff < 0.01f) {
        return true;
      }
    }
    return false;
  }
}
