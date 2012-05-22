package org.grailrtls.plunder.client;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.grailrtls.plunder.resource.PlunderResource;


import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Plunder implements EntryPoint {
  
  
  
  
  private final TextBox regionBox = new TextBox();

  private final Button regionButton = new Button("Change Region");

  private Canvas canvas;
  private Canvas backBufferCanvas;
  private Context2d context;
  private Context2d backBufferContext;

  
  private static final Image receiverImage = new Image(PlunderResource.INSTANCE.transmitter().getSafeUri());
  private static final Image transmitterImage = new Image(PlunderResource.INSTANCE.transmitter().getSafeUri());
  private static final Image unknownImage = new Image(PlunderResource.INSTANCE.unknown().getSafeUri());
  
  private Image regionImage = null;
  private float regionWidth = 1f;
  private float regionHeight = 1f;
  private float regionWidthToHeight = 1f;
  private String regionUri = null;

  // Data from world model
  private WorldState regionState = null;
  private Map<String, DrawableObject> objectLocations = new HashMap<String, DrawableObject>();

  private DockLayoutPanel mainPanel = new DockLayoutPanel(Style.Unit.EM);
  private FlowPanel regionPanel = new FlowPanel();

  public static String QUERY_HOST = "grail.rutgers.edu";
  public static String QUERY_PORT = "7011";
  public static String QUERY_PATH = "/grailrest";
  public static final String SNAPSHOT_PATH = "/snapshot?uri=";
  
  private boolean initDimens = true;

  private int jsonRequestId = 0;

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    this.canvas = Canvas.createIfSupported();
    this.backBufferCanvas = Canvas.createIfSupported();
    
    // Load images for receivers
    if (this.canvas == null) {
      return;
    }
    this.canvas.setSize("100%", "100%");

    this.context = this.canvas.getContext2d();
    this.backBufferContext = this.backBufferCanvas.getContext2d();

    this.regionPanel.add(this.regionBox);
    this.regionPanel.add(this.regionButton);

    this.mainPanel.addNorth(this.regionPanel, 5);
    this.mainPanel.add(this.canvas);

    
   


    // this.mainPanel.setSize("100%", "100%");

    RootLayoutPanel.get().add(this.mainPanel);
    

    final Timer objectTimer = new Timer() {

      @Override
      public void run() {
        Plunder.this.prepareLocatableUrl();
      }
    };

    objectTimer.scheduleRepeating(3000);

    this.regionBox.addKeyPressHandler(new KeyPressHandler() {

      @Override
      public void onKeyPress(KeyPressEvent event) {
        if (event.getCharCode() == KeyCodes.KEY_ENTER) {
          Plunder.this.loadNewRegion();
        }
      }
    });

    this.regionButton.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        Plunder.this.loadNewRegion();
      }
    });

    Window.addResizeHandler(new ResizeHandler() {

      @Override
      public void onResize(final ResizeEvent event) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

          @Override
          public void execute() {
            
            int width = event.getWidth();
            int height = event.getHeight();
            Plunder.this.canvas.setCoordinateSpaceWidth(width);
            Plunder.this.canvas.setCoordinateSpaceHeight(height);
            Plunder.this.backBufferCanvas.setCoordinateSpaceWidth(width);
            Plunder.this.backBufferCanvas.setCoordinateSpaceHeight(height);
            Plunder.this.updateBuffers();
          }
        });

      }
    });
    
    String initRegion = Window.Location.getParameter("q");
    if(initRegion != null && initRegion.trim().length() > 0){
      this.regionBox.setText(initRegion);
      this.regionUri = initRegion;
      this.loadNewRegion();
    }
  }

  void loadNewRegion() {
    this.regionImage = null;
    this.regionWidth = -1f;
    this.regionHeight = -1f;
    this.regionWidthToHeight = 1f;
    this.objectLocations.clear();
    String uri = this.regionBox.getText().trim();
    this.regionUri = uri;
    this.updateBuffers();
    if (uri.length() == 0) {
      this.regionUri = null;
      return;
    }
    this.prepareRegionUrl();
    
  }

  void repaint() {
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      
      @Override
      public void execute() {
        Plunder.this.context.drawImage(Plunder.this.backBufferContext.getCanvas(), 0, 0);
      }
    });
    
  }

  void updateBuffers() {
    
    if(this.initDimens){
      this.canvas.setCoordinateSpaceWidth(this.canvas.getOffsetWidth());
      this.canvas.setCoordinateSpaceHeight(this.canvas.getOffsetHeight());
      this.backBufferCanvas.setCoordinateSpaceWidth(this.canvas.getOffsetWidth());
      this.backBufferCanvas.setCoordinateSpaceHeight(this.canvas.getOffsetHeight());
      this.initDimens = false;
    }

    int sWidth = this.canvas.getOffsetWidth(); // this.canvas.getCoordinateSpaceWidth();
    int sHeight = this.canvas.getOffsetHeight(); // this.canvas.getCoordinateSpaceHeight();

    int drawWidth = sWidth;
    int drawHeight = (int) (sWidth / this.regionWidthToHeight);

    // Too tall? Rescale
    if (drawHeight > sHeight) {
      drawHeight = sHeight;
      drawWidth = (int) (sHeight * this.regionWidthToHeight);
    }

    // Get dimensions of browser of root panel

//    this.canvas.setCoordinateSpaceHeight(sHeight);
//    this.canvas.setCoordinateSpaceWidth(sWidth);
    this.backBufferContext.clearRect(0, 0, sWidth, sHeight);

    float xRtS = drawWidth / this.regionWidth;
    float yRtS = drawHeight / this.regionHeight;


    // Draw region image
    if (this.regionImage != null) {
      int regImgWidth = this.regionImage.getWidth();
      int regImgHeight = this.regionImage.getHeight();
      this.backBufferContext.drawImage(
          ImageElement.as(this.regionImage.getElement()), 0, 0, regImgWidth,
          regImgHeight, 0, 0, drawWidth, drawHeight);
    }

    // Draw receivers
    for (DrawableObject obj : this.objectLocations.values()) {
      
      
      float objX = obj.getxOffset();
      float objY = obj.getyOffset();

      if (objX < 0 || objY < 0) {
        continue;
      }

      objX = objX * xRtS;
      objY = (this.regionHeight - objY) * yRtS;
      int imgWidth = obj.getIcon().getWidth();
      int imgHeight = obj.getIcon().getHeight();
      int canvX0 = (int) (objX - imgWidth / 2f);
      int canvY0 = (int) (objY - imgHeight / 2f);
      
//      this.backBufferContext.setFillStyle("red");
//      this.backBufferContext.fillRect(canvX0, canvY0, imgWidth, imgHeight);
      this.backBufferContext.drawImage(ImageElement.as(obj.getIcon().getElement()), 0, 0, imgWidth,
          imgHeight, canvX0, canvY0, imgWidth, imgHeight);
    }

    this.repaint();
  }

  final void prepareRegionUrl() {
    if (this.regionUri == null) {
      return;
    }
    final String uri = URL.encode("http://" + Plunder.QUERY_HOST + ":"
        + Plunder.QUERY_PORT + Plunder.QUERY_PATH + Plunder.SNAPSHOT_PATH
        + "region." + this.regionUri)
        + "&callback=";

    createRegionCallback(this.jsonRequestId++, uri, this);
  }

  final void prepareLocatableUrl() {
    if (this.regionUri == null) {
      return;
    }
    final String url = URL.encode("http://" + Plunder.QUERY_HOST + ":"
        + Plunder.QUERY_PORT + Plunder.QUERY_PATH + Plunder.SNAPSHOT_PATH
        + this.regionUri + ".*")
        + "&attribute=location.[xy]offset&callback=";

    createLocatableCallback(this.jsonRequestId++, url, this);
  }

  protected native static void createLocatableCallback(final int requestId,
      final String url, Plunder handler)/*-{
		var callback = "callback" + requestId;

		var script = document.createElement("script");

		script.setAttribute("src", url + callback);
		script.setAttribute("type", "text/javascript");

		window[callback] = function(jsonObj) {
			handler.@org.grailrtls.plunder.client.Plunder::handleLocatableJson(Lcom/google/gwt/core/client/JavaScriptObject;)(jsonObj);
			window[callback + "done"] = true;
		}

		setTimeout(
				function() {
					if (!window[callback + "done"]) {
						handler.@org.grailrtls.plunder.client.Plunder::handleLocatableJson(Lcom/google/gwt/core/client/JavaScriptObject;)(null);
					}

					document.body.removeChild(script);
					delete window[callback];
					delete window[callback + "done"];
				}, 2000);

		document.body.appendChild(script);

  }-*/;

  protected native static void createRegionCallback(final int requestId,
      final String url, Plunder handler)/*-{
		var callback = "callback" + requestId;

		var script = document.createElement("script");

		script.setAttribute("src", url + callback);
		script.setAttribute("type", "text/javascript");

		window[callback] = function(jsonObj) {
			handler.@org.grailrtls.plunder.client.Plunder::handleRegionJson(Lcom/google/gwt/core/client/JavaScriptObject;)(jsonObj);
			window[callback + "done"] = true;
		}

		setTimeout(
				function() {
					if (!window[callback + "done"]) {
						handler.@org.grailrtls.plunder.client.Plunder::handleRegionJson(Lcom/google/gwt/core/client/JavaScriptObject;)(null);
					}

					document.body.removeChild(script);
					delete window[callback];
					delete window[callback + "done"];
				}, 2000);

		document.body.appendChild(script);

  }-*/;

  public void handleRegionJson(JavaScriptObject jso) {
    if (jso == null) {
      return;
    }
    this.updateRegionInfo(asArrayOfWorldState(jso));
  }

  public void handleLocatableJson(JavaScriptObject jso) {
    if (jso == null) {
      return;
    }
    this.updateLocatableObjInfo(asArrayOfWorldState(jso));
  }

  private final native JsArray<JsWorldState> asArrayOfWorldState(
      JavaScriptObject jso) /*-{
		return jso;
  }-*/;

  protected void updateRegionInfo(JsArray<JsWorldState> regionStateArray) {

    if (regionStateArray == null || regionStateArray.length() == 0) {
      return;
    }

    JsWorldState iState = regionStateArray.get(0);
    WorldState newRegion = new WorldState();
    newRegion.setUri(iState.getUri());
    Attribute[] attribs = new Attribute[iState.getAttributes().length()];
    newRegion.setAttributes(attribs);
    for (int j = 0; j < iState.getAttributes().length(); ++j) {
      JsAttribute jAttr = iState.getAttributes().get(j);
      Attribute newAttr = new Attribute();
      newAttr.setCreated(jAttr.getCreated());
      newAttr.setExpires(jAttr.getExpires());
      newAttr.setData(jAttr.getData());
      newAttr.setName(jAttr.getName());
      newAttr.setOrigin(jAttr.getOrigin());
      attribs[j] = newAttr;

      // Check for region image
      if (jAttr.getName().equals("image.url")) {
        String imageUrl = jAttr.getData();
        // Image.prefetch(imageUrl);
        Image regionBgImg = new Image(imageUrl);

        this.regionImage = regionBgImg;
      }

      // Check for region dimensions
      else if (jAttr.getName().equals("location.maxx")) {
        this.regionWidth = Float.parseFloat(jAttr.getData());

      } else if (jAttr.getName().equals("location.maxy")) {
        this.regionHeight = Float.parseFloat(jAttr.getData());
      }

    }
    if (this.regionHeight > 0 && this.regionWidth > 0) {
      this.regionWidthToHeight = this.regionWidth / this.regionHeight;
    }
    this.regionState = newRegion;
    
    this.updateBuffers();
    
  }

  protected void updateLocatableObjInfo(JsArray<JsWorldState> objectStates) {

    if (objectStates == null || objectStates.length() == 0) {
      return;
    }

    boolean dirty = false;
    for (int i = 0; i < objectStates.length(); ++i) {
      
      JsWorldState iState = objectStates.get(i);
      String uri = iState.getUri();
      DrawableObject newObject = new DrawableObject(uri);
      if(uri.contains("receiver")){
        newObject.setIcon(this.receiverImage);
      }else if(uri.contains("transmitter")){
        newObject.setIcon(this.transmitterImage);
      }else{
        // Unknown object
        newObject.setIcon(this.unknownImage);
      }

      DrawableObject currObject = this.objectLocations.get(uri);

      for (int j = 0; j < iState.getAttributes().length(); ++j) {
        JsAttribute jAttr = iState.getAttributes().get(j);
        if (jAttr.getName().equals("location.xoffset")) {
          
          newObject.setxOffset(Float.parseFloat(jAttr.getData()));
        } else if (jAttr.getName().equals("location.yoffset")) {
          newObject.setyOffset(Float.parseFloat(jAttr.getData()));
        }
      }
      if (currObject == null || !currObject.equals(newObject)) {
        dirty = true;
        this.objectLocations.put(uri, newObject);
      }
    }
    if (dirty) {

      Scheduler.get().scheduleDeferred(new ScheduledCommand() {

        @Override
        public void execute() {
          Plunder.this.updateBuffers();
        }
      });
    }
  }
}
