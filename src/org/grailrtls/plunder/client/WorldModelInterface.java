package org.grailrtls.plunder.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.URL;

public class WorldModelInterface {
  
  private static final Logger log = Logger.getLogger(WorldModelInterface.class.getName());

  public static String QUERY_HOST = "grail.rutgers.edu";
  public static String QUERY_PORT = "7011";
  public static String QUERY_PATH = "/grailrest";
  public static final String SNAPSHOT_PATH = "/snapshot?uri=";
  
  int requestTimeoutMs = 3000;

  private int jsonRequestId = 0;

  private final Plunder parent;

  public WorldModelInterface(final Plunder parent) {
    this.parent = parent;
  }

  public void getRegionDetails(String regionUri) {
    final String uri = URL.encode("http://" + QUERY_HOST + ":" + QUERY_PORT
        + QUERY_PATH + SNAPSHOT_PATH + "region." + regionUri)
        + "&callback=";

    createRegionCallback(this.jsonRequestId++, uri, this,this.requestTimeoutMs);
  }
  
  public void getLocatableDetails(String regionUri){
    
    log.severe("@OBJECTS@");
  
    final String url = URL.encode("http://" + QUERY_HOST + ":" + QUERY_PORT
        + QUERY_PATH + SNAPSHOT_PATH + regionUri + ".*")
        + "&attribute=location.[xy]offset&callback=";

    createLocatableCallback(this.jsonRequestId++, url, this,this.requestTimeoutMs);
  }

  protected native static void createLocatableCallback(final int requestId,
      final String url, WorldModelInterface handler, int timeout)/*-{
		var callback = "callback" + requestId;

		var script = document.createElement("script");

		script.setAttribute("src", url + callback);
		script.setAttribute("type", "text/javascript");

		window[callback] = function(jsonObj) {
			handler.@org.grailrtls.plunder.client.WorldModelInterface::handleLocatableJson(Lcom/google/gwt/core/client/JavaScriptObject;)(jsonObj);
			window[callback + "done"] = true;
		}

		setTimeout(
				function() {
					if (!window[callback + "done"]) {
						handler.@org.grailrtls.plunder.client.WorldModelInterface::handleLocatableJson(Lcom/google/gwt/core/client/JavaScriptObject;)(null);
					}

					document.body.removeChild(script);
					delete window[callback];
					delete window[callback + "done"];
				}, timeout);

		document.body.appendChild(script);

  }-*/;

  protected native static void createRegionCallback(final int requestId,
      final String url, WorldModelInterface handler, int timeout)/*-{
		var callback = "callback" + requestId;

		var script = document.createElement("script");

		script.setAttribute("src", url + callback);
		script.setAttribute("type", "text/javascript");

		window[callback] = function(jsonObj) {
			handler.@org.grailrtls.plunder.client.WorldModelInterface::handleRegionJson(Lcom/google/gwt/core/client/JavaScriptObject;)(jsonObj);
			window[callback + "done"] = true;
		}

		setTimeout(
				function() {
					if (!window[callback + "done"]) {
						handler.@org.grailrtls.plunder.client.WorldModelInterface::handleRegionJson(Lcom/google/gwt/core/client/JavaScriptObject;)(null);
					}

					document.body.removeChild(script);
					delete window[callback];
					delete window[callback + "done"];
				}, timeout);

		document.body.appendChild(script);

  }-*/;

  public void handleRegionJson(JavaScriptObject jso) {
    if (jso == null) {
      return;
    }
    this.parent.updateRegionInfo(asArrayOfWorldState(jso));
  }

  public void handleLocatableJson(JavaScriptObject jso) {
    if (jso == null) {
      return;
    }
    this.parent.updateLocatableObjInfo(asArrayOfWorldState(jso));
  }

  private final native JsArray<JsWorldState> asArrayOfWorldState(
      JavaScriptObject jso) /*-{
		return jso;
  }-*/;

  public int getRequestTimeoutMs() {
    return requestTimeoutMs;
  }

  public void setRequestTimeoutMs(int requestTimeoutMs) {
    this.requestTimeoutMs = requestTimeoutMs;
  }
}
