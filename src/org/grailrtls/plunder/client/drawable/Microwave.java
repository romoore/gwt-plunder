package org.grailrtls.plunder.client.drawable;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class Microwave extends DrawableObject {

  private ImageElement onIcon;
  private ImageElement offIcon;
  private boolean isOn;
  private int onw, onh, ofw, ofh;

//  public Microwave(String uri) {
//    this(uri, null, null, false);
//  }
//
//  public Microwave(String uri, Image onIcon, Image offIcon) {
//    this(uri, onIcon, offIcon, false);
//  }

  public Microwave(String uri, ImageElement onIcon, int onw, int onh, ImageElement offIcon, int ofw, int ofh, boolean isOn) {
    super(uri);
    this.onIcon = onIcon;
    this.offIcon = offIcon;
    this.isOn = isOn;
    this.onw = onw;
    this.onh = onh;
    this.ofw = ofw;
    this.ofh = ofh;
  }

  public boolean equals(Microwave m) {
    if (super.equals(m)) {
      return this.isOn == m.isOn;
    }
    return false;
  }

  /**
   * The {@code setIcon(Image)} method does nothing for microwaves, and
   * {@code setOnIcon(Image)} and {@code setOffIcon(Image)} should be used
   * instead.
   */
  @Override
  public void setIcon(ImageElement icon, int w, int h) {
    // Does nothing
  }

  public boolean isOn() {
    return isOn;
  }

  public void setOn(boolean isOn) {
    this.isOn = isOn;
  }

  public void setOnIcon(ImageElement onIcon) {
    this.onIcon = onIcon;
  }

  public void setOffIcon(ImageElement offIcon) {
    this.offIcon = offIcon;
  }

  @Override
  public void draw(Context2d context) {
    if(this.isOn){
      super.setIcon(this.onIcon, this.onw, this.onh);
    }
    else{
      super.setIcon(this.offIcon, this.ofw, this.ofh);
    }

    super.draw(context);
  }

}
