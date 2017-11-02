package com.imatz.toto.util.event.consumer.def;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.imatz.toto.util.event.consumer.TotoEventSubscriber;
import com.imatz.toto.util.event.consumer.TotoEventSubscription;
import com.imatz.toto.util.event.consumer.TotoEventSubscriptionsProvider;

/**
 * This is a default implementation of the
 * {@link TotoEventSubscriptionsProvider} that allows to configure in the Spring
 * XML file a map of <event code, {@link TotoEventSubscriber}> that will be used
 * to resolve the subscribers when receiving an event.
 * 
 * @author nick
 *
 */
public class DefaultTotoEventSubscriptionProvider implements TotoEventSubscriptionsProvider {

	private Map<String, TotoEventSubscriber> subscriptions_;

	@Override
	public List<TotoEventSubscription> getSubscriptions() {

		if (subscriptions_ == null || subscriptions_.isEmpty())
			return null;

		List<TotoEventSubscription> subscriptions = new ArrayList<TotoEventSubscription>();

		for (String eventCode : subscriptions_.keySet()) {

			subscriptions.add(new TotoEventSubscription(eventCode, subscriptions_.get(eventCode)));

		}

		return subscriptions;

	}

	public void setSubscriptions(Map<String, TotoEventSubscriber> subscriptions) {
		subscriptions_ = subscriptions;
	}
}
