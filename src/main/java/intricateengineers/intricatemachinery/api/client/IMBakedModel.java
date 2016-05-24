package intricateengineers.intricatemachinery.api.client;

import intricateengineers.intricatemachinery.api.module.IMModel;
import intricateengineers.intricatemachinery.api.module.IMModule;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author topisani
 */
@SideOnly(Side.CLIENT)
public class IMBakedModel implements IBakedModel {

    private final ResourceLocation particle;
    private IMModel model;
    public static final FaceBakery  faceBakery = new FaceBakery();
    public final List<BakedQuad> quads = new ArrayList<>();

    public IMBakedModel(IMModel model) {
        this.model = model;
        this.particle = TextureMap.LOCATION_MISSING_TEXTURE;
    }

    public void initQuads() {
        quads.clear();
        for (IMModel.Box box : model.getBoxes()) {
            for (EnumFacing face : EnumFacing.values()) {
                Pair<Vector3f, Vector3f> vecs = box.getFace(face);
                if (vecs.getLeft() == null) {
                    continue;
                }
                TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(box.faces.get(face).getLeft().toString());
                BlockPartFace partFace = new BlockPartFace(null, 0, "", box.faces.get(face).getRight());
                ModelRotation mr = ModelRotation.X0_Y0;
                BlockPartRotation rotation =  null;
                BakedQuad quad = faceBakery.makeBakedQuad(vecs.getLeft(), vecs.getRight(), partFace, texture, face, mr, rotation, true, true);
                quads.add(quad);
            }
        }
    }

    public void initTextures() {
        for (IMModel.Box box : model.getBoxes()) {
            for (EnumFacing face : EnumFacing.values()) {
                Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(box.faces.get(face).getLeft());
            }
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        IMModule module = null;
        if (state instanceof IExtendedBlockState) {
            module = ((IExtendedBlockState) state).getValue(IMModule.PROPERTY);
        }
        if (module == null) {
            return this.quads;
        }
        int[] vertexData;
        List<BakedQuad> quads1 = new ArrayList<>();

        for (BakedQuad quad : quads) {
            vertexData = quad.getVertexData().clone();
            for (int i = 0; i < 4; ++i)
            {
                float xFloat = Float.intBitsToFloat(vertexData[i*7]);
                float yFloat = Float.intBitsToFloat(vertexData[i*7+1]);
                float zFloat = Float.intBitsToFloat(vertexData[i*7+2]);

                vertexData[i*7] = Float.floatToRawIntBits((xFloat)+(5/16f));
                //vertexData[(i*7)+1] = Float.floatToRawIntBits((yFloat)+(6f/16f));
                vertexData[(i*7)+2] = Float.floatToRawIntBits((zFloat)+(5f/16f));
            }
            BakedQuad quad1 = new BakedQuad(vertexData, quad.getTintIndex(), quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), DefaultVertexFormats.ITEM);
            quads1.add(quad1);
        }

        return quads1;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(particle.toString());
    }

    @Override
    @Deprecated
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}