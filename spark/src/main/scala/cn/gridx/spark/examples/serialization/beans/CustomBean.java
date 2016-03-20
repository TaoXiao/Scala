package cn.gridx.spark.examples.serialization.beans;

/**
 * Created by tao on 1/2/16.
 */
public class CustomBean implements  java.io.Serializable {
    public String msg;
    public int id;

    public CustomBean() {
        this.msg = "Unknown msg";
        this.id  = -1;
    }

    public CustomBean(String msg, int id) {
        this.msg = msg;
        this.id  = id;
    }

    @Override
    public String toString() {
        return "msg: " + msg + ", id: " + id;
    }
}
