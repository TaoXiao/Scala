package cn.gridx.scala.lang.generics;

/**
 * Created by tao on 11/18/15.
 */
public class Student {
    public String name;
    public String city;
    private String age;

    public Student() {
        name = null;
        city = null;
        age  = null;
    }

    public Student(String name, String city, String age) {
        this.name = name;
        this.city = city;
        this.age  = age;
    }

    @Override
    public String toString() {
        return name + ", " + city + ", " + age;
    }

}
