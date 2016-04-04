package cn.gridx.scala.lang.json;


import com.google.gson.annotations.SerializedName;

/**
 * Created by tao on 4/4/16.
 */
public class ST {
    @SerializedName("my name")
    public String Name;

    @SerializedName("my age")
    public int Age;

    public ST(String name, int age) {
        this.Name = name;
        this.Age = age;
    }
}
