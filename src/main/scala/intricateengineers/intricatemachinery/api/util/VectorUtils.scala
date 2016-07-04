package intricateengineers.intricatemachinery.api.util

import net.minecraft.util.math.Vec3d
import org.lwjgl.util.vector.Vector3f

object VectorUtils {

  // CRAZY FRIKIN VOODOO MAGIC
  implicit def tupleToVector3f(tuple: (Double, Double, Double)): Vector3f = {
    new Vector3f(tuple._1.toFloat, tuple._2.toFloat, tuple._3.toFloat)
  }

  def smallest(v1: Vector3f, v2: Vector3f): Vector3f = {
    new Vector3f(Math.min(v1.getX, v2.getX), Math.min(v1.getY, v2.getY), Math.min(v1.getZ, v2.getZ))
  }

  def greatest(v1: Vector3f, v2: Vector3f): Vector3f = {
    new Vector3f(Math.max(v1.getX, v2.getX), Math.max(v1.getY, v2.getY), Math.max(v1.getZ, v2.getZ))
  }

  def modulus(vec: Vector3f, mod: Float): Vector3f = {
    new Vector3f(vec.x % mod, vec.y % mod, vec.y % mod)
  }
}

object ImplicitVectors {
  // CRAZY FRIKIN VOODOO MAGIC
  implicit def tupleToVector3f(tuple: (Double, Double, Double)): Vector3f = {
    new Vector3f(tuple._1.toFloat, tuple._2.toFloat, tuple._3.toFloat)
  }

  implicit def vector3FtoTuple(vector: Vector3f): (Double, Double, Double) = {
    (vector.x, vector.y, vector.z)
  }
}