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

import intricateengineers.intricatemachinery.api.module.BlockModel.IMBakedModel
import intricateengineers.intricatemachinery.api.module.MachineryFrame
import mcp.MethodsReturnNonnullByDefault
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BlockPartFace
import net.minecraft.client.renderer.block.model.BlockPartRotation
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.block.model.ModelRotation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.property.IExtendedBlockState
import javax.annotation.Nullable
import org.apache.commons.lang3.tuple.Pair
import org.lwjgl.util.vector.Vector3f

import scala.collection.JavaConversions._

class BakedModelFrame extends IMBakedModel {
  final protected val quads: java.util.List[BakedQuad] = new java.util.ArrayList[BakedQuad]

  def initQuads {
    quads.clear()
    for (box <- MachineryFrame.MODEL.boxes) {
      for (face <- EnumFacing.values) {
        val vecs: (Vector3f, Vector3f) = box.faceVecs(face)
        if (vecs._1 != null) {
          val texture: TextureAtlasSprite = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(box.faces.get(face).get._1.toString)
          val partFace: BlockPartFace = new BlockPartFace(null, 0, "", box.faces.get(face).get._2)
          val mr: ModelRotation = ModelRotation.X0_Y0
          val blockPartRotation: BlockPartRotation = null
          val quad: BakedQuad = QuadHandler.FaceBakery.makeBakedQuad(vecs._1, vecs._2, partFace, texture, face, mr, blockPartRotation, true, true)
          quads.add(quad)
        }
      }
    }
  }

  def initTextures {
    for (box <- MachineryFrame.MODEL.boxes) {
      for (face <- EnumFacing.values) {
        if (box.faces.get(face) != null) {
          Minecraft.getMinecraft.getTextureMapBlocks.registerSprite(box.faces.get(face).get._1)
        }
      }
    }
  }

  @MethodsReturnNonnullByDefault def getQuads(@Nullable state: IBlockState, @Nullable side: EnumFacing, rand: Long): java.util.List[BakedQuad] = {
    if (side != null) {
      return new java.util.ArrayList[BakedQuad]
    }
    if (state.isInstanceOf[IExtendedBlockState]) {
      val frame: MachineryFrame = state.asInstanceOf[IExtendedBlockState].getValue(MachineryFrame.PROPERTY)
      if (frame != null) {
        val quads1: java.util.List[BakedQuad] = new java.util.ArrayList[BakedQuad]
        quads1.addAll(quads)
        frame.modules.foreach(module => quads1.addAll(module.model.quadHandler.quads(frame, module, rand)))
        return quads1
      }
      return quads
    }
    else return new java.util.ArrayList[BakedQuad]
  }

  def isAmbientOcclusion: Boolean = false

  def isGui3d: Boolean = false

  def isBuiltInRenderer: Boolean = false

  def getParticleTexture: TextureAtlasSprite = null

  def getItemCameraTransforms: ItemCameraTransforms = null

  def getOverrides: ItemOverrideList = null
}