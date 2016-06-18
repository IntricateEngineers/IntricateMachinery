package intricateengineers.intricatemachinery.api.module

import net.minecraft.util.ResourceLocation

import scala.collection.mutable

object Modules {
  private val registry: mutable.Map[ResourceLocation, Function[MachineryFrame, Module]] = mutable.Map()

  def registerModule(name: ResourceLocation, newModule: Function[MachineryFrame, Module]) {
    registry(name) = newModule
  }

  def newModule(name: ResourceLocation, frame: MachineryFrame): Module = {
    try {
      return registry.get(name).get(frame)
    }
    catch {
      case e: Exception => {
        return null
      }
    }
  }
}