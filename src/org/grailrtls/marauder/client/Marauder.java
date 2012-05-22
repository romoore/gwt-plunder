package org.grailrtls.marauder.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Marauder implements EntryPoint {

  private static final String IMAGE_URL_RECVR = "images/antenna.png";
  private static final String IMAGE_URL_DOOR = "images/door.png";
  private static final String IMAGE_URL_DOOR_OPEN = "images/door_open.png";

  private final TextBox regionBox = new TextBox();

  private final Button regionButton = new Button("Change Region");

  private Canvas canvas;
  private Canvas backBufferCanvas;
  private Context2d context;
  private Context2d backBufferContext;

  private Image receiverImage = new Image(GWT.getModuleBaseURL().toString()
      + "images/antenna.png");
  private Image regionImage = null;
  private float regionWidth = 1f;
  private float regionHeight = 1f;
  private float regionWidthToHeight = 1f;
  private String regionUri = null;

  // Data from world model
  private WorldState regionState = null;
  private Map<String, Receiver> receiverLocations = new HashMap<String, Receiver>();

  private DockLayoutPanel mainPanel = new DockLayoutPanel(Style.Unit.EM);
  private FlowPanel regionPanel = new FlowPanel();

  public static String QUERY_HOST = "localhost";
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
        Marauder.this.prepareLocatableUrl();
      }
    };

    objectTimer.scheduleRepeating(3000);

    this.regionBox.addKeyPressHandler(new KeyPressHandler() {

      @Override
      public void onKeyPress(KeyPressEvent event) {
        if (event.getCharCode() == KeyCodes.KEY_ENTER) {
          Marauder.this.loadNewRegion();
        }
      }
    });

    this.regionButton.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        Marauder.this.loadNewRegion();
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
            Marauder.this.canvas.setCoordinateSpaceWidth(width);
            Marauder.this.canvas.setCoordinateSpaceHeight(height);
            Marauder.this.backBufferCanvas.setCoordinateSpaceWidth(width);
            Marauder.this.backBufferCanvas.setCoordinateSpaceHeight(height);
            Marauder.this.updateBuffers();
          }
        });

      }
    });
  }

  void loadNewRegion() {
    this.regionImage = null;
    this.regionWidth = -1f;
    this.regionHeight = -1f;
    this.regionWidthToHeight = 1f;
    this.receiverLocations.clear();
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
        Marauder.this.context.drawImage(Marauder.this.backBufferContext.getCanvas(), 0, 0);
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
    this.backBufferContext.clearRect(0, 0, this.backBufferCanvas.getCoordinateSpaceWidth(), this.backBufferCanvas.getCoordinateSpaceHeight());

    float xRtS = drawWidth / this.regionWidth;
    float yRtS = drawHeight / this.regionHeight;

    ImageElement recvElm = ImageElement.as(this.receiverImage.getElement());

    int rcvImgWidth = this.receiverImage.getWidth();
    int rcvImgHeight = this.receiverImage.getHeight();

    // Draw region image
    if (this.regionImage != null) {
      int regImgWidth = this.regionImage.getWidth();
      int regImgHeight = this.regionImage.getHeight();
      this.backBufferContext.drawImage(
          ImageElement.as(this.regionImage.getElement()), 0, 0, regImgWidth,
          regImgHeight, 0, 0, drawWidth, drawHeight);
    }

    // Draw receivers
    for (Receiver recv : this.receiverLocations.values()) {
      float recvX = recv.getxOffset();
      float recvY = this.regionHeight - recv.getyOffset();

      if (recvX < 0 || recvY < 0) {
        continue;
      }

      recvX = recvX * xRtS;
      recvY = recvY * yRtS;

      int canvX0 = (int) (recvX - rcvImgWidth / 2);
      int canvY0 = (int) (recvY - rcvImgHeight / 2);

      this.backBufferContext.drawImage(recvElm, 0, 0, rcvImgWidth,
          rcvImgHeight, canvX0, canvY0, rcvImgWidth, rcvImgHeight);
    }

    this.repaint();
  }

  final void prepareRegionUrl() {
    if (this.regionUri == null) {
      return;
    }
    final String uri = URL.encode("http://" + Marauder.QUERY_HOST + ":"
        + Marauder.QUERY_PORT + Marauder.QUERY_PATH + Marauder.SNAPSHOT_PATH
        + "region." + this.regionUri)
        + "&callback=";

    createRegionCallback(this.jsonRequestId++, uri, this);
  }

  final void prepareLocatableUrl() {
    if (this.regionUri == null) {
      return;
    }
    final String url = URL.encode("http://" + Marauder.QUERY_HOST + ":"
        + Marauder.QUERY_PORT + Marauder.QUERY_PATH + Marauder.SNAPSHOT_PATH
        + this.regionUri + "\\.anchor\\..*\\.receiver.*")
        + "&attribute=location.[xy]offset&callback=";

    createLocatableCallback(this.jsonRequestId++, url, this);
  }

  protected native static void createLocatableCallback(final int requestId,
      final String url, Marauder handler)/*-{
		var callback = "callback" + requestId;

		var script = document.createElement("script");

		script.setAttribute("src", url + callback);
		script.setAttribute("type", "text/javascript");

		window[callback] = function(jsonObj) {
			handler.@org.grailrtls.marauder.client.Marauder::handleLocatableJson(Lcom/google/gwt/core/client/JavaScriptObject;)(jsonObj);
			window[callback + "done"] = true;
		}

		setTimeout(
				function() {
					if (!window[callback + "done"]) {
						handler.@org.grailrtls.marauder.client.Marauder::handleLocatableJson(Lcom/google/gwt/core/client/JavaScriptObject;)(null);
					}

					document.body.removeChild(script);
					delete window[callback];
					delete window[callback + "done"];
				}, 2000);

		document.body.appendChild(script);

  }-*/;

  protected native static void createRegionCallback(final int requestId,
      final String url, Marauder handler)/*-{
		var callback = "callback" + requestId;

		var script = document.createElement("script");

		script.setAttribute("src", url + callback);
		script.setAttribute("type", "text/javascript");

		window[callback] = function(jsonObj) {
			handler.@org.grailrtls.marauder.client.Marauder::handleRegionJson(Lcom/google/gwt/core/client/JavaScriptObject;)(jsonObj);
			window[callback + "done"] = true;
		}

		setTimeout(
				function() {
					if (!window[callback + "done"]) {
						handler.@org.grailrtls.marauder.client.Marauder::handleRegionJson(Lcom/google/gwt/core/client/JavaScriptObject;)(null);
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

  protected void updateLocatableObjInfo(JsArray<JsWorldState> receiverStates) {

    if (receiverStates == null || receiverStates.length() == 0) {
      return;
    }

    boolean dirty = false;
    for (int i = 0; i < receiverStates.length(); ++i) {
      JsWorldState iState = receiverStates.get(i);
      Receiver newReceiver = new Receiver(iState.getUri());

      Receiver currReceiver = this.receiverLocations.get(iState.getUri());

      for (int j = 0; j < iState.getAttributes().length(); ++j) {
        JsAttribute jAttr = iState.getAttributes().get(j);
        if (jAttr.getName().equals("location.xoffset")) {
          newReceiver.setxOffset(Float.parseFloat(jAttr.getData()));
        } else if (jAttr.getName().equals("location.yoffset")) {
          newReceiver.setyOffset(Float.parseFloat(jAttr.getData()));
        }
      }
      if (currReceiver == null || !currReceiver.equals(newReceiver)) {
        dirty = true;
        this.receiverLocations.put(newReceiver.getUri(), newReceiver);
      }
    }
    if (dirty) {

      Scheduler.get().scheduleDeferred(new ScheduledCommand() {

        @Override
        public void execute() {
          Marauder.this.updateBuffers();
        }
      });
    }
  }
}
