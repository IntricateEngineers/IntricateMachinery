package intricateengineers.intricatemachinery.api.util;

import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author topisani
 */
public class VectorUtils {

    public static final Vector3f smallest(Vector3f v1, Vector3f v2) {
        return new Vector3f(Math.min(v1.getX(), v2.getX()), Math.min(v1.getY(), v2.getY()), Math.min(v1.getZ(), v2.getZ()));
    }

    public static final Vector3f greatest(Vector3f v1, Vector3f v2) {
        return new Vector3f(Math.max(v1.getX(), v2.getX()), Math.max(v1.getY(), v2.getY()), Math.max(v1.getZ(), v2.getZ()));
    }

    public static final Vec3d modulus(Vec3d vec, double mod) {
        return new Vec3d(vec.xCoord % mod, vec.yCoord % mod, vec.zCoord % mod);
    }

}
