package cn.gridx.scala.lang.io.files

import java.io.{FileReader, BufferedReader}


/**
 * Created by tao on 11/20/15.
 *
 * 从文件中一行一行地读取，可以指定任意的单字符作为行之间的分隔符
 *
 * @param path 文件的路径
 * @param sep 自定义的行分隔符（限定为单个字符）
 *
 *  局限：1. 字符是一个一个读的，效率低
 *       2. 分隔符只能限于单个字符，不能用字符串作为分隔符
 */
class LinedReader(val path: String, val sep:Char) {
    val reader = new BufferedReader(new FileReader(path))
    var line   = new StringBuilder()

    class LineIterator extends Iterator[String] {
        // 首先读出一行来
        getNextLine()

        /**
         * 如果可以返回新的一行，则该行内容将会被保存在变量`line`中
         * 如果`line`为空，则表示该文件已经被读完了，再也不能取得新的一行了
         * @return
         * */
        def hasNext(): Boolean = {
            if (line.isEmpty)
                false
            else
                true
        }

        /**
         * 当`hasNext`为true时，读出下一行内容
         * @return
         */
        def next():String = {
            val ret = line.toString()
            getNextLine()
            ret
        }

        /**
         * 从文件中找出下一行内容，并将起放入到变量`line`中
         */
        def getNextLine(): Unit = {
            line.clear()
            var ch = reader.read

            // 先读一个字符出来
            if (-1 != ch && sep == ch) {
                // 如果读出的第一个字符就是分隔符，那么继续往下读
                getNextLine
            }

            while (-1 != ch && sep != ch) {
                line.append(ch.asInstanceOf[Char])
                ch = reader.read
            }
        }
    }


    def getLines(): LineIterator = new LineIterator

    def close() = reader.close()
}


/**
 * Char类型变量的范围是 java.lang.Character.MIN_VALUE ~ java.lang.Character.MAX_VALUE
 * 即 '\u0000' ~ '\uffff'
 * 因此可以用1个Char类型来保存一个汉字
 */

object LinedReader {
    def main(args: Array[String]): Unit = {
        val r = new LinedReader("/Users/tao/IdeaProjects/guizhou_food_security/kms_standard.dump", "\02".charAt(0))
        val it = r.getLines
        if (it.hasNext) {
            println(it.next)
        }

        println("*"*30)

        if (it.hasNext) {
            println(it.next)
        }

        /*
        for (line <- r.getLines())
            println(line)
        */
        r.close
    }
}

