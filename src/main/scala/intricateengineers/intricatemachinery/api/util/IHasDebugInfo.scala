package intricateengineers.intricatemachinery.api.util

import scala.collection.immutable.ListMap


trait IHasDebugInfo {
  val debugInfo: Cache[ListMap[String, String]] = Cache(updateDebugInfo)

  protected def updateDebugInfo(): ListMap[String, String]

}
