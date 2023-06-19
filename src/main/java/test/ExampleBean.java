package test;

import java.util.List;
import java.util.Map;

public class ExampleBean {
    private int id;
    private String name;
    private OtherBean otherBean;
    private List<String> stringList;
    private Map<Integer, Address> locations;

    public ExampleBean() {
    }

    public ExampleBean(int id, String name, OtherBean otherBean, List<String> stringList, Map<Integer, Address> locations) {
        this.id = id;
        this.name = name;
        this.otherBean = otherBean;
        this.stringList = stringList;
        this.locations = locations;
    }

    // Getters and setters


    public Map<Integer, Address> getLocations() {
        return locations;
    }

    public void setLocations(Map<Integer, Address> locations) {
        this.locations = locations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OtherBean getOtherBean() {
        return otherBean;
    }

    public void setOtherBean(OtherBean otherBean) {
        this.otherBean = otherBean;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    // Other methods

    @Override
    public String toString() {
        return "ExampleBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", otherBean=" + otherBean +
                ", stringList=" + stringList +
                '}';
    }
}
