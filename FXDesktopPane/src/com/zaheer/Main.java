package com.zaheer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import com.zaheer.controls.FXDesktopPane;
import com.zaheer.controls.FXWindow;

public class Main extends Application{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		AnchorPane mainPane = new AnchorPane();
		mainPane.setPrefSize(800, 600);
		
		FXDesktopPane desktopPane = new FXDesktopPane(FXDesktopPane.Theme.DEFAULT);
		AnchorPane.setBottomAnchor(desktopPane, 0d);
        AnchorPane.setLeftAnchor(desktopPane, 0d);
        AnchorPane.setTopAnchor(desktopPane, 25d);//Button place
        AnchorPane.setRightAnchor(desktopPane, 0d);
        
        mainPane.getChildren().add(desktopPane);
        
        desktopPane.addMDIWindow(new FXWindow("Window One", null, "Tax Calculator", new TaxCalculator()));
        
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
	}

}
