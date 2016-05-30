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

import intricateengineers.intricatemachinery.api.module.IMModule;
import intricateengineers.intricatemachinery.api.module.IMModuleItem;
import intricateengineers.intricatemachinery.api.module.MachineryFrame;
import intricateengineers.intricatemachinery.common.module.DummyModule;
import intricateengineers.intricatemachinery.common.module.FurnaceModule;
import intricateengineers.intricatemachinery.common.util.gui.IMCreativeTab;
import intricateengineers.intricatemachinery.core.ModInfo;
import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.MultipartRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author topisani
 */
public final class ModBlocks {

    public static final String unlocalizedPrefix = ModInfo.MOD_ID.toLowerCase() + ".";


    public static void init() {
        ItemMultiPart item = new ItemMultiPart() {
            @Override
            public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
                return new MachineryFrame();
            }
        };
        registerItem(item, "machinery_frame");
        MultipartRegistry.registerPart(MachineryFrame.class, "machinery_frame");

        registerModule("furnace", FurnaceModule.class);
        registerModule("dummy", DummyModule.class);
    }

    private static void registerModule(String name, Class<? extends IMModule> module) {
        ItemMultiPart item = new IMModuleItem(module);
        registerItem(item, name);

        MultipartRegistry.registerPart(module, name);
    }

    private static <T extends Item> T registerItem(
            final T item,
            final String name )
    {
        item.setCreativeTab( IMCreativeTab.INSTANCE );

        item.setUnlocalizedName( unlocalizedPrefix + name );
        item.setRegistryName( ModInfo.MOD_ID.toLowerCase(), name );

        GameRegistry.register( item );
        return item;
    }
}
