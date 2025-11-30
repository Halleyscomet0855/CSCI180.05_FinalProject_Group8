package app.components;

import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/**
 * Component for handling messaging operations via Twilio SMS
 * 
 * NOTE: This component requires the Twilio Java helper library.
 * Add it to your project's dependencies.
 * For Maven, add to pom.xml:
 * <dependency>
 *     <groupId>com.twilio.sdk</groupId>
 *     <artifactId>twilio</artifactId>
 *     <version>8.27.0</version> <!-- Use the latest version -->
 * </dependency>
 */
@Component
public class MessagingComponent {

	// TODO: Replace with your actual Twilio credentials from a secure configuration
	private static final String ACCOUNT_SID = "ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
	private static final String AUTH_TOKEN = "your_auth_token";
	private static final String TWILIO_PHONE_NUMBER = "+15017122661";

	// Message queue for storing pending messages
	private Queue<MessageQueueItem> messageQueue = new LinkedList<>();

	@PostConstruct
	public void init() {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
	}

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
			sendImmediateSMS(item.getPhoneNumber(), item.getMessage());
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

		try {
			Message twilioMessage = Message.creator(
				new PhoneNumber(phoneNumber),
				new PhoneNumber(TWILIO_PHONE_NUMBER),
				message
			).create();

			System.out.println("=== Sending Immediate SMS ===");
			System.out.println("To: " + phoneNumber);
			System.out.println("Message: " + message);
			System.out.println("Status: Sent successfully with SID: " + twilioMessage.getSid());
			System.out.println("=============================");
			return true;

		} catch (Exception e) {
			System.err.println("Error sending SMS to " + phoneNumber + ": " + e.getMessage());
			return false;
		}
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
