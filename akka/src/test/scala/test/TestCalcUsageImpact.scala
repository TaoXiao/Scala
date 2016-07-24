package test

import cn.gridx.scala.akka.app.crossfilter.CalcUsageImpact
import org.testng.annotations.Test


/**
  * Created by tao on 7/24/16.
  */
class TestCalcUsageImpact {
  @Test
  def testMyCase() {
    // CalcUsageImpact.calcByDiff("/Users/tao/Documents/data_2.txt", "/Users/tao/Documents/data_2.impact", null,  CalcUsageImpact.AbsDiffMode())
    CalcUsageImpact.calcByDiff("/Users/tao/Documents/data_2.txt", "/Users/tao/Documents/data_2.impact", null,  CalcUsageImpact.PercentMode())
  }
}
