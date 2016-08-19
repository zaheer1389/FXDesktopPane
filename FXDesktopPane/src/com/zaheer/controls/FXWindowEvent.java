/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zaheer.controls;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.image.ImageView;

/**
 * 
 * @author Zaheer Khorajiya
 *
 */

public class FXWindowEvent extends Event {

	private static final long serialVersionUID = 2249682426993045124L;
	public static final EventType<FXWindowEvent> EVENT_CLOSED = new EventType<>(
			ANY, "EVENT_CLOSED");
	public static final EventType<FXWindowEvent> EVENT_MINIMIZED = new EventType<>(
			ANY, "EVENT_MINIMIZED");
	public ImageView imgLogo;

	public FXWindowEvent(ImageView logoImage,
			EventType<? extends Event> eventType) {
		super(eventType);
		imgLogo = logoImage;
	}

	public FXWindowEvent(EventType<? extends Event> eventType) {
		super(eventType);
	}
}
