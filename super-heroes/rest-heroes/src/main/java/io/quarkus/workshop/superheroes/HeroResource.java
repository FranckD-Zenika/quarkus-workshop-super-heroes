package io.quarkus.workshop.superheroes;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.quarkus.workshop.superheroes.hero.Hero;
import io.quarkus.workshop.superheroes.hero.HeroService;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestPath;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;


@Path("/api/heroes")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class HeroResource {

    private final HeroService heroService;

    @Inject
    HeroResource(HeroService heroService) {
        this.heroService = heroService;
    }

    @GET
    public Uni<List<Hero>> findAll() {
        return heroService.findAll();
    }

    @GET
    @Path("/{id}")
    public Uni<Response> findById(@RestPath long id) {
        return heroService.findById(id)
            .onItem()
            .ifNull()
            .failWith(NotFoundException::new)
            .map(Response::ok)
            .map(Response.ResponseBuilder::build);
    }

    @GET
    @Path("/random")
    public Uni<Hero> findRandom() {
        return heroService.findRandom();
    }

    @POST
    @ReactiveTransactional
    public Uni<Response> save(@Valid Hero hero, @Context UriInfo uriInfo) {
        return heroService.persist(hero)
            .map(h -> {
                var uri = uriInfo.getAbsolutePathBuilder().path(h.id.toString()).build();
                return Response.created(uri).entity(h).build();
            });
    }

    @PUT
    @ReactiveTransactional
    public Uni<Hero> update(@Valid Hero hero) {
        return heroService.update(hero);
    }

    @DELETE
    @Path("/{id}")
    @ReactiveTransactional
    public Uni<Void> delete(@PathParam("id") long id) {
        return heroService.delete(id);
    }


    @GET
    @Path("/hello")
    @Produces(TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }
}
