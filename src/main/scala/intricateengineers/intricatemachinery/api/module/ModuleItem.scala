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
    try {
      //(frame.ModuleList += createModule(frame)).pos =
      //        ModulePos(Random.nextDouble.abs%1, Random.nextDouble.abs%1, Random.nextDouble.abs%1)

      val newModule = createModule(frame)
      val box = newModule.model.mainBox
      val moduleSizeNormalized = new Vector3f(box.size.x/Module.GRID_SIZE,box.size.y/Module.GRID_SIZE,box.size.z/Module.GRID_SIZE)

      val modulePos: Vector3f = hit

      facing match {
        case EnumFacing.NORTH =>
          modulePos.setZ(1 - modulePos.z - moduleSizeNormalized.z)
        case EnumFacing.WEST =>
          modulePos.setX(1 - modulePos.x - moduleSizeNormalized.x)
        case EnumFacing.DOWN =>
          modulePos.setY(1 - modulePos.y - moduleSizeNormalized.y)
        case _ =>
      }
      facing.getAxis match {
        case EnumFacing.Axis.X =>
          modulePos.setZ(modulePos.z - moduleSizeNormalized.z / 2)
          modulePos.setY(modulePos.y - moduleSizeNormalized.y / 2)
        case EnumFacing.Axis.Z =>
          modulePos.setX(modulePos.x - moduleSizeNormalized.x / 2)
          modulePos.setY(modulePos.y - moduleSizeNormalized.y / 2)
        case EnumFacing.Axis.Y =>
          modulePos.setZ(modulePos.z - moduleSizeNormalized.z / 2)
          modulePos.setX(modulePos.x - moduleSizeNormalized.x / 2)
      }
      newModule.pos = ModulePos(modulePos)
      frame.ModuleList += newModule

      return true
    }
    catch {
      case e: Exception =>
        e.printStackTrace()
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
