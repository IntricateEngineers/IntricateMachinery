package intricateengineers.intricatemachinery.api.module;

import intricateengineers.intricatemachinery.api.client.IMBakedModel;
import intricateengineers.intricatemachinery.api.client.util.UV;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author topisani
 */
public abstract class IMModel {

    protected static VertexFormat format = new VertexFormat();
    protected final List<Box> boxes = new ArrayList<>();
    @SideOnly(Side.CLIENT)
    protected IMBakedModel bakedModel;

    public IMModel() {
        this.init();
    }

    protected static final Vector3f vec(double x, double y, double z) {
        return new Vector3f((float) x, (float) y, (float) z);
    }

    @SideOnly(Side.CLIENT)
    public IMBakedModel getBakedModel() {
        if (this.bakedModel == null) {
            this.bakedModel = new IMBakedModel(this);
        }
        return this.bakedModel;
    }

    public abstract void init();

    public List<Box> getBoxes() {
        return boxes;
    }

    public Box addBox(Vector3f from, Vector3f to) {
        Box box = new Box(from, to);
        boxes.add(box);
        return box;
    }

    public static class Box {

        public final HashMap<EnumFacing, Pair<ResourceLocation, BlockFaceUV>> faces = new HashMap<>();
        private final Vector3f boxFrom;
        private final Vector3f boxTo;

        public Box(Vector3f boxFrom, Vector3f boxTo) {
            this.boxFrom = boxFrom;
            this.boxTo = boxTo;
        }

        public Box setFace(EnumFacing face, ResourceLocation texture, UV uv) {
            this.faces.put(face, Pair.of(texture, uv.toBFUV(face, getFace(face))));
            return this;
        }

        public Pair<Vector3f, Vector3f> getFace(EnumFacing face) {
            Vector3f from;
            Vector3f to;
            float k;
            switch (face) {
                case UP:
                    k = Math.max(boxFrom.getY(), boxTo.getY());
                    from = new Vector3f(boxFrom.getX(), k, boxFrom.getZ());
                    to = new Vector3f(boxTo.getX(), k, boxTo.getZ());
                    break;
                case DOWN:
                    k = Math.min(boxFrom.getY(), boxTo.getY());
                    from = new Vector3f(boxFrom.getX(), k, boxFrom.getZ());
                    to = new Vector3f(boxTo.getX(), k, boxTo.getZ());
                    break;
                case NORTH:
                    k = Math.min(boxFrom.getZ(), boxTo.getZ());
                    from = new Vector3f(boxFrom.getX(), boxFrom.getY(), k);
                    to = new Vector3f(boxTo.getX(), boxTo.getY(), k);
                    break;
                case SOUTH:
                    k = Math.max(boxFrom.getZ(), boxTo.getZ());
                    from = new Vector3f(boxFrom.getX(), boxFrom.getY(), k);
                    to = new Vector3f(boxTo.getX(), boxTo.getY(), k);
                    break;
                case WEST:
                    k = Math.min(boxFrom.getX(), boxTo.getX());
                    from = new Vector3f(k, boxFrom.getY(), boxFrom.getZ());
                    to = new Vector3f(k, boxTo.getY(), boxTo.getZ());
                    break;
                case EAST:
                    k = Math.max(boxFrom.getX(), boxTo.getX());
                    from = new Vector3f(k, boxFrom.getY(), boxFrom.getZ());
                    to = new Vector3f(k, boxTo.getY(), boxTo.getZ());
                    break;
                default:
                    from = null;
                    to = null;
            }
            return Pair.of(from, to);
        }

        public AxisAlignedBB toAABB() {
            return new AxisAlignedBB(boxFrom.getX() / 16, boxFrom.getY() / 16, boxFrom.getZ() / 16, boxTo.getX() / 16, boxTo.getY() / 16, boxTo.getZ() / 16);
        }
    }
}
