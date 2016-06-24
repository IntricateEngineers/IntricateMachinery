package intricateengineers.intricatemachinery.api.client.util

import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.util.EnumFacing
import org.apache.commons.lang3.tuple.Pair
import org.lwjgl.util.vector.Vector3f

object UV {
  def uv(u1: Double, v1: Double, u2: Double, v2: Double): UV = {
    new UV(u1, v1, u2, v2)
  }

  def auto(scale: Double): UV = {
    new UV(false, scale)
  }

  def auto: UV = {
    new UV(false, 16)
  }

  def reset(scale: Double): UV = {
    new UV(true, scale)
  }

  def reset: UV = {
    new UV(true, 16)
  }

  def fill: UV = {
    new UV(0, 0, 16, 16)
  }
}

class UV {
  private var u1: Double = .0
  private var v1: Double = .0
  private var u2: Double = .0
  private var v2: Double = .0
  private var auto: Boolean = false
  private var scale: Double = .0
  private var reset: Boolean = false

  def this(u1: Double, v1: Double, u2: Double, v2: Double) {
    this()
    this.u1 = u1
    this.v1 = v1
    this.u2 = u2
    this.v2 = v2
    this.auto = false
    this.scale = 16f
    this.reset = false
  }

  def this(reset: Boolean, scale: Double) {
    this()
    this.u1 = 0
    this.v1 = 0
    this.u2 = 0
    this.v2 = 0
    this.auto = true
    this.scale = scale
    this.reset = reset
  }

  def toBFUV(face: EnumFacing, vecs: (Vector3f, Vector3f)): BlockFaceUV = {
    if (!this.auto) {
      new BlockFaceUV(Array[Float](u1.toFloat, v1.toFloat, u2.toFloat, v2.toFloat), 0)
    }
    else {
      var x1: Double = .0
      var y1: Double = .0
      var x2: Double = .0
      var y2: Double = .0
      face match {
        case EnumFacing.DOWN =>
          x1 = vecs._1.getX
          y1 = (scale - vecs._2.getZ).toFloat
          x2 = vecs._2.getX
          y2 = (scale - vecs._1.getZ).toFloat
        case EnumFacing.UP =>
          x1 = vecs._1.getX
          y1 = vecs._1.getZ
          x2 = vecs._2.getX
          y2 = vecs._2.getZ
        case EnumFacing.NORTH =>
          x1 = (scale - vecs._2.getX).toFloat
          y1 = (scale - vecs._2.getY).toFloat
          x2 = (scale - vecs._1.getX).toFloat
          y2 = (scale - vecs._1.getY).toFloat
        case EnumFacing.SOUTH =>
          x1 = vecs._1.getX
          y1 = (scale - vecs._2.getY).toFloat
          x2 = vecs._2.getX
          y2 = (scale - vecs._1.getY).toFloat
        case EnumFacing.EAST =>
          x1 = (scale - vecs._2.getZ).toFloat
          y1 = (scale - vecs._2.getY).toFloat
          x2 = (scale - vecs._1.getZ).toFloat
          y2 = (scale - vecs._1.getY).toFloat
        case EnumFacing.WEST =>
          x1 = vecs._1.getZ
          y1 = (scale - vecs._2.getY).toFloat
          x2 = vecs._2.getZ
          y2 = (scale - vecs._1.getY).toFloat
        case _ =>
          return null
      }
      if (reset) {
        x2 -= x1
        y2 -= y1
        x1 = 0
        y1 = 0
      }
      val factor: Float = (16f / scale).toFloat
      val floats: Array[Float] = Array(x1 * factor, y1 * factor, x2 * factor, y2 * factor).map(_.toFloat)
      new BlockFaceUV(floats, 0)
    }
  }
}