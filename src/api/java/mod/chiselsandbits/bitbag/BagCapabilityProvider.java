package mod.chiselsandbits.bitbag;

import mod.chiselsandbits.items.ItemBitBag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;

public class BagCapabilityProvider extends BagStorage implements ICapabilityProvider
{

	public BagCapabilityProvider(
			final ItemStack stack,
			final NBTTagCompound nbt )
	{
		this.stack = stack;
	}

	/**
	 * Read NBT int array in and ensure its the proper size.
	 *
	 * @param stack
	 * @param size
	 * @return a usable int[] for the bag storage.
	 */
	private static int[] getStorageArray(
			final ItemStack stack,
			final int size )
	{
		int[] out = null;
		NBTTagCompound compound = stack.getTagCompound();

		if ( compound != null && compound.hasKey( "contents" ) )
		{
			out = compound.getIntArray( "contents" );
		}

		if ( out == null )
		{
			compound = new NBTTagCompound();
			stack.setTagCompound( compound );
			out = new int[size];
			compound.setIntArray( "contents", out );
		}

		if ( out.length != size )
		{
			final int[] tmp = out;
			out = new int[size];
			System.arraycopy( out, 0, tmp, 0, Math.min( size, tmp.length ) );
			compound.setIntArray( "contents", out );
		}

		return out;
	}

	@Override
	public boolean hasCapability(
			final Capability<?> capability,
			final EnumFacing facing )
	{
		if ( capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY )
		{
			return true;
		}

		return false;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public <T> T getCapability(
			final Capability<T> capability,
			final EnumFacing facing )
	{
		if ( capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY )
		{
			setStorage( getStorageArray( stack, BAG_STORAGE_SLOTS * ItemBitBag.INTS_PER_BIT_TYPE ) );
			return (T) this;
		}

		return null;
	}

}
