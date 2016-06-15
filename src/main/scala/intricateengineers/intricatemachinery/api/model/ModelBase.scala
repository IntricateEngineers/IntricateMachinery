package intricateengineers.intricatemachinery.api.model

import org.lwjgl.util.vector.Vector3f
import net.minecraft.util.{EnumFacing, ResourceLocation}

import scala.math.{max, min}
import scala.collection.mutable.ArrayBuffer
import intricateengineers.intricatemachinery.api.util.VectorUtils

import scala.collection.mutable.HashMap
import net.minecraft.client.renderer.block.model.BlockFaceUV

import scala.collection.mutable

abstract class ModelBase {
  val boxes = ArrayBuffer[Box]()

  init()

  val mainBox = initMainBox()

  def init()

  protected def initMainBox() : Box = {
    var min = new Vector3f(32, 32, 32)
    var max = new Vector3f(-16, -16, -16)
    for (box <- boxes) {
      min = VectorUtils.smallest(min, box.from)
      max = VectorUtils.greatest(max, box.to)
    }
    new Box(min, max)
  }

  def += (from : (Double, Double, Double), to : (Double, Double, Double)) {
    boxes += new Box(new Vector3f(from._1.toFloat, from._1.toFloat, from._3.toFloat),
      new Vector3f(to._1.toFloat, to._2.toFloat, to._3.toFloat))

  }

}

class Box(val from : Vector3f, val to : Vector3f) {
  val faces = new mutable.HashMap[EnumFacing, (ResourceLocation, BlockFaceUV)]
  val size = Vector3f.sub(to, from, new Vector3f)

  def faceVecs(face : EnumFacing) : (Vector3f, Vector3f) = {
    face match {
      case EnumFacing.UP =>
        val k = max(from.getY, to.getY)
        (new Vector3f(from.getX, k, from.getZ), new Vector3f(to.getX, k, to.getZ))
      case EnumFacing.DOWN =>
        val k = min(from.getY, to.getY)
        (new Vector3f(from.getX, k, from.getZ), new Vector3f(to.getX, k, to.getZ))
      case EnumFacing.NORTH =>
        val k = min(from.getZ, to.getZ)
        (new Vector3f(from.getX, from.getY, k), new Vector3f(to.getX, to.getY, k))
      case EnumFacing.SOUTH =>
        val k = max(from.getZ, to.getZ)
        (new Vector3f(from.getX, from.getY, k), new Vector3f(to.getX, to.getY, k))
      case EnumFacing.WEST =>
        val k = min(from.getX, to.getX)
        (new Vector3f(k, from.getY, from.getZ), new Vector3f(k, to.getY, to.getZ))
      case EnumFacing.EAST =>
        val k = max(from.getX, to.getX)
        (new Vector3f(k, from.getY, from.getZ), new Vector3f(k, to.getY, to.getZ))
    }
  }
}
