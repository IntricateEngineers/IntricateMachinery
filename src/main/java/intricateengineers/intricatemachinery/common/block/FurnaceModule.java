package intricateengineers.intricatemachinery.common.block;

import intricateengineers.intricatemachinery.api.module.IMModel;
import intricateengineers.intricatemachinery.api.module.IMModule;
import intricateengineers.intricatemachinery.core.ModInfo;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.util.EnumFacing.*;

/**
 * @author topisani
 */
public class FurnaceModule extends IMModule {

    public FurnaceModule() {
        super("furnace", new Model());
    }

    private static class Model extends IMModel {
        private static ResourceLocation topTexture = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "blocks/furnace_top");
        private static ResourceLocation sideTexture = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "blocks/furnace_side");
        private static ResourceLocation frontTexture = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "blocks/furnace_front_on");

        {
            addBox(vec(0, 0, 0), vec(16, 16, 16))
                .setFace(NORTH, sideTexture,    uv(0, 0, 16, 16))
                .setFace(EAST,  sideTexture,    uv(0, 0, 16, 16))
                .setFace(SOUTH, frontTexture,   uv(0, 0, 16, 16))
                .setFace(WEST,  sideTexture,    uv(0, 0, 16, 16))
                .setFace(UP,    topTexture,     uv(0, 0, 16, 16))
                .setFace(DOWN,  topTexture,     uv(0, 0, 16, 16));
        }
    }
}
