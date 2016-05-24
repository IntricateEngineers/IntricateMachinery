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

package intricateengineers.intricatemachinery.api.client.util;

import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author topisani
 */
public class UV {

    private final double u1, v1, u2, v2;
    private final boolean auto;
    private final double scale;
    private final boolean reset;

    public UV(double u1, double v1, double u2, double v2) {
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
        this.auto = false;
        this.scale = 16f;
        this.reset = false;
    }

    public UV(boolean reset, double scale) {
        this.u1 = 0;
        this.v1 = 0;
        this.u2 = 0;
        this.v2 = 0;
        this.auto = true;
        this.scale = scale;
        this.reset = reset;
    }

    public static UV uv(double u1, double v1, double u2, double v2) {
        return new UV(u1, v1, u2, v2);
    }

    public static UV auto(double scale) {
        return new UV(false, scale);
    }

    public static UV auto() {
        return new UV(false, 16);
    }

    public static UV reset(double scale) {
        return new UV(true, scale);
    }

    public static UV reset() {
        return new UV(true, 16);
    }

    public static UV fill() {
        return new UV(0, 0, 16, 16);
    }

    public BlockFaceUV toBFUV(EnumFacing face, Pair<Vector3f, Vector3f> vecs) {
        if (!this.auto) {
            return new BlockFaceUV(new float[] {(float) u1, (float) v1, (float) u2, (float) v2}, 0);
        } else {
            float x1, y1, x2, y2;
            switch (face) {
                case DOWN:
                    x1 = vecs.getLeft().getX();
                    y1 = (float) (scale - vecs.getRight().getZ());
                    x2 = vecs.getRight().getX();
                    y2 = (float) (scale - vecs.getLeft().getZ());
                    break;
                case UP:
                    x1 = vecs.getLeft().getX();
                    y1 = vecs.getLeft().getZ();
                    x2 = vecs.getRight().getX();
                    y2 = vecs.getRight().getZ();
                    break;
                case NORTH:
                    x1 = (float) (scale - vecs.getRight().getX());
                    y1 = (float) (scale - vecs.getRight().getY());
                    x2 = (float) (scale - vecs.getLeft().getX());
                    y2 = (float) (scale - vecs.getLeft().getY());
                    break;
                case SOUTH:
                    x1 = vecs.getLeft().getX();
                    y1 = (float) (scale - vecs.getRight().getY());
                    x2 = vecs.getRight().getX();
                    y2 = (float) (scale - vecs.getLeft().getY());
                    break;
                case EAST:
                    x1 = (float) (scale - vecs.getRight().getZ());
                    y1 = (float) (scale - vecs.getRight().getY());
                    x2 = (float) (scale - vecs.getLeft().getZ());
                    y2 = (float) (scale - vecs.getLeft().getY());
                    break;
                case WEST:
                    x1 = vecs.getLeft().getZ();
                    y1 = (float) (scale - vecs.getRight().getY());
                    x2 = vecs.getRight().getZ();
                    y2 = (float) (scale - vecs.getLeft().getY());
                    break;
                default:
                    return null;
            }
            if (reset) {
                x2 -= x1;
                y2 -= y1;
                x1 = 0;
                y1 = 0;
            }
            float factor = (float) (16f / scale);
            float[] floats = {x1 * factor, y1 * factor, x2 * factor, y2 * factor};
            return new BlockFaceUV(floats, 0);
        }
    }
}
