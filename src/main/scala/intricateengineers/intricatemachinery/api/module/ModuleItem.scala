package intricateengineers.intricatemachinery.api.module

import mcmultipart.multipart.{IMultipartContainer, MultipartHelper}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.{EnumAction, Item, ItemStack}
import net.minecraft.util._
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.lwjgl.util.vector.Vector3f

import scala.collection.JavaConversions._

class ModuleItem[T <: ModuleCompanion](val moduleObject: T, val createModule: (MachineryFrame) => Module) extends Item {
  final val name = moduleObject.Name

  override final def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {

    // BlockPos facing the block that was right clicked
    val posFacing = pos.add(facing.getDirectionVec)

    val container = Option(MultipartHelper.getPartContainer(worldIn, pos))
    val containerFacing = Option(MultipartHelper.getPartContainer(worldIn, posFacing))

    isContainerAMachineryFrame(container) match {
      case Some(frame) => {
        EnumActionResult.FAIL
      }
      case None => EnumActionResult.PASS
    }

    isContainerAMachineryFrame(containerFacing) match {
      case Some(frame) => {
        if (this.placeInFrameBlockFace(frame, stack, playerIn, hand, facing, new Vector3f(hitX, hitY, hitZ))) {
          playerIn.swingArm(EnumHand.MAIN_HAND)
          worldIn.playSound(playerIn, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F)
          EnumActionResult.SUCCESS
        }
        EnumActionResult.FAIL
      }
      case None => EnumActionResult.PASS
    }
  }

  def placeInFrameBlockFace(frame: MachineryFrame, stack: ItemStack, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hit: Vector3f): Boolean = {

      val newModule = createModule(frame)
      val mainBox = newModule.model.mainBox
      val moduleSizeNormalized = new Vector3f(mainBox.size.x/Module.GRID_SIZE, mainBox.size.y/Module.GRID_SIZE, mainBox.size.z/Module.GRID_SIZE)

      val modulePosVec: Vector3f = hit

      facing match {
        case EnumFacing.NORTH =>
          modulePosVec.setZ(1 - modulePosVec.z - moduleSizeNormalized.z)
        case EnumFacing.WEST =>
          modulePosVec.setX(1 - modulePosVec.x - moduleSizeNormalized.x)
        case EnumFacing.DOWN =>
          modulePosVec.setY(1 - modulePosVec.y - moduleSizeNormalized.y)
        case _ =>
      }
      facing.getAxis match {
        case EnumFacing.Axis.X =>
          modulePosVec.setZ((modulePosVec.z - moduleSizeNormalized.z / 2).max(0))
          modulePosVec.setY((modulePosVec.y - moduleSizeNormalized.y / 2).max(0))
        case EnumFacing.Axis.Z =>
          modulePosVec.setX((modulePosVec.x - moduleSizeNormalized.x / 2).max(0))
          modulePosVec.setY((modulePosVec.y - moduleSizeNormalized.y / 2).max(0))
        case EnumFacing.Axis.Y =>
          modulePosVec.setZ((modulePosVec.z - moduleSizeNormalized.z / 2).max(0))
          modulePosVec.setX((modulePosVec.x - moduleSizeNormalized.x / 2).max(0))
      }
      newModule.pos = correctBounds(modulePosVec, moduleSizeNormalized)
      frame.modules += newModule

      return true
  }

  def correctBounds(pos: Vector3f, mainBoxSize: Vector3f): ModulePos = {
    ModulePos(
      limit(pos.x % 1, 0f, 1f - mainBoxSize.x),
      limit(pos.y % 1, 0f, 1f - mainBoxSize.y),
      limit(pos.z % 1, 0f, 1f - mainBoxSize.z)
    )
  }

  def limit(x: Double, min: Double, max: Double): Double =
    if (x < min) min else if (x > max) max else x

  override def onEntitySwing(entityLiving: EntityLivingBase, stack: ItemStack): Boolean = false

  override def getItemUseAction(stack: ItemStack): EnumAction = EnumAction.BLOCK

  def isContainerAMachineryFrame(iMultipartContainer: Option[IMultipartContainer]): Option[MachineryFrame] = {
    iMultipartContainer
            .map(_.getParts.find(_.isInstanceOf[MachineryFrame]))   // Search for an MF in the parts of the container
            .map(_.get.asInstanceOf[MachineryFrame])   // If we found an MF, cast it to one and return it
  }
}
