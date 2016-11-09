package cn.gridx.scala.lang.network

import java.net.{Inet4Address, InetAddress, NetworkInterface}
import java.util

/**
  * Created by tao on 11/3/16.
  */
object GetLocalAddress {
  def main(args: Array[String]): Unit = {
    get_local_ip("en0")
  }

  def get_local_ip(interfaceName: String): Unit = {
    val interfaceList: util.Enumeration[NetworkInterface] = NetworkInterface.getNetworkInterfaces
    while (interfaceList.hasMoreElements) {
      val i: NetworkInterface = interfaceList.nextElement()
      if (i.getDisplayName == interfaceName) {
        val addressList: util.Enumeration[InetAddress] = i.getInetAddresses
        while (addressList.hasMoreElements) {
          val addr: InetAddress = addressList.nextElement()
          if (!addr.isLoopbackAddress) {
            if (addr.isInstanceOf[Inet4Address])
              println(s"IPV4: ${addr.getHostName} ${addr.getHostAddress}")
            else
              println(s"IPV6: ${addr.getHostName} ${addr.getHostAddress}")
          }
        }
      }
    }
  }
}
