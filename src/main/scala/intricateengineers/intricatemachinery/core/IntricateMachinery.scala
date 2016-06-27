package intricateengineers.intricatemachinery.core

import java.io.File

import intricateengineers.intricatemachinery.common.event.EventManager
import intricateengineers.intricatemachinery.common.init.{ModBlocks, ModItems, ModModules}
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Mod.{EventHandler, Instance}
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}


@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.MOD_VERSION, dependencies =
  IntricateMachinery.DEPENDENCIES, modLanguage = "scala")
object IntricateMachinery {

  final val DEPENDENCIES = "required-after:Forge@[" + "12.17.0.1968" + ",);after:mcmultipart@[1.0.8,)"

  @Instance(ModInfo.MOD_NAME)
  var instance = this

  @SidedProxy(serverSide = ModInfo.PROXY_COMMON, clientSide = ModInfo.PROXY_CLIENT)
  var proxy: CommonProxy = null

  @EventHandler
  def preInit(event: FMLPreInitializationEvent) {
    ConfigIM.config = new Configuration(new File(event.getModConfigurationDirectory, ModInfo.MOD_ID + ".cfg"))
    ModBlocks.init()
    ModModules.init()
    ModItems.init()
    println(DEPENDENCIES)
    MinecraftForge.EVENT_BUS.register(new EventManager)
    MinecraftForge.EVENT_BUS.register(proxy)
    proxy.preInit(event)
  }

  @EventHandler
  def init(event: FMLInitializationEvent) {
    proxy.init(event)
  }

  @EventHandler
  def postInit(event: FMLPostInitializationEvent) {
    proxy.postInit(event)
  }

  object ConfigIM {
    var config: Configuration = null
  }

}
