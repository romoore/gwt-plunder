package org.grailrtls.marauder.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.grailrtls.wmbrowser.client.Attribute;
import org.grailrtls.wmbrowser.client.AttributeDataProvider;
import org.grailrtls.wmbrowser.client.JsAttribute;
import org.grailrtls.wmbrowser.client.JsWorldState;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
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
  
  private final Canvas canvas = Canvas.createIfSupported();
  private final Context2d canvasContext = this.canvas.getContext2d();
  
  private Image receiverImage = new Image(GWT.getModuleBaseURL().toString() + "images/antenna.png");
  private Image regionImage = null;
  
  private List<WorldState> receiverLocations = new ArrayList<WorldState>();
  
  private VerticalPanel mainPanel = new VerticalPanel();
  private HorizontalPanel regionPanel = new HorizontalPanel();
  
  public static String QUERY_HOST = "grail.rutgers.edu";
  public static String QUERY_PORT = "7011";
  public static String QUERY_PATH = "/grailrest";
  public static final String SNAPSHOT_PATH = "/snapshot?uri=";
  
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    // Load images for receivers
    if(this.canvas == null){
      return;
    }
    
    this.regionPanel.add(this.regionBox);
    this.regionPanel.add(this.regionButton);
    
    this.mainPanel.add(this.regionPanel);
    this.mainPanel.add(this.canvas);
    
    RootLayoutPanel.get().add(this.mainPanel);
    
    final Timer timer = new Timer() {
      
      @Override
      public void run() {
        doUpdate();        
      }
    };
    
    timer.scheduleRepeating(3000);
    
    this.regionButton.addKeyPressHandler(new KeyPressHandler() {
      
      @Override
      public void onKeyPress(KeyPressEvent event) {
        if(event.getCharCode() == KeyCodes.KEY_ENTER){
          Marauder.this.loadNewRegion();
        }
      }
    });
  }
  
  void loadNewRegion(){
    String uri = this.regionBox.getText().trim();
    if(uri.length() == 0){
      return;
    }
    // Get snapshot of exactly "region." + uri 
    
    
    // Get a snapshot of everything in ".*" + uri + ".*" with attribute location.[xy]offset
    
    // For each WorldState that comes back, match-up the URIs and get the right image, then draw it...
    
    
  }
  
   void doUpdate(){
     
     // Get locations (WorldState) of objects in the region
     
     // Get dimensions of browser of root panel
     RootLayoutPanel rootPanel = RootLayoutPanel.get();
     int minX = rootPanel.getAbsoluteLeft();
     int minY = rootPanel.getAbsoluteTop();
     
     // Draw region image
     if(this.regionImage != null){
       
     }
     
     // Draw receivers
     for(String receiverName : )
     
     // Draw the rest
     
    this.canvasContext.drawImage(ImageElement.as(this.receiverImage.getElement()), 50, 50);
  }
   
   final void retrieveSnapshot(final String uri) {
     final String url = URL.encode(this.searchPath + uri) + "&callback=";

     getWSJson(this.jsonRequestId++, url, this);
   }
   
   protected native static void getWSJson(final int requestId, final String url,
       AttributeDataProvider handler)/*-{
     var callback = "callback" + requestId;

     var script = document.createElement("script");

     script.setAttribute("src", url + callback);
     script.setAttribute("type", "text/javascript");

     window[callback] = function(jsonObj) {
       handler.@org.grailrtls.marauder.client.Marauder::handleWSResponse(Lcom/google/gwt/core/client/JavaScriptObject;)(jsonObj);
       window[callback + "done"] = true;
     }

     setTimeout(
         function() {
           if (!window[callback + "done"]) {
             handler.@org.grailrtls.marauder.client.Marauder::handleWSResponse(Lcom/google/gwt/core/client/JavaScriptObject;)(null);
           }

           document.body.removeChild(script);
           delete window[callback];
           delete window[callback + "done"];
         }, 2000);

     document.body.appendChild(script);

   }-*/;
   
   public void handleWSResponse(JavaScriptObject jso) {
     if (jso == null) {
       return;
     }
     this.updateWorldState(asArrayOfWorldState(jso));
   }
   
   private final native JsArray<JsWorldState> asArrayOfWorldState(
       JavaScriptObject jso) /*-{
     return jso;
   }-*/;
   
   protected void updateWorldState(JsArray<JsWorldState> states) {
     this.theAttributes.clear();
     if (states == null || states.length() == 0) {
       return;
     }
     JsWorldState iState = states.get(0);
     for (int j = 0; j < iState.getAttributes().length(); ++j) {
       JsAttribute jAttr = iState.getAttributes().get(j);
       Attribute newAttr = new Attribute();
       newAttr.setCreated(jAttr.getCreated());
       newAttr.setExpires(jAttr.getExpires());
       newAttr.setData(jAttr.getData());
       newAttr.setName(jAttr.getName());
       newAttr.setOrigin(jAttr.getOrigin());
       this.theAttributes.add(newAttr);
     }
     
     Collections.sort(this.theAttributes, this.comparator);

     this.updateRowCount(this.theAttributes.size(), true);
     this.updateRowData(0, this.theAttributes);
   }
}
