package intricateengineers.intricatemachinery.api.module

import intricateengineers.intricatemachinery.api.model.ModuleModel
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.common.capabilities.{Capability, ICapabilitySerializable}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

abstract class Module(val frame: MachineryFrame) extends ICapabilitySerializable[NBTTagCompound] {

  val name: ResourceLocation
  val model: ModuleModel

  val boundingboxes = ArrayBuffer[AxisAlignedBB]
  var debugInfo: Map[String, List[String]]

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
  def posInFrame() = _posInFrame

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

  def rotation() = _rotation

  def rotation_=(rotation: Byte): Unit = {
    _rotation = rotation
    onUpdate()
  }

  def onUpdate(): Unit = {
    debugInfo = initDebugInfo
  }

  protected def initBoundingBoxes(): List[AxisAlignedBB] = {
    model.boxes.map(_.toAABB())
  }

  def initDebugInfo(): List[Map[String, String]] = {
    // Name of the module
    val hashMapName: Map[String, String] = Map("Name" -> name.getResourcePath)

    // Position in pixels in relation to current block
    val hashMapPosAndRot: Map[String, String] = Map(
      "posX" -> posInFrame._1,
      "posY" -> posInFrame._2,
      "posZ" -> posInFrame._3,
      "rotation" -> rotation).mapValues(_.toString)

    List(hashMapName, hashMapPosAndRot)
  }

  /**
    * Determines if this object has support for the capability in question on the specific side.
    * The return value of this MIGHT change during runtime if this object gains or looses support
    * for a capability.
    *
    * Example:
    * A Pipe getting a cover placed on one side causing it loose the Inventory attachment function for that side.
    *
    * This is a light weight version of getCapability, intended for metadata uses.
    *
    * @param capability The capability to check
    * @param facing     The Side to check from:
    *                   CAN BE NULL. Null is defined to represent 'internal' or 'self'
    * @return True if this object supports the capability.
    */
  override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = false

  /**
    * Retrieves the handler for the capability requested on the specific side.
    * The return value CAN be null if the object does not support the capability.
    * The return value CAN be the same for multiple faces.
    *
    * @param capability The capability to check
    * @param facing     The Side to check from:
    *                   CAN BE NULL. Null is defined to represent 'internal' or 'self'
    * @return True if this object supports the capability.
    */
  override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = capability.asInstanceOf
}

object Module {
  val GridSize: Double = 16
}
