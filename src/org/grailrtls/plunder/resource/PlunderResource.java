package org.grailrtls.plunder.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface PlunderResource extends ClientBundle {
  
  public static final PlunderResource INSTANCE = GWT.create(PlunderResource.class);

  @Source("images/antenna.png")
  ImageResource transmitter();
  
  @Source("images/antenna.png")
  ImageResource receiver();

  
  @Source("images/unlabelled.png")
  ImageResource unknown();

}
