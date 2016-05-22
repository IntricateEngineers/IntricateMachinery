package mod.chiselsandbits.crafting;

import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.items.ItemBitBag;
import mod.chiselsandbits.items.ItemChiseledBit;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class ChiselCrafting implements IRecipe
{

	/**
	 * Find the bag and pattern...
	 *
	 * @param inv
	 * @return
	 */
	private ChiselCraftingRequirements getCraftingReqs(
			final InventoryCrafting inv,
			final boolean copy )
	{
		ItemStack pattern = null;

		for ( int x = 0; x < inv.getSizeInventory(); x++ )
		{
			final ItemStack is = inv.getStackInSlot( x );

			if ( is == null )
			{
				continue;
			}

			if ( is.getItem() == ChiselsAndBits.getItems().itemPositiveprint && pattern == null )
			{
				pattern = is;
			}
			else if ( is.getItem() instanceof ItemBitBag )
			{
				continue;
			}
			else if ( is.getItem() instanceof ItemChiseledBit )
			{
				continue;
			}
			else
			{
				return null;
			}
		}

		if ( pattern == null || pattern.hasTagCompound() == false )
		{
			return null;
		}

		final ChiselCraftingRequirements r = new ChiselCraftingRequirements( inv, pattern, copy );
		if ( r.isValid() )
		{
			return r;
		}

		return null;
	}

	@Override
	public boolean matches(
			final InventoryCrafting inv,
			final World worldIn )
	{
		return getCraftingReqs( inv, true ) != null;
	}

	@Override
	public ItemStack getCraftingResult(
			final InventoryCrafting inv )
	{
		final ChiselCraftingRequirements req = getCraftingReqs( inv, true );

		if ( req != null )
		{
			return ChiselsAndBits.getItems().itemPositiveprint.getPatternedItem( req.pattern );
		}

		return null;
	}

	@Override
	public int getRecipeSize()
	{
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		// no inputs, means no output.
		return null;
	}

	@Override
	public ItemStack[] getRemainingItems(
			final InventoryCrafting inv )
	{
		final ItemStack[] out = new ItemStack[inv.getSizeInventory()];

		// just getting this will alter the stacks..
		final ChiselCraftingRequirements r = getCraftingReqs( inv, false );

		if ( inv.getSizeInventory() != r.pile.length )
		{
			throw new RuntimeException( "Inventory Changed Size!" );
		}

		for ( int x = 0; x < r.pile.length; x++ )
		{
			out[x] = r.pile[x];

			if ( out[x] != null && out[x].stackSize <= 0 )
			{
				out[x] = null;
			}
		}

		return out;
	}

}
