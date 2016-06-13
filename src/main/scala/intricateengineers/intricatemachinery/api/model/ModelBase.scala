package intricateengineers.intricatemachinery.api.model

import org.lwjgl.util.vector.Vector3f
import net.minecraft.util.{EnumFacing, ResourceLocation}

import scala.math.{min, max}
import scala.collection.mutable.ArrayBuffer
import intricateengineers.intricatemachinery.api.util.VectorUtils
import scala.collection.mutable.HashMap
import net.minecraft.client.renderer.block.model.BlockFaceUV

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
    return new Box(min, max)
  }

  def += (from : Vector3f, to : Vector3f) {
    boxes += new Box(from, to)
  }

}

class Box(val from : Vector3f, val to : Vector3f) {
  val faces = new HashMap[EnumFacing, (ResourceLocation, BlockFaceUV)]
  val size = Vector3f.sub(to, from, new Vector3f)

  def faceVecs(face : EnumFacing) : (Vector3f, Vector3f) = {
    face match {
      case EnumFacing.UP => {
        var k = max(from.getY(), to.getY())
        (new Vector3f(from.getX(), k, from.getZ()), new Vector3f(to.getX(), k, to.getZ()))
      }
      case EnumFacing.DOWN => {
        var k = min(from.getY(), to.getY())
        (new Vector3f(from.getX(), k, from.getZ()), new Vector3f(to.getX(), k, to.getZ()))
      }
      case EnumFacing.NORTH => {
        var k = min(from.getZ(), to.getZ())
        (new Vector3f(from.getX(), from.getY(), k), new Vector3f(to.getX(), to.getY(), k))
      }
      case EnumFacing.SOUTH => {
        var k = max(from.getZ(), to.getZ())
        (new Vector3f(from.getX(), from.getY(), k), new Vector3f(to.getX(), to.getY(), k))
      }
      case EnumFacing.WEST => {
        var k = min(from.getX(), to.getX())
        (new Vector3f(k, from.getY(), from.getZ()), new Vector3f(k, to.getY(), to.getZ()))
      }
      case EnumFacing.EAST => {
        var k = max(from.getX(), to.getX())
        (new Vector3f(k, from.getY(), from.getZ()), new Vector3f(k, to.getY(), to.getZ()))
      }
    }
  }
}
