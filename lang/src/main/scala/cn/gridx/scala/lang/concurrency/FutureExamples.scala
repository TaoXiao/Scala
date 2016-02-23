package cn.gridx.scala.lang.concurrency


import java.io.{File, PrintWriter}
import java.util.Timer

import scala.collection.immutable.IndexedSeq
import scala.concurrent.{Await, Future, future}

/*
* time duration, for example:
* `100 nanos`,  `500 millis`,  `5 seconds`, `1 minute`, `1 hour`, `3 days`
* */
import scala.concurrent.duration._

/**
 * The `ExecutionContext.Implicits.global` import statement imports
 * the default global execution context.  You can think of an
 * execution context as being a thread pool, and this is a simple way
 * to get access to a thread pool.
 */
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Random}


/**
 * Created by tao on 6/25/15.
 *
 *
 * 参考 [Concurrency with Scala Futures (Futures tutorial)](http://alvinalexander.com/scala/concurrency-with-scala-futures-tutorials-examples)
 */
object FutureExamples {
    def main(args: Array[String]): Unit = {
        BlockingExample
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 阻塞地使用`Future`的一个例子
     *
     * 运行结果为
     *      开始啦 。。。
            executing task #5
            executing task #6
            executing task #1
            executing task #3
            executing task #2
            executing task #4
            executing task #0
            executing task #7
            executing task #9
            executing task #10
            executing task #8
            Future#sequence 将 `TraversableOnce[Future[A]]` 转变为 Future[TraversableOnce[A]]
            Squares = Vector(0, 1, 4, 9, 16, 25, 36, 49, 64, 81, 100)
            结束啦 。。。
     */
    def BlockingExample(): Unit = {
        println("开始啦 。。。")

        // create a sequence of individual asynchronous tasks that,
        // when complete, provides an Int
        // Future一旦创建，会立即运行，不需要调用类似于`start`那样的方法
        val tasks: IndexedSeq[Future[Int]] =
            for (i <- 0 to 10) yield Future {
                println("executing task #" + i)
                Thread.sleep(1000)
                i*i
            }

        Thread.sleep(4000)
        println(s"Future#sequence 将 `TraversableOnce[Future[A]]` 转变为 Future[TraversableOnce[A]]")

        // combine those async tasks into a single async task
        val aggregated: Future[IndexedSeq[Int]] = Future.sequence(tasks)

        /** block the current thread for up to 15 seconds while waiting for the result (`completed` state)
         *  实际上应该尽量避免阻塞线程！因为在等待Future完成的过程中本线程无法干其他事情
         *  更好的方式是使用callback
         *
         *  If the Future doesn’t return within that time, it throws a `java.util.concurrent.TimeoutException`.
         *
         *  不再需要 Thread.sleep(10)，因为Await.result已经block了本线程
         */
        val squares: IndexedSeq[Int] = Await.result(aggregated, 10 second)

        println("Squares = " + squares)

        println("结束啦 。。。")
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 非阻塞地使用`Future`的一个例子
     * 为`Future`添加`onComplete`方法
     *
     * 实际上, `onComplete`包含了`onSuccess`和`onFailure`，因此，不要重复使用
     *
     * 本例运行结果

        sleeping ...
        onComplete(Failure) => / by zero
        onFailure => / by zero
        wake up ....
     *
     *
     * 『callback方法被谁执行？不确定』
     * There is no guarantee that it will be called by the thread that completed the future
     *  or the thread that created the callback.
     */
    def NonblockingExample(): Unit = {

        // 一旦创建了Future，该任务会立即进行调度，不需要类似于start这样的启动命令
        val f: Future[Int] = Future {
            Thread.sleep(Random.nextInt(1000))
            1024/0
        }

        // `Future`有三种callback，即
        // `onComplete`， `onSuccess` 和 `onFailure`
        f onComplete {
            case Success(value) => println(s"onComplete(Success) => $value")
            case Failure(e) => println(s"onComplete(Failure) => ${e.getMessage}")
        }

        f onSuccess {
            case result => println(s"onSuccess =>  $result")
        }

        f onFailure {
            case ex => println(s"onFailure => ${ex.getMessage}")
        }

        println("sleeping ...")
        Thread.sleep(5000)  // 这里主线程可以一边等待Future的完成，一边继续干自己的其他事情
        println("wake up ....")
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * 用方法`future`来定义一个Future任务
     *
     * 本例运行结果
     *
        start ...
        onComplete(Success) => 10010
        stop ...
     *
     */
    def FutureCallbackExample(): Unit = {
        println("  start ...")

        // 定义Future任务
        def longRunningComutation(x: Int): Future[Int] = future {
            Thread.sleep(2000)
            x
        }

        // 异步执行该Future任务，并定义callback方法
        longRunningComutation(10010).onComplete{
            case Success(result) => println(s"onComplete(Success) => $result")
            case Failure(ex) => println(s"onComplete(Failure) => ${ex.getMessage}")
        }

        Thread.sleep(5000)
        println(" stop ...")
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 创建多个Future 任务
     * 等待它们全部完成
     * 并把它们的结果都join起来
     *
     * 除了 for-comprehension外，能够作为combinator的还有：
     * `map`, `flatMap`, `filter`, `foreach`, `recover`, `recoverWith`, `fallbackTo`
     *
     * 其中，`recover`,`recoverWith`和`fallbackTo`这三个combinator用于处理错误
     *
     * 本例的运行结果：
     *
        Before execution
        running algorithm with i=200
        running algorithm with i=100
        running algorithm with i=400
        onSuccess => 700
        After execution
     */
    def ForkAndJoinExample(): Unit = {
        object Cloud {
            def runAlgorithm(i: Int): Future[Int] = future {
                Thread.sleep(Random.nextInt(2000))
                println(s"running algorithm with i=$i")
                i
            }
        }

        println("Before execution")

        val f1 = Cloud.runAlgorithm(100)
        val f2 = Cloud.runAlgorithm(200)
        val f3 = Cloud.runAlgorithm(400)

        val result: Future[Int] = for {
            r1 <- f1
            r2 <- f2
            r3 <- f3
        } yield (r1 + r2 + r3)

        result onSuccess {
            case res => println(s"onSuccess => $res")
        }

        Thread.sleep(5000)

        println("After execution")
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Combinator fallbackTo creates a new future which holds the result of this future
     * if it was completed successfully, or otherwise the successful result of the argument future.
     *
     * In the event that both this future and the argument future fail, the new future is completed
     * with the exception from this future
     *
     *
     * 本例doesn't work
     */
    def FallbackToExample(): Unit = {
        val f1 = Future {
            println("running f1 ...")
            300/0
        }

        val f2 = Future {
            println("running f2 ...")
            4/1
        } map {
            x => x*100
        }

        f1 fallbackTo f2

        Await.result(f1, 5 second)

        println(" 结束了 ")

    }

    /**
     * `Future`超时的例子
     *
     * 当捕捉到Future超时的异常后，Future自己会结束吗？
     */
    def TimeoutedFutureExample(): Unit = {
        /**
         * 如果加上这一行的话，即使主线程结束了，进程也不会结束
         * 因为` new Timer()`会开启一个`TimerThread`(在后台不断地运行)
         * 这个无限循环的后台线程导致主进程无法结束
         * 加上System.exit()可以立即结束整个进程
         */
        val timer = new Timer()

        val f = future {
            val writer = new PrintWriter(new File("./hello.ttx"))
            for (i <- 0 to 50) {
//                writer.println(i)
//                writer.flush
                println(i)
                Thread.sleep(2000)
            }
            println("wake up from thread")
        }

        try {
            // Await会因为f超时而抛出异常
            // 按道理f自身不受影响，依然在运行，没有退出
            // 但是观察时发现f随着主线程的退出而结束了！！ 为什么？？
            // 实际上是因为主进程退出了，所以f当然会结束了
            // 应该表述为，Await.result抛出超时异常对Future本身没有影响
            Await.result(f, 2 second)
        } catch {
            case _ => println("捕获了Future超时的异常")
        }

        Thread.sleep(10000)

        println("主线程退出")
    }
}
