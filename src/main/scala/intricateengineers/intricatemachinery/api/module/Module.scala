package intricateengineers.intricatemachinery.api.module

import intricateengineers.intricatemachinery.api.model.ModuleModel
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.common.capabilities.ICapabilitySerializable

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

}

object Module {
  val GridSize: Double = 16
}
