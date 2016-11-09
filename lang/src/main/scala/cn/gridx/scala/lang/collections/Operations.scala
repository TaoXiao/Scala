package cn.gridx.scala.lang.collections

import scala.collection.mutable.{ListBuffer, HashMap => MMap}

/**
 * Created by tao on 9/7/15.
 */
object Operations {
    def main(args: Array[String]): Unit = {
      val BaseScenario = "Base Scenario"
      val ScenarioAlt1 = "Alternative Scenario 1"
      val ScenarioAlt2 = "Alternative Scenario 2"
      val ScenarioAlt3 = "Alternative Scenario 3"
      val Scenarios = Array(BaseScenario, ScenarioAlt1, ScenarioAlt2, ScenarioAlt3)


      for (scenario <- Scenarios) {
        scenario match {
          case BaseScenario =>
            println("0")
          case ScenarioAlt1 =>
            println("1")
          case ScenarioAlt2 =>
            println("2")
          case ScenarioAlt3 =>
            println("3")
          case _ =>
            throw new RuntimeException(s"Invalid Scenario Name, only ${Scenarios.mkString("[", ", ", "]")} are supported ")
        }
      }

    }

    def Zip(): Unit = {
        val a = ListBuffer[Int]()
        val b = ListBuffer[Int]()

        for (i <- 0 until 10) {
            a.append(i)
            b.append(i*1000)
        }

        a zip b.filter(_ <= 6000) foreach println
    }


    def Slice(): Unit = {
        val A = new Array[String](4)
        A(0) = "a"
        A(1) = "b"
        A(2) = "c"
        A(3) = "d"

        val B = A.slice(1, A.length)
        B.foreach(println)
    }


    def Foreach(): Unit = {
        var A = Array(1,2,3,4,5)
        A = A.map(_*100)
        A.foreach(println)
    }


    def GroupBy(): Unit = {
        val tuples = Array(("A", "A1"), ("A", "A2"), ("A", "A3"), ("B", "B1"), ("C", "C1"), ("C", "C2"))
        val result: Map[String, Array[(String, String)]] = tuples.groupBy(_._1)
        for ((k, v) <- result.map(x => (x._1, x._2.map(_._2))))
            println(s"k -> [$k], v -> [${v.mkString(",")}]")
    }

    def FilterMap(): Unit = {
      val m = MMap(1 -> "A", 2 -> "B", 3 -> "C", 4 -> "D")
      val m1 = m.filter{ case (k, v) => k%2 == 0 }
      println(m1)
    }


    def FlagMap(): Unit = {
      val m = MMap(1 -> "A", 2 -> "B", 3 -> "C", 4 -> "D")

      val m1: MMap[Int, String] = m.flatMap{ case (k, v) =>
        if (k%30 == 0) Some(k, v)
        else None
      }

      println(m1.isEmpty)

      for ((k, v) <- m1)
        println(k + " -> " + v)
    }
}
