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
package intricateengineers.intricatemachinery.core

import intricateengineers.intricatemachinery.api.module.MachineryFrame
import intricateengineers.intricatemachinery.client.event.DebugRenderHandler
import intricateengineers.intricatemachinery.common.module.DummyModule
import intricateengineers.intricatemachinery.common.module.FurnaceModule
import mcmultipart.client.multipart.MultipartRegistryClient
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model._
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import javax.annotation.Nullable
import java.util.ArrayList
import java.util.List

@SuppressWarnings(Array("unused")) object ClientProxy {
    var EMPTY_MODEL: IBakedModel = new IBakedModel() {
        def getQuads(@Nullable state: IBlockState, @Nullable side: EnumFacing, rand: Long): java.util.List[BakedQuad] = {
            return new java.util.ArrayList[BakedQuad]
        }

        def isAmbientOcclusion: Boolean = false
        def isGui3d: Boolean = false
        def isBuiltInRenderer: Boolean = false
        def getParticleTexture: TextureAtlasSprite = null
        def getItemCameraTransforms: ItemCameraTransforms = null
        def getOverrides: ItemOverrideList = null
    }
}

@SuppressWarnings(Array("unused")) class ClientProxy extends CommonProxy {
    @SubscribeEvent
    def onPostBake(event: ModelBakeEvent) {
        event.getModelRegistry.putObject(new ModelResourceLocation(MachineryFrame.NAME, "inventory"), ClientProxy.EMPTY_MODEL)
        event.getModelRegistry.putObject(new ModelResourceLocation(MachineryFrame.NAME, "multipart"), ClientProxy.EMPTY_MODEL)
    }

    @SubscribeEvent
    def onTextureStitch(event: TextureStitchEvent.Pre) {
        FurnaceModule.MODEL.getQuadHandler.initTextures
        FurnaceModule.MODEL.init
        DummyModule.MODEL.getQuadHandler.initTextures
        DummyModule.MODEL.init
        MachineryFrame.MODEL.getBakedModel.initTextures
        MachineryFrame.MODEL.init
    }

    @SubscribeEvent
    def onTextureStitch(event: TextureStitchEvent.Post) {
        FurnaceModule.MODEL.getQuadHandler.initQuads
        DummyModule.MODEL.getQuadHandler.initQuads
        MachineryFrame.MODEL.getBakedModel.initQuads
    }

    override def preInit(event: FMLPreInitializationEvent) {
    }

    override def init(event: FMLInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(DebugRenderHandler.instance)
        MultipartRegistryClient.bindMultipartSpecialRenderer(classOf[MachineryFrame], new Nothing)
    }

    override def postInit(event: FMLPostInitializationEvent) {
    }
}