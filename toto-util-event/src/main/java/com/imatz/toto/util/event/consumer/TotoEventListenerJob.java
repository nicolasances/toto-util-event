package com.imatz.toto.util.event.consumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.imatz.toto.util.event.model.TotoEvent;
import com.imatz.toto.util.event.producer.TotoEventProducer;

/**
 * This class is a Spring singleton, initialized at startup, that will start a
 * thread that listens on Kafka for toto events {@link TotoEvent}.
 * <p>
 * To work it requires that the app it runs in provides an implementation, as a
 * Spring singleton, of the {@link TotoEventSubscriptionsProvider} interface.
 * </p>
 * <p>
 * Note that this listener uses the FQN of the provided implementation of the
 * {@link TotoEventSubscriptionsProvider} class as the <b>Group ID</b> to use in
 * the subscription to the Kafka topic.
 * </p>
 * 
 * @author nick
 *
 */
@Service("toto-util-event.event.listener")
@Scope("singleton")
public class TotoEventListenerJob implements InitializingBean {

	private static final Logger logger_ = LogManager.getLogger();
	
	private boolean finishedPolling_ = true;

	@Value("${kafka.host}")
	private String kafkaHost_;

	@Value("${kafka.port}")
	private String kafkaPort_;

	@Value("${toto.event.kafka.consumer.auto.commit.interval.ms}")
	private String kafkaAutoCommitInterval_;

	@Autowired(required = false)
	private TotoEventSubscriptionsProvider eventSubscriptionsProvider_;
	
	private KafkaConsumer<String, String> consumer_;

	public void startJob() {

		if (consumer_ == null) return;
		
		if (!finishedPolling_) return;
		
		logger_.info(this + " - Starting Kafka consumer polling for Events. Subscription provider: " + eventSubscriptionsProvider_.getClass().getCanonicalName());
		
		getAndHandleEvents();
		
		logger_.info(this + " - Finished polling");
		
	}

	/**
	 * Retrieves any event from the Kafka topic and handle them
	 * 
	 * @param consumer
	 */
	private void getAndHandleEvents() {

		List<String> events = new ArrayList<String>();
		
		finishedPolling_ = false;

		ConsumerRecords<String, String> records = consumer_.poll(59000L);
		
		finishedPolling_ = true;

		for (ConsumerRecord<String, String> record : records) {
			
			logger_.info("Read event " + record.value());

			events.add(record.value());

		}

		if (events == null || events.isEmpty())
			return;

		for (String event : events) {

			handleEvent(event);

		}

	}

	/**
	 * Handles the provided event by doing the following:
	 * <ul>
	 * <li>Looks for subscribers to this event in the list of provided
	 * {@link TotoEventSubscription}</li>
	 * <li>For every subscriber, invoke the
	 * {@link TotoEventSubscriber#handleEvent(com.imatz.toto.util.event.model.TotoEvent)}
	 * method.</li>
	 * </ul>
	 * Every invocation is made in a <b>separate thread</b>.
	 * 
	 * @param event
	 *            the {@link TotoEvent} to handle in a JSON format
	 */
	private void handleEvent(String event) {

		String eventCode = TotoEvent.parseEventCode(event);

		try {

			for (TotoEventSubscription subscription : eventSubscriptionsProvider_.getSubscriptions()) {

				if (subscription.isInterestedIn(eventCode)) {

					logger_.info("Subscription provider " + eventSubscriptionsProvider_.getClass().getCanonicalName() + " is interested in event " + eventCode + ". Handler: " + subscription.getSubscriber().getClass().getCanonicalName() + ". Will soon start an execution thread.");

					new Thread(new TotoEventHandlingThread(subscription.getSubscriber(), event)).start();

				}

			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * This method initializes the Kafka consumer. This method has to be called
	 * before using this class for doing anything.
	 */
	private void newConsumer(String consumerGroup) {

		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaHost_ + ":" + kafkaPort_);
		props.put("group.id", consumerGroup);
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", kafkaAutoCommitInterval_);
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		consumer_ = new KafkaConsumer<String, String>(props);

		consumer_.subscribe(Arrays.asList(TotoEventProducer.EVENTS_TOPIC_NAME));

	}

	/**
	 * Verifies if there are any subscriptions to events in the including app.
	 * 
	 * @return
	 */
	private boolean areThereAnySubscriptions() {

		if (eventSubscriptionsProvider_ == null) {

			logger_.warn("No implementation of the TotoEventSubscriptionsProvider interface has been provided. Without this implementation, no events will be listened to.");

			return false;
		}

		if (eventSubscriptionsProvider_.getSubscriptions() == null || eventSubscriptionsProvider_.getSubscriptions().isEmpty()) {

			logger_.warn("The provided implementation of TotoEventSubscriptionsProvider (" + eventSubscriptionsProvider_.getClass().getCanonicalName() + ") doesn't provide any event subscription.");

			return false;
		}

		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		if (!areThereAnySubscriptions()) return;
		
		newConsumer(eventSubscriptionsProvider_.getClass().getCanonicalName());
		
	}

}
