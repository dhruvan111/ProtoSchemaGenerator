package test;

import java.util.List;

public class Address {
    private int id;
    List<List<List<Integer>>> matrix;

    public Address() {
    }

    public Address(int id, List<List<List<Integer>>> matrix) {
        this.id = id;
        this.matrix = matrix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<List<List<Integer>>> getMatrix() {
        return matrix;
    }

    public void setMatrix(List<List<List<Integer>>> matrix) {
        this.matrix = matrix;
    }

}
