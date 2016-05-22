package mod.chiselsandbits.bitbag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.LocalStrings;
import mod.chiselsandbits.items.ItemBitBag;
import mod.chiselsandbits.items.ItemChiseledBit;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

public class BagInventory implements IInventory
{

	// internal storage, the capability.
	BagStorage inv;

	// tmp storage, the IInventory
	ItemStack[] stackSlots;

	public BagInventory(
			final ItemStack is )
	{
		inv = (BagStorage) is.getCapability( CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null );
		stackSlots = new ItemStack[BagStorage.BAG_STORAGE_SLOTS];
	}

	public ItemStack getItemStack()
	{
		return inv.stack;
	}

	@Override
	public String getName()
	{
		return null;
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
		return stackSlots.length;
	}

	@Override
	public ItemStack getStackInSlot(
			final int index )
	{
		final int qty = inv.contents[ItemBitBag.INTS_PER_BIT_TYPE * index + ItemBitBag.OFFSET_QUANTITY];
		final int id = inv.contents[ItemBitBag.INTS_PER_BIT_TYPE * index + ItemBitBag.OFFSET_STATE_ID];

		if ( stackSlots[index] != null )
		{
			stackSlots[index].stackSize = qty;
			return stackSlots[index];
		}

		if ( qty == 0 || id == 0 )
		{
			return null;
		}

		return stackSlots[index] = ItemChiseledBit.createStack( id, qty, false );
	}

	@Override
	public ItemStack decrStackSize(
			final int index,
			int count )
	{
		final int qty = inv.contents[ItemBitBag.INTS_PER_BIT_TYPE * index + ItemBitBag.OFFSET_QUANTITY];
		final int id = inv.contents[ItemBitBag.INTS_PER_BIT_TYPE * index + ItemBitBag.OFFSET_STATE_ID];

		if ( qty == 0 || id == 0 )
		{
			return null;
		}

		if ( count > qty )
		{
			count = qty;
		}

		inv.contents[ItemBitBag.INTS_PER_BIT_TYPE * index + ItemBitBag.OFFSET_QUANTITY] -= count;
		inv.onChange();

		if ( stackSlots[index] != null )
		{
			stackSlots[index].stackSize -= count;
		}

		return ItemChiseledBit.createStack( id, count, false );
	}

	@Override
	public ItemStack removeStackFromSlot(
			final int index )
	{
		return null;
	}

	@Override
	public void setInventorySlotContents(
			final int index,
			final ItemStack stack )
	{
		stackSlots[index] = null;

		if ( stack != null && stack.getItem() instanceof ItemChiseledBit )
		{
			inv.contents[ItemBitBag.INTS_PER_BIT_TYPE * index + ItemBitBag.OFFSET_QUANTITY] = stack.stackSize;
			inv.contents[ItemBitBag.INTS_PER_BIT_TYPE * index + ItemBitBag.OFFSET_STATE_ID] = ItemChiseledBit.getStackState( stack );
		}
		else
		{
			inv.contents[ItemBitBag.INTS_PER_BIT_TYPE * index + ItemBitBag.OFFSET_QUANTITY] = 0;
			inv.contents[ItemBitBag.INTS_PER_BIT_TYPE * index + ItemBitBag.OFFSET_STATE_ID] = 0;
		}

		inv.onChange();
	}

	@Override
	public int getInventoryStackLimit()
	{
		return ChiselsAndBits.getConfig().bagStackSize;
	}

	@Override
	public void markDirty()
	{
		for ( int x = 0; x < getSizeInventory(); x++ )
		{
			if ( stackSlots[x] != null )
			{
				inv.contents[ItemBitBag.INTS_PER_BIT_TYPE * x + ItemBitBag.OFFSET_QUANTITY] = stackSlots[x].stackSize;
				stackSlots[x] = null;
				inv.onChange();
			}
		}
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
		return stack != null && stack.getItem() instanceof ItemChiseledBit;
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
		for ( int x = 0; x < inv.contents.length; ++x )
		{
			inv.contents[x] = 0;
		}

		for ( int x = 0; x < stackSlots.length; ++x )
		{
			stackSlots[x] = null;
		}

		inv.onChange();
	}

	public void restockItem(
			final ItemStack target )
	{
		for ( int x = getSizeInventory() - 1; x >= 0; x-- )
		{
			final ItemStack is = getStackInSlot( x );
			if ( is != null && is.getItem() == target.getItem() && ItemChiseledBit.sameBit( target, ItemChiseledBit.getStackState( is ) ) )
			{
				target.stackSize += is.stackSize;
				final int total = target.stackSize;
				target.stackSize = Math.min( is.getMaxStackSize(), target.stackSize );
				final int overage = total - target.stackSize;

				if ( overage > 0 )
				{
					is.stackSize = overage;
				}
				else
				{
					setInventorySlotContents( x, null );
				}

				markDirty();
			}
		}
	}

	public ItemStack insertItem(
			final ItemStack which )
	{
		for ( int x = 0; x < getSizeInventory(); x++ )
		{
			final ItemStack is = getStackInSlot( x );
			if ( is != null && ItemChiseledBit.getStackState( which ) == ItemChiseledBit.getStackState( is ) )
			{
				is.stackSize += which.stackSize;
				final int total = is.stackSize;
				is.stackSize = Math.min( getInventoryStackLimit(), is.stackSize );
				final int overage = total - is.stackSize;
				if ( overage > 0 )
				{
					which.stackSize = overage;
					markDirty();
				}
				else
				{
					markDirty();
					return null;
				}
			}
			else if ( is == null )
			{
				setInventorySlotContents( x, which );
				markDirty();
				return null;
			}
		}

		return which;
	}

	public int extractBit(
			final int bitMeta,
			int total )
	{
		int used = 0;

		for ( int index = stackSlots.length - 1; index >= 0; index-- )
		{
			final int qty_idx = ItemBitBag.INTS_PER_BIT_TYPE * index + ItemBitBag.OFFSET_QUANTITY;

			final int qty = inv.contents[qty_idx];
			final int id = inv.contents[ItemBitBag.INTS_PER_BIT_TYPE * index + ItemBitBag.OFFSET_STATE_ID];

			if ( id == bitMeta && qty > 0 )
			{
				inv.contents[qty_idx] -= total;

				if ( inv.contents[qty_idx] < 0 )
				{
					inv.contents[qty_idx] = 0;
				}

				inv.onChange();

				final int diff = qty - inv.contents[qty_idx];
				used += diff;
				total -= diff;

				if ( 0 == total )
				{
					return used;
				}
			}
		}

		return used;
	}

	@SideOnly( Side.CLIENT )
	public List<String> listContents(
			final List<String> details )
	{
		final TreeMap<String, Integer> contents = new TreeMap<String, Integer>();

		for ( int x = 0; x < getSizeInventory(); x++ )
		{
			final ItemStack is = getStackInSlot( x );

			if ( is != null )
			{
				final IBlockState state = Block.getStateById( ItemChiseledBit.getStackState( is ) );
				if ( state == null )
				{
					continue;
				}

				final Block blk = state.getBlock();
				if ( blk == null )
				{
					continue;
				}

				final Item what = Item.getItemFromBlock( blk );
				if ( what == null )
				{
					continue;
				}

				final String name = what.getItemStackDisplayName( new ItemStack( what, 1, blk.getMetaFromState( state ) ) );

				Integer count = contents.get( name );
				if ( count == null )
				{
					count = is.stackSize;
				}
				else
				{
					count += is.stackSize;
				}

				contents.put( name, count );
			}
		}

		if ( contents.isEmpty() )
		{
			details.add( LocalStrings.Empty.getLocal() );
		}

		final List<Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>();
		list.addAll( contents.entrySet() );

		Collections.sort( list, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(
					final Entry<String, Integer> o1,
					final Entry<String, Integer> o2 )
			{
				final int y = o1.getValue();
				final int x = o2.getValue();

				return x < y ? -1 : x == y ? 0 : 1;
			}

		} );

		for ( final Entry<String, Integer> e : list )
		{
			details.add( new StringBuilder().append( e.getValue() ).append( ' ' ).append( e.getKey() ).toString() );
		}

		return details;
	}
}
