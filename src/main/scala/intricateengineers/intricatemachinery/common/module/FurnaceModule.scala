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
import net.minecraft.util.EnumFacing._
import net.minecraft.util.ResourceLocation


class FurnaceModule(val parentFrame: MachineryFrame) extends {
  val model = FurnaceModel
  val name = new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "furnace")
} with Module(parentFrame) {

}

object FurnaceModel extends ModuleModel {

  val topTexture = new ResourceLocation(ModInfo.MOD_ID.toLowerCase, "blocks/furnace_top")
  val sideTexture = new ResourceLocation(ModInfo.MOD_ID.toLowerCase, "blocks/furnace_side")
  val frontTexture = new ResourceLocation(ModInfo.MOD_ID.toLowerCase, "blocks/furnace_front_on")
  val frameTexture = new ResourceLocation(ModInfo.MOD_ID.toLowerCase, "blocks/furnace_top")

  define {
    |#|:(1, 1, 1)(5, 5, 5) {
      |-(NORTH, sideTexture, UV.fill)
      |-(EAST, sideTexture, UV.fill)
      |-(SOUTH, frontTexture, UV.fill)
      |-(WEST, sideTexture, UV.fill)
      |-(UP, topTexture, UV.fill)
      |-(DOWN, topTexture, UV.fill)
    }
    |#|:(0, 0, 0)(1, 6, 1) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(0, 0, 5)(1, 6, 6) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(5, 0, 0)(6, 6, 1) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(5, 0, 5)(6, 6, 6) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(1, 0, 0)(5, 1, 1) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(1, 5, 0)(5, 6, 1) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(0, 0, 1)(1, 1, 5) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(0, 5, 1)(1, 6, 5) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(5, 0, 1)(6, 1, 5) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(5, 5, 1)(6, 6, 5) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(1, 0, 5)(5, 1, 6) {
      |*(frameTexture, UV.auto(6))
    }
    |#|:(1, 5, 5)(5, 6, 6) {
      |*(frameTexture, UV.auto(6))
    }
  }
}
