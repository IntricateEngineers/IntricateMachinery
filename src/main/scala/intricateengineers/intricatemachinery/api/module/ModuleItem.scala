package intricateengineers.intricatemachinery.api.module

import intricateengineers.intricatemachinery.api.model.Box
import mcmultipart.multipart.{IMultipartContainer, MultipartHelper}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.{EnumAction, Item, ItemStack}
import net.minecraft.util.{EnumActionResult, _}
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.world.World
import org.lwjgl.util.vector.Vector3f

import scala.collection.JavaConversions._

class ModuleItem[T <: ModuleCompanion](val moduleObject: T, val createModule: (MachineryFrame) => Module) extends Item {
  final val name = moduleObject.Name

  override final def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos,
                               hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {

    // BlockPos facing the block that was right clicked
    val posFacing = pos.add(facing.getDirectionVec)

    val container = Option(MultipartHelper.getPartContainer(worldIn, pos))
    val containerFacing = Option(MultipartHelper.getPartContainer(worldIn, posFacing))

    val hitVec = new Vector3f(hitX, hitY, hitZ)

    // Was the player looking at the MF itself or a Module inside it?
    isContainerAMachineryFrame(container).map(maybeFrame => {
      if(placeInFrame(maybeFrame, playerIn, worldIn, pos, facing, hitVec, isFromBlockFace = false))
        EnumActionResult.SUCCESS
      else
        EnumActionResult.FAIL
    }
    )

    // Was the player looking at a Block next to the MF?
    isContainerAMachineryFrame(containerFacing).map(maybeFrame => {
      if (placeInFrame(maybeFrame, playerIn, worldIn, pos, facing, hitVec, isFromBlockFace = true))
        EnumActionResult.SUCCESS
      else
        EnumActionResult.FAIL
    }
    )

    EnumActionResult.PASS
  }

  // Place when the player was looking at a Block next to the MF
  @inline
  private def placeInFrame(frame: MachineryFrame, playerIn: EntityPlayer, worldIn: World, pos: BlockPos,
                           facing: EnumFacing, hit: Vector3f, isFromBlockFace: Boolean): Boolean = {

    val newModule = createModule(frame)
    val mainBox = newModule.model.mainBox
    val moduleSizeNormalized = normalizeModuleSize(mainBox)

    val modulePosVec: Vector3f = hit

    if (isFromBlockFace) {
      facing match {
        case EnumFacing.NORTH =>
          modulePosVec.setZ(1 - modulePosVec.z - moduleSizeNormalized.z)
        case EnumFacing.WEST =>
          modulePosVec.setX(1 - modulePosVec.x - moduleSizeNormalized.x)
        case EnumFacing.DOWN =>
          modulePosVec.setY(1 - modulePosVec.y - moduleSizeNormalized.y)
        case _ =>
      }
    }
    else {
      facing match {
        case EnumFacing.NORTH =>
          modulePosVec.setZ(modulePosVec.z - moduleSizeNormalized.z)
        case EnumFacing.WEST =>
          modulePosVec.setX(modulePosVec.x - moduleSizeNormalized.x)
        case EnumFacing.DOWN =>
          modulePosVec.setY(modulePosVec.y - moduleSizeNormalized.y)
        case _ =>
      }
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

    if(isModuleObstructed(frame, newModule)) {
      return false
    }

    frame.modules += newModule

    playerIn.swingArm(EnumHand.MAIN_HAND)
    worldIn.playSound(playerIn, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F)
    return true
  }

  private def correctBounds(pos: Vector3f, boxSize: Vector3f): ModulePos = {
    ModulePos(
      limit(pos.x % 1, 0f, 1f - boxSize.x),
      limit(pos.y % 1, 0f, 1f - boxSize.y),
      limit(pos.z % 1, 0f, 1f - boxSize.z)
    )
  }

  def isModuleObstructed(frame: MachineryFrame, newModule: Module): Boolean = {
    val moduleAABB = newModule.model.mainBox.aabb.offset(newModule.pos.dX, newModule.pos.dY, newModule.pos.dZ)
    println(moduleAABB)
    //frame.bbCache().foreach(println)
    frame.bbCache().exists(_.intersectsWith(moduleAABB))

  }

  private def normalizeModuleSize(moduleBox: Box) = {
    new Vector3f(moduleBox.size.x / Module.GRID_SIZE, moduleBox.size.y / Module.GRID_SIZE, moduleBox.size.z / Module.GRID_SIZE)
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
