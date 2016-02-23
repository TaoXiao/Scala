package cn.gridx.scala.lang.traits.basics

/**
 * Created by tao on 12/30/15.
 */
abstract class Bird {
    def song: String
    def sing() = println(s"唱个歌 -> ${song}")
}
