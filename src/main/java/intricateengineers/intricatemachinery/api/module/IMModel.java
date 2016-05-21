package intricateengineers.intricatemachinery.api.module;

import javafx.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
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

        private final Vector3f boxFrom;
        private final Vector3f boxTo;
        private final HashMap<EnumFacing, Pair<ResourceLocation, BlockFaceUV>> faces;

        public Box(String name, Vector3f boxFrom, Vector3f boxTo, HashMap<EnumFacing, Pair<ResourceLocation, BlockFaceUV>> faces) {
            this.boxFrom = boxFrom;
            this.boxTo = boxTo;
            this.faces = faces;
        }

        public Pair<Vector3f, Vector3f> getFace(EnumFacing face) {
            Vector3f from;
            Vector3f to;
            switch (face) {
                case UP:
                    from = new Vector3f(boxFrom.getX(), Math.max(boxFrom.getY(), boxTo.getY()), boxFrom.getZ());
                    to = new Vector3f(boxTo.getX(), Math.max(boxFrom.getY(), boxTo.getY()), boxTo.getZ());
                    break;
                case DOWN:
                    from = new Vector3f(boxFrom.getX(), Math.min(boxFrom.getY(), boxTo.getY()), boxFrom.getZ());
                    to = new Vector3f(boxTo.getX(), Math.min(boxFrom.getY(), boxTo.getY()), boxTo.getZ());
                    break;
                case NORTH:
                    from = new Vector3f(boxFrom.getX(), boxFrom.getY(), Math.min(boxFrom.getZ(), boxTo.getZ()));
                    to = new Vector3f(boxTo.getX(), boxTo.getY(), Math.min(boxFrom.getZ(), boxTo.getZ()));
                    break;
                case SOUTH:
                    from = new Vector3f(boxFrom.getX(), boxFrom.getY(), Math.max(boxFrom.getZ(), boxTo.getZ()));
                    to = new Vector3f(boxTo.getX(), boxTo.getY(), Math.max(boxFrom.getZ(), boxTo.getZ()));
                    break;
                case WEST:
                    from = new Vector3f(Math.min(boxFrom.getX(), boxTo.getX()), boxFrom.getY(), boxFrom.getZ());
                    to = new Vector3f(Math.min(boxFrom.getX(), boxTo.getX()), boxTo.getY(),  boxTo.getZ());
                    break;
                case EAST:
                default:
                    from = new Vector3f(Math.max(boxFrom.getX(), boxTo.getX()), boxFrom.getY(), boxFrom.getZ());
                    to = new Vector3f(Math.max(boxFrom.getX(), boxTo.getX()), boxTo.getY(),  boxTo.getZ());
                    break;
            }
            return new Pair<>(from, to);
        }

        public AxisAlignedBB toAABB() {
            return new AxisAlignedBB(boxFrom.getX(), boxFrom.getY(), boxFrom.getZ(), boxTo.getX(), boxTo.getY(), boxTo.getZ());
        }

        public BakedQuad[] toQuads(FaceBakery faceBakery) {
            BakedQuad[] quads = new BakedQuad[6];
            for (EnumFacing face : EnumFacing.values()) {
                Pair<Vector3f, Vector3f> vecs = this.getFace(face);

                BlockFaceUV uv = faces.get(face).getValue();
                String textureName = faces.get(face).getKey().toString();
                BlockPartFace partFace = new BlockPartFace(face, 0, textureName, uv );
                TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(textureName);
                ModelRotation mr = ModelRotation.X0_Y0;
                quads[face.getIndex()] = faceBakery.makeBakedQuad(vecs.getKey(), vecs.getValue(), partFace, texture, face, mr, null, true, true);
            }
            return quads;
        }
    }
}
