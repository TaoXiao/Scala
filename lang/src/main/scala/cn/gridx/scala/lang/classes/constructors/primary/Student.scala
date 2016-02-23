package cn.gridx.scala.lang.classes.constructors.primary

import org.apache.hadoop.hbase.util.Bytes

/**
 * Created by tao on 7/16/15.
 *
 * The primary constructor of a Scala class is a combination of:
    • The constructor parameters
    • Methods that are called in the body of the class
    • Statements and expressions that are executed in the body of the class

    Anything defined within the body of the class other than method
    declarations is a part of the `primary class constructor`

    Because auxiliary constructors must always call a previously defined constructor
    in the same class, auxiliary constructors will also execute the same code.
 */

// 这里的`name`和`bytes`都是类`Student`的fields
// 且它们都是immutable fields
class Student(val name:String, bytes:Array[Byte], var age:Int) {
    val homeDir = System.getProperty("user.home")

    private var timestamp = System.currentTimeMillis

    // bytes = "new value".getBytes ;; Error - `bytes` is immutable field
    override
    def toString() = s"Name = $name, bytes = ${Bytes.toString(bytes)}," +
            s"age = ${age},  homeDir = ${homeDir}, timestamp = ${timestamp}"

    println("start constructing!")

    println(toString)

    println("finish constructing!")
}


/**
 * Student.class文件经过Jad反编译后的文件内容为
 *
----------------------------------------------------------------------------------------
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Student.scala

package cn.gridx.scala.example.classes.constructors.primary;

import org.apache.hadoop.hbase.util.Bytes;
import scala.Predef$;
import scala.StringContext;
import scala.runtime.BoxesRunTime;

public class Student
{

    public String name()
    {
        return name;
    }

    public int age()
    {
        return age;
    }

    // 由于`age`和`timestamp`都是mutable field，所以它们后面加上了_$eq
    public void age_$eq(int x$1)
    {
        age = x$1;
    }

    public String homeDir()
    {
        return homeDir;
    }

    private long timestamp()
    {
        return timestamp;
    }

    private void timestamp_$eq(long x$1)
    {
        timestamp = x$1;
    }

    public String toString()
    {
        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[] {
            "Name = ", ", bytes = ", ",age = ", " homeDir = ", ", timestamp = ", ""
        })))).s(Predef$.MODULE$.genericWrapArray(((Object) (new Object[] {
            name(), Bytes.toString(bytes), BoxesRunTime.boxToInteger(age()), homeDir(), BoxesRunTime.boxToLong(timestamp())
        }))));
    }

    public Student(String name, byte bytes[], int age)
    {
        this.name = name;
        this.bytes = bytes;
        this.age = age;
        super();
        timestamp = System.currentTimeMillis();
        Predef$.MODULE$.println("start constructing!");
        Predef$.MODULE$.println(toString());
        Predef$.MODULE$.println("finish constructing!");
    }

    private final String name;
    private final byte bytes[];
    private int age;
    private final String homeDir = System.getProperty("user.home");
    private long timestamp;
}

----------------------------------------------------------------------------------------
 *
 */
