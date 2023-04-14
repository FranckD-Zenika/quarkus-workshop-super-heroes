package io.quarkus.workshop.superheroes;

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
import java.net.URI;
import java.util.List;

import io.quarkus.workshop.superheroes.villain.Villain;
import io.quarkus.workshop.superheroes.villain.VillainService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestPath;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("api/villains")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Tag(name = "villains")
public class VillainResource {

    private final VillainService villainService;

    @Inject
    VillainResource(VillainService villainService) {
        this.villainService = villainService;
    }

    @GET
    @Operation(summary = "Returns all the villains from the database")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Villain.class, type = SchemaType.ARRAY)))
    public List<Villain> findAll() {
        return villainService.findAll();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Returns a villain for a given identifier")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Villain.class)))
    @APIResponse(responseCode = "404", description = "The villain is not found for a given identifier")
    public Response findById(@RestPath long id) {
        return villainService.findByIdOptional(id)
            .map(Response::ok)
            .orElseGet(() -> Response.status(404))
            .build();
    }

    @GET
    @Path("/random")
    @Operation(summary = "Returns a random villain")
    @APIResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Villain.class, required = true)))
    public Villain findRandom() {
        return villainService.findRandom();
    }

    @POST
    @Operation(summary = "Creates a valid villain")
    @APIResponse(responseCode = "201", description = "The URI of the created villain", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = URI.class)))
    public Response save(@Valid Villain villain, @Context UriInfo info) {
        var newVillain = villainService.persist(villain);
        var uri = info.getAbsolutePathBuilder().path(Long.toString(newVillain.id)).build();
        return Response.created(uri).entity(villain).build();
    }

    @PUT
    @Operation(summary = "Updates an exiting  villain")
    @APIResponse(responseCode = "200", description = "The updated villain", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Villain.class)))
    public Villain update(@Valid Villain villain) {
        return villainService.update(villain);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletes an exiting villain")
    @APIResponse(responseCode = "204")
    public Response delete(@PathParam("id") long id) {
        villainService.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/hello")
    @Produces(TEXT_PLAIN)
    @Tag(name="hello")
    public String hello() {
        return "Hello Villain Resource";
    }

}
