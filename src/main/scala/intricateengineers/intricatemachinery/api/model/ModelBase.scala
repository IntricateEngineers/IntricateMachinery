package intricateengineers.intricatemachinery.api.model

import intricateengineers.intricatemachinery.api.client.util.UV
import intricateengineers.intricatemachinery.api.module.Module
import intricateengineers.intricatemachinery.api.util.ImplicitVectors._
import intricateengineers.intricatemachinery.api.util.VectorUtils
import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.{EnumFacing, ResourceLocation}
import org.lwjgl.util.vector.Vector3f

import scala.collection.mutable
import scala.math.{max, min}

abstract class ModelBase {
  lazy val mainBox = initMainBox()
  val boxes: List[Box]

  protected def initMainBox() : Box = {
    var min = (32d, 32d, 32d)
    var max = (-16d, -16d, -16d)
    for (box <- boxes) {
      min = VectorUtils.smallest(min, box.from)
      max = VectorUtils.greatest(max, box.to)
    }
    new Box(min, max)
  }

}

case class Box(from: (Double, Double, Double), to: (Double, Double, Double)) {
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
      case _ => (null, null)
    }
  }

  def face(face: EnumFacing, texture: ResourceLocation, uv: UV): Box = {
    faces(face) = (texture, uv.toBFUV(face, (from, to)))
    this
  }

  def aabb(pos: (Int, Int, Int) = (0, 0, 0)): AxisAlignedBB = {
    new AxisAlignedBB(
      (from.x + pos._1) / Module.GridSize,
      (from.y + pos._2) / Module.GridSize,
      (from.z + pos._3) / Module.GridSize,
      (to.x + pos._1) / Module.GridSize,
      (to.y + pos._2) / Module.GridSize,
      (to.z + pos._3) / Module.GridSize
    )
  }
}
