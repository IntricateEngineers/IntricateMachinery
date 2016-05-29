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
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author topisani
 */
@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {


    @SubscribeEvent
    public void onPostBake(ModelBakeEvent event) {
        event.getModelRegistry().putObject(new ModelResourceLocation(ModInfo.MOD_ID + ":furnace", "inventory"), FurnaceModule.MODEL.getBakedModel());
        event.getModelRegistry().putObject(new ModelResourceLocation(ModInfo.MOD_ID + ":furnace", "multipart"), FurnaceModule.MODEL.getBakedModel());
        event.getModelRegistry().putObject(new ModelResourceLocation(ModInfo.MOD_ID + ":dummy", "inventory"), DummyModule.MODEL.getBakedModel());
        event.getModelRegistry().putObject(new ModelResourceLocation(ModInfo.MOD_ID + ":dummy", "multipart"), DummyModule.MODEL.getBakedModel());
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        FurnaceModule.MODEL.getBakedModel().initTextures();
        FurnaceModule.MODEL.init();
        DummyModule.MODEL.getBakedModel().initTextures();
        DummyModule.MODEL.init();
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Post event) {
        FurnaceModule.MODEL.getBakedModel().initQuads();
        DummyModule.MODEL.getBakedModel().initQuads();
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Override
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(DebugRenderHandler.instance);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
    }
}
