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

package intricateengineers.intricatemachinery.core;

import intricateengineers.intricatemachinery.client.event.DebugRenderHandler;
import intricateengineers.intricatemachinery.common.module.DummyModule;
import intricateengineers.intricatemachinery.common.module.FurnaceModule;
import mcmultipart.client.multipart.MultipartRegistryClient;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author topisani
 */
@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    public static IBakedModel EMPTY_MODEL = new IBakedModel() {
        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
            return new ArrayList<>();
        }

        @Override
        public boolean isAmbientOcclusion() {
            return false;
        }

        @Override
        public boolean isGui3d() {
            return false;
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleTexture() {
            return null;
        }

        @Override
        public ItemCameraTransforms getItemCameraTransforms() {
            return null;
        }

        @Override
        public ItemOverrideList getOverrides() {
            return null;
        }
    };

    @SubscribeEvent
    public void onPostBake(ModelBakeEvent event) {
        event.getModelRegistry().putObject(new ModelResourceLocation(MachineryFrame.NAME, "inventory"), EMPTY_MODEL);
        event.getModelRegistry().putObject(new ModelResourceLocation(MachineryFrame.NAME, "multipart"), EMPTY_MODEL);
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        FurnaceModule.MODEL.getQuadHandler().initTextures();
        FurnaceModule.MODEL.init();
        DummyModule.MODEL.getQuadHandler().initTextures();
        DummyModule.MODEL.init();
        MachineryFrame.MODEL.getBakedModel().initTextures();
        MachineryFrame.MODEL.init();
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Post event) {
        FurnaceModule.MODEL.getQuadHandler().initQuads();
        DummyModule.MODEL.getQuadHandler().initQuads();
        MachineryFrame.MODEL.getBakedModel().initQuads();
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Override
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(DebugRenderHandler.instance);
        MultipartRegistryClient.bindMultipartSpecialRenderer(MachineryFrame.class, new FrameRenderer());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
    }
}
