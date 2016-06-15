package intricateengineers.intricatemachinery.api.client.util

import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.util.EnumFacing
import org.apache.commons.lang3.tuple.Pair
import org.lwjgl.util.vector.Vector3f

object UV {
  def uv(u1: Double, v1: Double, u2: Double, v2: Double): UV = {
    return new UV(u1, v1, u2, v2)
  }

  def auto(scale: Double): UV = {
    return new UV(false, scale)
  }

  def auto: UV = {
    return new UV(false, 16)
  }

  def reset(scale: Double): UV = {
    return new UV(true, scale)
  }

  def reset: UV = {
    return new UV(true, 16)
  }

  def fill: UV = {
    return new UV(0, 0, 16, 16)
  }
}

class UV {
  final private var u1: Double = .0
  final private var v1: Double = .0
  final private var u2: Double = .0
  final private var v2: Double = .0
  final private var auto: Boolean = false
  final private var scale: Double = .0
  final private var reset: Boolean = false

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

  def toBFUV(face: EnumFacing, vecs: Pair[Vector3f, Vector3f]): BlockFaceUV = {
    if (!this.auto) {
      return new BlockFaceUV(Array[Float](u1.toFloat, v1.toFloat, u2.toFloat, v2.toFloat), 0)
    }
    else {
      var x1: Float = .0
      var y1: Float = .0
      var x2: Float = .0
      var y2: Float = .0
      face match {
        case DOWN =>
          x1 = vecs.getLeft.getX
          y1 = (scale - vecs.getRight.getZ).toFloat
          x2 = vecs.getRight.getX
          y2 = (scale - vecs.getLeft.getZ).toFloat
          break //todo: break is not supported
        case UP =>
          x1 = vecs.getLeft.getX
          y1 = vecs.getLeft.getZ
          x2 = vecs.getRight.getX
          y2 = vecs.getRight.getZ
          break //todo: break is not supported
        case NORTH =>
          x1 = (scale - vecs.getRight.getX).toFloat
          y1 = (scale - vecs.getRight.getY).toFloat
          x2 = (scale - vecs.getLeft.getX).toFloat
          y2 = (scale - vecs.getLeft.getY).toFloat
          break //todo: break is not supported
        case SOUTH =>
          x1 = vecs.getLeft.getX
          y1 = (scale - vecs.getRight.getY).toFloat
          x2 = vecs.getRight.getX
          y2 = (scale - vecs.getLeft.getY).toFloat
          break //todo: break is not supported
        case EAST =>
          x1 = (scale - vecs.getRight.getZ).toFloat
          y1 = (scale - vecs.getRight.getY).toFloat
          x2 = (scale - vecs.getLeft.getZ).toFloat
          y2 = (scale - vecs.getLeft.getY).toFloat
          break //todo: break is not supported
        case WEST =>
          x1 = vecs.getLeft.getZ
          y1 = (scale - vecs.getRight.getY).toFloat
          x2 = vecs.getRight.getZ
          y2 = (scale - vecs.getLeft.getY).toFloat
          break //todo: break is not supported
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
      val floats: Array[Float] = Array(x1 * factor, y1 * factor, x2 * factor, y2 * factor)
      return new BlockFaceUV(floats, 0)
    }
  }
}