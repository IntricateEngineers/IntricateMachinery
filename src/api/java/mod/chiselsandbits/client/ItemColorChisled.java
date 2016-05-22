package mod.chiselsandbits.client;

import mod.chiselsandbits.render.helpers.ModelUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemColorChisled implements IItemColor
{

	@Override
	public int getColorFromItemstack(
			final ItemStack stack,
			final int tint )
	{
		final IBlockState state = Block.getStateById( tint );
		final Block blk = state.getBlock();
		final Item i = Item.getItemFromBlock( blk );

		if ( i != null )
		{
			return ModelUtil.getItemStackColor( new ItemStack( i, 1, blk.getMetaFromState( state ) ), 0 );
		}

		return 0xffffff;
	}

}
