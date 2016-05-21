package intricateengineers.intricatemachinery.common.blocks;

import intricateengineers.intricatemachinery.api.module.IMModel;
import intricateengineers.intricatemachinery.api.module.IMModule;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.util.EnumFacing.*;

/**
 * @author topisani
 */
public class FurnaceModule extends IMModule {

    public FurnaceModule() {
        super("furnace", new ModelFurnace());
    }

    public static class ModelFurnace extends IMModel {
        ResourceLocation topTexture;
        ResourceLocation sideTexture;
        ResourceLocation frontTexture;

        {
            addBox(vec(0.5, 0.5, 0.5), vec(3.5, 3.5, 3.5))
                .setFace(NORTH, sideTexture, uv(0.0, 0.0, 16.0, 16.0))
                .setFace(EAST, sideTexture, uv(0.0, 0.0, 16.0, 16.0))
                .setFace(SOUTH, frontTexture, uv(0.0, 0.0, 16.0, 16.0))
                .setFace(WEST, sideTexture, uv(0.0, 0.0, 16.0, 16.0))
                .setFace(UP, topTexture, uv(0.0, 0.0, 16.0, 16.0))
                .setFace(DOWN, topTexture, uv(0.0, 0.0, 16.0, 16.0));

        }
    }
}
