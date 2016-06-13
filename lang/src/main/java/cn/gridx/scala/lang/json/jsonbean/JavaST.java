package cn.gridx.scala.lang.json.jsonbean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tao on 6/13/16.
 */
public class JavaST {
    @SerializedName("my name")
    public String Name;

    @SerializedName("my age")
    public int Age;

    public JavaST(String name, int age) {
        this.Name = name;
        this.Age = age;
    }
}
