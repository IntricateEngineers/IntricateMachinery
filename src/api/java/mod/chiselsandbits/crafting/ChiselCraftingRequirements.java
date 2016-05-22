package mod.chiselsandbits.crafting;

import java.util.ArrayList;
import java.util.List;

import mod.chiselsandbits.bitbag.BagInventory;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob.TypeRef;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.items.ItemBitBag;
import mod.chiselsandbits.items.ItemChiseledBit;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

class ChiselCraftingRequirements
{
	private final VoxelBlob voxelBlob;
	final ItemStack pattern;

	private Boolean isValid = null;

	final ItemStack[] pile;
	private final ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
	private final ArrayList<BagInventory> bags = new ArrayList<BagInventory>();

	public ChiselCraftingRequirements(
			final IInventory inv,
			final ItemStack inPattern,
			final boolean copy )
	{
		pile = new ItemStack[inv.getSizeInventory()];
		pattern = inPattern;

		for ( int x = 0; x < inv.getSizeInventory(); x++ )
		{
			final ItemStack is = inv.getStackInSlot( x );
			pile[x] = is;

			if ( !copy )
			{
				// if we are not copying.. then we remove it...
				inv.setInventorySlotContents( x, null );
			}

			if ( is == null )
			{
				continue;
			}

			if ( is.getItem() instanceof ItemBitBag )
			{
				bags.add( new BagInventory( copy ? is.copy() : is ) );
			}

			if ( is.getItem() instanceof ItemChiseledBit )
			{
				stacks.add( copy ? is.copy() : is );
			}
		}

		voxelBlob = ModUtil.getBlobFromStack( inPattern, null );
	}

	public boolean isValid()
	{
		if ( isValid != null )
		{
			return isValid;
		}

		final List<TypeRef> count = voxelBlob.getBlockCounts();

		isValid = true;
		for ( final TypeRef ref : count )
		{
			if ( ref.stateId != 0 )
			{

				for ( final ItemStack is : stacks )
				{
					if ( ItemChiseledBit.getStackState( is ) == ref.stateId && is.stackSize > 0 )
					{
						final int original = is.stackSize;
						is.stackSize = Math.max( 0, is.stackSize - ref.quantity );
						ref.quantity -= original - is.stackSize;
					}
				}

				for ( final BagInventory bag : bags )
				{
					ref.quantity -= bag.extractBit( ref.stateId, ref.quantity );
				}

				if ( ref.quantity > 0 )
				{
					isValid = false;
					break;
				}
			}
		}
		return isValid;
	}
}