package rest;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet();

        resources.add(Exit.class);
        resources.add(Info.class);
        resources.add(Leave.class);
        resources.add(Login.class);
        resources.add(Message.class);
        resources.add(Register.class);
        //resources.add(SecondResource.class);
        //...

        resources.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        resources.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        return resources;
    }
}
