package intricateengineers.intricatemachinery.core;

import intricateengineers.intricatemachinery.client.event.DebugRenderHandler;
import intricateengineers.intricatemachinery.common.block.FurnaceModule;
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
        event.getModelRegistry().putObject(new ModelResourceLocation(ModInfo.MOD_ID + ":furnace", null), FurnaceModule.MODEL.getBakedModel());
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        FurnaceModule.MODEL.getBakedModel().initTextures();
        FurnaceModule.MODEL.init();
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Post event) {
        FurnaceModule.MODEL.getBakedModel().initQuads();
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
