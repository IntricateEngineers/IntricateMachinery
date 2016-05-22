package mod.chiselsandbits.crafting;

import mod.chiselsandbits.chiseledblock.ItemBlockChiseled;
import mod.chiselsandbits.chiseledblock.NBTBlobConverter;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.World;

public class StackableCrafting implements IRecipe
{

	@Override
	public boolean matches(
			final InventoryCrafting craftingInv,
			final World worldIn )
	{
		ItemStack target = null;

		for ( int x = 0; x < craftingInv.getSizeInventory(); x++ )
		{
			final ItemStack f = craftingInv.getStackInSlot( x );
			if ( f == null )
			{
				continue;
			}

			if ( target == null )
			{
				target = f;
			}
			else
			{
				return false;
			}
		}

		if ( target == null || !target.hasTagCompound() || !( target.getItem() instanceof ItemBlockChiseled ) )
		{
			return false;
		}

		return true;
	}

	@Override
	public ItemStack getCraftingResult(
			final InventoryCrafting craftingInv )
	{
		ItemStack target = null;

		for ( int x = 0; x < craftingInv.getSizeInventory(); x++ )
		{
			final ItemStack f = craftingInv.getStackInSlot( x );
			if ( f == null )
			{
				continue;
			}

			if ( target == null )
			{
				target = f;
			}
			else
			{
				return null;
			}
		}

		if ( target == null || !target.hasTagCompound() || !( target.getItem() instanceof ItemBlockChiseled ) )
		{
			return null;
		}

		return getSortedVersion( target );
	}

	private ItemStack getSortedVersion(
			final ItemStack stack )
	{
		final NBTBlobConverter tmp = new NBTBlobConverter();
		tmp.readChisleData( stack.getSubCompound( ItemBlockChiseled.NBT_CHISELED_DATA, false ) );

		VoxelBlob bestBlob = tmp.getBlob();
		byte[] bestValue = bestBlob.toLegacyByteArray();

		VoxelBlob lastBlob = bestBlob;
		for ( int x = 0; x < 34; x++ )
		{
			lastBlob = lastBlob.spin( Axis.Y );
			final byte[] aValue = lastBlob.toLegacyByteArray();

			if ( arrayCompare( bestValue, aValue ) )
			{
				bestBlob = lastBlob;
				bestValue = aValue;
			}
		}

		tmp.setBlob( bestBlob );
		return tmp.getItemStack( false );
	}

	private boolean arrayCompare(
			final byte[] bestValue,
			final byte[] aValue )
	{
		if ( aValue.length < bestValue.length )
		{
			return true;
		}

		if ( aValue.length > bestValue.length )
		{
			return false;
		}

		for ( int x = 0; x < aValue.length; x++ )
		{
			if ( aValue[x] < bestValue[x] )
			{
				return true;
			}

			if ( aValue[x] > bestValue[x] )
			{
				return false;
			}
		}

		return false;
	}

	@Override
	public int getRecipeSize()
	{
		return 1;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return null; // nope
	}

	@Override
	public ItemStack[] getRemainingItems(
			final InventoryCrafting p_179532_1_ )
	{
		final ItemStack[] aitemstack = new ItemStack[p_179532_1_.getSizeInventory()];

		for ( int i = 0; i < aitemstack.length; ++i )
		{
			final ItemStack itemstack = p_179532_1_.getStackInSlot( i );
			aitemstack[i] = net.minecraftforge.common.ForgeHooks.getContainerItem( itemstack );
		}

		return aitemstack;
	}

}
