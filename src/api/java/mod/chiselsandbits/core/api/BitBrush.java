package mod.chiselsandbits.core.api;

import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.items.ItemChiseledBit;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class BitBrush implements IBitBrush
{

	protected final int stateID;

	public BitBrush(
			final int blockStateID )
	{
		stateID = blockStateID;
	}

	@Override
	public ItemStack getItemStack(
			final int count )
	{
		if ( stateID == 0 )
		{
			return null;
		}

		return ItemChiseledBit.createStack( stateID, count, true );
	}

	@Override
	public boolean isAir()
	{
		return stateID == 0;
	}

	@Override
	public IBlockState getState()
	{
		if ( stateID == 0 )
		{
			return null;
		}

		return Block.getStateById( stateID );
	}

	@Override
	public int getStateID()
	{
		return stateID;
	}

}
