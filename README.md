Plunderer's Plot: Developer and Set-Up Guide
============================================
Author: Robert Moore
Last updated: March 28, 2014

# Development Environment
Plunderer's Plot is a Google Web Toolkit (GWT) application and so requires
both a Java Development Kit (JDK) and the GWT Development Toolkit (GDT).  I
recommend you use Oracle Java 7, the latest GWT available, and the Eclipse IDE
for Java EE developers.

 * Oracle Java 7 [http://java.oracle.com](http://java.oracle.com)
 * Eclipse IDE [http://eclipse.org/](http://eclipse.org/)
 * Google Web Toolkit [http://www.gwtproject.org/](http://www.gwtproject.org/)

You may need to cherry-pick the versions you use so that everything is
compatible, but the important part is Google Web Toolkit.  

# Deployment Environment
Plunderer's plot is a web-based application that also relies on the
unsupported "Owl Platform REST Interface" that was written for internal demo
applications (like this one!).  Furthermore, you will need an HTTP server to
store static files (like images, icons, etc.).  Finally, since the author is a
lazy developer, he relies on Apache Maven to do a lot of heavy lifting
regarding project build/deploy, especially the REST interface.

 * Apache HTTPD [https://httpd.apache.org/](https://httpd.apache.org/)
 * Old Owl Platform REST Interface [https://github.com/romoore/old-owl-rest](https://github.com/romoore/old-owl-rest)
 * Apache Maven [http://maven.apache.org/](http://maven.apache.org/)

# Building the Project
Here's the hardest part, and I'll try to highlight the important steps:

 * Launch Eclipse and install the Apache Maven and  GWT integration
	 components.  This requires you to have previously installed Maven and GWT
	 somewhere.
 * Import this project into Eclipse as an Existing Maven Project.
 * Once the project is imported, open the WorldModelInterface class and adjust
	 the QUERY_HOST, QUERY_PORT, and QUERY_PATH values to match your
	 environment. These should reflect the URL of the Owl Platform REST
	 Interface service.
 * Select the "project folder" for the Plunderer's Plot project in the Package
	 Explorer view and then click the GDT Pull-down menu (a blue sphere with
	 'g') and click the GWT Compile Project option.  Set the log level to
	 something appropriate (Warn for production, Debug or Trace for
	 development), configure the Output style, and click Compile. When prompted,
	 make sure the "war" directory of the project is chosen for the output.
 * GWT Compile will take a long time (perhaps several minutes), and will
	 usually go from 0% to 100% without any intermediate steps.
 * When completed, add 'index.html', 'Plunder.css', and the 'plunder'
	 directory to a zip/tgz archive and copy this archive to the server.
 * You should uncompress the archive inside a subdirectory in the HTTP
	 server's "public" directory, so something like "/var/www/map".  This will
	 create the "/var/www/map/index.html", "/var/www/map/Plunder.css", and
	 "/var/www/map/plunder/" files and directory.
 * Adjust the file/directory permissions and ownership to be correct for your
	 installation.
 * Try to load up the page in a browser.  Be sure to specify the correct
	 Region name in the text box, or as a parameter to the URL.  For example:
	 http://example.org/map/index.html?q=myregion

