package org.grailrtls.plunder.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.ScrollPaneLayout;

import org.grailrtls.plunder.client.drawable.Chair;
import org.grailrtls.plunder.client.drawable.Door;
import org.grailrtls.plunder.client.drawable.DrawableObject;
import org.grailrtls.plunder.client.drawable.Microwave;
import org.grailrtls.plunder.client.drawable.Projector;
import org.grailrtls.plunder.client.drawable.Screen;
import org.grailrtls.plunder.resource.PlunderResource;

import sun.management.counter.Units;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.URL;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.sun.jndi.url.corbaname.corbanameURLContextFactory;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Plunder implements EntryPoint {
  
  private static final Logger log = Logger.getLogger(Plunder.class.getName());

  private final TextBox regionBox = new TextBox();

  private final Button regionButton = new Button("Change Region");

  private Canvas canvas;
  private Canvas backBufferCanvas;
  private Context2d context;
  private Context2d backBufferContext;

  private int refreshRate = 2000;
  private int requestTimeout = 1000;

  private static final ImageElement IMG_RECEIVER = ImageElement.as(new Image(
      PlunderResource.INSTANCE.transmitter().getSafeUri()).getElement());
  private static final ImageElement IMG_TRANSMITTER = ImageElement
      .as(new Image(PlunderResource.INSTANCE.transmitter().getSafeUri())
          .getElement());
  private static final ImageElement IMG_DOOR_OPEN = ImageElement.as(new Image(
      PlunderResource.INSTANCE.doorOpen().getSafeUri()).getElement());
  private static final ImageElement IMG_DOOR_CLOSED = ImageElement
      .as(new Image(PlunderResource.INSTANCE.doorClosed().getSafeUri())
          .getElement());
  private static final ImageElement IMG_PROJECTOR_ON = ImageElement
      .as(new Image(PlunderResource.INSTANCE.projectorOn().getSafeUri())
          .getElement());
  private static final ImageElement IMG_PROJECTOR_OFF = ImageElement
      .as(new Image(PlunderResource.INSTANCE.projectorOff().getSafeUri())
          .getElement());
  private static final ImageElement IMG_UNKNOWN = ImageElement.as(new Image(
      PlunderResource.INSTANCE.unknown().getSafeUri()).getElement());
  private static final ImageElement IMG_CHAIR_EMPTY = ImageElement
      .as(new Image(PlunderResource.INSTANCE.chairEmpty().getSafeUri())
          .getElement());
  private static final ImageElement IMG_CHAIR_OCCUPIED = ImageElement
      .as(new Image(PlunderResource.INSTANCE.chairOccupied().getSafeUri())
          .getElement());
  private static final ImageElement IMG_COFFEE_OLD = ImageElement.as(new Image(
      PlunderResource.INSTANCE.coffeepotOld().getSafeUri()).getElement());
  private static final ImageElement IMG_COFFEE_FRESH = ImageElement
      .as(new Image(PlunderResource.INSTANCE.coffeepotFresh().getSafeUri())
          .getElement());
  private static final ImageElement IMG_DUCT_TAPE = ImageElement.as(new Image(
      PlunderResource.INSTANCE.ductTape().getSafeUri()).getElement());
  private static final ImageElement IMG_GLUEGUN = ImageElement.as(new Image(
      PlunderResource.INSTANCE.glueGun().getSafeUri()).getElement());
  private static final ImageElement IMG_MICROWAVE_OFF = ImageElement
      .as(new Image(PlunderResource.INSTANCE.microwaveOff().getSafeUri())
          .getElement());
  private static final ImageElement IMG_MICROWAVE_ON = ImageElement
      .as(new Image(PlunderResource.INSTANCE.microwaveOn().getSafeUri())
          .getElement());
  private static final ImageElement IMG_MUG = ImageElement.as(new Image(
      PlunderResource.INSTANCE.mug().getSafeUri()).getElement());
  private static final ImageElement IMG_PACKINGTAPE = ImageElement
      .as(new Image(PlunderResource.INSTANCE.packingTape().getSafeUri())
          .getElement());
  private static final ImageElement IMG_SCREEN_ON = ImageElement.as(new Image(
      PlunderResource.INSTANCE.screenOn().getSafeUri()).getElement());
  private static final ImageElement IMG_SCREEN_OFF = ImageElement.as(new Image(
      PlunderResource.INSTANCE.screenOff().getSafeUri()).getElement());

  private Image regionImage = null;
  private float regionWidth = 1f;
  private float regionHeight = 1f;
  private float regionWidthToHeight = 1f;
  private String regionUri = null;

  private float regionToScreenX = 1f;
  private float regionToScreenY = 1f;

  private Map<String, DrawableObject> objectLocations = new HashMap<String, DrawableObject>();

  private DockLayoutPanel mainPanel = new DockLayoutPanel(Style.Unit.EM);
  private FlowPanel regionPanel = new FlowPanel();

  private boolean initDimens = true;

  private WorldModelInterface wmi = new WorldModelInterface(this);

  private float magnification = 1f;

  private Timer locationUpdateTimer;

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
    this.canvas.setSize("800" + Style.Unit.PX, "600" + Style.Unit.PX);

    this.context = this.canvas.getContext2d();
    this.backBufferContext = this.backBufferCanvas.getContext2d();

    ScrollPanel scroller = new ScrollPanel();
    scroller.add(this.canvas);

    this.regionPanel.add(this.regionBox);
    this.regionPanel.add(this.regionButton);

    this.mainPanel.addNorth(this.regionPanel, 5);
    this.mainPanel.add(scroller);

    this.mainPanel.setSize("100%", "100%");

    RootLayoutPanel.get().add(this.mainPanel);

    this.locationUpdateTimer = new Timer() {

      @Override
      public void run() {
        if (Plunder.this.regionUri != null
            && Plunder.this.regionUri.length() > 0) {

        }
        
        log.info("@TIMER: Retrieving objects automatically. (" + Plunder.this.refreshRate + " ms)");
        Plunder.this.wmi.getLocatableDetails(Plunder.this.regionUri);

      }
    };

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
        log.info("@WINDOW: Resized to (" + event.getWidth() + ", " + event.getHeight() + ")");
        Plunder.this.resizeCanvas(event.getWidth(), event.getHeight());

      }
    });

    String refreshString = Window.Location.getParameter("refresh");
    if (refreshString != null) {
      this.refreshRate = Math.max(Integer.parseInt(refreshString) * 1000, 1000);
      this.requestTimeout = Math.max(this.refreshRate / 2, 500);
    }

    String initRegion = Window.Location.getParameter("q");
    if (initRegion != null && initRegion.trim().length() > 0) {
      this.regionBox.setText(initRegion);
      this.regionUri = initRegion;
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {

        @Override
        public void execute() {
          log.info("@DEFER: Loading initial region value: " + Plunder.this.regionUri);
          Plunder.this.loadNewRegion();
        }
      });

    }
  }

  void loadNewRegion() {
    this.regionUri = this.regionBox.getText().trim();
    log.severe("Loading region " + this.regionUri);
    
    if (this.locationUpdateTimer != null) {
      this.locationUpdateTimer.cancel();
    }
    this.regionImage = null;
    this.regionWidth = 1f;
    this.regionHeight = 1f;
    this.regionWidthToHeight = 1f;
    this.objectLocations.clear();
    
    
    this.redrawBuffer();
    if (this.regionUri.length() == 0) {
      this.regionUri = null;
      return;
    }
    this.prepareRegionUrl();

  }

  void repaint() {

    log.severe("+REPAINT: Clearing (" + this.canvas.getCoordinateSpaceWidth() + ", " + this.canvas.getCoordinateSpaceHeight() +")");
    
    this.context.clearRect(0, 0, Plunder.this.canvas.getCoordinateSpaceWidth(),
        Plunder.this.canvas.getCoordinateSpaceHeight());
    if (this.canvas.getCoordinateSpaceWidth() > 0
        && this.canvas.getCoordinateSpaceHeight() > 0) {
      log.severe("+REPAINT: Drawing (" + this.backBufferCanvas.getCoordinateSpaceWidth() + ", " + this.backBufferCanvas.getCoordinateSpaceHeight() +")");
      this.context.drawImage(Plunder.this.backBufferCanvas.getCanvasElement(),
          0, 0);
    }

  }

  void resizeCanvas(int newWidth, int newHeight) {
    log.severe("+RESIZE (" + newWidth + ", " + newHeight + ")");
    int width = newWidth;
    int height = newHeight;

    if (this.regionImage != null && this.regionImage.getWidth() > 0) {
      width = this.regionImage.getWidth();
      height = this.regionImage.getHeight();
      log.info("+RESIZE: Using image dimensions (" + width + ", " + height + ")");
    }

    // this.canvas.getCanvasElement().setWidth(width);
    // this.canvas.getCanvasElement().setHeight(height);

    this.canvas.setCoordinateSpaceWidth((int) width);
    this.canvas.setCoordinateSpaceHeight((int) height);
    this.backBufferCanvas.setCoordinateSpaceWidth((int) width);
    this.backBufferCanvas.setCoordinateSpaceHeight((int) height);
    this.canvas.setSize(width + Style.Unit.PX.getType(), height + Style.Unit.PX.getType());

    int drawWidth = width;
    int drawHeight = (int) (width / this.regionWidthToHeight);

    // Too tall? Rescale
    if (drawHeight > height) {
      drawHeight = height;
      drawWidth = (int) (height * this.regionWidthToHeight);
    }

    log.info("+RESIZE: Drawable area (" + drawWidth + ", " + drawHeight + ")");
    
    this.regionToScreenX = drawWidth / this.regionWidth;
    this.regionToScreenY = drawHeight / this.regionHeight;
    
    log.info("+RESIZE: Ratios X: " + this.regionToScreenX + ", Y: " + this.regionToScreenY);

    // this.canvas.getCanvasElement().setWidth((int) (width *
    // this.magnification));
    // this.canvas.getCanvasElement().setHeight(
    // (int) (height* this.magnification));

    for (DrawableObject obj : this.objectLocations.values()) {
      obj.setxScale(this.regionToScreenX);
      obj.setyScale(this.regionToScreenY);
    }

    this.redrawBuffer();
  }

  private void redrawBuffer() {
    
    int sWidth = this.canvas.getCoordinateSpaceWidth();
    int sHeight = this.canvas.getCoordinateSpaceHeight();
    log.severe("+REDRAW onto (" + sWidth + ", " + sHeight + ")");
    
    int drawWidth = sWidth;
    int drawHeight = (int) (sWidth / this.regionWidthToHeight);

    // Too tall? Rescale
    if (drawHeight > sHeight) {
      drawHeight = sHeight;
      drawWidth = (int) (sHeight * this.regionWidthToHeight);
    }

    log.info("+REDRAW: Drawable area (" + drawWidth + ", " + drawHeight + ")");
    
    // Get dimensions of browser of root panel

    // this.canvas.setCoordinateSpaceHeight(sHeight);
    // this.canvas.setCoordinateSpaceWidth(sWidth);
    log.info("+REDRAW: Clearing back (" + sWidth + ", " + sHeight + ")");
    this.backBufferContext.clearRect(0, 0, sWidth, sHeight);

    // Draw region image
    if (this.regionImage != null) {
      int regImgWidth = this.regionImage.getWidth();
      int regImgHeight = this.regionImage.getHeight();
      this.backBufferContext.drawImage(
          ImageElement.as(this.regionImage.getElement()), 0, 0, regImgWidth,
          regImgHeight, 0, 0, drawWidth, drawHeight);
    }

    this.backBufferContext.save();

    // Draw receivers
    for (DrawableObject obj : this.objectLocations.values()) {
      if (obj.getxOffset() >= 0 && obj.getyOffset() >= 0) {
        obj.draw(this.backBufferContext);
      }

    }
    this.backBufferContext.restore();
    this.repaint();

  }

  final void prepareRegionUrl() {
    if (this.regionUri == null) {
      return;
    }
    this.wmi.getRegionDetails(this.regionUri);
  }

  final void prepareLocatableUrl() {
    if (this.regionUri == null) {
      return;
    }
    this.wmi.getLocatableDetails(this.regionUri);
  }

  protected void updateRegionInfo(JsArray<JsWorldState> regionStateArray) {
    log.severe("+REGION UPDATE");
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
        log.info("+REGION: Image URL: " + imageUrl);
        this.regionImage = regionBgImg;
      }

      // Check for region dimensions
      else if (jAttr.getName().equals("location.maxx")) {
        this.regionWidth = Float.parseFloat(jAttr.getData());
        log.info("+REGION: X-Max: " + this.regionWidth);
        // this.regionToScreenX =
        // this.backBufferCanvas.getCoordinateSpaceWidth()
        // / this.regionWidth;

      } else if (jAttr.getName().equals("location.maxy")) {
        this.regionHeight = Float.parseFloat(jAttr.getData());
        log.info("+REGION: Y-Max: " + this.regionHeight);
        // this.regionToScreenY =
        // this.backBufferCanvas.getCoordinateSpaceHeight()
        // / this.regionHeight;
      }

    }

    if (this.regionHeight > 0 && this.regionWidth > 0) {
      this.regionWidthToHeight = this.regionWidth / this.regionHeight;
    }
    
    log.info("+REGION: Region A/R: " + this.regionWidthToHeight + " (" + this.regionWidth + "/" + this.regionHeight + ")");

    // Defer the next step until the region image is loaded
    if(this.regionImage != null){
      this.regionImage.setVisible(false);
      RootPanel.get().add(this.regionImage);
      this.regionImage.addLoadHandler(new LoadHandler() {
        
        @Override
        public void onLoad(LoadEvent event) {
          log.severe("+REGION IMAGE LOADED");
          RootPanel.get().remove(Plunder.this.regionImage);
          Plunder.this.finishRegionLoading();
          
          
        }
      });
    }
    else{
      this.finishRegionLoading();
    }
  }
  
  protected void finishRegionLoading(){
    this.resizeCanvas((int) Plunder.this.regionWidth,
        (int) Plunder.this.regionHeight);

//    Plunder.this.redrawBuffer();

    this.wmi.getLocatableDetails(Plunder.this.regionUri);
    this.locationUpdateTimer
        .scheduleRepeating(Plunder.this.refreshRate);
  }

  protected void updateLocatableObjInfo(JsArray<JsWorldState> objectStates) {

    log.severe("+OBJECT Update");
    
    if (objectStates == null || objectStates.length() == 0) {
      return;
    }

    boolean dirty = false;
    for (int i = 0; i < objectStates.length(); ++i) {

      JsWorldState iState = objectStates.get(i);
      String uri = iState.getUri();
      // In case we have old data coming back late.
      if (!uri.contains(this.regionUri)) {
        continue;
      }
      DrawableObject currObject = this.objectLocations.get(uri);

      DrawableObject newObject = createObject(iState);
      if (currObject == null || !currObject.equals(newObject)) {
        dirty = true;
        this.objectLocations.put(uri, newObject);
      }
    }
    if (dirty) {
      log.info("+OBJECT: Dirty, redrawing.");
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        
        @Override
        public void execute() {
          log.info("+DEFER: Redrawing dirty back buffer.");
          Plunder.this.redrawBuffer();
        }
      });
    }
  }

  DrawableObject createObject(final JsWorldState fromState) {
    DrawableObject obj = null;
    String uri = fromState.getUri();

    float xOff = -1f;
    float yOff = -1f;

    boolean binaryValue = false; // The binary state it has

    for (int j = 0; j < fromState.getAttributes().length(); ++j) {
      JsAttribute jAttr = fromState.getAttributes().get(j);
      if (jAttr.getName().equals("location.xoffset")) {
        xOff = Float.parseFloat(jAttr.getData());
      } else if (jAttr.getName().equals("location.yoffset")) {
        yOff = Float.parseFloat(jAttr.getData());
      } else if (jAttr.getName().equals("on")) {
        binaryValue = Boolean.valueOf(jAttr.getData());
      } else if (jAttr.getName().equals("closed")) {
        binaryValue = !Boolean.valueOf(jAttr.getData());
      } else if (jAttr.getName().equals("empty")) {
        binaryValue = !Boolean.valueOf(jAttr.getData());
      }
    }

    if (uri.contains("receiver")) {
      obj = new DrawableObject(uri);
      obj.setIcon(this.IMG_RECEIVER);
    } else if (uri.contains("transmitter")) {
      obj = new DrawableObject(uri);
      obj.setIcon(this.IMG_TRANSMITTER);
    } else if (uri.contains("door")) {
      obj = new Door(uri, IMG_DOOR_OPEN, IMG_DOOR_CLOSED, binaryValue);
    } else if (uri.contains("projector")) {
      obj = new Projector(uri, IMG_PROJECTOR_ON, IMG_PROJECTOR_OFF, binaryValue);
    } else if (uri.contains("coffee pot")) {
      obj = new DrawableObject(uri);
      // TODO: Coffee with state?
      obj.setIcon(IMG_COFFEE_OLD);
    } else if (uri.contains("mug")) {
      obj = new DrawableObject(uri);
      obj.setIcon(IMG_MUG);
    } else if (uri.contains("microwave")) {
      obj = new Microwave(uri, IMG_MICROWAVE_ON, IMG_MICROWAVE_OFF, binaryValue);
    } else if (uri.contains("duct tape")) {
      obj = new DrawableObject(uri);
      obj.setIcon(IMG_DUCT_TAPE);
    } else if (uri.contains("hot glue gun")) {
      obj = new DrawableObject(uri);
      obj.setIcon(IMG_GLUEGUN);
    } else if (uri.contains("chair")) {
      obj = new Chair(uri, IMG_CHAIR_OCCUPIED, IMG_CHAIR_EMPTY, binaryValue);
    } else if (uri.contains("packing tape")) {
      obj = new DrawableObject(uri);
      obj.setIcon(IMG_PACKINGTAPE);
    } else if (uri.contains("screen")) {
      obj = new Screen(uri, IMG_SCREEN_ON, IMG_SCREEN_OFF, binaryValue);
    }

    else {
      obj = new DrawableObject(uri);
      obj.setIcon(this.IMG_UNKNOWN);
    }

    obj.setxOffset(xOff);
    // FIXME: Nasty hack for Y-translation
    obj.setyOffset(this.regionHeight - yOff);
    
    log.info("+DRAWABLE (" + uri + "): (" + xOff + ", " + yOff + "->" + obj.getyOffset() + ")");

    obj.setxScale(this.regionToScreenX);
    obj.setyScale(this.regionToScreenY);

    return obj;
  }
}
