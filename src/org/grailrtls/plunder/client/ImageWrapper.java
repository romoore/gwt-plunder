package org.grailrtls.plunder.client;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class ImageWrapper {
  private final Image image;
  private final ImageElement element;
  
  public ImageWrapper(final Image image){
    this.image = image;
    this.element = ImageElement.as(this.image.getElement());
  }

  public Image getImage() {
    return image;
  }

  public ImageElement getElement() {
    return element;
  }
}
