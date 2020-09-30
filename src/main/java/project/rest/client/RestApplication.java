package project.rest.client;

import project.rest.client.resources.AuthResource;
import project.rest.client.resources.MiningResource;
import project.rest.client.response.CORSFilter;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        // Register JAX-RS classes
        HashSet<Class<?>> set = new HashSet<>();
        set.add(AuthResource.class);
        set.add(MiningResource.class);
        set.add(CORSFilter.class);
        return set;
    }
}
