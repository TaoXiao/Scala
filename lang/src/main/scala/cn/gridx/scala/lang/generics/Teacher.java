package cn.gridx.scala.lang.generics;

/**
 * Created by tao on 11/18/15.
 */
public class Teacher {
    public String name;
    public String home;
    public String gender;

    public Teacher() {
        name = null;
        home = null;
        gender = null;
    }

    public Teacher(String name, String home, String gender) {
        this.name = name;
        this.home = home;
        this.gender = gender;
    }

    @Override
    public String toString() {
        return name + ", " + home + ", " + gender;
    }
}
