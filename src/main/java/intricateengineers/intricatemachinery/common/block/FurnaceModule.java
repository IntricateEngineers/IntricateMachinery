package intricateengineers.intricatemachinery.common.block;

import intricateengineers.intricatemachinery.api.module.IMModel;
import intricateengineers.intricatemachinery.api.module.IMModule;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import static net.minecraft.util.EnumFacing.*;

/**
 * @author topisani
 */
public class FurnaceModule extends IMModule {

    public FurnaceModule() {
        super("furnace", new ModelFurnace());
    }

    public static class ModelFurnace extends IMModel {
        public static TextureAtlasSprite topTexture;
        public static TextureAtlasSprite sideTexture;
        public static TextureAtlasSprite frontTexture;

        {
            addBox(vec(1, 1, 1), vec(4, 4, 4))
                .setFace(NORTH, sideTexture, uv(0.0, 0.0, 16.0, 16.0))
                .setFace(EAST, sideTexture, uv(0.0, 0.0, 16.0, 16.0))
                .setFace(SOUTH, frontTexture, uv(0.0, 0.0, 16.0, 16.0))
                .setFace(WEST, sideTexture, uv(0.0, 0.0, 16.0, 16.0))
                .setFace(UP, topTexture, uv(0.0, 0.0, 16.0, 16.0))
                .setFace(DOWN, topTexture, uv(0.0, 0.0, 16.0, 16.0));
        }
    }
}
