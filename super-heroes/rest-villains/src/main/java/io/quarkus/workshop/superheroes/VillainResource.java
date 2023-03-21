package io.quarkus.workshop.superheroes;

import io.quarkus.workshop.superheroes.villain.Villain;
import io.quarkus.workshop.superheroes.villain.VillainService;
import org.jboss.resteasy.reactive.RestPath;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("api/villains")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class VillainResource {

    private final VillainService villainService;

    @Inject
    VillainResource(VillainService villainService) {
        this.villainService = villainService;
    }

    @GET
    public List<Villain> findAll() {
        return villainService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response findById(@RestPath long id) {
        return villainService.findByIdOptional(id)
            .map(Response::ok)
            .orElseGet(() -> Response.status(404))
            .build();
    }

    @GET
    @Path("/random")
    public Villain findRandom() {
        return villainService.findRandom();
    }

    @POST
    public Response save(@Valid Villain villain, @Context UriInfo info) {
        var newVillain = villainService.persist(villain);
        var uri = info.getAbsolutePathBuilder().path(Long.toString(newVillain.id)).build();
        return Response.created(uri).entity(villain).build();
    }

    @PUT
    public Villain update(@Valid Villain villain) {
        return villainService.update(villain);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") long id) {
        villainService.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/hello")
    @Produces(TEXT_PLAIN)
    public String hello() {
        return "Hello Villain Resource";
    }

}
