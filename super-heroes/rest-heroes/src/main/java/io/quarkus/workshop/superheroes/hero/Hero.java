package io.quarkus.workshop.superheroes.hero;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Random;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;

@Entity
public class Hero extends PanacheEntity {

    private static final Random RANDOM = new Random();

    @NotNull
    @Size(min = 3, max = 50)
    private String name;

    private String otherName;

    @NotNull
    @Min(1)
    private int level;
    private String picture;

    @Column(columnDefinition = "TEXT")
    private String powers;

    public Hero() {}

    public Hero(String name, String otherName, int level, String picture, String powers) {
        this.name = name;
        this.otherName = otherName;
        this.level = level;
        this.picture = picture;
        this.powers = powers;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String otherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public int level() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String picture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String powers() {
        return powers;
    }

    public void setPowers(String powers) {
        this.powers = powers;
    }

    @Override
    public String toString() {
        return "Hero{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", otherName='" + otherName + '\'' +
            ", level=" + level +
            ", picture='" + picture + '\'' +
            ", powers='" + powers + '\'' +
            '}';
    }

    public static Uni<Hero> findRandom() {
        return count()
            .map(Long::intValue)
            .map(RANDOM::nextInt)
            .chain(integer -> findAll().page(integer, 1).firstResult());
    }

}
