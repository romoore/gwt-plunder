package org.grailrtls.plunder.client.drawable;

import java.util.logging.Logger;

import org.grailrtls.plunder.client.WorldState;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class DrawableObject {
  private static final Logger log = Logger.getLogger(DrawableObject.class.getName());
  
  private final String uri;
  private float xOffset = -1f;
  private float yOffset = -1f;
  protected ImageElement icon = null;
  private int iconWidth = 0;
  private int iconHeight = 0;
  protected float xScale = 1f;
  protected float yScale = 1f;
  protected static int desiredWidth = 32;
  protected static int maxHeight = 48;
  private boolean isVisible = true;
  private long lastUpdate = System.currentTimeMillis();
  
  private WorldState state;

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
    this.lastUpdate = System.currentTimeMillis();
  }

  public float getyOffset() {
    return yOffset;
  }

  public void setyOffset(float yOffset) {
    this.yOffset = yOffset;
    this.lastUpdate = System.currentTimeMillis();
  }
  
  public boolean containsPoint(int x, int y){
    if(!this.isVisible){
      return false;
    }
    if (this.iconWidth == 0 || this.iconHeight == 0) {
      return false;
    }
    float imgRatio = 1f*this.iconHeight / this.iconWidth;
    float drawHeight = this.desiredWidth * imgRatio;
    
    int xPos = (int) (this.xOffset * this.xScale - this.desiredWidth / 2);
    int yPos = (int) (this.yOffset * this.yScale - drawHeight / 2);
    
    return (x >= xPos && x <= (xPos+this.iconWidth) && y >= yPos && y <= (yPos + drawHeight));
     
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

  public void setIcon(ImageElement icon, int width, int height) {
    this.icon = icon;
    this.iconWidth = width;
    this.iconHeight = height;
    this.lastUpdate = System.currentTimeMillis();
  }

  @Override
  public String toString() {

    return this.uri + " @ (" + this.xOffset + ", " + this.yOffset + ") "
        + (this.icon == null ? "NOIMG" : "IMG");
  }

  public void draw(Context2d context) {
    if(!this.isVisible){
      return;
    }
    context.save();
    
    if (this.iconWidth == 0 || this.iconHeight == 0) {
      log.warning("NOT Drawing [" + this + "] @ (" + this.iconWidth+ ", " + this.iconHeight+ ")");
     return;
    }
    float imgRatio = ((float)this.iconHeight) / this.iconWidth;
    float drawHeight = DrawableObject.desiredWidth * imgRatio;
    float drawWidth = DrawableObject.desiredWidth;
    if(drawHeight > DrawableObject.maxHeight){
      drawHeight = DrawableObject.maxHeight;
      drawWidth = DrawableObject.maxHeight / imgRatio;
    }
    
    int xPos = (int) (this.xOffset * this.xScale - drawWidth / 2);
    int yPos = (int) (this.yOffset * this.yScale - drawHeight / 2);
    
    context.translate(xPos, yPos);
    log.finer("Drawing [" + this + "] @ (" + xPos + "->"+ drawWidth+ ", " + yPos + "->" + drawHeight+ ")");
//    this.drawHighlight(context, drawWidth, drawHeight);
    
    context.drawImage(this.icon, 0, 0, this.iconWidth, this.iconHeight, 0, 0,
        drawWidth, drawHeight);
    context.restore();
  }
  
  protected void drawHighlight(Context2d context, float drawWidth, float drawHeight){
    long now = System.currentTimeMillis();
    if(now - this.lastUpdate < 5000l){
      FillStrokeStyle color = context.getFillStyle();
      context.setFillStyle("#F00");
      context.beginPath();
      context.rect(-2, -2, drawWidth+2, drawHeight+2);
      context.closePath();
      context.fill();
      context.setFillStyle(color);
    }
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

  public void setDesiredWidth(int desiredWidth) {
    this.desiredWidth = desiredWidth;
  }

  public boolean isVisible() {
    return isVisible;
  }

  public void setVisible(boolean visible) {
    this.isVisible = visible;
  }

  public WorldState getState() {
    return state;
  }

  public void setState(WorldState state) {
    this.state = state;
    this.lastUpdate = System.currentTimeMillis();
  }
  
}
