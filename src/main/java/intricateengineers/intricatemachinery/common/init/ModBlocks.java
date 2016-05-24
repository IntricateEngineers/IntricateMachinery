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

package intricateengineers.intricatemachinery.common.init;

import intricateengineers.intricatemachinery.api.module.IMModuleItem;
import intricateengineers.intricatemachinery.common.block.FurnaceModule;
import intricateengineers.intricatemachinery.common.util.gui.IMCreativeTab;
import intricateengineers.intricatemachinery.core.ModInfo;
import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.MultipartRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author topisani
 */
public final class ModBlocks {

    public static void init() {
        registerModule("furnace", FurnaceModule.class);
    }

    private static void registerModule(String name, Class<? extends FurnaceModule> module) {
        ItemMultiPart item = new IMModuleItem(module);
        item.setUnlocalizedName(name);
        item.setCreativeTab(IMCreativeTab.INSTANCE);
        GameRegistry.<Item>register(item, new ResourceLocation(ModInfo.MOD_ID.toLowerCase(), name));
        MultipartRegistry.registerPart(module, name);
    }
}
