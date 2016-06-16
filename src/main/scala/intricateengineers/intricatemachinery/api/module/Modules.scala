package intricateengineers.intricatemachinery.api.module

import intricateengineers.intricatemachinery.api.model
import net.minecraft.util.ResourceLocation

import scala.collection.mutable

object Modules {
  private val registry: mutable.Map[ResourceLocation, Function[MachineryFrame, model.Module]] = Map()

  def registerModule(name: ResourceLocation, newModule: Function[MachineryFrame, model.Module]) {
    registry(name) = newModule
  }

  def newModule(name: ResourceLocation, frame: MachineryFrame): model.Module = {
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