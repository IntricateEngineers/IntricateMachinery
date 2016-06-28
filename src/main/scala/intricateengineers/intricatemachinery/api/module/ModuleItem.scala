package intricateengineers.intricatemachinery.api.module

import mcmultipart.multipart.MultipartHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.{EnumActionResult, EnumFacing, EnumHand, ResourceLocation}
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.lwjgl.util.vector.Vector3f

import scala.collection.JavaConversions._

class ModuleItem(val name: ResourceLocation, val createModule: (MachineryFrame) => Module) extends Item {
  override final def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
    val container = Option(MultipartHelper.getPartContainer(worldIn, pos))

    container match {
      case Some(cont) => {    // Container is non-null (not a block and an actual MultipartContainer)
        for (part <- container.get.getParts) {
          part match {
            case frame: MachineryFrame =>
              if (this.placeInFrame(frame, stack, playerIn, hand, facing, new Vector3f(hitX, hitY, hitZ)))
                EnumActionResult.SUCCESS
          }
        }
        EnumActionResult.FAIL
      }
      case None => EnumActionResult.PASS
    }
  }

  def placeInFrame(frame: MachineryFrame, stack: ItemStack, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hit: Vector3f): Boolean = {
    try {
      frame.ModuleList += createModule(frame)
      return true
    }
    catch {
      case e: Exception => {
        e.printStackTrace()
      }
    }
    false
  }
}
