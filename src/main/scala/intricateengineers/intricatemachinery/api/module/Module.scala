package intricateengineers.intricatemachinery.api.module

import intricateengineers.intricatemachinery.api.model.{Box, ModuleModel}
import intricateengineers.intricatemachinery.api.util.{Cache, IHasDebugInfo}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.common.capabilities.{Capability, ICapabilitySerializable}

import scala.util.Random

abstract class Module(frame: MachineryFrame) extends ICapabilitySerializable[NBTTagCompound]
  with IHasDebugInfo {

  val name: ResourceLocation
  val model: ModuleModel
  val boxCache: Cache[List[Box]] = Cache(() ⇒ model.boxes.map(_.offset(pos.ints)))
  val bbCache: Cache[List[AxisAlignedBB]] = Cache(() ⇒ boxCache.get.map(_.aabb))
  // Temporary hardcoded values
  private var _pos: ModulePos = ModulePos(0, 0, 0)
  private var _rotation: Byte = Random.nextInt(3).toByte

  override def updateDebugInfo(): Map[String, String] = {
    Map[String, String](
      "Name" -> name.getResourcePath,

      "posX" -> pos.iX.toString,
      "posY" -> pos.iY.toString,
      "posZ" -> pos.iZ.toString,
      "rotation" -> rotation.toString
    )
  }

  // Getters and setters
  def pos = _pos

  def pos_=(newPos: ModulePos) = {
    _pos = newPos
    boxCache.invalidate()
    bbCache.invalidate()
    debugInfo.invalidate()
  }

  def invalidateAllCaches(): Unit = {
    boxCache.invalidate()
    bbCache.invalidate()
    debugInfo.invalidate()
  }

  override def serializeNBT: NBTTagCompound = {
    val tag: NBTTagCompound = new NBTTagCompound
    val pos: NBTTagCompound = new NBTTagCompound
    pos.setInteger("x", _pos.iX)
    pos.setInteger("y", _pos.iY)
    pos.setInteger("z", _pos.iZ)
    pos.setByte("rot", rotation)
    tag.setTag("module_pos", pos)
    this.writeNBT(tag)
    tag
  }

  def rotation = _rotation

  def rotation_=(rotation: Byte): Unit = {
    _rotation = rotation
    boxCache.invalidate()
    bbCache.invalidate()
    debugInfo.invalidate()
  }

  def writeNBT(tag: NBTTagCompound): NBTTagCompound = tag

  override def deserializeNBT(tag: NBTTagCompound) {
    val pos: NBTTagCompound = tag.getCompoundTag("module_pos")
    _pos = ModulePos(pos.getInteger("x"), pos.getInteger("y"), pos.getInteger("z"))
    _rotation = pos.getByte("rot")
    readNBT(tag)
  }

  def readNBT(tag: NBTTagCompound): Unit = {}

  override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = {
    false
  }

  override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = capability.asInstanceOf
}

object Module {
  final val GridSize: Double = 16
}
