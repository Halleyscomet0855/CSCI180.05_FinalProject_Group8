package app.components;

import java.util.LinkedList;
import java.util.Queue;

import org.springframework.stereotype.Component;

/**
 * Component for handling messaging operations via Twilio SMS
 */
@Component
public class MessagingComponent {

	// Message queue for storing pending messages
	private Queue<MessageQueueItem> messageQueue = new LinkedList<>();

	/**
	 * Inner class to represent a queued message
	 */
	private static class MessageQueueItem {
		private String phoneNumber;
		private String message;

		public MessageQueueItem(String phoneNumber, String message) {
			this.phoneNumber = phoneNumber;
			this.message = message;
		}

		public String getPhoneNumber() {
			return phoneNumber;
		}

		public String getMessage() {
			return message;
		}
	}

	/**
	 * Prepares SMS for sending by adding it to the queue
	 * @param studentPhone The student's phone number
	 * @param classDetails The class details to send
	 */
	public void queueMessage(String studentPhone, String classDetails) {
		if (studentPhone == null || studentPhone.isEmpty()) {
			System.out.println("Invalid phone number. Cannot queue message.");
			return;
		}

		if (classDetails == null || classDetails.isEmpty()) {
			System.out.println("No message content provided. Cannot queue message.");
			return;
		}

		MessageQueueItem item = new MessageQueueItem(studentPhone, classDetails);
		messageQueue.offer(item);

		System.out.println("Message queued for: " + studentPhone);
		System.out.println("Message content: " + classDetails);
	}

	/**
	 * Sends SMS through Twilio
	 * This method processes the message queue and sends pending messages
	 */
	public void sendSMS() {
		if (messageQueue.isEmpty()) {
			System.out.println("No messages in queue to send.");
			return;
		}

		System.out.println("Processing message queue. Messages to send: " + messageQueue.size());

		while (!messageQueue.isEmpty()) {
			MessageQueueItem item = messageQueue.poll();

			// TODO: Integrate with Twilio API
			// This is where you would add Twilio SDK code to actually send the SMS
			// Example:
			// Message message = Message.creator(
			//     new PhoneNumber(item.getPhoneNumber()),
			//     new PhoneNumber("YOUR_TWILIO_NUMBER"),
			//     item.getMessage()
			// ).create();

			// For now, we'll simulate sending
			System.out.println("=== Sending SMS ===");
			System.out.println("To: " + item.getPhoneNumber());
			System.out.println("Message: " + item.getMessage());
			System.out.println("Status: Sent successfully (simulated)");
			System.out.println("===================");
		}

		System.out.println("All messages sent.");
	}

	/**
	 * Sends a single SMS immediately without queuing
	 * @param phoneNumber The recipient's phone number
	 * @param message The message to send
	 * @return true if sent successfully, false otherwise
	 */
	public boolean sendImmediateSMS(String phoneNumber, String message) {
		if (phoneNumber == null || phoneNumber.isEmpty() || message == null || message.isEmpty()) {
			System.out.println("Invalid phone number or message. Cannot send SMS.");
			return false;
		}

		// TODO: Integrate with Twilio API
		// This is where you would add Twilio SDK code to actually send the SMS

		System.out.println("=== Sending Immediate SMS ===");
		System.out.println("To: " + phoneNumber);
		System.out.println("Message: " + message);
		System.out.println("Status: Sent successfully (simulated)");
		System.out.println("=============================");

		return true;
	}

	/**
	 * Gets the current size of the message queue
	 * @return Number of pending messages
	 */
	public int getQueueSize() {
		return messageQueue.size();
	}

	/**
	 * Clears all pending messages from the queue
	 */
	public void clearQueue() {
		messageQueue.clear();
		System.out.println("Message queue cleared.");
	}
}
