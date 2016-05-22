package mod.chiselsandbits.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockColorChisled implements IBlockColor
{

	@Override
	public int colorMultiplier(
			final IBlockState state,
			final IBlockAccess worldIn,
			final BlockPos pos,
			final int tint )
	{
		final IBlockState tstate = Block.getStateById( tint );
		return Minecraft.getMinecraft().getBlockColors().colorMultiplier( tstate, worldIn, pos, 0 );
	}

}
