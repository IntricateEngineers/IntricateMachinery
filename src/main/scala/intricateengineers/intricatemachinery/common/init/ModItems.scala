package intricateengineers.intricatemachinery.common.init

import intricateengineers.intricatemachinery.api.module.{MachineryFrame, ModuleCompanion, ModuleItem, Modules}
import intricateengineers.intricatemachinery.common.module._
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

    // Module items
    registerModuleItem(new ModuleItem(DummyModule, new DummyModule(_)))
    registerModuleItem(new ModuleItem(FurnaceModule, new FurnaceModule(_)))
  }

  private def registerItem[T <: Item](item: T, name: String): T = {
    item.setCreativeTab(IMCreativeTab.INSTANCE)
    item.setUnlocalizedName(ModInfo.UNLOCALIZED_PREFIX + name)
    item.setRegistryName(ModInfo.MOD_ID_LOWERCASE, name)
    GameRegistry.register(item)
    return item
  }

  private def registerModuleItem(item: ModuleItem[ModuleCompanion]): ModuleItem[ModuleCompanion] = {
    val name = item.moduleObject.Name.getResourcePath

    item.setCreativeTab(IMCreativeTab.INSTANCE)
    item.setUnlocalizedName(ModInfo.UNLOCALIZED_PREFIX + name)
    item.setRegistryName(ModInfo.MOD_ID_LOWERCASE, name)
    GameRegistry.register(item)

    Modules.registerModuleItem(item, item.createModule)
    return item
  }


}
