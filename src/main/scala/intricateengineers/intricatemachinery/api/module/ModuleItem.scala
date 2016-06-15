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
package intricateengineers.intricatemachinery.api.module

import javax.vecmath.Vector3f
import mcmultipart.multipart.IMultipart
import mcmultipart.multipart.MultipartHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class ModuleItem(val name: ResourceLocation) extends Item {
  override final def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult = {
    import scala.collection.JavaConversions._
    for (part <- MultipartHelper.getPartContainer(worldIn, pos).getParts) {
      if (part.isInstanceOf[MachineryFrame]) {
        val frame: MachineryFrame = part.asInstanceOf[MachineryFrame]
        if (this.placeInFrame(frame, stack, playerIn, hand, facing, new Vector3f(hitX, hitY, hitZ))) return EnumActionResult.SUCCESS
      }
    }
    return EnumActionResult.PASS
  }

  def placeInFrame(frame: MachineryFrame, stack: ItemStack, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hit: Vector3f): Boolean = {
    try {
      frame.addModule(Modules.newModule(name, frame))
      return true
    }
    catch {
      case e: Exception => {
        e.printStackTrace
      }
    }
    return false
  }
}