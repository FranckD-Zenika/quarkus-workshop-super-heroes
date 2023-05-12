package io.quarkus.workshop.superheroes.hero;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import java.util.List;

import io.smallrye.mutiny.Uni;

public interface HeroService {

    Uni<List<Hero>> findAll();
    Uni<Hero> findById(long id);
    Uni<Hero> findRandom();
    Uni<Hero> persist(@Valid Hero hero);
    Uni<Hero> update(@Valid Hero hero);
    Uni<Void> delete(long id);

    @ApplicationScoped
    class DefaultheroService implements HeroService {

        @Override
        public Uni<List<Hero>> findAll() {
            return Hero.findAll().list();
        }

        @Override
        public Uni<Hero> findById(long id) {
            return Hero.findById(id);
        }

        @Override
        public Uni<Hero> findRandom() {
            return Hero.findRandom();
        }

        @Override
        public Uni<Hero> persist(Hero hero) {
            return hero.persist();
        }

        @Override
        public Uni<Hero> update(Hero hero) {
            return findById(hero.id)
                .map(retrievedHero -> {
                    retrievedHero.setName(hero.name());
                    retrievedHero.setOtherName(hero.otherName());
                    retrievedHero.setLevel(hero.level());
                    retrievedHero.setPowers(hero.powers());
                    retrievedHero.setPicture(hero.picture());
                    return retrievedHero;
                });
        }

        @Override
        public Uni<Void> delete(long id) {
            return Hero.deleteById(id)
                .replaceWith(Uni.createFrom().voidItem());
        }
    }

}
