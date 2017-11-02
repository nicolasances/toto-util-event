package com.imatz.toto.util.event.consumer;

import com.imatz.toto.util.event.model.TotoEvent;

/**
 * This class models a generic subscription to a specific type of event.
 * 
 * @author nick
 *
 */
public class TotoEventSubscription {

	private String eventCode_;

	private TotoEventSubscriber subscriber_;

	public TotoEventSubscription(String eventCode, TotoEventSubscriber subscriber) {
		subscriber_ = subscriber;
		eventCode_ = eventCode;
	}

	/**
	 * Returns the event to which the subscription relates to.<br/>
	 * The event is a Class that specifies which type of {@link TotoEvent} this
	 * subscription relates to.
	 * 
	 * @return
	 */
	public String getEventCode() {
		return eventCode_;
	}

	/**
	 * Returns the subscriber to the event
	 * 
	 * @return
	 */
	public TotoEventSubscriber getSubscriber() {
		return subscriber_;
	}

	/**
	 * Says if this subscription is related to the specified event
	 * 
	 * @param eventCode
	 *            the code of the event
	 * 
	 * @return true if this subscription is related to the provided event
	 */
	public boolean isInterestedIn(String eventCode) {

		return eventCode.equals(eventCode_);
		
	}

}
