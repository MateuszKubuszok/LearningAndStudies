/*
 * KiBILista4App.java
 */

package kibilista4;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class KiBILista4App extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new KiBILista4View(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of KiBILista4App
     */
    public static KiBILista4App getApplication() {
        return Application.getInstance(KiBILista4App.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(KiBILista4App.class, args);
    }
}
