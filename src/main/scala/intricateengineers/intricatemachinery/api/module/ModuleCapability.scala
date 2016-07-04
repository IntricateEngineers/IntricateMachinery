package main.scala.intricateengineers.intricatemachinery.api.module

import intricateengineers.intricatemachinery.api.model.BoxFace
import intricateengineers.intricatemachinery.api.module.Module
import intricateengineers.intricatemachinery.api.util.Cache
import scala.collection.mutable.ListBuffer


abstract class ModuleCapability(val module: Module, val face: BoxFace) {

  private var _attachedCaps = ListBuffer[ModuleCapability]()
  private val attachedCache = Cache[List[ModuleCapability]](() => _attachedCaps.toList)

  def attachedCaps = attachedCache()

  def canInteractWith(otherCap: ModuleCapability): Boolean

  def attach(otherCap: ModuleCapability): Unit = {
    
  }

  def detach(otherCap: ModuleCapability): Unit

}
