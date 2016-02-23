package cn.gridx.scala.lang.classes.getters_setters

/**
 * Created by tao on 8/20/15.
 *
 * `_name` and `name_` are conventions for Scala's
 * setter and getter
 *
 *  构造函数里，参数 `_name`前面的`private`或者`private[this]`非常重要，不能缺少
 *  如果缺少`private`呢？参考类 `AnotherPerson`的例子
 */
class Person(private var _name: String, var age: Int) {

    def name = _name    // getter, or accessor

    /** mutator
     * `name_=` 等效于编译后的 `name_$eq`
     * p.name = "Tao" 等价于  p.name_$eq("Tao")
     */
    def name_=(aName:String) {  // setter, or mutator
        _name = aName
    }
}


/*  上面Person类的反编译结果如下：

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Person.scala

package cn.gridx.scala.example.classes.getters_setters;


public class Person
{

    private String _name()
    {
        return _name;
    }

    private void _name_$eq(String x$1)
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

    public Person(String _name, int age)
    {
        this._name = _name;
        this.age = age;
        super();
    }

    private String _name;
    private int age;
}


 */
