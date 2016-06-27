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
package intricateengineers.intricatemachinery.api.client

import javax.annotation.Nullable

import intricateengineers.intricatemachinery.api.model.IMBakedModel
import intricateengineers.intricatemachinery.api.module.{FrameModel, FrameProperty, MachineryFrame}
import mcp.MethodsReturnNonnullByDefault
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model._
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.property.IExtendedBlockState

object BakedModelFrame extends IMBakedModel {

  def initTextures(): Unit = {
    FrameModel.boxes.foreach(_.initTextures())
  }

  @MethodsReturnNonnullByDefault
  def getQuads(@Nullable state: IBlockState, @Nullable side: EnumFacing, rand: Long): java.util.List[BakedQuad] = {
    if (side != null) {
      return new java.util.ArrayList[BakedQuad]
    }
    state match {
      case extendedState: IExtendedBlockState =>
        val frame: MachineryFrame = extendedState.getValue(FrameProperty)
        if (frame != null) {
          return frame.quadCache()
        }
    }
    new java.util.ArrayList[BakedQuad]
  }

  def isAmbientOcclusion: Boolean = false

  def isGui3d: Boolean = false

  def isBuiltInRenderer: Boolean = false

  def getParticleTexture: TextureAtlasSprite = null

  def getItemCameraTransforms: ItemCameraTransforms = null

  def getOverrides: ItemOverrideList = null
}
