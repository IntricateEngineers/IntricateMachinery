package mod.chiselsandbits.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IItemScrollWheel
{

	void scroll(
			EntityPlayer player,
			ItemStack stack,
			int dwheel );

}
