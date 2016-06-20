package intricateengineers.intricatemachinery.api.model

import intricateengineers.intricatemachinery.api.client.util.UV
import intricateengineers.intricatemachinery.api.util.VectorUtils
import org.lwjgl.util.vector.Vector3f
import net.minecraft.util.{EnumFacing, ResourceLocation}

import scala.math.{max, min}
import scala.collection.mutable.ArrayBuffer
import intricateengineers.intricatemachinery.api.util.ImplicitVectors._
import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.util.math.AxisAlignedBB

import scala.collection.mutable

abstract class ModelBase {
  val boxes = ArrayBuffer[Box]()

  val mainBox = initMainBox()

  init()

  def init()

  protected def initMainBox() : Box = {
    var min = (32d, 32d, 32d)
    var max = (-16d, -16d, -16d)
    for (box <- boxes) {
      min = VectorUtils.smallest(min, box.from)
      max = VectorUtils.greatest(max, box.to)
    }
    new Box(min, max)
  }

  def += (from : (Double, Double, Double), to : (Double, Double, Double)): Box = {
    val box = new Box((from._1.toFloat, from._2.toFloat, from._3.toFloat),
      (to._1.toFloat, to._2.toFloat, to._3.toFloat))
    boxes += box
    return box
  }

}

class Box(val from : (Double, Double, Double), val to : (Double, Double, Double)) {
  val faces = new mutable.HashMap[EnumFacing, (ResourceLocation, BlockFaceUV)]
  val size = Vector3f.sub(to, from, new Vector3f)

  def faceVecs(face : EnumFacing) : (Vector3f, Vector3f) = {
    face match {
      case EnumFacing.UP =>
        val k = max(from._2, to._2)
        ((from._1, k, from._3), (to._1, k, to._3))
      case EnumFacing.DOWN =>
        val k = min(from._2, to._2)
        ((from._1, k, from._3), (to._1, k, to._3))
      case EnumFacing.NORTH =>
        val k = min(from._3, to._3)
        ((from._1, from._2, k), (to._1, to._2, k))
      case EnumFacing.SOUTH =>
        val k = max(from._3, to._3)
        ((from._1, from._2, k), (to._1, to._2, k))
      case EnumFacing.WEST =>
        val k = min(from._1, to._1)
        ((k, from._2, from._3), (k, to._2, to._3))
      case EnumFacing.EAST =>
        val k = max(from._1, to._1)
        ((k, from._2, from._3), (k, to._2, to._3))
    }
  }

  def face(face: EnumFacing, texture: ResourceLocation, uv: UV): Box = {
    faces(face) = (texture, uv.toBFUV(face, (from, to)))
    return this
  }

  def aabb(pos: (Byte, Byte, Byte) = (0, 0, 0)): AxisAlignedBB = {
    new AxisAlignedBB(
      from.x + pos._1,
      from.y + pos._2,
      from.z + pos._3,
      from.x + pos._1,
      from.y + pos._2,
      from.z + pos._3
    )
  }
}
