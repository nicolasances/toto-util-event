package com.imatz.toto.util.event.consumer;

import com.imatz.toto.util.event.model.TotoEvent;

/**
 * Interface that any class that wants to subscribe to events {@link TotoEvent}
 * has to implement.
 * 
 * @author nick
 *
 */
public interface TotoEventSubscriber {

	/**
	 * This method handles the provided event
	 * 
	 * @param event
	 *            the event to handle, passed as a JSON string
	 */
	public void handleEvent(String event);

}
