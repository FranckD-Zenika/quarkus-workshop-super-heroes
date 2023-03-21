package io.quarkus.workshop.superheroes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("api/villains")
public class VillainResource {

    @GET
    @Produces(TEXT_PLAIN)
    public String hello() {
        return "Hello Villain Resource";
    }

}
