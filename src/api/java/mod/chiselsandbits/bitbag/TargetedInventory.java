package mod.chiselsandbits.bitbag;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class TargetedInventory implements IInventory
{

	private IInventory src;

	public TargetedInventory()
	{
		src = null;
	}

	public void setInventory(
			final IInventory a )
	{
		src = a;
	}

	@Override
	public String getName()
	{
		return src.getName();
	}

	@Override
	public boolean hasCustomName()
	{
		return src.hasCustomName();
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return src.getDisplayName();
	}

	@Override
	public int getSizeInventory()
	{
		return src.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(
			final int index )
	{
		return src.getStackInSlot( index );
	}

	@Override
	public ItemStack decrStackSize(
			final int index,
			final int count )
	{
		return src.decrStackSize( index, count );
	}

	@Override
	public ItemStack removeStackFromSlot(
			final int index )
	{
		return src.removeStackFromSlot( index );
	}

	@Override
	public void setInventorySlotContents(
			final int index,
			final ItemStack stack )
	{
		src.setInventorySlotContents( index, stack );
	}

	@Override
	public int getInventoryStackLimit()
	{
		return src.getInventoryStackLimit();
	}

	@Override
	public void markDirty()
	{
		src.markDirty();
	}

	@Override
	public boolean isUseableByPlayer(
			final EntityPlayer player )
	{
		return src.isUseableByPlayer( player );
	}

	@Override
	public void openInventory(
			final EntityPlayer player )
	{
		src.openInventory( player );
	}

	@Override
	public void closeInventory(
			final EntityPlayer player )
	{
		src.closeInventory( player );
	}

	@Override
	public boolean isItemValidForSlot(
			final int index,
			final ItemStack stack )
	{
		return src.isItemValidForSlot( index, stack );
	}

	@Override
	public int getField(
			final int id )
	{
		return src.getField( id );
	}

	@Override
	public void setField(
			final int id,
			final int value )
	{
		src.setField( id, value );
	}

	@Override
	public int getFieldCount()
	{
		return src.getFieldCount();
	}

	@Override
	public void clear()
	{
		src.clear();
	}

}
