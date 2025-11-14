package app.rest;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import app.components.MessagingComponent;
import app.components.SchedulerComponent;
import app.components.StudentComponent;
import app.entities.Class;
import app.entities.Student;

/**
 * REST Controller for Schedule Operations
 */
@Path("/schedule")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScheduleController {

	@EJB
	private StudentComponent studentComponent;

	@EJB
	private SchedulerComponent schedulerComponent;

	@EJB
	private MessagingComponent messagingComponent;

	/**
	 * Add Class Schedule
	 * POST /schedule/add
	 * Input: { studentId, classId, dayofweek, startTime, endTime, location }
	 * Output: { "status": "success", "message": "Schedule added" }
	 * Error: 400 Bad Request, 404 Not Found
	 */
	@POST
	@Path("/add")
	public Response addClassSchedule(Map<String, Object> scheduleData) {
		try {
			// Validate input
			if (scheduleData == null || !scheduleData.containsKey("studentId") ||
				!scheduleData.containsKey("classId")) {
				return Response.status(Response.Status.BAD_REQUEST)
					.entity(createErrorResponse("Missing required parameters"))
					.build();
			}

			// Extract parameters
			Long studentId = Long.valueOf(scheduleData.get("studentId").toString());
			Long classId = Long.valueOf(scheduleData.get("classId").toString());
			String dayofweek = (String) scheduleData.get("dayofweek");
			String startTime = (String) scheduleData.get("startTime");
			String endTime = (String) scheduleData.get("endTime");
			String location = (String) scheduleData.get("location");

			// TODO: Implement schedule enrollment logic
			// This would require an Enrollment entity or similar
			// For now, we'll assume the student is registered

			Map<String, Object> response = new HashMap<>();
			response.put("status", "success");
			response.put("message", "Schedule added");

			return Response.ok(response).build();

		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(createErrorResponse("Invalid student or class ID"))
				.build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(createErrorResponse("Server error: " + e.getMessage()))
				.build();
		}
	}

	/**
	 * Get Today's Schedule and Send Reminder
	 * GET /schedule/reminder?phone=+639123456789
	 * Output: { "status": "success", "message": "Reminder sent", "classes": [...] }
	 * Error: 401 Unauthorized, 500 Server Error
	 */
	@GET
	@Path("/reminder")
	public Response getScheduleAndSendReminder(@QueryParam("phone") String phone) {
		try {
			// Check upcoming classes
			List<Class> upcomingClasses = schedulerComponent.checkUpcomingClasses();

			if (upcomingClasses.isEmpty()) {
				Map<String, Object> response = new HashMap<>();
				response.put("status", "success");
				response.put("message", "No upcoming classes");
				response.put("classes", upcomingClasses);
				return Response.ok(response).build();
			}

			// Send reminders for upcoming classes
			for (Class classEntity : upcomingClasses) {
				schedulerComponent.sendClassReminder(classEntity);

				// If phone number is provided, queue specific message
				if (phone != null && !phone.isEmpty()) {
					String classDetails = "Reminder: " + classEntity.getClassName() +
						" with Professor " + classEntity.getProfessorName() +
						" starts at " + classEntity.getTime();
					messagingComponent.queueMessage(phone, classDetails);
				}
			}

			// Send all queued messages
			messagingComponent.sendSMS();

			Map<String, Object> response = new HashMap<>();
			response.put("status", "success");
			response.put("message", "Reminder sent");
			response.put("classes", upcomingClasses);

			return Response.ok(response).build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(createErrorResponse("Server error: " + e.getMessage()))
				.build();
		}
	}

	/**
	 * Helper method to create error response
	 */
	private Map<String, Object> createErrorResponse(String message) {
		Map<String, Object> error = new HashMap<>();
		error.put("status", "error");
		error.put("message", message);
		return error;
	}
}
