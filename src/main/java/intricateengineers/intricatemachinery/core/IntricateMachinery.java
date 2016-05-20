package intricateengineers.intricatemachinery.core;

import intricateengineers.intricatemachinery.event.EventManager;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;



@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.MOD_VERSION, dependencies = "required-after:Forge@[" + ModInfo.FORGE_DEP + ",)", acceptableRemoteVersions = "*")
public class IntricateMachinery {

    @Instance(ModiInfo.MOD_ID)
    public static IntricateMachinery instance;
    public static Configuration config;
    public static EventManager eventManager;

    @SidedProxy(serverSide = ModInfo.PROXY_COMMON, clientSide = ModInfo.PROXY_CLIENT)
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;

        config = new Configuration(new File(event.getModConfigurationDirectory(), ModInfo.MOD_ID + ".cfg"));
        eventManager = new EventManager();

        MinecraftForge.EVENT_BUS.register(eventManager);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        config.load();
        config.save();
    }
}
