/*
 * Copyright (c) 2016 IntricateEngineers
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import intricateengineers.intricatemachinery.common._
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.Mod.Instance
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import java.io.File

import intricateengineers.intricatemachinery.common.event.EventManager
import intricateengineers.intricatemachinery.common.init.ModBlocks


@SuppressWarnings("unused")
@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.MOD_VERSION, dependencies = IntricateMachinery.DEPENDENCIES, acceptableRemoteVersions = "*", modLanguage = "scala")
object IntricateMachinery {

    val DEPENDENCIES = "required-after:Forge@["
            + net.minecraftforge.common.ForgeVersion.majorVersion + '.'
            + net.minecraftforge.common.ForgeVersion.minorVersion + '.'
            + net.minecraftforge.common.ForgeVersion.revisionVersion + '.'
            + net.minecraftforge.common.ForgeVersion.buildVersion + ",)after:mcmultipart"

    val config : Configuration

    @SidedProxy(serverSide = ModInfo.PROXY_COMMON, clientSide = ModInfo.PROXY_CLIENT)
    val proxy : CommonProxy

    @EventHandler
    def preInit(event : FMLPreInitializationEvent) = {
        config = new Configuration(new File(event.getModConfigurationDirectory(), ModInfo.MOD_ID + ".cfg"))
        ModBlocks.init

        MinecraftForge.EVENT_BUS.register(new EventManager)
        MinecraftForge.EVENT_BUS.register(proxy)
        proxy.preInit(event)
    }

    @EventHandler
    def init(event : FMLInitializationEvent) {
        proxy.init(event)
    }


    @EventHandler
    def postInit(event : FMLPostInitializationEvent) {
        proxy.postInit(event)
    }


}
