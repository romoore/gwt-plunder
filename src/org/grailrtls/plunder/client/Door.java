package org.grailrtls.plunder.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class Door extends DrawableObject {

  public Door(String uri, ImageElement openIcon, ImageElement closedIcon,
      boolean isOpen) {
    super(uri);
    this.openIcon = openIcon;
    this.closedIcon = closedIcon;
    this.open = isOpen;
  }

  private boolean open = false;
  private ImageElement openIcon = null;
  private ImageElement closedIcon = null;

  public void setOpenIcon(ImageElement openIcon) {
    this.openIcon = openIcon;
  }

  public void setClosedIcon(ImageElement closedIcon) {
    this.closedIcon = closedIcon;
  }

  @Override
  public void draw(Context2d context) {
    super.icon = this.open ? this.openIcon : this.closedIcon;
    super.draw(context);
  }

  /**
   * This method has no effect for doors. Instead, {@code setOpenIcon(Image)} or
   * {@code setClosedIcon(Image)} should be called to set icon images.
   */
  @Override
  public void setIcon(ImageElement icon) {
    // No effect
  }

  public void setOpen(boolean isOpen) {
    this.open = isOpen;
  }

  public boolean equals(Door d) {
    if (super.equals(d)) {
      return this.open == d.open;
    }
    return false;
  }
}
