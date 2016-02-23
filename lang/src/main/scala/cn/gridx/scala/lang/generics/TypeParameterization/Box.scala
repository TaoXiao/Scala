package cn.gridx.scala.lang.generics.TypeParameterization

/**
 * Created by tao on 11/23/15.
 *
 * F必须是Fruit的子类
 */
class Box[+F <: Fruit](f: Fruit) {
    def getFruit = f
    def contains(aFruit: Fruit): Boolean = f.name == aFruit.name
}
