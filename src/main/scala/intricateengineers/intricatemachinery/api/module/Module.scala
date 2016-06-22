package intricateengineers.intricatemachinery.api.module

import intricateengineers.intricatemachinery.api.model.ModuleModel
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.common.capabilities.{Capability, ICapabilitySerializable}

import scala.util.Random

abstract class Module(frame: MachineryFrame) extends ICapabilitySerializable[NBTTagCompound] {

  val name: ResourceLocation
  val model: ModuleModel

  // TODO: Do these need to be updated in onUpdate or can they just be vals?
  // TODO: Also possibly make them ListBuffers
  var boundingBoxes: List[AxisAlignedBB] = List()
  var debugInfo: Map[String, String] = null

  // Temporary hardcoded values
  private var _pos: ModulePos = ModulePos(8, 3, 5)

  // Temporary hardcoded value
  private var _rotation: Byte = Random.nextInt(3).toByte

  onUpdate()

  def serializeNBT: NBTTagCompound = {
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

  // Getters and setters
  def pos = _pos

  def writeNBT(tag: NBTTagCompound): NBTTagCompound = {
    val retrn: Any = null
    tag
  }

  def pos_=(newPos: ModulePos) = {
    _pos = newPos
    onUpdate()
  }

  def deserializeNBT(tag: NBTTagCompound) {
    val pos: NBTTagCompound = tag.getCompoundTag("module_pos")
    _pos = ModulePos(pos.getInteger("x"), pos.getInteger("y"), pos.getInteger("z"))
    _rotation = pos.getByte("rot")
    readNBT(tag)
    onUpdate()
  }

  def rotation = _rotation

  def readNBT(tag: NBTTagCompound) {
  }

  def rotation_=(rotation: Byte): Unit =
  {
    _rotation = rotation
    onUpdate()
  }

  def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = {
    false
  }

  def onUpdate(): Unit =
  {
    debugInfo = initDebugInfo()
    boundingBoxes = initBoundingBoxes()
  }

  def getCapability[T](capability: Capability[T], facing: EnumFacing): T = capability.asInstanceOf

  protected

  def initBoundingBoxes(): List[AxisAlignedBB] = {
    model.boxes.map(_.aabb()).toList
  }

  def initDebugInfo(): Map[String, String] = {
    var debInfo: Map[String, String] = Map()

    // Name of the module
    debInfo += "Name" -> name.getResourcePath

    // Position in pixels in relation to current block
    debInfo += "posX" -> pos.iX.toString
    debInfo += "posY" -> pos.iY.toString
    debInfo += "posZ" -> pos.iZ.toString
    debInfo += "rotation" -> rotation.toString

    return debInfo
  }


}

object Module {
  final val GridSize: Double = 16
}
