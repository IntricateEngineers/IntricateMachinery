package mod.chiselsandbits.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.ModUtil.ItemStackSlot;
import mod.chiselsandbits.items.ItemChisel;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ContinousChisels implements IContinuousInventory
{

	private final ActingPlayer who;
	private final List<ItemStackSlot> options = new ArrayList<ItemStackSlot>();
	private final HashMap<Integer, List<ItemStackSlot>> actionCache = new HashMap<Integer, List<ItemStackSlot>>();
	private final boolean canEdit;

	public ContinousChisels(
			final ActingPlayer who,
			final BlockPos pos,
			final EnumFacing side )
	{
		this.who = who;
		final ItemStack inHand = who.getCurrentEquippedItem();
		final IInventory inv = who.getInventory();

		// test can edit...
		canEdit = who.canPlayerManipulate( pos, side, new ItemStack( ChiselsAndBits.getItems().itemChiselDiamond, 1 ) );

		if ( inHand != null && inHand.stackSize > 0 && inHand.getItem() instanceof ItemChisel )
		{
			if ( who.canPlayerManipulate( pos, side, inHand ) )
			{
				options.add( new ItemStackSlot( inv, who.getCurrentItem(), inHand, who, canEdit ) );
			}
		}
		else
		{
			final ArrayListMultimap<Integer, ItemStackSlot> discovered = ArrayListMultimap.create();

			for ( int x = 0; x < inv.getSizeInventory(); x++ )
			{
				final ItemStack is = inv.getStackInSlot( x );

				if ( is == inHand )
				{
					continue;
				}

				if ( !who.canPlayerManipulate( pos, side, is ) )
				{
					continue;
				}

				if ( is != null && is.stackSize > 0 && is.getItem() instanceof ItemChisel )
				{
					final ToolMaterial newMat = ( (ItemChisel) is.getItem() ).whatMaterial();
					discovered.put( newMat.getHarvestLevel(), new ItemStackSlot( inv, x, is, who, canEdit ) );
				}
			}

			final List<ItemStackSlot> allValues = Lists.newArrayList( discovered.values() );
			for ( final ItemStackSlot f : Lists.reverse( allValues ) )
			{
				options.add( f );
			}
		}
	}

	@Override
	public ItemStackSlot getItem(
			final int BlockID )
	{
		if ( !actionCache.containsKey( BlockID ) )
		{
			actionCache.put( BlockID, new ArrayList<ItemStackSlot>( options ) );
		}

		final List<ItemStackSlot> choices = actionCache.get( BlockID );

		if ( choices.isEmpty() )
		{
			return new ItemStackSlot( null, -1, null, who, canEdit );
		}

		final ItemStackSlot slot = choices.get( choices.size() - 1 );

		if ( slot.isValid() )
		{
			return slot;
		}
		else
		{
			fail( BlockID );
		}

		return getItem( BlockID );
	}

	@Override
	public void fail(
			final int BlockID )
	{
		final List<ItemStackSlot> choices = actionCache.get( BlockID );

		if ( !choices.isEmpty() )
		{
			choices.remove( choices.size() - 1 );
		}
	}

	@Override
	public boolean isValid()
	{
		return !options.isEmpty() || who.isCreative();
	}

	@Override
	public void useItem(
			final int blk )
	{
		getItem( blk ).damage( who );
	}

}
