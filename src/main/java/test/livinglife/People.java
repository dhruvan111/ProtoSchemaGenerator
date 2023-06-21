package test.livinglife;

import test.edu.edusrc.School;

import java.util.List;

public class People extends LivingAnimals{
    public List<List<List<School>>> getAnimals() {
        return animals;
    }

    public void setAnimals(List<List<List<School>>> animals) {
        this.animals = animals;
    }

    private List<List<List<School>>> animals;
}
