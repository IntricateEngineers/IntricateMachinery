package intricateengineers.intricatemachinery.api.module

import java.util.HashMap
import java.util.Map
import com.google.common.base.Function
import net.minecraft.util.ResourceLocation

object Modules {
  private val registry: util.Map[ResourceLocation, Function[MachineryFrame, Module]] = new util.HashMap[ResourceLocation, Function[MachineryFrame, Module]]

  def registerModule(name: ResourceLocation, newModule: Function[MachineryFrame, Module]) {
    registry.put(name, newModule)
  }

  def newModule(name: ResourceLocation, frame: MachineryFrame): Module = {
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