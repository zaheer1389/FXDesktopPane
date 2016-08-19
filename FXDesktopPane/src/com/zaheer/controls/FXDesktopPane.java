package com.zaheer.controls;

import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zaheer.exception.PositionOutOfBoundsException;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * 
 * @author Zaheer Khorajiya
 *
 */

public class FXDesktopPane extends VBox {

	private class WidthChangeListener implements ChangeListener {

		private FXDesktopPane mdi;
		private FXWindow window;

		public WidthChangeListener(FXDesktopPane mdi, FXWindow window) {
			this.mdi = mdi;
			this.window = window;
		}

		@Override
		public void changed(ObservableValue observable, Object oldValue,
				Object newValue) {
			this.mdi.centerMdiWindow(this.window);
			observable.removeListener(this);
		}
	}

	private final ScrollPane taskBar;
	private HBox tbWindows;
	private final AnchorPane paneMDIContainer;
	private FXDesktopPane mdiCanvas = this;
	private final int taskbarHeightWithoutScrool = 44;
	private final int taskbarHeightWithScrool = 54;

	/**
	 * *********** CONSTRUICTOR *************
	 */
	public FXDesktopPane(Theme theme) {
		super();

		Platform.runLater(() -> {
			switch (theme) {
			case DEFAULT:
				setTheme(Theme.DEFAULT, this.getScene());
				break;
			case DARK:
				setTheme(Theme.DARK, this.getScene());
				break;
			}
		});

		setAlignment(Pos.TOP_LEFT);

		paneMDIContainer = new AnchorPane();
		paneMDIContainer.setId("MDIContainer");
		paneMDIContainer.getStyleClass().add("mdiCanvasContainer");
		tbWindows = new HBox();
		tbWindows.setSpacing(3);
		tbWindows.setMaxHeight(taskbarHeightWithoutScrool);
		tbWindows.setMinHeight(taskbarHeightWithoutScrool);
		tbWindows.setAlignment(Pos.CENTER_LEFT);
		setVgrow(paneMDIContainer, Priority.ALWAYS);
		taskBar = new ScrollPane(tbWindows);
		Platform.runLater(() -> {
			Node viewport = taskBar.lookup(".viewport");
			try {
				viewport.setStyle(" -fx-background-color: transparent; ");
			} catch (Exception e) {
			}
		});
		taskBar.setMaxHeight(taskbarHeightWithoutScrool);
		taskBar.setMinHeight(taskbarHeightWithoutScrool);
		taskBar.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		taskBar.setVmax(0);
		taskBar.getStyleClass().add("taskBar");
		// taskBar.styleProperty().bind(StylesCSS.taskBarStyleProperty);

		tbWindows.widthProperty().addListener(
				(ObservableValue<? extends Number> observable, Number oldValue,
						Number newValue) -> {
					Platform.runLater(() -> {
						if ((double) newValue <= taskBar.getWidth()) {
							taskBar.setMaxHeight(taskbarHeightWithoutScrool);
							taskBar.setPrefHeight(taskbarHeightWithoutScrool);
							taskBar.setMinHeight(taskbarHeightWithoutScrool);
						} else {
							taskBar.setMaxHeight(taskbarHeightWithScrool);
							taskBar.setPrefHeight(taskbarHeightWithScrool);
							taskBar.setMinHeight(taskbarHeightWithScrool);
						}
					});
				});
		getChildren().addAll(paneMDIContainer, taskBar);

		addEventHandler(FXWindowEvent.EVENT_CLOSED, mdiCloseHandler);
		addEventHandler(FXWindowEvent.EVENT_MINIMIZED, mdiMinimizedHandler);
	}

	/**
	 * ***********************GETTER***********************************
	 */
	public AnchorPane getPaneMDIContainer() {
		return paneMDIContainer;
	}

	public HBox getTbWindows() {
		return tbWindows;
	}

	/**
	 * *************************REMOVE_WINDOW******************************
	 */
	public void removeMDIWindow(String mdiWindowID) {
		Node mdi = getItemFromMDIContainer(mdiWindowID);
		Node iconBar = getItemFromToolBar(mdiWindowID);

		if (mdi != null) {
			getItemFromMDIContainer(mdiWindowID).isClosed(true);

			paneMDIContainer.getChildren().remove(mdi);
		}
		if (iconBar != null) {
			tbWindows.getChildren().remove(iconBar);
		}
	}

	/**
	 * *****************************ADD_WINDOW*********************************
	 */
	public void addMDIWindow(FXWindow mdiWindow) {
		if (getItemFromMDIContainer(mdiWindow.getId()) == null) {
			addNew(mdiWindow, null);
		} else {
			restoreExisting(mdiWindow);
		}
	}

	public void addMDIWindow(FXWindow mdiWindow, Point2D position) {
		if (getItemFromMDIContainer(mdiWindow.getId()) == null) {
			addNew(mdiWindow, position);
		} else {
			restoreExisting(mdiWindow);
		}
	}

	private void addNew(FXWindow mdiWindow, Point2D position) {
		mdiWindow.setVisible(false);
		paneMDIContainer.getChildren().add(mdiWindow);
		if (position == null) {
			mdiWindow.layoutBoundsProperty().addListener(
					new WidthChangeListener(this, mdiWindow));
		} else {
			this.placeMdiWindow(mdiWindow, position);
		}
		mdiWindow.toFront();
	}

	private void restoreExisting(FXWindow mdiWindow) {
		if (getItemFromToolBar(mdiWindow.getId()) != null) {
			tbWindows.getChildren().remove(
					getItemFromToolBar(mdiWindow.getId()));
		}
		for (int i = 0; i < paneMDIContainer.getChildren().size(); i++) {
			Node node = paneMDIContainer.getChildren().get(i);
			if (node.getId().equals(mdiWindow.getId())) {
				node.toFront();
				node.setVisible(true);
			}
		}
	}

	/**
	 * *****************************MDI_EVENT_HANDLERS**************************
	 */
	public EventHandler<FXWindowEvent> mdiCloseHandler = new EventHandler<FXWindowEvent>() {
		@Override
		public void handle(FXWindowEvent event) {
			FXWindow win = (FXWindow) event.getTarget();
			tbWindows.getChildren().remove(getItemFromToolBar(win.getId()));
			win = null;
		}
	};
	public EventHandler<FXWindowEvent> mdiMinimizedHandler = new EventHandler<FXWindowEvent>() {
		@Override
		public void handle(FXWindowEvent event) {
			FXWindow win = (FXWindow) event.getTarget();
			String id = win.getId();
			if (getItemFromToolBar(id) == null) {
				try {
					FXWindowIcon icon = new FXWindowIcon(event.imgLogo,
							mdiCanvas, win.getWindowsTitle());
					icon.setId(win.getId());
					icon.getBtnClose().disableProperty()
							.bind(win.getBtnClose().disableProperty());
					tbWindows.getChildren().add(icon);
				} catch (Exception ex) {
					Logger.getLogger(FXDesktopPane.class.getName()).log(
							Level.SEVERE, null, ex);
				}

			}
		}
	};

	/**
	 * ***************** UTILITIES******************************************
	 */
	public FXWindowIcon getItemFromToolBar(String id) {
		for (Node node : tbWindows.getChildren()) {
			if (node instanceof FXWindowIcon) {
				FXWindowIcon icon = (FXWindowIcon) node;
				// String key = icon.getLblName().getText();
				String key = icon.getId();
				if (key.equalsIgnoreCase(id)) {
					return icon;
				}
			}
		}
		return null;
	}

	public FXWindow getItemFromMDIContainer(String id) {
		for (Node node : paneMDIContainer.getChildren()) {
			if (node instanceof FXWindow) {
				FXWindow win = (FXWindow) node;
				if (win.getId().equals(id)) {

					return win;
				}
			}
		}
		return null;
	}

	public static void setTheme(Theme theme, Scene scene) {
		File f = null;
		ClassLoader classLoader = FXDesktopPane.class.getClassLoader();
		switch (theme) {
		case DEFAULT:
			try {
				f = new File((classLoader.getResource("styles/DefaultTheme.css")).getFile());
			} catch (Exception e) {
				e.printStackTrace();
			}
			scene.getStylesheets().clear();
			scene.getStylesheets().add(classLoader.getResource("styles/DefaultTheme.css").toExternalForm());
			
			break;
		case DARK:
			try {
				f = new File((classLoader.getResource("styles/DarkTheme.css")).getFile());
			} catch (Exception e) {
				e.printStackTrace();
			}
			scene.getStylesheets().clear();
			scene.getStylesheets().add(classLoader.getResource("styles/DefaultTheme.css").toExternalForm());
			break;
		}
	}

	public void placeMdiWindow(FXWindow mdiWindow,
			FXWindow.AlignPosition alignPosition) {
		double canvasH = getPaneMDIContainer().getLayoutBounds().getHeight();
		double canvasW = getPaneMDIContainer().getLayoutBounds().getWidth();
		double mdiH = mdiWindow.getLayoutBounds().getHeight();
		double mdiW = mdiWindow.getLayoutBounds().getWidth();

		switch (alignPosition) {
		case CENTER:
			centerMdiWindow(mdiWindow);
			break;
		case CENTER_LEFT:
			placeMdiWindow(mdiWindow, new Point2D(0, (int) (canvasH / 2)
					- (int) (mdiH / 2)));
			break;
		case CENTER_RIGHT:
			placeMdiWindow(mdiWindow, new Point2D((int) canvasW - (int) mdiW,
					(int) (canvasH / 2) - (int) (mdiH / 2)));
			break;
		case TOP_CENTER:
			placeMdiWindow(mdiWindow, new Point2D((int) (canvasW / 2)
					- (int) (mdiW / 2), 0));
			break;
		case TOP_LEFT:
			placeMdiWindow(mdiWindow, Point2D.ZERO);
			break;
		case TOP_RIGHT:
			placeMdiWindow(mdiWindow,
					new Point2D((int) canvasW - (int) mdiW, 0));
			break;
		case BOTTOM_LEFT:
			placeMdiWindow(mdiWindow,
					new Point2D(0, (int) canvasH - (int) mdiH));
			break;
		case BOTTOM_RIGHT:
			placeMdiWindow(mdiWindow, new Point2D((int) canvasW - (int) mdiW,
					(int) canvasH - (int) mdiH));
			break;
		case BOTTOM_CENTER:
			placeMdiWindow(mdiWindow, new Point2D((int) (canvasW / 2)
					- (int) (mdiW / 2), (int) canvasH - (int) mdiH));
			break;
		}
	}

	public void placeMdiWindow(FXWindow mdiWindow, Point2D point) {
		double windowsWidth = mdiWindow.getLayoutBounds().getWidth();
		double windowsHeight = mdiWindow.getLayoutBounds().getHeight();
		mdiWindow.setPrefSize(windowsWidth, windowsHeight);

		double containerWidth = this.paneMDIContainer.getLayoutBounds()
				.getWidth();
		double containerHeight = this.paneMDIContainer.getLayoutBounds()
				.getHeight();
		if (containerWidth <= point.getX() || containerHeight <= point.getY()) {
			try {
				throw new PositionOutOfBoundsException(
						"Tried to place MDI Window with ID "
								+ mdiWindow.getId()
								+ " at a coordinate "
								+ point.toString()
								+ " that is beyond current size of the MDI container "
								+ containerWidth + "px x " + containerHeight
								+ "px.");
			} catch (PositionOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if ((containerWidth - point.getX() < 40)
				|| (containerHeight - point.getY() < 40)) {
			try {
				throw new PositionOutOfBoundsException(
						"Tried to place MDI Window with ID "
								+ mdiWindow.getId()
								+ " at a coordinate "
								+ point.toString()
								+ " that is too close to the edge of the parent of size "
								+ containerWidth
								+ "px x "
								+ containerHeight
								+ "px "
								+ " for user to comfortably grab the title bar with the mouse.");
			} catch (PositionOutOfBoundsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		mdiWindow.setLayoutX((int) point.getX());
		mdiWindow.setLayoutY((int) point.getY());
		mdiWindow.setVisible(true);
	}

	public void centerMdiWindow(FXWindow mdiWindow) {
		double w = getPaneMDIContainer().getLayoutBounds().getWidth();
		double h = getPaneMDIContainer().getLayoutBounds().getHeight();

		Platform.runLater(() -> {
			double windowsWidth = mdiWindow.getLayoutBounds().getWidth();
			double windowsHeight = mdiWindow.getLayoutBounds().getHeight();

			Point2D centerCoordinate = new Point2D((int) (w / 2)
					- (int) (windowsWidth / 2), (int) (h / 2)
					- (int) (windowsHeight / 2));
			this.placeMdiWindow(mdiWindow, centerCoordinate);
		});
	}

	public enum Theme {

		DEFAULT, DARK,
	}
}
