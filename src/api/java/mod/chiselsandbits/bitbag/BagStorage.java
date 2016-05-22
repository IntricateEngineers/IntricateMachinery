package mod.chiselsandbits.bitbag;

import java.util.Arrays;

import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.IBitBag;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.ItemType;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.Log;
import mod.chiselsandbits.items.ItemBitBag;
import mod.chiselsandbits.items.ItemChiseledBit;
import net.minecraft.item.ItemStack;

public class BagStorage implements IBitBag
{

	public static final int BAG_STORAGE_SLOTS = 63;

	protected ItemStack stack;
	protected int[] contents;

	protected void setStorage(
			final int[] source )
	{
		contents = source;
	}

	public void onChange()
	{
		// noop at the moment.
	}

	@Override
	public boolean equals(
			final Object obj )
	{
		if ( obj instanceof BagStorage )
		{
			return Arrays.equals( contents, ( (BagStorage) obj ).contents );
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode( contents );
	}

	@Override
	public int getSlots()
	{
		return BAG_STORAGE_SLOTS;
	}

	@Override
	public ItemStack getStackInSlot(
			final int slot )
	{
		if ( slot < BAG_STORAGE_SLOTS )
		{
			final int slotQty = contents[ItemBitBag.INTS_PER_BIT_TYPE * slot + ItemBitBag.OFFSET_QUANTITY];
			final int slotId = contents[ItemBitBag.INTS_PER_BIT_TYPE * slot + ItemBitBag.OFFSET_STATE_ID];

			if ( slotId != 0 && slotQty > 0 )
			{
				return ItemChiseledBit.createStack( slotId, slotQty, false );
			}
		}

		return null;
	}

	@Override
	public ItemStack insertItem(
			final int slot,
			final ItemStack stack,
			final boolean simulate )
	{
		if ( slot >= 0 && slot < BAG_STORAGE_SLOTS && stack != null )
		{
			final int indexId = ItemBitBag.INTS_PER_BIT_TYPE * slot + ItemBitBag.OFFSET_STATE_ID;
			final int indexQty = ItemBitBag.INTS_PER_BIT_TYPE * slot + ItemBitBag.OFFSET_QUANTITY;

			final int slotId = contents[indexId];
			final int slotQty = slotId == 0 ? 0 : contents[indexQty];

			final ItemType type = ChiselsAndBits.getApi().getItemType( stack );
			if ( type == ItemType.CHISLED_BIT )
			{
				try
				{
					final IBitBrush brush = ChiselsAndBits.getApi().createBrush( stack );
					if ( brush.getStateID() == slotId || slotId == 0 )
					{
						int newTotal = slotQty + stack.stackSize;
						final int overFlow = newTotal > getBitbagStackSize() ? newTotal - getBitbagStackSize() : 0;
						newTotal -= overFlow;

						if ( !simulate )
						{
							contents[indexId] = brush.getStateID();
							contents[indexQty] = newTotal;

							onChange();
						}

						if ( overFlow > 0 )
						{
							return ItemChiseledBit.createStack( brush.getStateID(), overFlow, false );
						}

						return null;
					}
				}
				catch ( final InvalidBitItem e )
				{
					Log.logError( "Something went wrong", e );
				}
			}
		}

		return stack;
	}

	@Override
	public int getBitbagStackSize()
	{
		return ChiselsAndBits.getConfig().bagStackSize;
	}

	@Override
	public ItemStack extractItem(
			final int slot,
			final int amount,
			final boolean simulate )
	{
		if ( slot >= 0 && slot < BAG_STORAGE_SLOTS )
		{
			final int indexId = ItemBitBag.INTS_PER_BIT_TYPE * slot + ItemBitBag.OFFSET_STATE_ID;
			final int indexQty = ItemBitBag.INTS_PER_BIT_TYPE * slot + ItemBitBag.OFFSET_QUANTITY;

			final int slotId = contents[indexId];
			final int slotQty = slotId == 0 ? 0 : contents[indexQty];

			final int extracted = slotQty >= amount ? amount : slotQty;
			if ( extracted > 0 )
			{
				if ( !simulate )
				{
					contents[indexQty] -= extracted;
					if ( contents[indexQty] <= 0 )
					{
						contents[indexId] = 0;
					}

					onChange();
				}

				return ItemChiseledBit.createStack( slotId, extracted, false );
			}
		}

		return null;
	}

	@Override
	public int getSlotsUsed()
	{
		int used = 0;
		for ( int index = 0; index < contents.length; index += ItemBitBag.INTS_PER_BIT_TYPE )
		{
			final int slotQty = contents[index + ItemBitBag.OFFSET_QUANTITY];
			final int slotId = contents[index + ItemBitBag.OFFSET_STATE_ID];

			if ( slotQty > 0 && slotId > 0 )
			{
				used++;
			}
		}

		return used;
	}

}
