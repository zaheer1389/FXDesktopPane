package com.zaheer.controls;


import javafx.scene.Node;

/**
 * 
 * @author Zaheer Khorajiya
 *
 */

public class Utility {

    /**
     * @param mainPane
     * @return
     */
    public static FXWindow getMDIWindow(Node mainPane){
        FXWindow mdiW = (FXWindow) mainPane.getParent().getParent();
        return mdiW;
    }
}
