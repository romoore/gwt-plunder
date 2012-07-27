package org.grailrtls.plunder.client.drawable;

import java.util.logging.Logger;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class Door extends DrawableObject {
  
  private static final Logger log = Logger.getLogger(Door.class.getName());

  public Door(String uri, ImageElement openIcon, int ow, int oh, ImageElement closedIcon, int cw, int ch,
      boolean isOpen) {
    super(uri);
    this.openIcon = openIcon;
    this.closedIcon = closedIcon;
    this.open = isOpen;
    this.ow = ow;
    this.oh = oh;
    this.cw = cw;
    this.ch = ch;
    super.icon = this.open ? this.openIcon: this.closedIcon;
  }

  private boolean open = false;
  private ImageElement openIcon = null;
  private ImageElement closedIcon = null;
  private int ow, oh, cw, ch;

  public void setOpenIcon(ImageElement openIcon) {
    this.openIcon = openIcon;
  }

  public void setClosedIcon(ImageElement closedIcon) {
    this.closedIcon = closedIcon;
  }

  @Override
  public void draw(Context2d context) {
    if(this.open){
      super.setIcon(this.openIcon, this.ow, this.oh);
    }
    else{
      super.setIcon(this.closedIcon, this.cw, this.ch);
    }
    super.draw(context);
  }

  /**
   * This method has no effect for doors. Instead, {@code setOpenIcon(Image)} or
   * {@code setClosedIcon(Image)} should be called to set icon images.
   */
  @Override
  public void setIcon(ImageElement icon, int w, int h) {
    // No effect
  }

  public void setOpen(boolean isOpen) {
    this.open = isOpen;
  }

  public boolean equals(Door d) {
    log.fine("Comparing doors.");
    if (super.equals(d)) {
      return this.open == d.open;
    }
    return false;
  }
  
  @Override
  public boolean equals(DrawableObject o){
    if(o instanceof Door){
      return this.equals((Door)o);
    }
    return super.equals(o);
  }
  
  @Override
  public String toString(){
    return super.toString() + ": " + this.open;
  }
}
