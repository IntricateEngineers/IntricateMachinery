package intricateengineers.intricatemachinery.common.util

import intricateengineers.intricatemachinery.core.ModInfo
import net.minecraft.util.ResourceLocation


object IMRL {
  def apply(st: String) = {
    new ResourceLocation(ModInfo.mod_id, st)
  }
}
