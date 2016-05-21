package intricateengineers.intricatemachinery.core;

import intricateengineers.intricatemachinery.common.event.EventManager;
import intricateengineers.intricatemachinery.common.init.ModBlocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;


//@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.MOD_VERSION, dependencies = "required-after:Forge@[" + ModInfo.FORGE_DEP + ",)", acceptableRemoteVersions = "*")
@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.MOD_VERSION, dependencies = "required-after:Forge@[" + "12.17.0.1909" + ",)", acceptableRemoteVersions = "*")
public class IntricateMachinery {

    @Instance(ModInfo.MOD_ID)
    public static IntricateMachinery instance;
    public static Configuration config;
    public static EventManager eventManager;

    @SidedProxy(serverSide = ModInfo.PROXY_COMMON, clientSide = ModInfo.PROXY_CLIENT)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        config = new Configuration(new File(event.getModConfigurationDirectory(), ModInfo.MOD_ID + ".cfg"));
        ModBlocks.init();
        eventManager = new EventManager();

        MinecraftForge.EVENT_BUS.register(eventManager);
        MinecraftForge.EVENT_BUS.register(proxy);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        config.load();
        config.save();
    }
}
