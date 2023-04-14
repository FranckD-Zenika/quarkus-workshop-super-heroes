package io.quarkus.workshop.superheroes;

import java.util.List;
import java.util.Random;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.workshop.superheroes.villain.Villain;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.BAD_REQUEST;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.NOT_FOUND;
import static org.jboss.resteasy.reactive.RestResponse.StatusCode.OK;

@QuarkusTest
class VillainResourceTest {

    private static final TypeRef<List<Villain>> VILLAIN_LIST_TYPE_REF = new TypeRef<>() {};
    private static final String JSON = "application/json;charset=UTF-8";
    private static final String DEFAULT_NAME = "Super Chocolatine";
    private static final String UPDATED_NAME = "Super Chocolatine (updated)";
    private static final String DEFAULT_OTHER_NAME = "Super Chocolatine chocolate in";
    private static final String UPDATED_OTHER_NAME = "Super Chocolatine chocolate in (updated)";
    private static final String DEFAULT_PICTURE = "super_chocolatine.png";
    private static final String UPDATED_PICTURE = "super_chocolatine_updated.png";
    private static final String DEFAULT_POWERS = "does not eat pain au chocolat";
    private static final String UPDATED_POWERS = "does not eat pain au chocolat (updated)";
    private static final int DEFAULT_LEVEL = 42;
    private static final int UPDATED_LEVEL = 43;
//    private static final int NB_VILLAINS = 570;

    private static String villainId;

    @Test
    void shouldNotGetUnknownVillain() {
        var randomId = new Random().nextLong();
        given()
            .pathParam("id", randomId)
            .when().get("/api/villains/{id}")
            .then()
            .statusCode(NOT_FOUND);
    }

    @Test
    void shouldGetRandomVillain() {
        given()
        .when()
            .get("/api/villains/random")
        .then()
            .statusCode(OK);
    }

    @Test
    void shouldNotAddInvalidItem() {
        var villain = new Villain();
        villain.setName(null);
        villain.setOtherName(DEFAULT_OTHER_NAME);
        villain.setPicture(DEFAULT_PICTURE);
        villain.setPowers(DEFAULT_POWERS);
        villain.setLevel(0);

        given()
            .contentType(JSON)
            .accept(JSON)
            .body(villain)
        .when()
            .post("/api/villains")
        .then()
            .statusCode(BAD_REQUEST);
    }

    @Test
    void shouldGetInitialItems() {
        var villains = given()
            .when()
                .get("/api/villains")
            .then()
                .statusCode(200)
                .extract()
                    .body()
                    .as(VILLAIN_LIST_TYPE_REF);
        assertThat(villains).isNotEmpty().hasSizeGreaterThan(0);
//        assertThat(villains).hasSize(NB_VILLAINS);
    }

    @Test
    void shouldAddAnItem() {

        var villain = new Villain();
        villain.setName(DEFAULT_NAME);
        villain.setOtherName(DEFAULT_OTHER_NAME);
        villain.setPicture(DEFAULT_PICTURE);
        villain.setPowers(DEFAULT_POWERS);
        villain.setLevel(DEFAULT_LEVEL);

        var initialVillains = get("/api/villains")
            .then()
            .statusCode(200)
            .contentType(APPLICATION_JSON)
            .extract()
            .body()
            .as(VILLAIN_LIST_TYPE_REF);

        int initialVillainsCount = initialVillains.size();

        var location = given()
                .contentType(JSON)
                .body(villain)
            .when()
                .post("/api/villains")
            .then()
                .statusCode(201)
                .extract()
                    .header("Location");

        assertThat(location).contains("/api/villains/");

        var segments = location.split("/");
        villainId = segments[segments.length - 1];
        assertThat(villainId).isNotBlank();

        given()
            .pathParam("id", villainId)
        .when()
            .get("/api/villains/{id}")
        .then()
            .statusCode(200)
            .contentType(APPLICATION_JSON)
            .body("name", is(DEFAULT_NAME))
            .body("otherName", is(DEFAULT_OTHER_NAME))
            .body("level", is(DEFAULT_LEVEL))
            .body("picture", is(DEFAULT_PICTURE))
            .body("powers", is(DEFAULT_POWERS));

        var villains = get("/api/villains")
            .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .extract()
                    .body()
                    .as(VILLAIN_LIST_TYPE_REF);
        assertThat(villains).hasSize(initialVillainsCount + 1);
    }

    @Test
    void shouldUpdateAnItem() {
        var villain = new Villain();
        villain.id = Long.valueOf(villainId);
        villain.setName(UPDATED_NAME);
        villain.setOtherName(UPDATED_OTHER_NAME);
        villain.setPicture(UPDATED_PICTURE);
        villain.setPowers(UPDATED_POWERS);
        villain.setLevel(UPDATED_LEVEL);

        var initialVillains = get("/api/villains")
            .then()
            .statusCode(200)
            .contentType(APPLICATION_JSON)
            .extract()
            .body()
            .as(VILLAIN_LIST_TYPE_REF);

        int initialVillainsCount = initialVillains.size();

        var updatedVillain = given()
                .contentType(JSON)
                .accept(JSON)
                .body(villain)
            .when()
                .put("/api/villains")
            .then()
                .statusCode(200)
                .extract()
                    .body()
                    .as(Villain.class);
        assertThat(updatedVillain)
            .hasFieldOrPropertyWithValue("name", UPDATED_NAME)
            .hasFieldOrPropertyWithValue("otherName", UPDATED_OTHER_NAME)
            .hasFieldOrPropertyWithValue("level", UPDATED_LEVEL)
            .hasFieldOrPropertyWithValue("picture", UPDATED_PICTURE)
            .hasFieldOrPropertyWithValue("powers", UPDATED_POWERS);

        var villains = get("/api/villains")
            .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .extract()
                    .body()
                    .as(VILLAIN_LIST_TYPE_REF);
        assertThat(villains).hasSize(initialVillainsCount);
    }

    @Test
    void shouldRemoveAnItem() {
        var initialVillains = get("/api/villains")
            .then()
            .statusCode(200)
            .contentType(APPLICATION_JSON)
            .extract()
            .body()
            .as(VILLAIN_LIST_TYPE_REF);

        int initialVillainsCount = initialVillains.size();

        given()
            .pathParam("id", villainId)
        .when()
            .delete("/api/villains/{id}")
        .then()
            .statusCode(204);

        var villains = get("/api/villains")
            .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .extract()
                    .body()
                    .as(VILLAIN_LIST_TYPE_REF);
        assertThat(villains).hasSize(initialVillainsCount - 1);
    }

    @Test
    void testHelloEndpoint() {
        given()
        .when()
            .get("/api/villains/hello")
        .then()
            .statusCode(200)
            .body(is("Hello Villain Resource"));
    }

    @Test
    void testOpenApi() {
        given()
            .accept(JSON)
        .when()
            .get("/q/openapi")
        .then()
            .statusCode(200);
    }

}
