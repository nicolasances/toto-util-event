package com.imatz.toto.util.event.consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is a thread that executes Event Handling operations. It is a
 * thread dedicated to the invocation of the
 * {@link TotoEventSubscriber#handleEvent(com.imatz.toto.util.event.model.TotoEvent)}
 * method.
 * 
 * @author nick
 *
 */
public class TotoEventHandlingThread implements Runnable {
	
	private static final Logger logger_ = LogManager.getLogger(); 

	private TotoEventSubscriber subscriber_;
	
	private String event_;
	
	public TotoEventHandlingThread(TotoEventSubscriber subscriber, String event) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
	
		subscriber_ = subscriber;

		event_ = event;
		
	}

	public void run() {
		
		logger_.info("Executing #handleEvent() method on " + subscriber_.getClass().getCanonicalName() + " for event " + event_);
		
		subscriber_.handleEvent(event_);
		
	}

}
