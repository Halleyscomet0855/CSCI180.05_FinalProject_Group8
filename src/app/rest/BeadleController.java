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

import app.components.BeadleComponent;
import app.entities.ClassEntry;

@Component
@Path("/beadle")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BeadleController {

    private final BeadleComponent beadleComponent;

    public BeadleController(BeadleComponent beadleComponent) {
        this.beadleComponent = beadleComponent;
    }

    @POST
    @Path("/late")
    public Response incrementLate(Map<String, Long> request) {
        try {
            Long studentId = request.get("studentId");
            Long classId = request.get("classId");

            if (studentId == null || classId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse("Missing required parameters"))
                    .build();
            }

            ClassEntry updatedClassEntry = beadleComponent.incrementLateCount(studentId, classId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Late count incremented successfully.");
            response.put("classEntry", updatedClassEntry);

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
