package intricateengineers.intricatemachinery.common.block;

import intricateengineers.intricatemachinery.api.client.util.UV;
import intricateengineers.intricatemachinery.api.module.IMModel;
import intricateengineers.intricatemachinery.api.module.IMModule;
import intricateengineers.intricatemachinery.core.ModInfo;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.util.EnumFacing.*;

/**
 * @author topisani
 */
public class FurnaceModule extends IMModule {

    public static Model MODEL = new Model();

    public FurnaceModule() {
        super("furnace", MODEL);
    }

    public static class Model extends IMModel {
        private static ResourceLocation topTexture = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "blocks/furnace_top");
        private static ResourceLocation sideTexture = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "blocks/furnace_side");
        private static ResourceLocation frontTexture = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "blocks/furnace_front_on");
        private static ResourceLocation frameTexture = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "blocks/modular_components/empty_frame");

        @Override
        public void init() {
            this.boxes.clear();
            //Middle
            addBox(vec(1, 1, 1), vec(5, 5, 5))
                .setFace(NORTH, sideTexture,    UV.fill())
                .setFace(EAST,  sideTexture,    UV.fill())
                .setFace(SOUTH, frontTexture,   UV.fill())
                .setFace(WEST,  sideTexture,    UV.fill())
                .setFace(UP,    topTexture,     UV.fill())
                .setFace(DOWN,  topTexture,     UV.fill());
            // Corners
            addBox(vec(0, 0, 0), vec(1, 6, 1))
                .setFace(NORTH, frameTexture,     UV.auto(6))
                .setFace(EAST,  frameTexture,     UV.auto(6))
                .setFace(SOUTH, frameTexture,     UV.auto(6))
                .setFace(WEST,  frameTexture,     UV.auto(6))
                .setFace(UP,    frameTexture,     UV.auto(6))
                .setFace(DOWN,  frameTexture,     UV.auto(6));
            addBox(vec(0, 0, 5), vec(1, 6, 6))
                .setFace(NORTH, frameTexture,     UV.auto(6))
                .setFace(EAST,  frameTexture,     UV.auto(6))
                .setFace(SOUTH, frameTexture,     UV.auto(6))
                .setFace(WEST,  frameTexture,     UV.auto(6))
                .setFace(UP,    frameTexture,     UV.auto(6))
                .setFace(DOWN,  frameTexture,     UV.auto(6));
            addBox(vec(5, 0, 0), vec(6, 6, 1))
                .setFace(NORTH, frameTexture,     UV.auto(6))
                .setFace(EAST,  frameTexture,     UV.auto(6))
                .setFace(SOUTH, frameTexture,     UV.auto(6))
                .setFace(WEST,  frameTexture,     UV.auto(6))
                .setFace(UP,    frameTexture,     UV.auto(6))
                .setFace(DOWN,  frameTexture,     UV.auto(6));
            addBox(vec(5, 0, 5), vec(6, 6, 6))
                .setFace(NORTH, frameTexture,     UV.auto(6))
                .setFace(EAST,  frameTexture,     UV.auto(6))
                .setFace(SOUTH, frameTexture,     UV.auto(6))
                .setFace(WEST,  frameTexture,     UV.auto(6))
                .setFace(UP,    frameTexture,     UV.auto(6))
                .setFace(DOWN,  frameTexture,     UV.auto(6));
            // Horisontals
            addBox(vec(1, 0, 0), vec(5, 1, 1))
                .setFace(NORTH, frameTexture,     UV.auto(6))
                .setFace(EAST,  frameTexture,     UV.auto(6))
                .setFace(SOUTH, frameTexture,     UV.auto(6))
                .setFace(WEST,  frameTexture,     UV.auto(6))
                .setFace(UP,    frameTexture,     UV.auto(6))
                .setFace(DOWN,  frameTexture,     UV.auto(6));
            addBox(vec(1, 5, 0), vec(5, 6, 1))
                .setFace(NORTH, frameTexture,     UV.auto(6))
                .setFace(EAST,  frameTexture,     UV.auto(6))
                .setFace(SOUTH, frameTexture,     UV.auto(6))
                .setFace(WEST,  frameTexture,     UV.auto(6))
                .setFace(UP,    frameTexture,     UV.auto(6))
                .setFace(DOWN,  frameTexture,     UV.auto(6));
            addBox(vec(0, 0, 1), vec(1, 1, 5))
                .setFace(NORTH, frameTexture,     UV.auto(6))
                .setFace(EAST,  frameTexture,     UV.auto(6))
                .setFace(SOUTH, frameTexture,     UV.auto(6))
                .setFace(WEST,  frameTexture,     UV.auto(6))
                .setFace(UP,    frameTexture,     UV.auto(6))
                .setFace(DOWN,  frameTexture,     UV.auto(6));
            addBox(vec(0, 5, 1), vec(1, 6, 5))
                .setFace(NORTH, frameTexture,     UV.auto(6))
                .setFace(EAST,  frameTexture,     UV.auto(6))
                .setFace(SOUTH, frameTexture,     UV.auto(6))
                .setFace(WEST,  frameTexture,     UV.auto(6))
                .setFace(UP,    frameTexture,     UV.auto(6))
                .setFace(DOWN,  frameTexture,     UV.auto(6));
            addBox(vec(5, 0, 1), vec(6, 1, 5))
                .setFace(NORTH, frameTexture,     UV.auto(6))
                .setFace(EAST,  frameTexture,     UV.auto(6))
                .setFace(SOUTH, frameTexture,     UV.auto(6))
                .setFace(WEST,  frameTexture,     UV.auto(6))
                .setFace(UP,    frameTexture,     UV.auto(6))
                .setFace(DOWN,  frameTexture,     UV.auto(6));
            addBox(vec(5, 5, 1), vec(6, 6, 5))
                .setFace(NORTH, frameTexture,     UV.auto(6))
                .setFace(EAST,  frameTexture,     UV.auto(6))
                .setFace(SOUTH, frameTexture,     UV.auto(6))
                .setFace(WEST,  frameTexture,     UV.auto(6))
                .setFace(UP,    frameTexture,     UV.auto(6))
                .setFace(DOWN,  frameTexture,     UV.auto(6));
            addBox(vec(1, 0, 5), vec(5, 1, 6))
                .setFace(NORTH, frameTexture,     UV.auto(6))
                .setFace(EAST,  frameTexture,     UV.auto(6))
                .setFace(SOUTH, frameTexture,     UV.auto(6))
                .setFace(WEST,  frameTexture,     UV.auto(6))
                .setFace(UP,    frameTexture,     UV.auto(6))
                .setFace(DOWN,  frameTexture,     UV.auto(6));
            addBox(vec(1, 5, 5), vec(5, 6, 6))
                .setFace(NORTH, frameTexture,     UV.auto(6))
                .setFace(EAST,  frameTexture,     UV.auto(6))
                .setFace(SOUTH, frameTexture,     UV.auto(6))
                .setFace(WEST,  frameTexture,     UV.auto(6))
                .setFace(UP,    frameTexture,     UV.auto(6))
                .setFace(DOWN,  frameTexture,     UV.auto(6));
        }


    }
}
