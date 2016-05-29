package intricateengineers.intricatemachinery.common.module;

import intricateengineers.intricatemachinery.api.client.util.UV;
import intricateengineers.intricatemachinery.api.module.IMModel;
import intricateengineers.intricatemachinery.api.module.IMModule;
import intricateengineers.intricatemachinery.core.ModInfo;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.util.EnumFacing.*;
import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

/**
 * Created by VelocityRa on 29/5/2016.
 */
public class DummyModule extends IMModule {

    public static Model MODEL = new Model();

    public DummyModule() {
        super("dummy", MODEL);
    }

    public static class Model extends IMModel {
        private static ResourceLocation texture = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "blocks/dummy");

        @Override
        public void init() {
            this.boxes.clear();
            //Middle
            addBox(vec(0, 0, 0), vec(8, 8, 8))
                    .setFace(NORTH, texture, UV.fill())
                    .setFace(EAST, texture, UV.fill())
                    .setFace(SOUTH, texture, UV.fill())
                    .setFace(WEST, texture, UV.fill())
                    .setFace(UP, texture, UV.fill())
                    .setFace(DOWN, texture, UV.fill());
        }
    }
}
