package intricateengineers.intricatemachinery.api.module

import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation

import scala.collection.mutable

// Modules Registry
object Modules {
  private val moduleRegistry: mutable.Map[ResourceLocation, (MachineryFrame) => Module] = mutable.Map()
  // TODO: Use Module instead of (MachineryFrame) => Module here:
  private val itemRegistry: mutable.Map[(MachineryFrame) => Module, Item] = mutable.Map()

  def registerModule(moduleObject: ModuleCompanion, moduleConstructor: (MachineryFrame) => Module) {
    moduleRegistry(moduleObject.Name) = moduleConstructor
  }

  def registerModuleItem(item: Item, moduleConstructor: (MachineryFrame) => Module) {
    itemRegistry(moduleConstructor) = item
  }

  def getModuleItem(module: Module): Item = {
    itemRegistry.get((frame)=>module).orNull
  }

  // Return a function that creates a Module of Type "name"
  def createModule(name: ResourceLocation) = moduleRegistry.get(name).get
}
