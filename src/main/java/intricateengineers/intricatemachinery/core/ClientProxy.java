package intricateengineers.intricatemachinery.core;

import intricateengineers.intricatemachinery.api.IMModules;
import intricateengineers.intricatemachinery.common.block.FurnaceModule;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author topisani
 */
public class ClientProxy extends CommonProxy {


    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPostBake(ModelBakeEvent event) {
        event.getModelRegistry().putObject(new ModelResourceLocation(IMModules.FURNACE.getType(), "inventory"), IMModules.FURNACE.getModel().getBakedModel());
        event.getModelRegistry().putObject(new ModelResourceLocation(IMModules.FURNACE.getType(), "multipart"), IMModules.FURNACE.getModel().getBakedModel());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        FurnaceModule.ModelFurnace.topTexture = event.getMap().registerSprite(new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "furnace_top"));
        FurnaceModule.ModelFurnace.sideTexture = event.getMap().registerSprite(new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "furnace_side"));
        FurnaceModule.ModelFurnace.frontTexture = event.getMap().registerSprite(new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), "furnace_front_on"));
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}
