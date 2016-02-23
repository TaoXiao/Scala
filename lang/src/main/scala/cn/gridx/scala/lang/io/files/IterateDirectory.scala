package cn.gridx.scala.lang.io.files

import java.io.File

/**
 * Created by tao on 9/6/15.
 */
object IterateDirectory {
    def main(args: Array[String]): Unit = {
        println(listDirectory("/Users/tao/IdeaProjects/Scala"))
    }

    /**
     * 获取一个目录下的全部文件名或者目录名
     * @param path
     * @return
     */
    def listDirectory(path: String): List[File] = {
        val dir = new File(path)
        if (dir.exists() && dir.isDirectory)
            dir.listFiles.toList
        else
            List[File]()
    }


}
