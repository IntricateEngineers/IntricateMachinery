package intricateengineers.intricatemachinery.api.module

import mcmultipart.multipart.{IMultipart, IMultipartContainer, MultipartHelper}
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

    // BlockPos facing the block that was right clicked
    val posFacing = pos.add(facing.getDirectionVec)

    val container = Option(MultipartHelper.getPartContainer(worldIn, pos))

    isContainerAMachineryFrame(container) match {
      case Some(frame) => {
        if (this.placeInFrame(frame, stack, playerIn, hand, facing, new Vector3f(hitX, hitY, hitZ))) {
          playerIn.swingArm(EnumHand.MAIN_HAND)
          worldIn.playSound(playerIn, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F)
          EnumActionResult.SUCCESS
        }
        EnumActionResult.FAIL
      }
      case None => EnumActionResult.PASS
    }
  }

  def placeInFrame(frame: MachineryFrame, stack: ItemStack, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hit: Vector3f): Boolean = {
    try {
      //frame.ModuleList += createModule(frame)
      (frame.ModuleList += createModule(frame)).pos =
              ModulePos(Random.nextDouble.abs%1, Random.nextDouble.abs%1, Random.nextDouble.abs%1)

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

  def isContainerAMachineryFrame(iMultipartContainer: Option[IMultipartContainer]): Option[MachineryFrame] = {
    iMultipartContainer
                    .map(_.getParts.find(_.isInstanceOf[MachineryFrame]))   // Search for an MF in the parts of the container
                    .map(_.get.asInstanceOf[MachineryFrame])   // If we found an MF, cast it to one and return it
  }
}
