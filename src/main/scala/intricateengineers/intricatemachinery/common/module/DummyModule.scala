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
import intricateengineers.intricatemachinery.api.model.ModuleModel
import intricateengineers.intricatemachinery.api.module.{MachineryFrame, Module}
import intricateengineers.intricatemachinery.core.ModInfo
import net.minecraft.util.EnumFacing.{DOWN, UP, _}
import net.minecraft.util.ResourceLocation

class DummyModule(val parentFrame: MachineryFrame) extends {
  val model = DummyModel
  val name = new ResourceLocation(ModInfo.MOD_ID.toLowerCase, "dummy")
} with Module(parentFrame) {

}

object DummyModel extends ModuleModel {

  private val texture: ResourceLocation = new ResourceLocation(ModInfo.MOD_ID.toLowerCase, "blocks/dummy")

  define {
    |#|:(0, 0, 0)(8, 8, 8) {
      |*(texture, UV.fill)
    }
  }
}
