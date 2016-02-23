package cn.gridx.scala.slick.mysql

/**
 A driver’s simple object contains all commonly needed imports
 from the driver and other parts of Slick such as session handling
*/
import scala.slick.driver.MySQLDriver.simple._


/**
 * `Database.threadLocalSession` simplifies the session handling
 * by attaching a session to the current thread so you do not
 * have to pass it around on your own (or at least assign it
 * to an implicit variable).
 * */
import Database.threadLocalSession


/*************************************************************************
 * 在运行时需要将 slf4j-api-1.6.4.jar 放入到 classpath中
  * 否则会报错: java.lang.NoClassDefFoundError: org/slf4j/LoggerFactory
 *************************************************************************
 */


object Basic extends App {
    def DB    = "slick"
    def TABLE = "Persons"

    /************************************************
      * 创建表
      ***********************************************/
    println(s"正在数据库 ${DB} 中创建表 ${TABLE} ...")

    // 定义表的名称及结构
    object Persons extends Table[(Int, String, String, Int)](TABLE) {
        def id      = column[Int]("ID", O.PrimaryKey)
        def name    = column[String]("NAME")
        def gender  = column[String]("GENDER")
        def age     = column[Int]("AGE")

        // Every table needs a * projection with the same type
        // as the table's type parameter
        def * = id ~ name ~ gender ~ age
    }

    /**
     * 连接到MYSQL
     * 这个block内的代码都运行在同一个session内
     * */
    Database.forURL(s"jdbc:mysql://localhost:3306/${DB}",
        driver = "com.mysql.jdbc.Driver",  user = "root", password = "njzd2014")
    .withSession {
        /**
         * The session is never named explicitly. It is bound to the current thread
         * as the `threadLocalSession` we imported
         * */

        // 创建表
        Persons.ddl.drop

        // 删除表
        Persons.ddl.create

        /************************************************
          * 向表中插入数据
          * 在默认情况下，Database Session总是auto-commit
          ***********************************************/
        println(s"\n正在向表 ${DB}:${TABLE} 中插入数据 ...")

        Persons.insert(1, "Tom",  "Male", 20)
        Persons.insert(2, "Jack", "Male", 30)

        Persons.insertAll(
            (3, "Lucy",  "Female",  20),
            (4, "Grace", "Female",  30),
            (5, "Pig",   "Unknown", 25))


        /************************************************
          * 从表中查询全部数据
          ***********************************************/
        println(s"\n正在从表 ${DB}:${TABLE} 中查询数据 ...")

        /** 这种方式会遍历表中的全部数据
          * 相当于  select * from Persons
          * 这里的星号(*)实际上就是上面我们定义的星号，即
          *        def * = id ~ name ~ gender ~ age
          */
        Query(Persons) foreach {
            case (id, name, gender, age) =>
                println(s"id: $id, name: $name, gender: $gender, age:$age")
        }

        /************************************************
          * 对表进行条件查询
          * 相等的判断用操作符 ===
          * 不等的判断用操作符 =!=
          ***********************************************/
        println(s"\n正在查询 gender === 'Male' 的数据 ...")
        val res1 = for { p <- Persons if p.gender === "Male" }
                yield (p.id, p.name, p.gender, p.age)
        for (x <- res1)
            println(s"${x._1}, ${x._2}, ${x._3}, ${x._4}" )


        println(s"\n正在查询 age =!= 20 的数据 ...")
        val res2 = for { p <- Persons if p.age =!= 20 }
                yield p
        res2 foreach println


        println("\n正在查询 age === 30 的数据 ...")
        val res3 = for { p <- Persons if p.age === 30 }
                yield p
         for (x <- res3)
             println(x)



        /********************************************************
         * 让数据库进行数据的转换（to string）和操作（字符串 连接）
          * 这里的类型转换和字符串连接都是在数据库内进行的，
          * 而不是在Scala客户端内完成的
          *
          * c.asColumnOf[String] 是将列 c 转化为String类型
         ********************************************************/
        println("\n让数据库为我们做一些事吧 ...")
        val res4 = for (p <- Persons) yield
                ConstColumn(" ") ++ p.id.asColumnOf[String] ++ "\t" ++
                p.name ++ "\t" ++ p.gender ++ "\t" ++
                p.age.asColumnOf[String]

        res4 foreach println

    }



}
