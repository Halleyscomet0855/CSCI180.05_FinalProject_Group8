package app.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import app.components.AttendanceComponent;
import app.components.BeadleComponent;
import app.components.MessagingComponent;
import app.components.SchedulerComponent;
import app.components.StudentComponent;
import app.entities.Beadle;
import app.entities.Class;
import app.repositories.BeadleRepository;
import app.repositories.ClassRepository;

/**
 * REST Controller for Schedule Operations
 */
@Component
@Path("/schedule")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScheduleController {

	private final StudentComponent studentComponent;
	private final SchedulerComponent schedulerComponent;
	private final MessagingComponent messagingComponent;
	
	@Autowired
	BeadleRepository beadleRepository;
	
	@Autowired
	ClassRepository classRepository;
	
	@Autowired
	AttendanceComponent attendanceComponent;
	
	@Autowired
	BeadleComponent beadleComponent;

	public ScheduleController(StudentComponent studentComponent,
			SchedulerComponent schedulerComponent,
			MessagingComponent messagingComponent) {
		this.studentComponent = studentComponent;
		this.schedulerComponent = schedulerComponent;
		this.messagingComponent = messagingComponent;
	}

	/** 
	 * Add Class List
	 * Input: {classPK, [studentID]}
	 * Output: {"status": "success", "message": "Class List added" }
	 * Error: 400 Bad Request
	 */

	@POST
	@Path("/addList")
	public Response addClassList(Map<String, Object> listData) {
		try {
			// Validate input
			if (listData == null) {
				return Response.status(Response.Status.BAD_REQUEST)
					.entity(createErrorResponse("Missing required parameters"))
					.build();
			}

			// Check if this is a beadle logging attendance for multiple students
			if (listData.containsKey("classId") &&
				listData.containsKey("presentStudentIds"))
			{

				Long classId = Long.valueOf(listData.get("classId").toString());
				app.entities.Class classObj = classRepository.findByClassPk(classId);

				// Parse student IDs
				List<Long> presentStudentIds = new ArrayList<>();
				Object studentIdsObj = listData.get("presentStudentIds");
				if (studentIdsObj instanceof List) {
					for (Object id : (List<?>) studentIdsObj) {
						presentStudentIds.add(Long.valueOf(id.toString()));
					}
				}
				
				int listCount = beadleComponent.addClassList(classObj, presentStudentIds);


				

				Map<String, Object> response = new HashMap<>();
				response.put("status", "success");
				response.put("message", "Class List Created");
				response.put("number of students", listCount);

				return Response.ok(response).build();

			} else {
				return Response.status(Response.Status.BAD_REQUEST)
					.entity(createErrorResponse("Invalid attendance data format"))
					.build();
			}

		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(createErrorResponse("Invalid ID format"))
				.build();
		} catch (IllegalArgumentException e) {
			return Response.status(Response.Status.FORBIDDEN)
				.entity(createErrorResponse(e.getMessage()))
				.build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(createErrorResponse("Server error: " + e.getMessage()))
				.build();
		}
	}

	/**
	 * Add Class Schedule
	 * POST /schedule/add
	 * Input: { beadleID, className, profName, timestamp }
	 * Output: { "status": "success", "message": "Schedule added" }
	 * Error: 400 Bad Request, 404 Not Found
	 */
	@POST
	@Path("/add")
	public Response addClassSchedule(Map<String, Object> classData) {
		try {
			// Validate input
			if (classData == null) {
				return Response.status(Response.Status.BAD_REQUEST)
					.entity(createErrorResponse("Missing required parameters"))
					.build();
			}

			// Check if this is a beadle logging attendance for multiple students
			if (classData.containsKey("beadleId") &&
				classData.containsKey("className") &&
				classData.containsKey("profName") &&
				classData.containsKey("timestamp"))
			{


				Long beadleId = Long.valueOf(classData.get("beadleId").toString());
				Beadle beadle = beadleRepository.findByBeadlePK(beadleId);
				
				String profName = classData.get("profName").toString();
				String className = classData.get("className").toString();
				String time = classData.get("timestamp").toString();
				// Log attendance via BeadleComponent
				Long classPK = attendanceComponent.createClass(beadle, profName, time, className);

				Map<String, Object> response = new HashMap<>();
				response.put("status", "success");
				response.put("message", "Class Created");
				response.put("attendancePk", classPK);

				return Response.ok(response).build();

			} else {
				return Response.status(Response.Status.BAD_REQUEST)
					.entity(createErrorResponse("Invalid attendance data format"))
					.build();
			}

		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(createErrorResponse("Invalid ID format"))
				.build();
		} catch (IllegalArgumentException e) {
			return Response.status(Response.Status.FORBIDDEN)
				.entity(createErrorResponse(e.getMessage()))
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
