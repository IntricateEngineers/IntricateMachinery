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

package intricateengineers.intricatemachinery.api.module;

import javax.vecmath.Vector3f;

import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.MultipartHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModuleItem extends Item {

    public final Class<? extends Module> module;

    public ModuleItem(Class<? extends Module> module) {
        this.module = module;
    }
    
    /**
     * Called when a Block is right-clicked with this Item
     */
    @Override
    public final EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        for (IMultipart part : MultipartHelper.getPartContainer(worldIn, pos).getParts()) {
            if (part instanceof MachineryFrame) {
                MachineryFrame frame = (MachineryFrame) part;
                //TODO: Raytracing. Let that function know which module was hit where
                if (this.placeInFrame(frame, stack, playerIn, hand, facing, new Vector3f(hitX, hitY, hitZ))) return EnumActionResult.SUCCESS; 
            }
        }

        return EnumActionResult.PASS;
    }

    /**
     * Do whatever you need to do when this item is rightclicked on a frame
     * @return whether the action was successfull
     * TODO: Raytracing. This function needs to know which module was clicked on which side.
     */
    public boolean placeInFrame(MachineryFrame frame, ItemStack stack, EntityPlayer player, EnumHand hand, EnumFacing facing, Vector3f hit) {
        //TODO: Place at correct positions
        try {
            frame.addModule(module.newInstance());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
