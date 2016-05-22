package mod.chiselsandbits.bitbag;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class TargetedTransferContainer extends Container
{

	@Override
	public boolean canInteractWith(
			final EntityPlayer playerIn )
	{
		return true;
	}

	public boolean doMergeItemStack(
			final ItemStack stack,
			final int startIndex,
			final int endIndex,
			final boolean reverseDirection )
	{
		return mergeItemStack( stack, startIndex, endIndex, reverseDirection );
	}

}
