package org.grailrtls.plunder.client.drawable;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class Chair extends DrawableObject {
  
  private ImageElement occupiedIcon;
  private ImageElement emptyIcon;
  private boolean isOccupied;
  
  private int ow, oh, ew, eh;
  
//  public Chair(String uri){
//    this(uri, null, null, false);
//  }
//  
//  public Chair(String uri, Image occupiedIcon, Image emptyIcon){
//    this(uri, occupiedIcon, emptyIcon, false);
//  }
//  
  public Chair(String uri, ImageElement occupiedIcon, int ow, int oh, ImageElement emptyIcon, int ew,int eh, boolean isOccupied){
    super(uri);
    this.occupiedIcon = occupiedIcon;
    this.emptyIcon = emptyIcon;
    this.isOccupied = isOccupied;
    this.ow = ow;
    this.oh = oh;
    this.ew = ew;
    this.eh = eh;
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
    if(this.isOccupied){
    super.setIcon(this.occupiedIcon,this.ow, this.oh );
    }else{
      super.setIcon(this.emptyIcon, this.ew, this.eh);
    }
    super.draw(context);
  }
  
  
  /**
   * Does nothing for chairs. Instead, use {@code setOccupiedIcon(Image)} or {@code setEmptyIcon(Image)} instead.
   */
  @Override
  public void setIcon(ImageElement icon, int w, int h){
    // Does nothing
  }
}
