# Tips and Trics présents dans le code.
## [VillainResource](./src/main/java/io/quarkus/workshop/superheroes/VillainResource.java)

### Ligne 45 et 72
Ici, on peut indifféremment utiliser `@RestPath` ou `@PathParam("id")` pour capturer l’id présent dans `@Path("/{id}")`.
Par contre, dans le cas de `@RestPath`, le nom de l’objet associé doit impérativement correspondre à la valeur présente dans `@Path`.
A contrario, il est possible d’avoir un nom d’objet totalement différent dans le cas du `@PathParam`.

```java
@GET
@Path("/{identifier}")
public Response findById(@PathParam("identifier") long id) {
    ...    
}
```
Est tout à fait valide.
### Ligne 46 à 50

Une autre implémentation possible du findById aurait été :
```java
public Response findById(long id){
    var villain = villainService.findById(id);
    if(villain != null)
        return Response.ok(villain).build();
    return Response.status(404).build();
}
```

### Ligne 47

Il est possible d’utiliser les lambda de manières différentes dans le `.map(...)` :
- en full, avec declaration du type crochets et return.

```java
.map((Villain villain) -> {
    return Response.ok(villain);
})
```
- en light (instruction unique dans la lambda), pas de déclaration du type.
```java
 .map(villain -> Response.ok(villain))
```
- en Method Reference, la méthode appelée DOIT impérative répondre à la signature de la méthode `apply` de `Function`.
```java
.map(Response::ok)
```

### Ligne 59
Ici, on injecte `@Context UriInfo info` dans la méthode, cette interface est fournie par Jax-Rs.
Elle permet d’accéder à un ensemble d’informations basiques et nous permet de construire le chemin d’accès à notre ressource via `info.getAbsolutePathBuilder()`. 

### Ligne 79
Ici, bien que la classe utilise `@Produces(APPLICATION_JSON)` on peut surcharger la méthode avec `@Produces(TEXT_PLAIN)` afin de forcer un retour au format texte.
