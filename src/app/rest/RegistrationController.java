package app.rest;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import app.components.StudentComponent;
import app.entities.Beadle;
import app.entities.Student;
import app.repositories.BeadleRepository;

/**
 * REST Controller for Registration Operations
 */
@Component
@Path("/register")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RegistrationController {

	private final StudentComponent studentComponent;
	private final BeadleRepository beadleRepository;

	public RegistrationController(StudentComponent studentComponent,
			BeadleRepository beadleRepository) {
		this.studentComponent = studentComponent;
		this.beadleRepository = beadleRepository;
	}

	/**
	 * Register Student
	 * POST /register/student
	 * Input: { studentId, name, phoneNumber }
	 * Output: { "status": "success", "message": "Student registered" }
	 * Error: 400 Bad Request
	 */
	@POST
	@Path("/student")
	public Response registerStudent(Map<String, Object> studentData) {
		try {
			// Validate input
			if (studentData == null ||
				!studentData.containsKey("studentId") ||
				!studentData.containsKey("name") ||
				!studentData.containsKey("phoneNumber")) {
				return Response.status(Response.Status.BAD_REQUEST)
					.entity(createErrorResponse("Missing required parameters: studentId, name, phoneNumber"))
					.build();
			}

			// Extract parameters
			Long studentId = Long.valueOf(studentData.get("studentId").toString());
			String name = (String) studentData.get("name");
			String phoneNumber = (String) studentData.get("phoneNumber");

			// Validate phone number format (basic validation)
			if (!phoneNumber.matches("\\+639\\d{9}")) {
				return Response.status(Response.Status.BAD_REQUEST)
					.entity(createErrorResponse("Invalid phone number format. Expected: +639XXXXXXXXX"))
					.build();
			}

			// Register student via StudentComponent
			Student student = studentComponent.addStudentProfile(studentId, name, phoneNumber);

			Map<String, Object> response = new HashMap<>();
			response.put("status", "success");
			response.put("message", "Student registered");
			response.put("studentPK", student.getStudentPK());

			return Response.ok(response).build();

		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(createErrorResponse("Invalid student ID format"))
				.build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(createErrorResponse("Server error: " + e.getMessage()))
				.build();
		}
	}

	/**
	 * Register Beadle
	 * POST /register/beadle
	 * Input: { idNumber, name, phoneNumber }
	 * Output: { "status": "success", "message": "Beadle registered" }
	 * Error: 400 Bad Request
	 */
	@POST
	@Path("/beadle")
	public Response registerBeadle(Map<String, Object> beadleData) {
		try {
			// Validate input
			if (beadleData == null ||
				!beadleData.containsKey("idNumber") ||
				!beadleData.containsKey("name") ||
				!beadleData.containsKey("phoneNumber")) {
				return Response.status(Response.Status.BAD_REQUEST)
					.entity(createErrorResponse("Missing required parameters: idNumber, name, phoneNumber"))
					.build();
			}

			// Extract parameters
			Long idNumber = Long.valueOf(beadleData.get("idNumber").toString());
			String name = (String) beadleData.get("name");
			String phoneNumber = (String) beadleData.get("phoneNumber");

			// Validate phone number format (basic validation)
			if (!phoneNumber.matches("\\+639\\d{9}")) {
				return Response.status(Response.Status.BAD_REQUEST)
					.entity(createErrorResponse("Invalid phone number format. Expected: +639XXXXXXXXX"))
					.build();
			}

			// Create and persist Beadle using repository
			Beadle beadle = new Beadle();
			beadle.setIDNumber(idNumber);
			beadle.setName(name);
			beadle.setPhoneNumber(phoneNumber);

			beadle = beadleRepository.save(beadle);

			Map<String, Object> response = new HashMap<>();
			response.put("status", "success");
			response.put("message", "Beadle registered");
			response.put("beadlePK", beadle.getBeadlePK());

			return Response.ok(response).build();

		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(createErrorResponse("Invalid ID number format"))
				.build();
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
