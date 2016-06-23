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
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model._
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.property.IExtendedBlockState
import org.lwjgl.util.vector.Vector3f

import scala.collection.JavaConversions._

class BakedModelFrame extends IMBakedModel {
  final protected val MFQuads: java.util.List[BakedQuad] = new java.util.ArrayList[BakedQuad]

  override def initQuads(): Unit = {
    MFQuads.clear()
    for (box <- FrameModel.boxes) {
      for (face <- EnumFacing.values) {
        val vecs: (Vector3f, Vector3f) = box.faceVecs(face)
        if (vecs._1 != null) {
          val texture: TextureAtlasSprite = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(box.faces.get(face).get._1.toString)
          val partFace: BlockPartFace = new BlockPartFace(null, 0, "", box.faces.get(face).get._2)
          val mr: ModelRotation = ModelRotation.X0_Y0
          val blockPartRotation: BlockPartRotation = null
          val quad: BakedQuad = QuadHandler.FaceBakery.makeBakedQuad(vecs._1, vecs._2, partFace, texture, face, mr, blockPartRotation, true, true)
          MFQuads.add(quad)
        }
      }
    }
  }

  def initTextures(): Unit = {
    for (box <- FrameModel.boxes) {
      for (face <- EnumFacing.values) {
        if (box.faces.get(face) != (null, null)) {
          Minecraft.getMinecraft.getTextureMapBlocks.registerSprite(box.faces.get(face).get._1)
        }
      }
    }
  }

  @MethodsReturnNonnullByDefault
  def getQuads(@Nullable state: IBlockState, @Nullable side: EnumFacing, rand: Long): java.util.List[BakedQuad] = {
    // TODO: Move to MachineryFrame (since it accesses only fields from there anyway) + possibly make it a lambda
    if (side != null) {
      return new java.util.ArrayList[BakedQuad]
    }
    state match {
      case extendedState: IExtendedBlockState =>
        val frame: MachineryFrame = extendedState.getValue(FrameProperty)
        if (frame != null) {
          if (frame.shouldUpdateQuads) {
            frame.moduleQuads.addAll(MFQuads)
            frame.modules.foreach(module => frame.moduleQuads.addAll(module.model.quadHandler.quads(frame, module, rand)))
            frame.shouldUpdateQuads = false
          }
          return frame.moduleQuads
        }
      case _ =>
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