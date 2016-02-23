package cn.gridx.scala.lang.classes.inner_class

/**
 * Created by tao on 11/21/15.
 */
class Graph {
    class Node {  // Node的类型与Graph的实例是绑定的，而不是与Graph类型绑定
        var connectedNodes: List[Node] = Nil
        def connectTo(node: Node): Unit = {
            connectedNodes = node :: connectedNodes
        }

        // nodeList的类型就只与Graph类型绑定了
        var nodeList: List[Graph#Node] = Nil
        def addTo(node: Graph#Node): Unit = {
            nodeList = node :: nodeList
        }
    }

    var heads: List[Node] = Nil  //  表头节点列表

    def newNode: Node = {  // 创建新的节点，并将其加入到heads中
        val node = new Node
        heads = node :: heads
        node
    }
}


object Graph {
    def main(args: Array[String]): Unit = {
        val g = new Graph
        val n1: g.Node = g.newNode
        val n2: g.Node = g.newNode
        n1.connectTo(n2)
        n2.connectTo(n1)


        val g2 = new Graph
        val n: g2.Node = g2.newNode
        // n.connectTo(n1)  // 不行，n1与n的类型不同, n的类型是g2.Node，而不是Graph.Node
                            // Graph.Node实际上可以表达为Graph#Node

    }

}