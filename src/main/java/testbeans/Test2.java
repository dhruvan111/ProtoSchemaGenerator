package testbeans;

import java.util.Map;

public class Test2 extends Test3 {
    private String uid;
    private Map<Test1, Integer> mapTesting;
    private Object obj;

    public Test2() {
    }

    public Test2(String uid, Map<Test1, Integer> mapTesting, Object obj) {
        this.uid = uid;
        this.mapTesting = mapTesting;
        this.obj = obj;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Map<Test1, Integer> getMapTesting() {
        return mapTesting;
    }

    public void setMapTesting(Map<Test1, Integer> mapTesting) {
        this.mapTesting = mapTesting;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
