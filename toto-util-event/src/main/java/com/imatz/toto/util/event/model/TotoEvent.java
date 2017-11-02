package com.imatz.toto.util.event.model;

import org.bson.Document;

/**
 * This interface provides the method that allow to manage a generic ToTo Event
 * 
 * @author nick
 *
 */
public abstract class TotoEvent {

	private TotoEventSender eventSender_;

	/**
	 * This constructor requires as input the reference of the event sender.
	 * 
	 * @param eventSender
	 *            the event sender
	 */
	public TotoEvent(TotoEventSender eventSender) {

		eventSender_ = eventSender;

	}

	/**
	 * This is the unique code that represents the <b>type</b> of event.<br/>
	 * This code will be the one used by event consumers to understand if
	 * they're interested in this event.
	 * 
	 * @return the event code
	 */
	public String getEventCode() {

		return this.getClass().getCanonicalName();

	}

	/**
	 * Extracts the event code from the JSON string representing the event.
	 * 
	 * @param jsonEvent
	 *            the JSON string representing the event.
	 * @return the event code (FQN of the event class)
	 */
	public static String parseEventCode(String jsonEvent) {

		return Document.parse(jsonEvent).getString("code");

	}

	/**
	 * Returns the {@link TotoEventSender}, which is the concrete object that
	 * wishes to send this event.
	 * 
	 * @return the sender of the event
	 */
	public TotoEventSender getEventSender() {

		return eventSender_;

	}

	/**
	 * This method serializes the event creating the body that will be attached
	 * to the event in the event bus. The body of the event <b>should always be
	 * a JSON string</b>
	 * <p>
	 * Note that the event code is <b>part of the event body</b> and has to be
	 * provided in the serialized string.
	 * </p
	 * /
	 * 
	 * @return the content of the event as a JSON string
	 */
	public String serialize() {

		Document doc = new Document("code", getEventCode());
		doc.append("sender", getEventSender().getClass().getCanonicalName());
		doc.append("body", serializeEventBody());

		return doc.toJson();
	}

	/**
	 * This method serializes the event body, that is the event context data
	 * that might be needed to contextualize this event and that might be
	 * usefull to the subscribers of this type of event.
	 * 
	 * @return the event body as a JSON {@link Document}
	 */
	protected abstract Document serializeEventBody();

	/**
	 * This method deserializes the JSON event body setting all the event custom
	 * internal variables that define the event's context.
	 * 
	 * @param eventBody
	 *            the JSON {@link Document} representing the body of the event
	 *            (excluding thus the "sender" and "code" elements of the
	 *            event).
	 */
	protected abstract void deserializeEventBody(Document eventBody);

	/**
	 * Deserializes the passed JSON string describing the event into an actual
	 * implementation of {@link TotoEvent}
	 * 
	 * @param event
	 *            the event in JSON
	 */
	public void deserialize(String event) {

		Document doc = Document.parse(event);
		
		if (doc.get("body") == null) return;
		
		deserializeEventBody((Document) doc.get("body"));

	}

}
