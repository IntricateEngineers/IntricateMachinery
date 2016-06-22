package intricateengineers.intricatemachinery.api.module

/**
  * A representation of the position of a module. Useful for many weird things.
  */
class ModulePos(val int: Int) {

  val ints: (Int, Int, Int) = ((int & 0xF00) >> 8, (int & 0x0F0) >> 4, int & 0x00F)
  val doubles: (Double, Double, Double) = (ints._1 / Module.GridSize, ints._2 / Module.GridSize, ints._3 / Module.GridSize)

  val (dX, dY, dZ) = doubles
  val (iX, iY, iZ) = ints
}

object ModulePos {

  def apply(x: Double, y: Double, z: Double): ModulePos = {
    ModulePos((x * Module.GridSize).toInt, (y * Module.GridSize).toInt, (z * Module.GridSize).toInt)
  }

  def apply(x: Int, y: Int, z: Int): ModulePos = {
    new ModulePos(((x << 8) | (y << 4)) | z)
  }

}