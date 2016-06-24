package intricateengineers.intricatemachinery.api.model

import intricateengineers.intricatemachinery.api.client.util.UV
import intricateengineers.intricatemachinery.api.util.ImplicitVectors._
import intricateengineers.intricatemachinery.api.util.VectorUtils
import net.minecraft.util.{EnumFacing, ResourceLocation}

import scala.collection.mutable.ListBuffer

abstract class ModelBase {
  lazy val mainBox = initMainBox()
  private val faceBuffer: ListBuffer[BoxFace] = ListBuffer()
  private val boxBuffer: ListBuffer[Box] = ListBuffer()
  private var _boxes: List[Box] = List()

  // Model definition stuff

  def |#|:(x1: Int, y1: Int, z1: Int)(x2: Int, y2: Int, z2: Int)(u: ⇒ Unit): Unit = {
    u
    boxBuffer += new Box((x1, y1, z1), (x2, y2, z2), faceBuffer.toList)
    faceBuffer.clear()
  }

  def |-(face: EnumFacing, texture: ResourceLocation, uv: UV = UV.auto(16)): Unit = {
    faceBuffer += BoxFace(face, texture, uv)
  }

  def |*(texture: ResourceLocation, uv: UV = UV.auto(16)): Unit = {
    EnumFacing.values.filterNot(face => faceBuffer.exists(_.side == face)).
            foreach(unfilledFace => faceBuffer += BoxFace(unfilledFace, texture, uv))
  }

  protected def initMainBox() : Box = {
    var min = (32d, 32d, 32d)
    var max = (-16d, -16d, -16d)
    for (box <- boxes) {
      min = VectorUtils.smallest(min, box.from)
      max = VectorUtils.greatest(max, box.to)
    }
    new Box(min, max)
  }

  def boxes = _boxes

  protected def define(f: ⇒ Unit): Unit = {
    boxBuffer.clear()
    f
    _boxes = boxBuffer.toList
  }
}



