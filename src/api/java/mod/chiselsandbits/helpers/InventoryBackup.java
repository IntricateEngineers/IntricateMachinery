package mod.chiselsandbits.helpers;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryBackup
{

	IInventory original;
	ItemStack[] slots;

	public InventoryBackup(
			final IInventory inventory )
	{
		original = inventory;
		slots = new ItemStack[original.getSizeInventory()];

		for ( int x = 0; x < slots.length; ++x )
		{
			slots[x] = original.getStackInSlot( x );

			if ( slots[x] != null )
			{
				slots[x] = slots[x].copy();
			}
		}
	}

	public void rollback()
	{
		for ( int x = 0; x < slots.length; ++x )
		{
			original.setInventorySlotContents( x, slots[x] );
		}
	}
}
