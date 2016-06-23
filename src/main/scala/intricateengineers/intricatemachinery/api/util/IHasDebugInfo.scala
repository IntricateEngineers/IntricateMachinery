package intricateengineers.intricatemachinery.api.util

/**
  * Created by topisani on 23/06/16.
  */
trait IHasDebugInfo {
  val debugInfo: Cache[Map[String, String]] = Cache(updateDebugInfo)

  protected def updateDebugInfo(): Map[String, String]

}
