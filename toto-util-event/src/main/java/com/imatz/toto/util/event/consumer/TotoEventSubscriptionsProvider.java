package com.imatz.toto.util.event.consumer;

import java.util.List;

/**
 * Classes implementing this interface will provide the list of subscriptions to
 * events that they are interested in.
 * <p>
 * Note that for every app there shoud be <b>at most</b> one implementation of
 * this interface, provided as a Spring singleton.
 * </p>
 * 
 * @author nick
 *
 */
public interface TotoEventSubscriptionsProvider {

	/**
	 * Returns the list of event subscriptions
	 * 
	 * @return
	 */
	public List<TotoEventSubscription> getSubscriptions();

}
