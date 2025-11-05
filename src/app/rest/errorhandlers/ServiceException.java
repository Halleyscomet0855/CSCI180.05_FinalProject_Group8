package app.rest.errorhandlers;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


// THIS WILL ACT AS YOUR DEFAULT EXCEPTION HANDLER SO IT WILL DISPLAY THE ERROR
// IN POSTMAN

@Provider
public class ServiceException extends Exception implements
                ExceptionMapper<Throwable> 
{
    private static final long serialVersionUID = 1L;
 
    public ServiceException() {
        super("Something bad has happened");
    }
 
    public ServiceException(String string) {
        super(string);
    }
 
    @Override
    public Response toResponse(Throwable exception) 
    {
    	// throw an error message as a big block of text
    	StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        
        return Response.status(500).entity(exceptionAsString)
                                    .type("text/plain").build();
    }
}