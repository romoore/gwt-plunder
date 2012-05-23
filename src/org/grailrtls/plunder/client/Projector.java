package org.grailrtls.plunder.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class Projector extends DrawableObject {

  private ImageElement onIcon;
  private ImageElement offIcon;
  private boolean isOn;



  public Projector(String uri, ImageElement onIcon, ImageElement offIcon, boolean isOn) {
    super(uri);
    this.onIcon = onIcon;
    this.offIcon = offIcon;
    this.isOn = isOn;
  }

  public boolean equals(Projector p) {
    if (super.equals(p)) {
      return this.isOn == p.isOn;
    }
    return false;
  }

  /**
   * The {@code setIcon(Image)} method does nothing for projectors, and
   * {@code setOnIcon(Image)} and {@code setOffIcon(Image)} should be used
   * instead.
   */
  @Override
  public void setIcon(ImageElement icon) {
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
    super.icon = this.isOn ? this.onIcon : this.offIcon;
    super.draw(context);
  }

}
