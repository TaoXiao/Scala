package cn.gridx.scala.lang.io.files

import java.io.{File, FileWriter, PrintWriter}

import scala.io.{BufferedSource, Source}

/**
 * Created by tao on 6/27/15.
 */
object ReadWriteTextFiles {
    def main(args: Array[String]): Unit = {

        CreateFile("jg.txt")

        /*
        val a = "\u0000"
        val b = "\u0001"

        if (a.equals("\u0000"))
            println("0")
        if (b.equals("\u0001"))
            println("1")

        //writeTextFile("/Users/tao/IdeaProjects/Scala/Examples/target/hello.txt")

        //readLinesFromFile("/Users/tao/IdeaProjects/Scala/Examples/pom.xml")

        */
    }

    /**
     * 写入文本文件
     *
     * Scala doesn’t offer any special file writing capability,
     * so fall back and use the Java PrintWriter or FileWriter approaches
     * @param path
     */
    def writeTextFile(path: String): Unit = {
        val writer = new PrintWriter(new File(path))
        writer.println("hello")
        writer.flush()
    }

    /**
     * 读取文件
     * 按行读
     */
    def readLinesFromFile(path:String): Unit = {
        val lines = Source.fromFile(path).getLines

        var i = 0
        for (line <- lines) {
            println(i + " : " + line)
            i += 1
        }
    }


    /**
     * 通过 Source.fromFile 读取文本文件全部内容
     * */
    def ReadWholeFileContents(): Unit = {
        val path = "/Users/tao/.Rapp.history"

        // 会取出该文件的所有内容
        val src: BufferedSource = Source.fromFile(path)

        // 并将所有的内容直接连成一个字符串
        val contents: String = src.mkString

        println(contents)
    }

    def CreateFile(path: String): Unit = {
        val file = new File(path)
        val writer = new PrintWriter(new FileWriter(file, true))
        writer.append("\nhello")
        writer.close()
    }
}


