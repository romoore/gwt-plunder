package org.grailrtls.plunder.client;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.grailrtls.plunder.client.drawable.Chair;
import org.grailrtls.plunder.client.drawable.Door;
import org.grailrtls.plunder.client.drawable.DrawableObject;
import org.grailrtls.plunder.client.drawable.Microwave;
import org.grailrtls.plunder.client.drawable.Projector;
import org.grailrtls.plunder.client.drawable.Screen;
import org.grailrtls.plunder.resource.PlunderResource;

import sun.security.krb5.internal.util.KrbDataOutputStream;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

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

  // FIXME: Big hack for totalIcons.
  int totalIcons = 23;
  int loadedIcons = 0;

  private static final String KEY_RECEIVER = "receiver";
  private static final String KEY_TRANSMITTER = "transmitter";
  private static final String KEY_DOOR_OPEN = "door-open";
  private static final String KEY_DOOR_CLOSED = "door-closed";
  private static final String KEY_PROJECTOR_ON = "projector-on";
  private static final String KEY_PROJECTOR_OFF = "projector-off";
  private static final String KEY_UNKNOWN = "unknown";
  private static final String KEY_CHAIR_EMPTY = "chair-empty";
  private static final String KEY_CHAIR_OCCUPIED = "chair-occupied";
  private static final String KEY_COFFEE_OLD = "coffee-old";
  private static final String KEY_COFFEE_FRESH = "coffee-fresh";
  private static final String KEY_DUCT_TAPE = "duct tape";
  private static final String KEY_GLUE_GUN = "glue gun";
  private static final String KEY_MICROWAVE_OFF = "microwave-off";
  private static final String KEY_MICROWAVE_ON = "microwave-on";
  private static final String KEY_MUG = "mug";
  private static final String KEY_PACKING_TAPE = "packing tape";
  private static final String KEY_SCREEN_OFF = "screen-off";
  private static final String KEY_SCREEN_ON = "screen-on";
  private static final String KEY_SOLDERING_IRON = "soldering iron";
  private static final String KEY_REFRIGERATOR = "refrigerator";
  private static final String KEY_PRINTER = "printer";
  private static final String KEY_PHONE = "phone";

  private Map<String, ImageWrapper> iconImages = new HashMap<String, ImageWrapper>();

  private static final Image IMG_RECEIVER = new Image(PlunderResource.INSTANCE
      .receiver().getSafeUri());

  private static final Image IMG_TRANSMITTER = new Image(
      PlunderResource.INSTANCE.transmitter().getSafeUri());

  private static final Image IMG_DOOR_OPEN = new Image(PlunderResource.INSTANCE
      .doorOpen().getSafeUri());

  private static final Image IMG_DOOR_CLOSED = new Image(
      PlunderResource.INSTANCE.doorClosed().getSafeUri());

  private static final Image IMG_PROJECTOR_ON = (new Image(
      PlunderResource.INSTANCE.projectorOn().getSafeUri()));
  private static final Image IMG_PROJECTOR_OFF = new Image(
      PlunderResource.INSTANCE.projectorOff().getSafeUri());
  private static final Image IMG_UNKNOWN = new Image(PlunderResource.INSTANCE
      .unknown().getSafeUri());
  private static final Image IMG_CHAIR_EMPTY = new Image(
      PlunderResource.INSTANCE.chairEmpty().getSafeUri());
  private static final Image IMG_CHAIR_OCCUPIED = new Image(
      PlunderResource.INSTANCE.chairOccupied().getSafeUri());
  private static final Image IMG_COFFEE_OLD = new Image(
      PlunderResource.INSTANCE.coffeepotOld().getSafeUri());
  private static final Image IMG_COFFEE_FRESH = new Image(
      PlunderResource.INSTANCE.coffeepotFresh().getSafeUri());

  private static final Image IMG_DUCT_TAPE = new Image(PlunderResource.INSTANCE
      .ductTape().getSafeUri());
  private static final Image IMG_GLUEGUN = new Image(PlunderResource.INSTANCE
      .glueGun().getSafeUri());
  private static final Image IMG_MICROWAVE_OFF = new Image(
      PlunderResource.INSTANCE.microwaveOff().getSafeUri());
  private static final Image IMG_MICROWAVE_ON = new Image(
      PlunderResource.INSTANCE.microwaveOn().getSafeUri());
  private static final Image IMG_MUG = new Image(PlunderResource.INSTANCE.mug()
      .getSafeUri());
  private static final Image IMG_PACKINGTAPE = new Image(
      PlunderResource.INSTANCE.packingTape().getSafeUri());
  private static final Image IMG_SCREEN_ON = new Image(PlunderResource.INSTANCE
      .screenOn().getSafeUri());
  private static final Image IMG_SCREEN_OFF = new Image(
      PlunderResource.INSTANCE.screenOff().getSafeUri());

  private static final Image IMG_SOLDER = new Image(PlunderResource.INSTANCE
      .solderingIron().getSafeUri());
  private static final Image IMG_FRIDGE = new Image(PlunderResource.INSTANCE
      .refrigerator().getSafeUri());
  private static final Image IMG_PRINTER = new Image(PlunderResource.INSTANCE
      .printer().getSafeUri());

  private static final Image IMG_PHONE = new Image(PlunderResource.INSTANCE
      .mobilePhone().getSafeUri());

  private Image regionImage = null;
  private float regionWidth = 1f;
  private float regionHeight = 1f;
  private float regionWidthToHeight = 1f;
  private String regionId = null;

  private float regionToScreenX = 1f;
  private float regionToScreenY = 1f;

  private Map<String, DrawableObject> objectLocations = new HashMap<String, DrawableObject>();

  private DockLayoutPanel mainPanel = new DockLayoutPanel(Style.Unit.EM);
  private FlowPanel regionPanel = new FlowPanel();

  private boolean initDimens = true;

  private WorldModelInterface wmi = new WorldModelInterface(this);

  private float magnification = 1f;

  private Timer locationUpdateTimer;

  private Label errorText = new Label();

  private WorldStatePopup popup = new WorldStatePopup();

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {

    this.canvas = Canvas.createIfSupported();
    this.backBufferCanvas = Canvas.createIfSupported();

    this.errorText.setVisible(false);
    this.regionPanel.add(this.regionBox);
    this.regionPanel.add(this.regionButton);

    this.mainPanel.addNorth(this.regionPanel, 5);

    if (this.canvas == null) {
      this.mainPanel.addNorth(this.errorText, 5);
      this.errorText
          .setText("Sorry, but your browser does not support the HTML5 Canvas element. If you wish to use this application, please upgrade to a newer browser.");
      this.errorText.setVisible(true);
      return;
    }
    this.canvas.setSize("800" + Style.Unit.PX, "600" + Style.Unit.PX);

    this.context = this.canvas.getContext2d();
    this.backBufferContext = this.backBufferCanvas.getContext2d();

    ScrollPanel scroller = new ScrollPanel();
    scroller.add(this.canvas);

    // this.mainPanel.add(this.canvas);
    this.mainPanel.add(scroller);

    this.mainPanel.setSize("100%", "100%");

    // Preload images
    {
      prepareIcon(KEY_RECEIVER, IMG_RECEIVER);
      prepareIcon(KEY_TRANSMITTER, IMG_TRANSMITTER);
      prepareIcon(KEY_DOOR_OPEN, IMG_DOOR_OPEN);
      prepareIcon(KEY_DOOR_CLOSED, IMG_DOOR_CLOSED);
      prepareIcon(KEY_PROJECTOR_ON, IMG_PROJECTOR_ON);
      prepareIcon(KEY_PROJECTOR_OFF, IMG_PROJECTOR_OFF);
      prepareIcon(KEY_UNKNOWN, IMG_UNKNOWN);
      prepareIcon(KEY_CHAIR_EMPTY, IMG_CHAIR_EMPTY);
      prepareIcon(KEY_CHAIR_OCCUPIED, IMG_CHAIR_OCCUPIED);
      prepareIcon(KEY_COFFEE_OLD, IMG_COFFEE_OLD);
      prepareIcon(KEY_COFFEE_FRESH, IMG_COFFEE_FRESH);
      prepareIcon(KEY_DUCT_TAPE, IMG_DUCT_TAPE);
      prepareIcon(KEY_GLUE_GUN, IMG_GLUEGUN);
      prepareIcon(KEY_MICROWAVE_OFF, IMG_MICROWAVE_OFF);
      prepareIcon(KEY_MICROWAVE_ON, IMG_MICROWAVE_ON);
      prepareIcon(KEY_MUG, IMG_MUG);
      prepareIcon(KEY_PACKING_TAPE, IMG_PACKINGTAPE);
      prepareIcon(KEY_SCREEN_ON, IMG_SCREEN_ON);
      prepareIcon(KEY_SCREEN_OFF, IMG_SCREEN_OFF);
      prepareIcon(KEY_SOLDERING_IRON, IMG_SOLDER);
      prepareIcon(KEY_REFRIGERATOR, IMG_FRIDGE);
      prepareIcon(KEY_PRINTER, IMG_PRINTER);
      prepareIcon(KEY_PHONE, IMG_PHONE);
    }
  }

  void finishModuleLoad() {
    log.fine("Finishing module load.");
    RootLayoutPanel.get().add(this.mainPanel);

    this.locationUpdateTimer = new Timer() {

      @Override
      public void run() {
        if (Plunder.this.regionId != null
            && Plunder.this.regionId.length() > 0) {

        }

        log.fine("@TIMER: Retrieving objects automatically. ("
            + Plunder.this.refreshRate + " ms)");
        Plunder.this.wmi.getLocatableDetails(Plunder.this.regionId);

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
        log.fine("Window resized to (" + event.getWidth() + ", "
            + event.getHeight() + ")");
        Plunder.this.resizeCanvas(event.getWidth(), event.getHeight());

      }
    });

    String refreshString = Window.Location.getParameter("refresh");
    if (refreshString != null) {
      this.refreshRate = Math.max(Integer.parseInt(refreshString) * 1000, 1000);
      this.requestTimeout = Math.max(this.refreshRate / 2, 500);
    }

    this.wmi.setRequestTimeoutMs(this.requestTimeout);

    String initRegion = Window.Location.getParameter("q");
    if (initRegion != null && initRegion.trim().length() > 0) {
      this.regionBox.setText(initRegion);
      this.regionId = initRegion;
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {

        @Override
        public void execute() {
          log.fine("(Def. Ex.)Loading initial region value: "
              + Plunder.this.regionId);
          Plunder.this.loadNewRegion();
        }
      });

    }

    this.canvas.addMouseDownHandler(new MouseDownHandler() {

      @Override
      public void onMouseDown(MouseDownEvent event) {
        if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
          Plunder.this.showDetailPopup(event);
        }

      }
    });
  }

  void showDetailPopup(final MouseDownEvent event) {
    for (DrawableObject obj : this.objectLocations.values()) {
      final int x = event.getX();
      final int y = event.getY();
      if (obj.containsPoint(x, y)) {
        this.popup.setState(obj.getState());
        this.popup.setPopupPosition(event.getClientX(), event.getClientY());
        this.popup.setPopupPositionAndShow(new PositionCallback() {

          @Override
          public void setPosition(int offsetWidth, int offsetHeight) {

            int newX = event.getClientX();
            int newY = event.getClientY();
            if (newX + offsetWidth > Window.getClientWidth()) {
              newX = Window.getClientWidth() - offsetWidth;
            }
            if (newY + offsetHeight > Window.getClientHeight()) {
              newY = Window.getClientHeight() - offsetHeight;
            }
            Plunder.this.popup.setPopupPosition(newX, newY);

          }
        });
      }

    }
  }

  void prepareIcon(final String match, final Image icon) {

    ImageWrapper wrapper = new ImageWrapper(icon);
    this.iconImages.put(match, wrapper);

    icon.setVisible(false);

    icon.addLoadHandler(new LoadHandler() {

      @Override
      public void onLoad(LoadEvent event) {
        RootPanel.get().remove(icon);
        ++Plunder.this.loadedIcons;
        if (Plunder.this.loadedIcons >= Plunder.this.totalIcons) {
          Plunder.this.finishModuleLoad();
        }
        Plunder.log.finer("Loaded: " + Plunder.this.loadedIcons + "/"
            + Plunder.this.totalIcons + " icons");
      }
    });
    RootPanel.get().add(icon);
  }

  void loadNewRegion() {
    this.regionId = this.regionBox.getText().trim();
    log.fine("Loading region " + this.regionId);

    if (this.locationUpdateTimer != null) {
      this.locationUpdateTimer.cancel();
    }
    this.regionImage = null;
    this.regionWidth = 1f;
    this.regionHeight = 1f;
    this.regionWidthToHeight = 1f;
    this.objectLocations.clear();

    this.redrawBuffer();
    if (this.regionId.length() == 0) {
      this.regionId = null;
      return;
    }
    this.prepareRegionUrl();

  }

  void repaint() {

    log.fine("Clearing canvas (" + this.canvas.getCoordinateSpaceWidth() + ", "
        + this.canvas.getCoordinateSpaceHeight() + ")");

    this.context.clearRect(0, 0, Plunder.this.canvas.getCoordinateSpaceWidth(),
        Plunder.this.canvas.getCoordinateSpaceHeight());
    if (this.canvas.getCoordinateSpaceWidth() > 0
        && this.canvas.getCoordinateSpaceHeight() > 0) {
      log.fine("Drawing from backbuffer ("
          + this.backBufferCanvas.getCoordinateSpaceWidth() + ", "
          + this.backBufferCanvas.getCoordinateSpaceHeight() + ")");
      this.context.drawImage(Plunder.this.backBufferCanvas.getCanvasElement(),
          0, 0);
    }

  }

  void resizeCanvas(int newWidth, int newHeight) {
    log.fine("Canvas resize requested (" + newWidth + ", " + newHeight + ")");
    int width = newWidth;
    int height = newHeight;

    if (this.regionImage != null && this.regionImage.getWidth() > 0) {
      width = this.regionImage.getWidth();
      height = this.regionImage.getHeight();

    }
    log.fine("Actual resize dimensions (" + width + ", " + height + ")");

    // this.canvas.getCanvasElement().setWidth(width);
    // this.canvas.getCanvasElement().setHeight(height);

    this.canvas.setCoordinateSpaceWidth((int) width);
    this.canvas.setCoordinateSpaceHeight((int) height);
    this.backBufferCanvas.setCoordinateSpaceWidth((int) width);
    this.backBufferCanvas.setCoordinateSpaceHeight((int) height);
    this.canvas.setSize(width + Style.Unit.PX.getType(),
        height + Style.Unit.PX.getType());

    int drawWidth = width;
    int drawHeight = (int) (width / this.regionWidthToHeight);

    // Too tall? Rescale
    if (drawHeight > height) {
      drawHeight = height;
      drawWidth = (int) (height * this.regionWidthToHeight);
    }

    log.fine("Drawable area (" + drawWidth + ", " + drawHeight + ")");

    this.regionToScreenX = drawWidth / this.regionWidth;
    this.regionToScreenY = drawHeight / this.regionHeight;

    log.fine("Ratios X: " + this.regionToScreenX + ", Y: "
        + this.regionToScreenY);

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
    log.fine("Rendering onto (" + sWidth + ", " + sHeight + ")");

    int drawWidth = sWidth;
    int drawHeight = (int) (sWidth / this.regionWidthToHeight);

    // Too tall? Rescale
    if (drawHeight > sHeight) {
      drawHeight = sHeight;
      drawWidth = (int) (sHeight * this.regionWidthToHeight);
    }

    log.fine("Drawable area (" + drawWidth + ", " + drawHeight + ")");

    // Get dimensions of browser of root panel

    // this.canvas.setCoordinateSpaceHeight(sHeight);
    // this.canvas.setCoordinateSpaceWidth(sWidth);
    log.fine("Clearing backbuffer (" + sWidth + ", " + sHeight + ")");
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
    if (this.regionId == null) {
      return;
    }
    this.wmi.getRegionDetails(this.regionId);
  }

  final void prepareLocatableUrl() {
    if (this.regionId == null) {
      return;
    }
    this.wmi.getLocatableDetails(this.regionId);
  }

  protected void updateLocatableObjInfo(
      final JsArray<JsWorldState> objStateArray) {

    if (objStateArray == null || objStateArray.length() == 0) {
      log.info("No objects received.");
      return;
    }

    Scheduler.get().scheduleIncremental(new RepeatingCommand() {
      private int stepSize = 10;
      private int offset = 0;

      @Override
      public boolean execute() {
        boolean retVal = Plunder.this.incrementalObjUpdate(objStateArray,
            stepSize, offset);
        if (retVal) {
          offset += stepSize;
        }
        return retVal;
      }
    });

  }

  protected void updateRegionInfo(JsArray<JsWorldState> regionStateArray) {
    log.info("Region information received.");
    if (regionStateArray == null || regionStateArray.length() == 0) {
      return;
    }
    JsWorldState iState = regionStateArray.get(0);
    WorldState newRegion = new WorldState();
    newRegion.setId(iState.getIdentifier());
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
        log.fine("Region Image URL: " + imageUrl);
        this.regionImage = regionBgImg;
      }

      // Check for region dimensions
      else if (jAttr.getName().equals("location.maxx")) {
        this.regionWidth = Float.parseFloat(jAttr.getData());
        log.fine("Region X-Max: " + this.regionWidth);
        // this.regionToScreenX =
        // this.backBufferCanvas.getCoordinateSpaceWidth()
        // / this.regionWidth;

      } else if (jAttr.getName().equals("location.maxy")) {
        this.regionHeight = Float.parseFloat(jAttr.getData());
        log.fine("Region Y-Max: " + this.regionHeight);
        // this.regionToScreenY =
        // this.backBufferCanvas.getCoordinateSpaceHeight()
        // / this.regionHeight;
      }

    }

    if (this.regionHeight > 0 && this.regionWidth > 0) {
      this.regionWidthToHeight = this.regionWidth / this.regionHeight;
    }

    log.fine("Region A/R: " + this.regionWidthToHeight + " ("
        + this.regionWidth + "/" + this.regionHeight + ")");

    // Defer the next step until the region image is loaded
    if (this.regionImage != null) {
      this.regionImage.setVisible(false);
      this.regionImage.addLoadHandler(new LoadHandler() {

        @Override
        public void onLoad(LoadEvent event) {
          log.fine("(Def. Ex.) Region image loaded.");
          RootPanel.get().remove(Plunder.this.regionImage);
          Plunder.this.finishRegionLoading();

        }
      });
      RootPanel.get().add(this.regionImage);

    } else {
      this.finishRegionLoading();
    }
  }

  protected void finishRegionLoading() {
    this.resizeCanvas((int) Plunder.this.regionWidth,
        (int) Plunder.this.regionHeight);

    // Plunder.this.redrawBuffer();

    this.wmi.getLocatableDetails(this.regionId);
    this.locationUpdateTimer.scheduleRepeating(Plunder.this.refreshRate);
  }

  boolean dirty = false;

  protected boolean incrementalObjUpdate(JsArray<JsWorldState> objectStates,
      int stepSize, int offset) {

    int totalObjects = objectStates.length();

    int i = offset;
    int j = 0;
    for (; j < stepSize && i < totalObjects; ++i) {
      log.finer("Object " + (i + 1) + "/" + (totalObjects) + ".");

      JsWorldState iState = objectStates.get(i);
      String identifier = iState.getIdentifier();
      // In case we have old data coming back late.
      // if (!uri.contains(this.regionUri)) {
      // log.info("Skipping " + uri + " with incorrect region name.");
      // continue;
      // }
      DrawableObject currObject = this.objectLocations.get(identifier);

      DrawableObject newObject = createObject(iState);
      if(newObject == null){
        continue;
      }
      if (currObject == null || !currObject.equals(newObject)) {
        log.finer("[" + newObject + "] has changed [" + currObject
            + "]. Updating object location.");
        dirty = true;
        this.objectLocations.put(identifier, newObject);
      }
    }

    if (i < totalObjects) {
      return true;
    }

    if (dirty) {
      log.fine("1 or more objects is dirty, buffer should be redrawn.");
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {

        @Override
        public void execute() {
          log.fine("(Def. Ex.) Redrawing dirty back buffer.");
          Plunder.this.redrawBuffer();
        }
      });
    }
    dirty = false;
    log.fine("Completed object updates.");
    return false;
  }

  DrawableObject createObject(final JsWorldState fromState) {
    DrawableObject obj = null;
    String identifier = fromState.getIdentifier();

    float xOff = -1f;
    float yOff = -1f;

    boolean binaryValue = false; // The binary state it has
    
    boolean locationUriMatch = false;

    for (int j = 0; j < fromState.getAttributes().length(); ++j) {
      JsAttribute jAttr = fromState.getAttributes().get(j);
      if (jAttr.getName().equals("location.xoffset")) {
        // Skip dynamic locations for certain objects.
        if (jAttr.getOrigin().contains("solver")
            && (identifier.contains("screen") || identifier.contains("door")
                || identifier.contains("projector") || identifier.contains("refrigerator")
                || identifier.contains("printer") || identifier.contains("coffee pot") || identifier
                  .contains("microwave"))) {
          continue;
        }

        xOff = Float.parseFloat(jAttr.getData());
      } else if (jAttr.getName().equals("location.yoffset")) {
        // Skip dynamic locations for certain objects.
        if (jAttr.getOrigin().contains("solver")
            && (identifier.contains("screen") || identifier.contains("door")
                || identifier.contains("projector") || identifier.contains("refrigerator")
                || identifier.contains("printer") || identifier.contains("coffee pot") || identifier
                  .contains("microwave"))) {
          continue;
        }
        yOff = Float.parseFloat(jAttr.getData());
      } else if (jAttr.getName().equals("on")) {
        binaryValue = Boolean.valueOf(jAttr.getData());
      } else if (jAttr.getName().equals("closed")) {
        binaryValue = !Boolean.valueOf(jAttr.getData());
      } else if (jAttr.getName().equals("empty")) {
        binaryValue = !Boolean.valueOf(jAttr.getData());
      }else if(jAttr.getName().equals("location.uri")){
        locationUriMatch = this.regionId.equals(jAttr.getData());
      }
    }

    // Not in this region, return null
    if (!identifier.contains(this.regionId) && !locationUriMatch) {
      return null;
    }
    
    // No coordinates
    if(xOff < 0 || yOff < 0){
      return null;
    }

    ImageWrapper wrapper1, wrapper2;

    if (identifier.contains("receiver")) {
      wrapper1 = this.iconImages.get(KEY_RECEIVER);
      obj = new DrawableObject(identifier);
      obj.setIcon(wrapper1.getElement(), wrapper1.getImage().getWidth(),
          wrapper1.getImage().getHeight());
    } else if (identifier.contains("transmitter")) {
      wrapper1 = this.iconImages.get(KEY_TRANSMITTER);
      obj = new DrawableObject(identifier);
      obj.setIcon(wrapper1.getElement(), wrapper1.getImage().getWidth(),
          wrapper1.getImage().getHeight());
    } else if (identifier.contains("door")) {
      wrapper1 = this.iconImages.get(KEY_DOOR_OPEN);
      wrapper2 = this.iconImages.get(KEY_DOOR_CLOSED);
      obj = new Door(identifier, wrapper1.getElement(),
          wrapper1.getImage().getWidth(), wrapper1.getImage().getHeight(),
          wrapper2.getElement(), wrapper2.getImage().getWidth(), wrapper2
              .getImage().getHeight(), binaryValue);
    } else if (identifier.contains("projector")) {
      wrapper1 = this.iconImages.get(KEY_PROJECTOR_ON);
      wrapper2 = this.iconImages.get(KEY_PROJECTOR_OFF);
      obj = new Projector(identifier, wrapper1.getElement(), wrapper1.getImage()
          .getWidth(), wrapper1.getImage().getHeight(), wrapper2.getElement(),
          wrapper2.getImage().getWidth(), wrapper2.getImage().getHeight(),
          binaryValue);
    } else if (identifier.contains("coffee pot")) {
      obj = new DrawableObject(identifier);
      wrapper1 = this.iconImages.get(KEY_COFFEE_OLD);
      // TODO: Coffee with state?
      obj.setIcon(wrapper1.getElement(), wrapper1.getImage().getWidth(),
          wrapper1.getImage().getHeight());
    } else if (identifier.contains("mug")) {
      obj = new DrawableObject(identifier);
      wrapper1 = this.iconImages.get(KEY_MUG);
      obj.setIcon(wrapper1.getElement(), wrapper1.getImage().getWidth(),
          wrapper1.getImage().getHeight());
    } else if (identifier.contains("microwave")) {
      wrapper1 = this.iconImages.get(KEY_MICROWAVE_ON);
      wrapper2 = this.iconImages.get(KEY_MICROWAVE_OFF);
      obj = new Microwave(identifier, wrapper1.getElement(), wrapper1.getImage()
          .getWidth(), wrapper1.getImage().getHeight(), wrapper2.getElement(),
          wrapper2.getImage().getWidth(), wrapper2.getImage().getHeight(),
          binaryValue);
    } else if (identifier.contains("duct tape")) {
      wrapper1 = this.iconImages.get(KEY_DUCT_TAPE);
      obj = new DrawableObject(identifier);
      obj.setIcon(wrapper1.getElement(), wrapper1.getImage().getWidth(),
          wrapper1.getImage().getHeight());
    } else if (identifier.contains("hot glue gun")) {
      obj = new DrawableObject(identifier);
      wrapper1 = this.iconImages.get(KEY_GLUE_GUN);
      obj.setIcon(wrapper1.getElement(), wrapper1.getImage().getWidth(),
          wrapper1.getImage().getHeight());
    } else if (identifier.contains("chair")) {
      wrapper1 = this.iconImages.get(KEY_CHAIR_OCCUPIED);
      wrapper2 = this.iconImages.get(KEY_CHAIR_EMPTY);
      obj = new Chair(identifier, wrapper1.getElement(), wrapper1.getImage()
          .getWidth(), wrapper1.getImage().getHeight(), wrapper2.getElement(),
          wrapper2.getImage().getWidth(), wrapper2.getImage().getHeight(),
          binaryValue);
    } else if (identifier.contains("packing tape")) {
      wrapper1 = this.iconImages.get(KEY_PACKING_TAPE);
      obj = new DrawableObject(identifier);
      obj.setIcon(wrapper1.getElement(), wrapper1.getImage().getWidth(),
          wrapper1.getImage().getHeight());
    } else if (identifier.contains("screen")) {
      wrapper1 = this.iconImages.get(KEY_SCREEN_ON);
      wrapper2 = this.iconImages.get(KEY_SCREEN_OFF);
      obj = new Screen(identifier, wrapper1.getElement(), wrapper1.getImage()
          .getWidth(), wrapper1.getImage().getHeight(), wrapper2.getElement(),
          wrapper2.getImage().getWidth(), wrapper2.getImage().getHeight(),
          binaryValue);
    } else if (identifier.contains(KEY_SOLDERING_IRON)) {
      obj = new DrawableObject(identifier);
      wrapper1 = this.iconImages.get("soldering iron");
      obj.setIcon(wrapper1.getElement(), wrapper1.getImage().getWidth(),
          wrapper1.getImage().getHeight());
    } else if (identifier.contains("refrigerator")) {
      wrapper1 = this.iconImages.get(KEY_REFRIGERATOR);
      obj = new DrawableObject(identifier);
      obj.setIcon(wrapper1.getElement(), wrapper1.getImage().getWidth(),
          wrapper1.getImage().getHeight());
    } else if (identifier.contains("printer")) {
      obj = new DrawableObject(identifier);
      wrapper1 = this.iconImages.get(KEY_PRINTER);
      obj.setIcon(wrapper1.getElement(), wrapper1.getImage().getWidth(),
          wrapper1.getImage().getHeight());
    } else if (identifier.contains("phone")) {
      obj = new DrawableObject(identifier);
      wrapper1 = this.iconImages.get(KEY_PHONE);
      obj.setIcon(wrapper1.getElement(), wrapper1.getImage().getWidth(),
          wrapper1.getImage().getHeight());
    }

    else {
      wrapper1 = this.iconImages.get(KEY_UNKNOWN);
      obj = new DrawableObject(identifier);
      obj.setIcon(wrapper1.getElement(), wrapper1.getImage().getWidth(),
          wrapper1.getImage().getHeight());
    }

    obj.setxOffset(xOff);
    // FIXME: Nasty hack for Y-translation
    obj.setyOffset(this.regionHeight - yOff);

    log.finer(obj.toString() + ": (" + xOff + ", " + yOff + "->"
        + obj.getyOffset() + ")");

    obj.setxScale(this.regionToScreenX);
    obj.setyScale(this.regionToScreenY);
    obj.setState(fromJavaScript(fromState));

    return obj;
  }

  protected WorldState fromJavaScript(JsWorldState fromState) {
    WorldState toState = new WorldState();
    toState.setId(fromState.getIdentifier());

    Attribute[] attrs = new Attribute[fromState.getAttributes().length()];
    toState.setAttributes(attrs);

    for (int i = 0; i < attrs.length; ++i) {
      attrs[i] = fromJavaScript(fromState.getAttributes().get(i));
    }
    return toState;
  }

  protected Attribute fromJavaScript(JsAttribute fromAttr) {
    Attribute toAttr = new Attribute();

    toAttr.setName(fromAttr.getName());
    toAttr.setOrigin(fromAttr.getOrigin());
    toAttr.setCreated(fromAttr.getCreated());
    toAttr.setExpires(fromAttr.getExpires());
    toAttr.setData(fromAttr.getData());

    return toAttr;
  }
}
