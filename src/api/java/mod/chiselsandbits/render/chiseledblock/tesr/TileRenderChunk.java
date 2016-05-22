package mod.chiselsandbits.render.chiseledblock.tesr;

import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseledTESR;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileRenderChunk extends TileRenderCache
{

	private final TileList tiles = new TileList();
	public boolean singleInstanceMode = false;

	// upon registration convert new tiles into the correct type...
	public void register(
			final TileEntityBlockChiseledTESR which )
	{
		if ( which == null )
		{
			throw new NullPointerException();
		}

		rebuild( true );

		tiles.getWriteLock().lock();
		try
		{
			tiles.add( which );
		}
		finally
		{
			tiles.getWriteLock().unlock();
		}
	}

	@Override
	public void rebuild(
			final boolean conversion )
	{
		if ( singleInstanceMode )
		{
			tiles.getReadLock().lock();

			try
			{
				for ( final TileEntityBlockChiseledTESR te : tiles )
				{
					te.getCache().rebuild( conversion );
				}
			}
			finally
			{
				tiles.getReadLock().unlock();
			}

			return;
		}

		super.rebuild( conversion );
	}

	// nothing special here...
	public void unregister(
			final TileEntityBlockChiseledTESR which )
	{
		tiles.getWriteLock().lock();

		try
		{
			tiles.remove( which );
		}
		finally
		{
			tiles.getWriteLock().unlock();
		}

		super.rebuild( true );
	}

	public BlockPos chunkOffset()
	{
		tiles.getReadLock().lock();

		try
		{
			if ( getTiles().isEmpty() )
			{
				return BlockPos.ORIGIN;
			}
		}
		finally
		{
			tiles.getReadLock().unlock();
		}

		final int bitMask = ~0xf;
		final BlockPos tilepos = getTiles().get( 0 ).getPos();
		return new BlockPos( tilepos.getX() & bitMask, tilepos.getY() & bitMask, tilepos.getZ() & bitMask );
	}

	AxisAlignedBB renderBox = null;

	public AxisAlignedBB getBounds()
	{
		if ( renderBox == null )
		{
			final BlockPos p = chunkOffset();
			renderBox = new AxisAlignedBB( p.getX(), p.getY(), p.getZ(), p.getX() + 16, p.getY() + 16, p.getZ() + 16 );
		}

		return renderBox;
	}

	@Override
	public TileList getTiles()
	{
		return tiles;
	}

}
