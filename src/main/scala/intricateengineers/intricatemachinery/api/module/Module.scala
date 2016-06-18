package intricateengineers.intricatemachinery.api.module

import intricateengineers.intricatemachinery.api.model.ModuleModel
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.common.capabilities.{Capability, ICapabilitySerializable}

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.util.Random

abstract class Module(val frame: MachineryFrame) extends ICapabilitySerializable[NBTTagCompound] {

  val name: ResourceLocation
  val model: ModuleModel

  val boundingBoxes: ListBuffer[AxisAlignedBB] = ListBuffer()
  var debugInfo: mutable.Map[String, String] = null

  onUpdate()

  // Temporary hardcoded values
  private var _posInFrame: (Byte, Byte, Byte) = (
    (8 / Module.GridSize).toByte,
    (8 / Module.GridSize).toByte,
    (8 / Module.GridSize).toByte
    )

  // Temporary hardcoded value
  private var _rotation: Byte = Random.nextInt(3).toByte

  // Getters and setters
  def posInFrame = _posInFrame

  def posInFrame_=(vec: (Double, Double, Double)) = {
    _posInFrame = (
      (vec._1 * Module.GridSize).toByte,
      (vec._2 * Module.GridSize).toByte,
      (vec._3 * Module.GridSize).toByte
      )
    onUpdate()
  }

  def posInFrame_=(vec: (Byte, Byte, Byte)) = {
    _posInFrame = vec
    onUpdate()
  }

  def posX = _posInFrame._1
  def posY = _posInFrame._2
  def posZ = _posInFrame._3

  def rotation = _rotation

  def rotation_=(rotation: Byte): Unit = {
    _rotation = rotation
    onUpdate()
  }

  def onUpdate(): Unit = {
    debugInfo = initDebugInfo
  }

  protected def initBoundingBoxes(): List[AxisAlignedBB] = {
    model.boxes.map(_.aabb()).toList
  }

  def initDebugInfo: Map[String, String] = {
    var debInfo: Map[String, String] = Map()

    // Name of the module
    debInfo += "Name" -> name.getResourcePath

    // Position in pixels in relation to current block
    debInfo += "posX" -> posInFrame._1
    debInfo += "posY" -> posInFrame._2
    debInfo += "posZ" -> posInFrame._3
    debInfo += "rotation" -> rotation

    return debInfo
  }

  def serializeNBT: NBTTagCompound = {
    val tag: NBTTagCompound = new NBTTagCompound
    val pos: NBTTagCompound = new NBTTagCompound
    pos.setByte("x", posInFrame._1)
    pos.setByte("y", posInFrame._2)
    pos.setByte("z", posInFrame._3)
    pos.setByte("rot", rotation)
    tag.setTag("module_pos", pos)
    this.writeNBT(tag)
    tag
  }

  def deserializeNBT(tag: NBTTagCompound) {
    val pos: NBTTagCompound = tag.getCompoundTag("module_pos")
    _posInFrame = (pos.getByte("x"), pos.getByte("y"), pos.getByte("z"))
    _rotation = pos.getByte("rot")
    readNBT(tag)
    onUpdate()
  }

  def writeNBT(tag: NBTTagCompound): NBTTagCompound = {
    val retrn: Any = retrn
    tag
  }

  def readNBT(tag: NBTTagCompound) {
  }

  def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = {
    false
  }

  def getCapability[T](capability: Capability[T], facing: EnumFacing): T = {
    // Arbitrary stuff, it compiles
    new T
  }
}

object Module {
  val GridSize: Double = 16
}
