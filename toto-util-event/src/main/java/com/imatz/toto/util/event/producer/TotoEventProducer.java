package com.imatz.toto.util.event.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.imatz.toto.util.event.model.TotoEvent;

/**
 * This class provides the capabilities to publish events to the event bus
 * (kafka).
 * <p>
 * It is a Spring prototype bean that has to be wired into the caller and it
 * expects two properties to be found in a properties file: kafka.host,
 * kafka.port
 * </p>
 * 
 * @author nick
 *
 */
@Service
@Scope("prototype")
public class TotoEventProducer implements InitializingBean {

	public static final Logger logger_ = LogManager.getLogger();

	public static final String EVENTS_TOPIC_NAME = "toto-events";

	@Value("${kafka.host}")
	private String kafkaHost_;

	@Value("${kafka.port}")
	private String kafkaPort_;

	private KafkaProducer<String, String> producer_;

	public void afterPropertiesSet() throws Exception {

	}

	/**
	 * Publishes the event of a Kafka Topic.
	 * 
	 * @param event
	 *            the event to publish
	 */
	public void publishEvent(TotoEvent event) {
		
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaHost_ + ":" + kafkaPort_);
		props.put("acks", "all");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		producer_ = new KafkaProducer<String, String>(props);
		
		String data = event.serialize();
		
		producer_.send(new ProducerRecord<String, String>(EVENTS_TOPIC_NAME, data));

		producer_.close();
		
		logger_.info("Event " + event.getEventCode() + " has been sent by " + event.getEventSender().getClass().getCanonicalName());

	}

}
