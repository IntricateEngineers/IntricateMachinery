package intricateengineers.intricatemachinery.api.module

import org.lwjgl.util.vector.Vector3f

/**
  * A representation of the position of a module. Useful for many weird things.
  */
class ModulePos(val int: Int) {

  val ints: (Int, Int, Int) = ((int & 0xF00) >> 8, (int & 0x0F0) >> 4, int & 0x00F)
  val doubles: (Double, Double, Double) = (ints._1 / Module.GRID_SIZE.toDouble, ints._2 / Module.GRID_SIZE.toDouble, ints._3 / Module.GRID_SIZE.toDouble)

  val (dX, dY, dZ) = doubles
  val (iX, iY, iZ) = ints
}

object ModulePos {

  def apply(x: Int, y: Int, z: Int): ModulePos = {
    new ModulePos(((x << 8) | (y << 4)) | z)
  }

  def apply(x: Double, y: Double, z: Double): ModulePos = {
    ModulePos((x * Module.GRID_SIZE).toInt, (y * Module.GRID_SIZE).toInt, (z * Module.GRID_SIZE).toInt)
  }

  def apply(vec: Vector3f): ModulePos = {
    ModulePos(vec.x, vec.y, vec.z)
  }

}