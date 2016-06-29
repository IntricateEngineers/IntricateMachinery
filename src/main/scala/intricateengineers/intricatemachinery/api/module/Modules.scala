package intricateengineers.intricatemachinery.api.module

import net.minecraft.util.ResourceLocation

import scala.collection.mutable

// Modules Registry
object Modules {
  private val moduleRegistry: mutable.Map[ResourceLocation, (MachineryFrame) => Module] = mutable.Map()
  private val itemRegistry: mutable.Map[ModuleItem[ModuleCompanion], (MachineryFrame) => Module] = mutable.Map()

  def registerModule(moduleObject: ModuleCompanion, moduleConstructor: (MachineryFrame) => Module) {
    moduleRegistry(moduleObject.Name) = moduleConstructor
  }

  def registerModuleItem(item: ModuleItem[ModuleCompanion], moduleConstructor: (MachineryFrame) => Module) {
    itemRegistry(item) = moduleConstructor
  }

  // Return a function that creates a Module of Type "name"
  def createModule(name: ResourceLocation) = moduleRegistry.get(name).get
}
