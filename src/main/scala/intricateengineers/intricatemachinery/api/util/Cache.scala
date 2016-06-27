package intricateengineers.intricatemachinery.api.util

/**
  * A simple cache that can be invalidated.
  *
  * @tparam A type ta cache
  * @param function used to refill the cache if it was invalidated
  * @author topisani
  */
class Cache[A >: Null](function: () ⇒ A) {

  private var cached: A = null
  private var valid = false

  /**
    * Get the value in the cache
    * Will recalculate if the cache was invalidated
    *
    * @return the valid cache value
    */
  def get: A = {
    if(!valid) {
      update()
    }
    cached
  }

  /**
    * Recalculate the value in the cache
    */
  def update(): Unit = {
      cached = function()
      valid = true
  }

  /**
    * Recalculate the value in the cache only if it is invalid
    */
  def updateIfInvalid(): Unit = {
    if (!valid) {
      cached = function()
      valid = true
    }
  }

  /**
    * Invalidate the cache. This will not update the value, just mark it for updating
    */
  def invalidate(): Unit = valid = false

  /**
    * @return true if the value in the cache is valid
    */
  def isValid: Boolean = valid
}

object Cache {
  def apply[A >: Null](f: () ⇒ A): Cache[A] = {
    new Cache[A](f)
  }
}
