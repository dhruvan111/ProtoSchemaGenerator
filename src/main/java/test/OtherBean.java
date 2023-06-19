package test;

public class OtherBean {
    private int otherId;
    private String otherName;

    public OtherBean() {
    }

    public OtherBean(int otherId, String otherName) {
        this.otherId = otherId;
        this.otherName = otherName;
    }

    // Getters and setters

    public int getOtherId() {
        return otherId;
    }

    public void setOtherId(int otherId) {
        this.otherId = otherId;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    // Other methods

    @Override
    public String toString() {
        return "OtherBean{" +
                "otherId=" + otherId +
                ", otherName='" + otherName + '\'' +
                '}';
    }
}
