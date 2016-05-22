package mod.chiselsandbits.chiseledblock;

import java.util.Collections;
import java.util.List;

import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.chiseledblock.data.VoxelNeighborRenderTracker;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.render.chiseledblock.tesr.TileRenderCache;
import mod.chiselsandbits.render.chiseledblock.tesr.TileRenderChunk;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.property.IExtendedBlockState;

public class TileEntityBlockChiseledTESR extends TileEntityBlockChiseled
{
	private TileRenderChunk renderChunk;
	private TileRenderCache singleCache;

	@Override
	public boolean canRenderBreaking()
	{
		return true;
	}

	public TileRenderChunk getRenderChunk()
	{
		return renderChunk;
	}

	@Override
	public boolean hasFastRenderer()
	{
		return true;
	}

	@Override
	protected void tesrUpdate(
			final VoxelNeighborRenderTracker vns )
	{
		if ( renderChunk == null )
		{
			renderChunk = findRenderChunk();
			renderChunk.register( this );
		}

		renderChunk.update( null, 1 );

		if ( vns.isShouldUpdate() )
		{
			renderChunk.rebuild( false );
		}
	}

	private TileRenderChunk findRenderChunk()
	{
		int chunkPosX = getPos().getX();
		int chunkPosY = getPos().getY();
		int chunkPosZ = getPos().getZ();

		final int mask = ~0xf;
		chunkPosX = chunkPosX & mask;
		chunkPosY = chunkPosY & mask;
		chunkPosZ = chunkPosZ & mask;

		for ( int x = 0; x < 16; ++x )
		{
			for ( int y = 0; y < 16; ++y )
			{
				for ( int z = 0; z < 16; ++z )
				{
					final TileEntityBlockChiseled te = ModUtil.getChiseledTileEntity( worldObj, new BlockPos( chunkPosX + x, chunkPosY + y, chunkPosZ + z ), false );
					if ( te instanceof TileEntityBlockChiseledTESR )
					{
						final TileRenderChunk trc = ( (TileEntityBlockChiseledTESR) te ).renderChunk;
						if ( trc != null )
						{
							return trc;
						}
					}
				}
			}
		}

		return new TileRenderChunk();
	}

	public TileRenderCache getCache()
	{
		final TileEntityBlockChiseledTESR self = this;

		if ( singleCache == null )
		{
			singleCache = new TileRenderCache() {

				@Override
				public List<TileEntityBlockChiseledTESR> getTiles()
				{
					return Collections.singletonList( self );
				}
			};
		}

		return singleCache;
	}

	@Override
	public void invalidate()
	{
		if ( renderChunk != null )
		{
			renderChunk.unregister( this );
		}
	}

	public IExtendedBlockState getTileRenderState()
	{
		return getState( true, 0 );
	}

	@Override
	public boolean shouldRenderInPass(
			final int pass )
	{
		return true;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		if ( getRenderChunk() != null )
		{
			return getRenderChunk().getBounds();
		}

		final BlockPos p = getPos();
		return new AxisAlignedBB( p.getX(), p.getY(), p.getZ(), p.getX() + 1, p.getY() + 1, p.getZ() + 1 );
	}

	@Override
	public boolean isSideOpaque(
			final EnumFacing side )
	{
		return false; // since TESRs can blink out of existence never block..
	}

	@Override
	public double getMaxRenderDistanceSquared()
	{
		return ChiselsAndBits.getConfig().dynamicModelRange * ChiselsAndBits.getConfig().dynamicModelRange;
	}

	@Override
	public void completeEditOperation(
			final VoxelBlob vb )
	{
		super.completeEditOperation( vb );

		if ( renderChunk != null )
		{
			if ( renderChunk.singleInstanceMode )
			{
				getCache().rebuild( true );
			}
			else
			{
				renderChunk.rebuild( true );
			}
		}
	}

}
