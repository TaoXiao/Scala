package cn.gridx.scala.lang.classes.constructors.fields.visibility

/**
 * Created by tao on 7/17/15.
 *
    • If a field is declared as a `var`,Scala generates both getter and setter methods for that field.
    • If the field is a `val`, Scala generates only a getter method for it.
    • If a field doesn’t have a `var` or `val` modifier, Scala gets conservative, and doesn’t
        generate a getter or setter method for the field.
    • Additionally, `var` and `val` fields can be modified with the `private` keyword, which prevents getters and setters from being generated.
 *
 */
class Cat (val a:Int,       // only getter method
           var b:String,    // getter & setter method
           c:Boolean,       // neither getter nor setter method
           private val d:String)  // 同上
{
    // 无法从非Cat类访问
    // x是class-private
    private val x = "X"

    // 无法从其他Cat instance访问
    // y是object-private
    private[this] val y = "Y"


    val z = "Z"                     // 可以从Cat的外部访问
}
