/**
  * Created by tao on 1/24/17.
  */
object TestList extends App {
  val L = "ABCD-EFG"
  val x = L.takeWhile(_ != '-')
  println(x)
}
