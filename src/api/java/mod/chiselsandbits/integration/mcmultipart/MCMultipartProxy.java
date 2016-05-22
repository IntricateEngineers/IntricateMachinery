package mod.chiselsandbits.integration.mcmultipart;

import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.integration.IntegrationBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MCMultipartProxy extends IntegrationBase
{

	private static class MCMultiPartNullRelay implements IMCMultiPart
	{

		@Override
		public void swapRenderIfPossible(
				final TileEntity current,
				final TileEntityBlockChiseled newTileEntity )
		{
		}

		@Override
		public void removePartIfPossible(
				final TileEntity te )
		{

		}

		@Override
		public TileEntityBlockChiseled getPartIfPossible(
				final World world,
				final BlockPos pos,
				final boolean create )
		{
			return null;
		}

		@Override
		public void triggerPartChange(
				final TileEntity te )
		{
		}

		@Override
		public boolean isMultiPart(
				final World w,
				final BlockPos pos )
		{
			return false;
		}

		@Override
		public void populateBlobWithUsedSpace(
				final World w,
				final BlockPos pos,
				final VoxelBlob vb )
		{
		}

		@Override
		public boolean rotate(
				final World world,
				final BlockPos pos,
				final EntityPlayer player )
		{
			return false;
		}

	};

	public static final MCMultipartProxy proxyMCMultiPart = new MCMultipartProxy();
	protected IMCMultiPart relay = new MCMultiPartNullRelay();

	protected void setRelay(
			final IMCMultiPart mcMultiPart )
	{
		relay = mcMultiPart;
	}

	public TileEntityBlockChiseled getChiseledTileEntity(
			final World world,
			final BlockPos pos,
			final boolean create )
	{
		return relay.getPartIfPossible( world, pos, create );
	}

	public void removeChisledBlock(
			final TileEntity te )
	{
		relay.removePartIfPossible( te );
	}

	public boolean isMultiPartTileEntity(
			final World w,
			final BlockPos pos )
	{
		return relay.isMultiPart( w, pos );
	}

	public void convertTo(
			final TileEntity current,
			final TileEntityBlockChiseled newTileEntity )
	{
		relay.swapRenderIfPossible( current, newTileEntity );
	}

	public void triggerPartChange(
			final TileEntity te )
	{
		relay.triggerPartChange( te );
	}

	public void addFiller(
			final World w,
			final BlockPos pos,
			final VoxelBlob vb )
	{
		relay.populateBlobWithUsedSpace( w, pos, vb );
	}

	public boolean rotate(
			final World world,
			final BlockPos pos,
			final EntityPlayer player )
	{
		return relay.rotate( world, pos, player );
	}

}
