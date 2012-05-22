package org.grailrtls.plunder.client;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class DrawableObject {
  private final String uri;
  private float xOffset= -1f;
  private float yOffset = -1f;
  private Image icon = null;

  public DrawableObject(final String uri) {
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
    if (o instanceof DrawableObject) {
      return this.equals((DrawableObject) o);
    }
    return super.equals(o);
  }

  public boolean equals(DrawableObject r) {
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

  public Image getIcon() {
    return icon;
  }

  public void setIcon(Image icon) {
    this.icon = icon;
  }
  
  @Override
  public String toString(){
    
    return this.uri + " @ (" + this.xOffset + ", " + this. yOffset + ") " + (this.icon == null ? "NOIMG" : "IMG");
  }
}
