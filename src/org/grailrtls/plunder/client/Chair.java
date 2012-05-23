package org.grailrtls.plunder.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class Chair extends DrawableObject {
  
  private ImageElement occupiedIcon;
  private ImageElement emptyIcon;
  private boolean isOccupied;
  
//  public Chair(String uri){
//    this(uri, null, null, false);
//  }
//  
//  public Chair(String uri, Image occupiedIcon, Image emptyIcon){
//    this(uri, occupiedIcon, emptyIcon, false);
//  }
//  
  public Chair(String uri, ImageElement occupiedIcon, ImageElement emptyIcon, boolean isOccupied){
    super(uri);
    this.occupiedIcon = occupiedIcon;
    this.emptyIcon = emptyIcon;
    this.isOccupied = isOccupied;
  }

  public boolean isOccupied() {
    return isOccupied;
  }

  public void setOccupied(boolean isOccupied) {
    this.isOccupied = isOccupied;
  }

  public void setOccupiedIcon(ImageElement occupiedIcon) {
    this.occupiedIcon = occupiedIcon;
  }

  public void setEmptyIcon(ImageElement emptyIcon) {
    this.emptyIcon = emptyIcon;
  }
  
  public boolean equals(Chair c){
    if(super.equals(c)){
      return this.isOccupied == c.isOccupied;
    }
    return false;
  }
  
  @Override
  public void draw(Context2d context){
    
    super.icon = this.isOccupied ? this.occupiedIcon : this.emptyIcon;
    super.draw(context);
  }
  
  
  /**
   * Does nothing for chairs. Instead, use {@code setOccupiedIcon(Image)} or {@code setEmptyIcon(Image)} instead.
   */
  @Override
  public void setIcon(ImageElement icon){
    // Does nothing
  }
}
