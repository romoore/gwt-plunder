package org.grailrtls.plunder.client.drawable;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class DrawableObject {
  private final String uri;
  private float xOffset= -1f;
  private float yOffset = -1f;
  protected ImageElement icon = null;
  private int iconWidth = 0;
  private int iconHeight = 0;
  protected float xScale = 1f;
  protected float yScale = 1f;

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

 

  public void setIcon(ImageElement icon) {
    this.icon = icon;
    this.iconWidth = icon.getWidth();
    this.iconHeight = icon.getHeight();
  }
  
  @Override
  public String toString(){
    
    return this.uri + " @ (" + this.xOffset + ", " + this. yOffset + ") " + (this.icon == null ? "NOIMG" : "IMG");
  }
  
  public void draw(Context2d context){
    context.save();
    int xPos = (int)(this.xOffset*this.xScale - this.iconWidth / 2);
    int yPos = (int)(this.yOffset*this.yScale - this.iconHeight / 2);
    
    context.translate(xPos, yPos);
    context.drawImage(this.icon, 0, 0);
    context.restore();
  }

  public void setIconWidth(int iconWidth) {
    this.iconWidth = iconWidth;
  }

  public void setIconHeight(int iconHeight) {
    this.iconHeight = iconHeight;
  }

  public void setxScale(float xScale) {
    this.xScale = xScale;
  }

  public void setyScale(float yScale) {
    this.yScale = yScale;
  }
}
