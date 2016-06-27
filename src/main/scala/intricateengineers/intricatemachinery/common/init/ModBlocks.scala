package intricateengineers.intricatemachinery.common.init

import intricateengineers.intricatemachinery.api.module.{MachineryFrame, Module}
import intricateengineers.intricatemachinery.common.util.gui.IMCreativeTab
import intricateengineers.intricatemachinery.core.ModInfo
import mcmultipart.item.ItemMultiPart
import mcmultipart.multipart.IMultipart
import mcmultipart.multipart.MultipartRegistry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry

object ModBlocks {
    val unlocalizedPrefix: String = ModInfo.MOD_ID.toLowerCase + "."

    def init() {
        val item: ItemMultiPart = new ItemMultiPart() {
            def createPart(world: World, pos: BlockPos, side: EnumFacing, hit: Vec3d, stack: ItemStack, player: EntityPlayer): IMultipart = {
                new MachineryFrame
            }
        }
        registerItem(item, "machinery_frame")
        MultipartRegistry.registerPart(classOf[MachineryFrame], "machinery_frame")
    }

    private def registerModule(name: String, module: Class[_ <: Module]) {
    }

    private def registerItem[T <: Item](item: T, name: String): T = {
        item.setCreativeTab(IMCreativeTab.INSTANCE)
        item.setUnlocalizedName(unlocalizedPrefix + name)
        item.setRegistryName(ModInfo.MOD_ID.toLowerCase, name)
        GameRegistry.register(item)
        return item
    }
}
