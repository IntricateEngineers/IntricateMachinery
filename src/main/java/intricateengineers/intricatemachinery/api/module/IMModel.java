package intricateengineers.intricatemachinery.api.module;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * @author topisani
 */
public abstract class IMModel {

    public List<Box> getBoxes() {
        return boxes;
    }

    protected final List<Box> boxes = new ArrayList<>();

    public static class Box {

        private final String name;
        private final Vector3f from;
        private final Vector3f to;

        public Box(String name, Vector3f from, Vector3f to) {
            this.name = name;
            this.from = from;
            this.to = to;
        }

        public AxisAlignedBB toAABB() {
            return new AxisAlignedBB(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
        }

        public BakedQuad toQuad(FaceBakery faceBakery) {
            return faceBakery.makeBakedQuad(from, to);
        }
    }
}
