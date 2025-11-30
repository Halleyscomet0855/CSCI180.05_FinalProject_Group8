package app.rest;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import app.components.ProfessorComponent;

@Component
@Path("/professor")
@Produces(MediaType.APPLICATION_JSON)
public class ProfessorController {

    private final ProfessorComponent professorComponent;

    public ProfessorController(ProfessorComponent professorComponent) {
        this.professorComponent = professorComponent;
    }

    /**
     * Generates a cut report for a specific class.
     * GET /professor/cut-report/{classId}
     * Output: { "status": "success", "report": "..." }
     * Error: 404 Not Found, 500 Internal Server Error
     */
    @GET
    @Path("/cut-report/{classId}")
    public Response getCutReport(@PathParam("classId") Long classId) {
        try {
            if (classId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse("Missing classId parameter"))
                    .build();
            }

            String report = professorComponent.generateCutReport(classId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("report", report);

            return Response.ok(response).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(createErrorResponse(e.getMessage()))
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(createErrorResponse("Server error: " + e.getMessage()))
                .build();
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "error");
        error.put("message", message);
        return error;
    }
}
