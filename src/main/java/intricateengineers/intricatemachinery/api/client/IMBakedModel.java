package intricateengineers.intricatemachinery.api.client;

import intricateengineers.intricatemachinery.api.module.IMModel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
        String textureName = "minecraft:blocks/stone";
        TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(textureName);
        BlockPartFace partFace = new BlockPartFace(null, 0, textureName, new BlockFaceUV(new float[]{0f, 0f, 16f, 16f}, 0));
        ModelRotation mr = ModelRotation.X0_Y0;
        BlockPartRotation rotation =  null;
        quads.add(faceBakery.makeBakedQuad(new Vector3f(0f, 16f, 0f), new Vector3f(16f, 16f, 16f), partFace, texture, EnumFacing.UP, mr, rotation, true, true));
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
    @Deprecated
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}