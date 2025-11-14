package app.rest;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import app.components.AttendanceComponent;
import app.components.BeadleComponent;
import app.components.ProfessorComponent;
import app.entities.Student;

/**
 * REST Controller for Attendance Operations
 */
@Path("/attendance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AttendanceController {

	@EJB
	private BeadleComponent beadleComponent;

	@EJB
	private AttendanceComponent attendanceComponent;

	@EJB
	private ProfessorComponent professorComponent;

	/**
	 * Log Attendance Entry
	 * POST /attendance/log
	 * Input: { attendanceId, studentId, status } OR { beadleId, classId, presentStudentIds[], date }
	 * Output: { "status": "success", "message": "Attendance logged" }
	 * Error: 400 Bad Request, 403 Forbidden
	 */
	@POST
	@Path("/log")
	public Response logAttendanceEntry(Map<String, Object> attendanceData) {
		try {
			// Validate input
			if (attendanceData == null) {
				return Response.status(Response.Status.BAD_REQUEST)
					.entity(createErrorResponse("Missing required parameters"))
					.build();
			}

			// Check if this is a beadle logging attendance for multiple students
			if (attendanceData.containsKey("beadleId") &&
				attendanceData.containsKey("classId") &&
				attendanceData.containsKey("presentStudentIds")) {

				Long beadleId = Long.valueOf(attendanceData.get("beadleId").toString());
				Long classId = Long.valueOf(attendanceData.get("classId").toString());

				// Parse student IDs
				List<Long> presentStudentIds = new ArrayList<>();
				Object studentIdsObj = attendanceData.get("presentStudentIds");
				if (studentIdsObj instanceof List) {
					for (Object id : (List<?>) studentIdsObj) {
						presentStudentIds.add(Long.valueOf(id.toString()));
					}
				}

				// Get date or use current date
				Date date = attendanceData.containsKey("date") ?
					Date.valueOf(attendanceData.get("date").toString()) :
					new Date(System.currentTimeMillis());

				// Log attendance via BeadleComponent
				Long attendancePk = beadleComponent.logAttendance(beadleId, classId, presentStudentIds, date);

				Map<String, Object> response = new HashMap<>();
				response.put("status", "success");
				response.put("message", "Attendance logged");
				response.put("attendancePk", attendancePk);

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
	 * View Cut Classes Report
	 * GET /attendance/cut-report/{classId}
	 * Output: { "classId": 1010, "students": [...] }
	 * Error: 404 Not Found, 403 Forbidden
	 */
	@GET
	@Path("/cut-report/{classId}")
	public Response viewCutClassesReport(@PathParam("classId") Long classId) {
		try {
			if (classId == null) {
				return Response.status(Response.Status.BAD_REQUEST)
					.entity(createErrorResponse("Missing class ID"))
					.build();
			}

			// Get list of present students for all sessions
			Map<String, Integer> cutReport = professorComponent.generateCutReport(classId);

			if (cutReport.isEmpty()) {
				return Response.status(Response.Status.NOT_FOUND)
					.entity(createErrorResponse("No attendance records found for this class"))
					.build();
			}

			// Build response with student cut information
			List<Map<String, Object>> studentCutList = new ArrayList<>();
			for (Map.Entry<String, Integer> entry : cutReport.entrySet()) {
				Map<String, Object> studentCut = new HashMap<>();
				studentCut.put("student", entry.getKey());
				studentCut.put("cutCount", entry.getValue());
				studentCutList.add(studentCut);
			}

			Map<String, Object> response = new HashMap<>();
			response.put("status", "success");
			response.put("classId", classId);
			response.put("students", studentCutList);

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
