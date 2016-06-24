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


  /* ------======================------
        START Model Definition code
     ------======================------ */

  // Starts the definition of a Box. Everything else for this box should go to the "u" parameter.
  def |#|:(x1: Int, y1: Int, z1: Int)(x2: Int, y2: Int, z2: Int)(u: ⇒ Unit): Unit = {
    u
    boxBuffer += new Box((x1, y1, z1), (x2, y2, z2), faceBuffer.toList)
    faceBuffer.clear()
  }

  // The functions below are supposed to be passed as arguments to the above function's "u" parameter

  // Adds a texture and a UV to a given face
  def |-(face: EnumFacing, texture: ResourceLocation, uv: UV = UV.auto(16)): Unit = {
    faceBuffer += BoxFace(face, texture, uv)
  }

  // Adds a texture and a UV, to all faces of the box that haven't been assigned anything yet
  def |*(texture: ResourceLocation, uv: UV = UV.auto(16)): Unit = {
    EnumFacing.values.filterNot(face ⇒ faceBuffer.exists(_.side == face)).           // Get all faces that aren't filled in yet
            foreach(unfilledFace ⇒ faceBuffer += BoxFace(unfilledFace, texture, uv)) // Fill them up with the appropriate arguments
  }

  /* ------======================------
         END Model Definition code
     ------======================------ */


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



