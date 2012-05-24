package org.grailrtls.plunder.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface PlunderResource extends ClientBundle {

  public static final PlunderResource INSTANCE = GWT
      .create(PlunderResource.class);

  @Source("images/chair_alt-empty.png")
  ImageResource chairEmpty();

  @Source("images/chair_alt.png")
  ImageResource chairOccupied();

  @Source("images/coffeepot.png")
  ImageResource coffeepotFresh();

  @Source("images/coffeepot.png")
  ImageResource coffeepotOld();

  @Source("images/door.png")
  ImageResource doorClosed();

  @Source("images/door_open.png")
  ImageResource doorOpen();

  @Source("images/duct_tape.png")
  ImageResource ductTape();

  @Source("images/gluegun.png")
  ImageResource glueGun();

  @Source("images/microwave.png")
  ImageResource microwaveOff();

  @Source("images/microwaveon.png")
  ImageResource microwaveOn();

  @Source("images/mug.png")
  ImageResource mug();

  @Source("images/packtape.png")
  ImageResource packingTape();

  @Source("images/projector.png")
  ImageResource projectorOff();

  @Source("images/projectoron.png")
  ImageResource projectorOn();
  
  @Source("images/printer-alt.png")
  ImageResource printer();

  @Source("images/receiver_2.png")
  ImageResource receiver();
  
  @Source("images/refrigerator.png")
  ImageResource refrigerator();
  
  @Source("images/screen_off.png")
  ImageResource screenOff();
  
  @Source("images/screen_on.png")
  ImageResource screenOn();
  
  @Source("images/solder_iron.png")
  ImageResource solderingIron();

  @Source("images/transmitter_2.png")
  ImageResource transmitter();

  @Source("images/unlabelled.png")
  ImageResource unknown();
}
