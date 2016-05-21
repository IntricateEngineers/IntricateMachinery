package intricateengineers.intricatemachinery.core;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
        rendererPipeStatic = new ModelPipe();

        event.getModelRegistry().putObject(new ModelResourceLocation("charsetpipes:pipe", "multipart"), rendererPipeStatic);
        event.getModelRegistry().putObject(new ModelResourceLocation("charsetpipes:pipe", "inventory"), rendererPipeStatic);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(ModelPipe.PIPE_TEXTURE_LOC);
        rendererPipe.clearCache();
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
