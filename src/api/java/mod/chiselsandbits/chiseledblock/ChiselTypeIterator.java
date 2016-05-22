package mod.chiselsandbits.chiseledblock;

import java.util.Iterator;

import mod.chiselsandbits.chiseledblock.data.IntegerBox;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.core.ChiselMode;
import mod.chiselsandbits.helpers.IVoxelSrc;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public class ChiselTypeIterator implements Iterator<ChiselTypeIterator>
{

	private final int full_size;
	private final int max_dim;

	private int x_range = 1;
	private int y_range = 1;
	private int z_range = 1;

	private int x, y, z;

	private final int original_x;
	private final int original_y;
	private final int original_z;
	public final EnumFacing side;
	final ChiselMode mode;

	private final int parts;
	private int offset = -1;

	public ChiselTypeIterator(
			final int dim,
			final int x,
			final int y,
			final int z,
			final int x_size,
			final int y_size,
			final int z_size,
			final EnumFacing side )
	{
		full_size = dim;
		max_dim = dim - 1;
		mode = ChiselMode.DRAWN_REGION;
		this.side = side;

		x_range = x_size;
		y_range = y_size;
		z_range = z_size;
		parts = x_range * y_range * z_range;

		original_x = x;
		original_y = y;
		original_z = z;
	}

	public ChiselTypeIterator(
			final int dim,
			int x,
			int y,
			int z,
			final IVoxelSrc source,
			final ChiselMode mode,
			final EnumFacing side )
	{
		int offset = 0;
		full_size = dim;
		max_dim = dim - 1;

		this.side = side;
		this.mode = mode;

		switch ( mode )
		{
			case CUBE_SMALL:
				x_range = 3;
				y_range = 3;
				z_range = 3;
				offset = -1;
				parts = x_range * y_range * z_range;
				break;

			case SNAP2:
				x -= x % 2;
				y -= y % 2;
				z -= z % 2;
				x_range = 2;
				y_range = 2;
				z_range = 2;
				parts = x_range * y_range * z_range;
				break;

			case SNAP4:
				x -= x % 4;
				y -= y % 4;
				z -= z % 4;
				x_range = 4;
				y_range = 4;
				z_range = 4;
				parts = x_range * y_range * z_range;
				break;

			case SNAP8:
				x -= x % 8;
				y -= y % 8;
				z -= z % 8;
				x_range = 8;
				y_range = 8;
				z_range = 8;
				parts = x_range * y_range * z_range;
				break;

			case LINE:
				parts = full_size;
				switch ( side )
				{
					case DOWN:
					case UP:
						y = 0;
						y_range = full_size;
						break;
					case NORTH:
					case SOUTH:
						z = 0;
						z_range = full_size;
						break;
					case WEST:
					case EAST:
						x = 0;
						x_range = full_size;
						break;
					default:
						throw new NullPointerException();
				}
				break;

			case PLANE:
				parts = full_size * full_size;
				switch ( side )
				{
					case DOWN:
					case UP:
						x = 0;
						z = 0;
						x_range = full_size;
						z_range = full_size;
						break;
					case NORTH:
					case SOUTH:
						x = 0;
						y = 0;
						x_range = full_size;
						y_range = full_size;
						break;
					case WEST:
					case EAST:
						y = 0;
						z = 0;
						y_range = full_size;
						z_range = full_size;
						break;
					default:
						throw new NullPointerException();
				}
				break;

			case CONNECTED_PLANE:

				final int ox = x;
				final int oy = y;
				final int oz = z;

				switch ( side )
				{
					case DOWN:
					case UP:
						while ( x > 0 && source.getSafe( x - 1, oy, oz ) != 0 && source.getSafe( x - 1, oy + side.getFrontOffsetY(), oz ) == 0 )
						{
							x--;
							x_range++;
						}
						while ( z > 0 && source.getSafe( ox, oy, z - 1 ) != 0 && source.getSafe( ox, oy + side.getFrontOffsetY(), z - 1 ) == 0 )
						{
							z--;
							z_range++;
						}
						while ( x_range < full_size && source.getSafe( x + x_range, oy, oz ) != 0 && source.getSafe( x + x_range, oy + side.getFrontOffsetY(), oz ) == 0 )
						{
							x_range++;
						}
						while ( z_range < full_size && source.getSafe( ox, oy, z + z_range ) != 0 && source.getSafe( ox, oy + side.getFrontOffsetY(), z + z_range ) == 0 )
						{
							z_range++;
						}
						break;
					case NORTH:
					case SOUTH:
						while ( x > 0 && source.getSafe( x - 1, oy, oz ) != 0 && source.getSafe( x - 1, oy, oz + side.getFrontOffsetZ() ) == 0 )
						{
							x--;
							x_range++;
						}
						while ( y > 0 && source.getSafe( ox, y - 1, oz ) != 0 && source.getSafe( ox, y - 1, oz + side.getFrontOffsetZ() ) == 0 )
						{
							y--;
							y_range++;
						}
						while ( x_range < full_size && source.getSafe( x + x_range, oy, oz ) != 0 && source.getSafe( x + x_range, oy, oz + side.getFrontOffsetZ() ) == 0 )
						{
							x_range++;
						}
						while ( y_range < full_size && source.getSafe( ox, y + y_range, oz ) != 0 && source.getSafe( ox, y + y_range, oz + side.getFrontOffsetZ() ) == 0 )
						{
							y_range++;
						}
						break;
					case WEST:
					case EAST:
						while ( y > 0 && source.getSafe( ox, y - 1, oz ) != 0 && source.getSafe( ox + side.getFrontOffsetX(), y - 1, oz ) == 0 )
						{
							y--;
							y_range++;
						}
						while ( z > 0 && source.getSafe( ox, oy, z - 1 ) != 0 && source.getSafe( ox + side.getFrontOffsetX(), oy, z - 1 ) == 0 )
						{
							z--;
							z_range++;
						}
						while ( y_range < full_size && source.getSafe( ox, y + y_range, oz ) != 0 && source.getSafe( ox + side.getFrontOffsetX(), y + y_range, oz ) == 0 )
						{
							y_range++;
						}
						while ( z_range < full_size && source.getSafe( ox, oy, z + z_range ) != 0 && source.getSafe( ox + side.getFrontOffsetX(), oy, z + z_range ) == 0 )
						{
							z_range++;
						}
						break;
					default:
						throw new NullPointerException();
				}

				parts = Math.min( full_size, x_range ) * Math.min( full_size, y_range ) * Math.min( full_size, z_range );
				break;

			case CUBE_MEDIUM:
				x_range = 5;
				y_range = 5;
				z_range = 5;
				offset = -2;
				parts = x_range * y_range * z_range;
				break;

			case CUBE_LARGE:
				x_range = 7;
				y_range = 7;
				z_range = 7;
				offset = -3;
				parts = x_range * y_range * z_range;
				break;

			case DRAWN_REGION:
			case SINGLE:
				parts = 1;
				break;

			default:
				throw new NullPointerException();
		}

		original_x = Math.max( 0, Math.min( full_size - x_range, x + offset ) );
		original_y = Math.max( 0, Math.min( full_size - y_range, y + offset ) );
		original_z = Math.max( 0, Math.min( full_size - z_range, z + offset ) );
	}

	@Override
	public boolean hasNext()
	{
		if ( ++offset != 0 )
		{

			++x;

			boolean x_up = false;
			if ( x >= x_range )
			{
				++y;
				x = 0;
				x_up = true;
			}

			if ( y >= y_range && x_up )
			{
				++z;
				y = 0;
			}

		}

		return offset < parts;
	}

	public int x()
	{
		return Math.max( 0, Math.min( max_dim, original_x + x ) );
	}

	public int y()
	{
		return Math.max( 0, Math.min( max_dim, original_y + y ) );
	}

	public int z()
	{
		return Math.max( 0, Math.min( max_dim, original_z + z ) );
	}

	@Override
	public ChiselTypeIterator next()
	{
		return this;
	}

	@Override
	public void remove()
	{

	}

	public IntegerBox getVoxelBox(
			final VoxelBlob vb,
			final boolean boundSolids )
	{
		final IntegerBox box = new IntegerBox( 0, 0, 0, 0, 0, 0 );

		boolean started = false;
		while ( hasNext() )
		{
			if ( vb.get( x(), y(), z() ) != 0 == boundSolids )
			{
				if ( started )
				{
					box.minX = Math.min( box.minX, x() );
					box.minY = Math.min( box.minY, y() );
					box.minZ = Math.min( box.minZ, z() );
					box.maxX = Math.max( box.maxX, x() );
					box.maxY = Math.max( box.maxY, y() );
					box.maxZ = Math.max( box.maxZ, z() );
				}
				else
				{
					started = true;
					box.minX = x();
					box.minY = y();
					box.minZ = z();
					box.maxX = x();
					box.maxY = y();
					box.maxZ = z();
				}
			}
		}

		if ( started )
		{
			return box;
		}
		else
		{
			return null;
		}
	}

	public AxisAlignedBB getBoundingBox(
			final VoxelBlob vb,
			final boolean boundSolids )
	{
		final float One16thf = 1.0f / vb.detail;
		final IntegerBox box = getVoxelBox( vb, boundSolids );

		if ( box != null )
		{
			return new AxisAlignedBB( box.minX * One16thf, box.minY * One16thf, box.minZ * One16thf, ( box.maxX + 1 ) * One16thf, ( box.maxY + 1 ) * One16thf, ( box.maxZ + 1 ) * One16thf );
		}
		else
		{
			return null;
		}
	}

}
