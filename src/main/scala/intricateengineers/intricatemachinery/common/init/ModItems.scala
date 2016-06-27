package intricateengineers.intricatemachinery.common.init

import intricateengineers.intricatemachinery.api.module.{MachineryFrame, ModuleItem, Modules}
import intricateengineers.intricatemachinery.common.module.{DummyModel, DummyModule, FurnaceModel, FurnaceModule}
import intricateengineers.intricatemachinery.common.util.gui.IMCreativeTab
import intricateengineers.intricatemachinery.core.ModInfo
import mcmultipart.item.ItemMultiPart
import mcmultipart.multipart.IMultipart
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{BlockPos, Vec3d}
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry

object ModItems {

  // Register items
  def init(): Unit = {

    val itemMachineFrame: ItemMultiPart = new ItemMultiPart() {
      def createPart(world: World, pos: BlockPos, side: EnumFacing, hit: Vec3d, stack: ItemStack, player: EntityPlayer): IMultipart = {
        new MachineryFrame
      }
    }
    // Machinery Frame item
    registerItem(itemMachineFrame, "machinery_frame")

    // TODO(velocity): Clean up registering module items
    // Module items
    val dummyItem = registerModuleItem(new ModuleItem(DummyModel.Name, new DummyModule(_)), DummyModel.Name.getResourcePath)
    val furnaceItem = registerModuleItem(new ModuleItem(FurnaceModel.Name, new FurnaceModule(_)), FurnaceModel.Name.getResourcePath)
    Modules.registerModuleItem(dummyItem, (frame) => new DummyModule(frame))
    Modules.registerModuleItem(furnaceItem, (frame) => new FurnaceModule(frame))
  }

  private def registerItem[T <: Item](item: T, name: String): T = {
    item.setCreativeTab(IMCreativeTab.INSTANCE)
    item.setUnlocalizedName(ModInfo.unlocalizedPrefix + name)
    item.setRegistryName(ModInfo.mod_id, name)
    GameRegistry.register(item)
    return item
  }

  private def registerModuleItem[T <: ModuleItem](item: T, name: String): T = {
    item.setCreativeTab(IMCreativeTab.INSTANCE)
    item.setUnlocalizedName(ModInfo.unlocalizedPrefix + name)
    item.setRegistryName(ModInfo.mod_id, name)
    GameRegistry.register(item)
    Modules.registerModuleItem(item, item.createModule)
    return item
  }


}
