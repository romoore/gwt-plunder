package org.grailrtls.plunder.client.drawable;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class Screen extends DrawableObject {

  private ImageElement onIcon;
  private ImageElement offIcon;
  private boolean isOn;
    private int onw, onh, ofw, ofh;


  public Screen(String uri, ImageElement onIcon, int onw, int onh, ImageElement offIcon, int ofw, int ofh, boolean isOn) {
    super(uri);
    this.onIcon = onIcon;
    this.offIcon = offIcon;
    this.isOn = isOn;
    this.onw = onw;
    this.onh = onh;
    this.ofw = ofw;
    this.ofh = ofh;
  }

  public boolean equals(Screen s) {
    if (super.equals(s)) {
      return this.isOn == s.isOn;
    }
    return false;
  }

  /**
   * The {@code setIcon(Image)} method does nothing for screens, and
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
    }else{
      super.setIcon(this.offIcon, this.ofw, this.ofh);
    }
    super.draw(context);
  }

}
