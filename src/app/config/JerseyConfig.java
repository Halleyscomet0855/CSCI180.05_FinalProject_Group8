package app.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        
    	// REQUIRED:
		// need to explicitly tell Jersey where the REST classes are found
		packages("app.rest");  
				    


    }
}