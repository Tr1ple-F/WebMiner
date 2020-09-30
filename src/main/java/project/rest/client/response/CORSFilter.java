package project.rest.client.response;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter {

    @Override
    // CORS Allow Filter
    public void filter(ContainerRequestContext requestCTX, ContainerResponseContext responseCTX) {
        String origin = requestCTX.getHeaders().getFirst("origin");
        // Has origin
        if (origin != null && !origin.isEmpty()) {
            responseCTX.getHeaders().add("Access-Control-Allow-Origin", origin);
        } else {
            responseCTX.getHeaders().add("Access-Control-Allow-Origin", "*");
        }
        responseCTX.getHeaders().add("Access-Control-Allow-Headers", "*");
        responseCTX.getHeaders().add("Access-Control-Allow-Credentials", true);
        responseCTX.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }
}
