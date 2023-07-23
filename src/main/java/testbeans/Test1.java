package testbeans;

import java.util.List;
import java.util.Map;

public class Test1 extends Test3{
    private int id;
    private List<List<String>> names;
    private Map<String, Test2> mapping;

    public Test1() {
    }

    public Test1(int id, List<List<String>> names, Map<String, Test2> mapping) {
        this.id = id;
        this.names = names;
        this.mapping = mapping;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<List<String>> getNames() {
        return names;
    }

    public void setNames(List<List<String>> names) {
        this.names = names;
    }

    public Map<String, Test2> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, Test2> mapping) {
        this.mapping = mapping;
    }
}
