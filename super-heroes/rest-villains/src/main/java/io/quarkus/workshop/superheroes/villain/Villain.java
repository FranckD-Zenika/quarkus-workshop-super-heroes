package io.quarkus.workshop.superheroes.villain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Random;

@Entity
public class Villain extends PanacheEntity {

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

    public Villain() {}

    public Villain(String name, String otherName, int level, String picture, String powers) {
        this.name = name;
        this.otherName = otherName;
        this.level = level;
        this.picture = picture;
        this.powers = powers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPowers() {
        return powers;
    }

    public void setPowers(String powers) {
        this.powers = powers;
    }

    @Override
    public String toString() {
        return "Villain{" +
            "name='" + name + '\'' +
            ", otherName='" + otherName + '\'' +
            ", level=" + level +
            ", picture='" + picture + '\'' +
            ", powers='" + powers + '\'' +
            ", id=" + id +
            '}';
    }

    public static Villain findRandom() {
        var villainCount = count();
        int randomVillain = RANDOM.nextInt((int) villainCount);
        return findAll().page(randomVillain, 1).firstResult();
    }

}
