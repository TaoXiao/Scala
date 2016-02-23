package cn.gridx.scala.lang.classes.getters_setters

/**
 * Created by tao on 8/20/15.
 */
class AnotherPerson(var _name: String, var age: Int) {
    def name = _name

    def name_=(aName:String): Unit = {
        _name = aName
    }
}


/* 反编译后的结果

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   AnotherPerson.scala

package cn.gridx.scala.example.classes.getters_setters;


public class AnotherPerson
{

    public String _name()
    {
        return _name;
    }

    public void _name_$eq(String x$1)
    {
        _name = x$1;
    }

    public int age()
    {
        return age;
    }

    public void age_$eq(int x$1)
    {
        age = x$1;
    }

    public String name()
    {
        return _name();
    }

    public void name_$eq(String aName)
    {
        _name_$eq(aName);
    }

    public AnotherPerson(String _name, int age)
    {
        this._name = _name;
        this.age = age;
        super();
    }

    private String _name;
    private int age;
}

 */
