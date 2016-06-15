package intricateengineers.intricatemachinery.api.util

import net.minecraft.util.math.Vec3d
import org.lwjgl.util.vector.Vector3f

object VectorUtils {
  def smallest(v1: Vector3f, v2: Vector3f): Vector3f = {
    return new Vector3f(Math.min(v1.getX, v2.getX), Math.min(v1.getY, v2.getY), Math.min(v1.getZ, v2.getZ))
  }

  def greatest(v1: Vector3f, v2: Vector3f): Vector3f = {
    return new Vector3f(Math.max(v1.getX, v2.getX), Math.max(v1.getY, v2.getY), Math.max(v1.getZ, v2.getZ))
  }

  def modulus(vec: Vec3d, mod: Double): Vec3d = {
    return new Vec3d(vec.xCoord % mod, vec.yCoord % mod, vec.zCoord % mod)
  }
}