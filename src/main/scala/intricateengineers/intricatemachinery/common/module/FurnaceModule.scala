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
package intricateengineers.intricatemachinery.common.module

import intricateengineers.intricatemachinery.api.client.util.UV
import intricateengineers.intricatemachinery.api.module.Module
import intricateengineers.intricatemachinery.api.module.ModuleModel
import intricateengineers.intricatemachinery.core.ModInfo
import net.minecraft.util.ResourceLocation
import net.minecraft.util.EnumFacing._

object FurnaceModule {
    var MODEL: FurnaceModule.Model = new FurnaceModule.Model

    object Model {
        private val topTexture: ResourceLocation = new ResourceLocation(ModInfo.MOD_ID.toLowerCase, "blocks/furnace_top")
        private val sideTexture: ResourceLocation = new ResourceLocation(ModInfo.MOD_ID.toLowerCase, "blocks/furnace_side")
        private val frontTexture: ResourceLocation = new ResourceLocation(ModInfo.MOD_ID.toLowerCase, "blocks/furnace_front_on")
        private val frameTexture: ResourceLocation = new ResourceLocation(ModInfo.MOD_ID.toLowerCase, "blocks/furnace_top")
    }

    class Model extends ModuleModel {
        def init {
            this.boxes.clear
            addBox(vec(1, 1, 1), vec(5, 5, 5)).setFace(NORTH, Model.sideTexture, UV.fill).setFace(EAST, Model.sideTexture, UV.fill).setFace(SOUTH, Model.frontTexture, UV.fill).setFace(WEST, Model.sideTexture, UV.fill).setFace(UP, Model.topTexture, UV.fill).setFace(DOWN, Model.topTexture, UV.fill)
            addBox(vec(0, 0, 0), vec(1, 6, 1)).setFace(NORTH, Model.frameTexture, UV.auto(6)).setFace(EAST, Model.frameTexture, UV.auto(6)).setFace(SOUTH, Model.frameTexture, UV.auto(6)).setFace(WEST, Model.frameTexture, UV.auto(6)).setFace(UP, Model.frameTexture, UV.auto(6)).setFace(DOWN, Model.frameTexture, UV.auto(6))
            addBox(vec(0, 0, 5), vec(1, 6, 6)).setFace(NORTH, Model.frameTexture, UV.auto(6)).setFace(EAST, Model.frameTexture, UV.auto(6)).setFace(SOUTH, Model.frameTexture, UV.auto(6)).setFace(WEST, Model.frameTexture, UV.auto(6)).setFace(UP, Model.frameTexture, UV.auto(6)).setFace(DOWN, Model.frameTexture, UV.auto(6))
            addBox(vec(5, 0, 0), vec(6, 6, 1)).setFace(NORTH, Model.frameTexture, UV.auto(6)).setFace(EAST, Model.frameTexture, UV.auto(6)).setFace(SOUTH, Model.frameTexture, UV.auto(6)).setFace(WEST, Model.frameTexture, UV.auto(6)).setFace(UP, Model.frameTexture, UV.auto(6)).setFace(DOWN, Model.frameTexture, UV.auto(6))
            addBox(vec(5, 0, 5), vec(6, 6, 6)).setFace(NORTH, Model.frameTexture, UV.auto(6)).setFace(EAST, Model.frameTexture, UV.auto(6)).setFace(SOUTH, Model.frameTexture, UV.auto(6)).setFace(WEST, Model.frameTexture, UV.auto(6)).setFace(UP, Model.frameTexture, UV.auto(6)).setFace(DOWN, Model.frameTexture, UV.auto(6))
            addBox(vec(1, 0, 0), vec(5, 1, 1)).setFace(NORTH, Model.frameTexture, UV.auto(6)).setFace(EAST, Model.frameTexture, UV.auto(6)).setFace(SOUTH, Model.frameTexture, UV.auto(6)).setFace(WEST, Model.frameTexture, UV.auto(6)).setFace(UP, Model.frameTexture, UV.auto(6)).setFace(DOWN, Model.frameTexture, UV.auto(6))
            addBox(vec(1, 5, 0), vec(5, 6, 1)).setFace(NORTH, Model.frameTexture, UV.auto(6)).setFace(EAST, Model.frameTexture, UV.auto(6)).setFace(SOUTH, Model.frameTexture, UV.auto(6)).setFace(WEST, Model.frameTexture, UV.auto(6)).setFace(UP, Model.frameTexture, UV.auto(6)).setFace(DOWN, Model.frameTexture, UV.auto(6))
            addBox(vec(0, 0, 1), vec(1, 1, 5)).setFace(NORTH, Model.frameTexture, UV.auto(6)).setFace(EAST, Model.frameTexture, UV.auto(6)).setFace(SOUTH, Model.frameTexture, UV.auto(6)).setFace(WEST, Model.frameTexture, UV.auto(6)).setFace(UP, Model.frameTexture, UV.auto(6)).setFace(DOWN, Model.frameTexture, UV.auto(6))
            addBox(vec(0, 5, 1), vec(1, 6, 5)).setFace(NORTH, Model.frameTexture, UV.auto(6)).setFace(EAST, Model.frameTexture, UV.auto(6)).setFace(SOUTH, Model.frameTexture, UV.auto(6)).setFace(WEST, Model.frameTexture, UV.auto(6)).setFace(UP, Model.frameTexture, UV.auto(6)).setFace(DOWN, Model.frameTexture, UV.auto(6))
            addBox(vec(5, 0, 1), vec(6, 1, 5)).setFace(NORTH, Model.frameTexture, UV.auto(6)).setFace(EAST, Model.frameTexture, UV.auto(6)).setFace(SOUTH, Model.frameTexture, UV.auto(6)).setFace(WEST, Model.frameTexture, UV.auto(6)).setFace(UP, Model.frameTexture, UV.auto(6)).setFace(DOWN, Model.frameTexture, UV.auto(6))
            addBox(vec(5, 5, 1), vec(6, 6, 5)).setFace(NORTH, Model.frameTexture, UV.auto(6)).setFace(EAST, Model.frameTexture, UV.auto(6)).setFace(SOUTH, Model.frameTexture, UV.auto(6)).setFace(WEST, Model.frameTexture, UV.auto(6)).setFace(UP, Model.frameTexture, UV.auto(6)).setFace(DOWN, Model.frameTexture, UV.auto(6))
            addBox(vec(1, 0, 5), vec(5, 1, 6)).setFace(NORTH, Model.frameTexture, UV.auto(6)).setFace(EAST, Model.frameTexture, UV.auto(6)).setFace(SOUTH, Model.frameTexture, UV.auto(6)).setFace(WEST, Model.frameTexture, UV.auto(6)).setFace(UP, Model.frameTexture, UV.auto(6)).setFace(DOWN, Model.frameTexture, UV.auto(6))
            addBox(vec(1, 5, 5), vec(5, 6, 6)).setFace(NORTH, Model.frameTexture, UV.auto(6)).setFace(EAST, Model.frameTexture, UV.auto(6)).setFace(SOUTH, Model.frameTexture, UV.auto(6)).setFace(WEST, Model.frameTexture, UV.auto(6)).setFace(UP, Model.frameTexture, UV.auto(6)).setFace(DOWN, Model.frameTexture, UV.auto(6))
        }
    }
}

class FurnaceModule(val parentFrame: Nothing) extends Module("furnace", FurnaceModule.MODEL, parentFrame) {
}