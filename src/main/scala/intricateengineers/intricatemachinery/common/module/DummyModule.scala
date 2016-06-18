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
import intricateengineers.intricatemachinery.api.module.Module
import intricateengineers.intricatemachinery.api.module.ModuleModel
import intricateengineers.intricatemachinery.core.ModInfo
import net.minecraft.util.ResourceLocation
import net.minecraft.util.EnumFacing._
import net.minecraft.util.EnumFacing.DOWN
import net.minecraft.util.EnumFacing.UP

object DummyModule {
  val Name = new ResourceLocation(ModInfo.MOD_ID.toLowerCase, "dummy")
}

class DummyModule(val parentFrame: Nothing) extends {
  val model = DummyModel
  val name = DummyModule.Name
} with Module(parentFrame) {

}


object DummyModel extends ModuleModel {

  private val texture: ResourceLocation = new ResourceLocation(ModInfo.MOD_ID.toLowerCase, "blocks/dummy")

  def init {
    this.boxes.clear
    +=((0, 0, 0), (8, 8, 8))
      .face(NORTH, texture, UV.fill)
      .face(EAST, texture, UV.fill)
      .face(SOUTH, texture, UV.fill)
      .face(WEST, texture, UV.fill)
      .face(UP, texture, UV.fill)
      .face(DOWN, texture, UV.fill)
  }
}
