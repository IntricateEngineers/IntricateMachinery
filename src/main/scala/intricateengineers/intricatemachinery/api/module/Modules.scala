package intricateengineers.intricatemachinery.api.module

import java.util.HashMap
import java.util.Map

import com.google.common.base.Function
import intricateengineers.intricatemachinery.api.model
import net.minecraft.util.ResourceLocation

object Modules {
  private val registry: util.Map[ResourceLocation, Function[MachineryFrame, model.Module]] = new util.HashMap[ResourceLocation, Function[MachineryFrame, model.Module]]

  def registerModule(name: ResourceLocation, newModule: Function[MachineryFrame, model.Module]) {
    registry.put(name, newModule)
  }

  def newModule(name: ResourceLocation, frame: MachineryFrame): model.Module = {
    try {
      return registry.get(name).apply(frame)
    }
    catch {
      case e: Exception => {
        return null
      }
    }
  }
}