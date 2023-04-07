package io.quarkus.workshop.superheroes.villain;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static javax.transaction.Transactional.TxType.SUPPORTS;


public interface VillainService {

    List<Villain> findAll();
    Optional<Villain> findByIdOptional(long id);
    Villain findById(long id);
    Villain findRandom();
    Villain persist(@Valid Villain villain);
    Villain update(@Valid Villain villain);
    void delete(long id);

    @ApplicationScoped
    @Transactional
    class DefaultVillainService implements VillainService {

        @Transactional(SUPPORTS)
        public List<Villain> findAll() {
            return Villain.findAll().list();
        }

        @Transactional(SUPPORTS)
        public Optional<Villain> findByIdOptional(long id) {
            return Villain.findByIdOptional(id);
        }

        @Transactional(SUPPORTS)
        public Villain findById(long id) {
            return Villain.findById(id);
        }

        @Transactional(SUPPORTS)
        public Villain findRandom() {
            return Villain.findRandom();
        }

        public Villain persist(@Valid Villain villain) {
            villain.persist();
            return villain;
        }

        public Villain update(@Valid Villain villain) {
            Villain toUpdate = Villain.findById(villain.id);
            toUpdate.setName(villain.getName());
            toUpdate.setOtherName(villain.getOtherName());
            toUpdate.setLevel(villain.getLevel());
            toUpdate.setPicture(villain.getPicture());
            toUpdate.setPowers(villain.getPowers());
            return toUpdate;
        }

        public void delete(long id) {
            Villain toDelete = Villain.findById(id);
            toDelete.delete();
        }

    }

}
