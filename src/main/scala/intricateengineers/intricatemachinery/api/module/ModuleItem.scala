package intricateengineers.intricatemachinery.api.module

import mcmultipart.multipart.MultipartHelper
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.{EnumAction, Item, ItemStack}
import net.minecraft.util._
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.lwjgl.util.vector.Vector3f

import scala.collection.JavaConversions._
import scala.util.Random

class ModuleItem[T <: ModuleCompanion](val moduleObject: T, val createModule: (MachineryFrame) => Module) extends Item {
  final val name = moduleObject.Name

  override final def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
    val container = Option(MultipartHelper.getPartContainer(worldIn, pos))

    container match {
      case Some(cont) => {    // Container is non-null (not a block and an actual MultipartContainer)
        for (part <- container.get.getParts) {
          part match {
            case frame: MachineryFrame =>
              if (this.placeInFrame(frame, stack, playerIn, hand, facing, new Vector3f(hitX, hitY, hitZ))) {
                playerIn.swingArm(EnumHand.MAIN_HAND)
                worldIn.playSound(playerIn, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F)
                EnumActionResult.SUCCESS
              }
          }
        }
        EnumActionResult.FAIL
      }
      case None => EnumActionResult.PASS
    }
  }

  def placeInFrame(frame: MachineryFrame, stack: ItemStack, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hit: Vector3f): Boolean = {
    try {
      //frame.ModuleList += createModule(frame)
      (frame.ModuleList += createModule(frame)).pos = ModulePos(Random.nextInt%7, Random.nextInt%7, Random.nextInt%7)

      return true
    }
    catch {
      case e: Exception => {
        e.printStackTrace()
      }
    }
    false
  }

  override def onEntitySwing(entityLiving: EntityLivingBase, stack: ItemStack): Boolean = false

  override def getItemUseAction(stack: ItemStack): EnumAction = EnumAction.BLOCK
}
