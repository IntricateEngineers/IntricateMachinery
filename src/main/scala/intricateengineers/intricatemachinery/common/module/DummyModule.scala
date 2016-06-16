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
import net.minecraft.util.EnumFacing.DOWN
import net.minecraft.util.EnumFacing.UP

object DummyModule {
    var MODEL: DummyModule.Model = new DummyModule.Model

    object Model {
        private val texture: ResourceLocation = new ResourceLocation(ModInfo.MOD_ID.toLowerCase, "blocks/dummy")
    }

    class Model extends ModuleModel {
        def init {
            this.boxes.clear
            += (vec(0, 0, 0), vec(8, 8, 8)).setFace(NORTH, Model.texture, UV.fill).setFace(EAST, Model.texture, UV.fill).setFace(SOUTH, Model.texture, UV.fill).setFace(WEST, Model.texture, UV.fill).setFace(UP, Model.texture, UV.fill).setFace(DOWN, Model.texture, UV.fill)
        }
    }
}

class DummyModule(val parentFrame: Nothing) extends {
    val model = DummyModule.MODEL
    val name = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "dummy")
} with Module(parentFrame) {

}