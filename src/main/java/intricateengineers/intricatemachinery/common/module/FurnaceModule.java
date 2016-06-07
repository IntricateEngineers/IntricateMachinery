/*
 * Copyright (c) 2016 IntricateEngineers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package intricateengineers.intricatemachinery.common.module;

import intricateengineers.intricatemachinery.api.client.util.UV;
import intricateengineers.intricatemachinery.api.module.MachineryFrame;
import intricateengineers.intricatemachinery.api.module.ModelBase;
import intricateengineers.intricatemachinery.api.module.Module;
import intricateengineers.intricatemachinery.api.module.ModuleModel;
import intricateengineers.intricatemachinery.core.ModInfo;

import net.minecraft.util.ResourceLocation;

import static net.minecraft.util.EnumFacing.*;

/**
 * @author topisani
 */
public class FurnaceModule extends Module {

    public static Model MODEL = new Model();

    public FurnaceModule(MachineryFrame parentFrame) {
        super("furnace", MODEL, parentFrame);
    }

    public static class Model extends ModuleModel {
        private static ResourceLocation topTexture = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "blocks/furnace_top");
        private static ResourceLocation sideTexture = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "blocks/furnace_side");
        private static ResourceLocation frontTexture = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "blocks/furnace_front_on");
        private static ResourceLocation frameTexture = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "blocks/furnace_top");

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
            // Horizontals
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
