package cn.gridx.scala.lang.base64

/**
 * Created by tao on 11/17/15.
 */
object Example {
    def main(args:Array[String]): Unit = {
        val encode = "MeOAgee6r+mynOOAgee6r+WHgOOAgee6r+WkqeeEtuOAgeeOr+S/neaXoOaKl+OAggogMuOAgeS/\\\ng+i/m+mSmeWQuOaUtu+8jOaUueWWhOedoeecoO+8jOe+juWuuee+juiCpO+8jOeyvuWKm+WFheay\\\nm++8jOi6q+S9k+WBpeW6t+OAggogM+OAgemHh+eUqOW3tOawj+adgOiPjOazleadgOiPjOOAgjIg\\\nb0MtNCBvQ+acquW8gOWMheWGt+iXj++8jOWPo+aEn+mjjuWRs+abtOS9s+OAggog6YWNICAgICAg\\\nICDmlpnvvJoxMDAl5LyY6LSo57qv54mb5aW2CiDkv50g6LSoIOacn++8muacq+W8gOWwgeS4jeS4\\\nreaWreWGt+iXj++8jDEw5aSp77yIMG9D4oCUNG9D77yJ44CBNeWkqe+8iDRvQ+KAlDdvQ++8iQ=="
        val newEncode = encode.replace("\n", "")
        println(new String(javax.xml.bind.DatatypeConverter.parseBase64Binary(newEncode)))


    }
}
