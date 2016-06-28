package intricateengineers.intricatemachinery.api.module

import net.minecraft.util.ResourceLocation

import scala.collection.mutable

// Modules Registry
object Modules {
  private val moduleRegistry: mutable.Map[ResourceLocation, (MachineryFrame) => Module] = mutable.Map()
  private val itemRegistry: mutable.Map[ModuleItem, (MachineryFrame) => Module] = mutable.Map()

  def registerModule(name: ResourceLocation, moduleConstructor: (MachineryFrame) => Module) {
    moduleRegistry(name) = moduleConstructor
  }

  def registerModuleItem(item: ModuleItem, moduleConstructor: (MachineryFrame) => Module) {
    itemRegistry(item) = moduleConstructor
  }

  // Return a function that creates a Module of Type "name"
  def createModule(name: ResourceLocation) = moduleRegistry.get(name).get
}
