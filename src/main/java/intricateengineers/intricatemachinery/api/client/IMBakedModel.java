package intricateengineers.intricatemachinery.api.client;

import intricateengineers.intricatemachinery.api.module.IMModel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author topisani
 */
public class IMBakedModel implements IBakedModel {

    private final ResourceLocation particle;
    private IMModel model;
    public static final FaceBakery  faceBakery = new FaceBakery();
    public final List<BakedQuad> quads = new ArrayList<>();

    public IMBakedModel(IMModel model) {
        this.model = model;
        this.particle = TextureMap.LOCATION_MISSING_TEXTURE;
        for (IMModel.Box box : model.getBoxes()) {
            for (BakedQuad quad : box.toQuads(faceBakery)) {
                quads.add(quad);
            }
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return quads;
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
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}