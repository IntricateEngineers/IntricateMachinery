/*
 * Copyright (c) 2016 IntricateEngineers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
