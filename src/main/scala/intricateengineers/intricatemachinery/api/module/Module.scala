package intricateengineers.intricatemachinery.api.module

import net.minecraft.util.math.Vec3d
import intricateengineers.intricatemachinery.api.model.ModuleModel
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraft.nbt.NBTTagCompound
import scala.util.Random
import net.minecraft.util.math.AxisAlignedBB
import scala.collection.mutable.ArrayBuffer

abstract class Module(val frame: MachineryFrame) extends ICapabilitySerializable[NBTTagCompound] {
  
  val name: ResourceLocation
  val model: ModuleModel

  val boundingboxes = ArrayBuffer[AxisAlignedBB]
  var debugInfo = initDebugInfo

  // Temporary hardcoded values
  private var _posInFrame: (Byte, Byte, Byte) = (
    8 / Module.Gridsize,
    8 / Module.GridSize,
    8 / Module.GridSize
  )

  // Temporary hardcoded value
  private var _rotation: Byte = Random.nextInt(3)

  // Getters and setters
  def posInFrame() = _posInFrame
  
  def posInFrame_=(vec: (Double, Double, Double)) = {
    _posInFrame = (
      vec._1 * Module.GridSize,
      vec._2 * Module.GridSize,
      vec._3 * Module.GridSize
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

}

object Module {
  val GridSize: Double = 16
}
