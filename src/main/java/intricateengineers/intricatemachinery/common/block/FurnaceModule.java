package intricateengineers.intricatemachinery.common.block;

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
        ResourceLocation topTexture = new ResourceLocation("minecraft", "blocks/furnace_top");
        ResourceLocation sideTexture = new ResourceLocation("minecraft", "blocks/furnace_top");
        ResourceLocation frontTexture = new ResourceLocation("minecraft", "blocks/furnace_front_on");

        {
            addBox(vec(0.5, 0.5, 0.5), vec(3.5, 3.5, 3.5))
                .setFace(NORTH, sideTexture, uvRandom(0.0, 0.0, 16.0, 16.0))
                .setFace(EAST, sideTexture, uvRandom(0.0, 0.0, 16.0, 16.0))
                .setFace(SOUTH, frontTexture, uvRandom(0.0, 0.0, 16.0, 16.0))
                .setFace(WEST, sideTexture, uvRandom(0.0, 0.0, 16.0, 16.0))
                .setFace(UP, topTexture, uvRandom(0.0, 0.0, 16.0, 16.0))
                .setFace(DOWN, topTexture, uvRandom(0.0, 0.0, 16.0, 16.0));

        }
    }
}
