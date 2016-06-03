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
import intricateengineers.intricatemachinery.core.ModInfo;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.util.EnumFacing.*;
import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

/**
 * Created by VelocityRa
 */
public class DummyModule extends Module {

    public static Model MODEL = new Model();

    public DummyModule(MachineryFrame parentFrame) {
        super("dummy", MODEL, parentFrame);
    }

    public static class Model extends ModelBase {
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
