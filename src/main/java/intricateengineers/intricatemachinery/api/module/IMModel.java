package intricateengineers.intricatemachinery.api.module;

import intricateengineers.intricatemachinery.api.client.IMBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @author topisani
 */
public abstract class IMModel {

    protected final List<Box> boxes = new ArrayList<>();
    @SideOnly(Side.CLIENT)
    protected IMBakedModel bakedModel;

    protected static final Vector3f vec(double x, double y, double z) {
        return new Vector3f((float) x, (float) y, (float) z);
    }

    protected static final BlockFaceUV uv(double x1, double y1, double x2, double y2) {
        return new BlockFaceUV(new float[] {(float) x1 / 16f, (float) y1 / 16f, (float) x2 / 16f - 8f, (float) y2 / 16f - 8f}, 0);
    }

    protected static final BlockFaceUV uvRandom(double x1, double y1, double x2, double y2) {
        return uvRandom();
    }

    protected static final BlockFaceUV uvRandom() {
        double x1, y1, x2, y2;
        x1 = new Random().nextInt(8) * 16;
        y1 = new Random().nextInt(8) * 16;

        x2 = x1 + 8;
        y2 = y1 + 16;

        return new BlockFaceUV(new float[] {(float) x1 / 32f, (float) y1 / 32f, (float) x2 / 32f, (float) y2 / 32f}, 0);
    }

    @SideOnly(Side.CLIENT)
    public IMBakedModel getBakedModel() {
        if (this.bakedModel == null) {
            this.bakedModel = new IMBakedModel(this);
        }
        return this.bakedModel;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public Box addBox(Vector3f from, Vector3f to) {
        Box box = new Box(from, to);
        boxes.add(box);
        return box;
    }

    public static class Box {

        private final Vector3f boxFrom;
        private final Vector3f boxTo;
        private final HashMap<EnumFacing, Pair<ResourceLocation, BlockFaceUV>> faces = new HashMap<>();

        public Box(Vector3f boxFrom, Vector3f boxTo) {
            this.boxFrom = boxFrom;
            this.boxTo = boxTo;
        }

        public Box setFace(EnumFacing face, ResourceLocation texture, BlockFaceUV uv) {
            this.faces.put(face, Pair.of(texture, uv));
            return this;
        }

        public AxisAlignedBB toAABB() {
            return new AxisAlignedBB(boxFrom.getX() / 16, boxFrom.getY() / 16, boxFrom.getZ() / 16, boxTo.getX() / 16, boxTo.getY() / 16, boxTo.getZ() / 16);
        }

        public List<BakedQuad> toQuads(FaceBakery faceBakery) {
            List<BakedQuad> quads = new ArrayList<>();
            for (EnumFacing face : EnumFacing.values()) {
                Pair<Vector3f, Vector3f> vecs = this.getFace(face);
                if (vecs.getKey() == null) {
                    continue;
                }

                //ITextureObject furn_tex = Minecraft.getMinecraft().renderEngine.getTexture(new ResourceLocation("minecraft", "blocks/furnace_top"));

                String textureName = faces.get(face).getKey().toString();
                TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(textureName);
                BlockFaceUV uv = faces.get(face).getValue();
                uv = new BlockFaceUV(new float[] {texture.getMinU() + uv.uvs[0], texture.getMinV() + uv.uvs[1], texture.getMinU() + uv.uvs[2], texture.getMinV() + uv.uvs[3]}, uv.rotation);
                BlockPartFace partFace = new BlockPartFace(face, 0, textureName, uv);
                ModelRotation mr = ModelRotation.X0_Y0;
                BlockPartRotation rotation =  new BlockPartRotation(vecs.getKey(), EnumFacing.Axis.X, 0, false);
                quads.add(faceBakery.makeBakedQuad(vecs.getKey(), vecs.getValue(), partFace, texture, face, mr, rotation, true, true));
            }
            return quads;
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
                    to = new Vector3f(Math.min(boxFrom.getX(), boxTo.getX()), boxTo.getY(), boxTo.getZ());
                    break;
                case EAST:
                    from = new Vector3f(Math.max(boxFrom.getX(), boxTo.getX()), boxFrom.getY(), boxFrom.getZ());
                    to = new Vector3f(Math.max(boxFrom.getX(), boxTo.getX()), boxTo.getY(), boxTo.getZ());
                    break;
                default:
                    from = null;
                    to = null;
            }
            return Pair.of(from, to);
        }
    }
}
