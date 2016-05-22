package mod.chiselsandbits.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class PlayerCopiedInventory implements IInventory
{

	InventoryPlayer logicBase;
	ItemStack[] slots;

	public PlayerCopiedInventory(
			final InventoryPlayer original )
	{
		logicBase = original;
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

	@Override
	public String getName()
	{
		return "NULL";
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return null;
	}

	@Override
	public int getSizeInventory()
	{
		return slots.length;
	}

	@Override
	public ItemStack getStackInSlot(
			final int index )
	{
		return slots[index];
	}

	@Override
	public ItemStack decrStackSize(
			final int index,
			final int count )
	{
		if ( slots[index] != null )
		{
			if ( slots[index].stackSize <= count )
			{
				return removeStackFromSlot( index );
			}
			else
			{
				return slots[index].splitStack( count );
			}
		}

		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(
			final int index )
	{
		final ItemStack r = slots[index];
		slots[index] = null;
		return r;
	}

	@Override
	public void setInventorySlotContents(
			final int index,
			final ItemStack stack )
	{
		slots[index] = stack;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return logicBase.getInventoryStackLimit();
	}

	@Override
	public void markDirty()
	{
	}

	@Override
	public boolean isUseableByPlayer(
			final EntityPlayer player )
	{
		return true;
	}

	@Override
	public void openInventory(
			final EntityPlayer player )
	{
	}

	@Override
	public void closeInventory(
			final EntityPlayer player )
	{
	}

	@Override
	public boolean isItemValidForSlot(
			final int index,
			final ItemStack stack )
	{
		return logicBase.isItemValidForSlot( index, stack );
	}

	@Override
	public int getField(
			final int id )
	{
		return 0;
	}

	@Override
	public void setField(
			final int id,
			final int value )
	{
	}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		for ( int x = 0; x < slots.length; ++x )
		{
			slots[x] = null;
		}
	}
}
