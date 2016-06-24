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
  def get(): A = {
    update()
    cached
  }

  /**
    * Recalculate the value in the cache if it is invalid
    *
    * @param force if true, the value will be recalculated even if the cache is valid.
    */
  def update(force: Boolean = false): Unit = {
    if (!valid || force) {
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
