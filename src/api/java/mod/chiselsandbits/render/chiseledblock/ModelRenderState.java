package mod.chiselsandbits.render.chiseledblock;

import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateReference;
import net.minecraft.util.EnumFacing;

public class ModelRenderState
{
	// less objects/garbage to clean up, and less memory usage.
	private VoxelBlobStateReference north, south, east, west, up, down;

	public VoxelBlobStateReference get(
			final EnumFacing side )
	{
		switch ( side )
		{
			case DOWN:
				return down;
			case EAST:
				return east;
			case NORTH:
				return north;
			case SOUTH:
				return south;
			case UP:
				return up;
			case WEST:
				return west;
			default:
		}

		return null;
	}

	public void put(
			final EnumFacing side,
			final VoxelBlobStateReference value )
	{
		switch ( side )
		{
			case DOWN:
				down = value;
				break;
			case EAST:
				east = value;
				break;
			case NORTH:
				north = value;
				break;
			case SOUTH:
				south = value;
				break;
			case UP:
				up = value;
				break;
			case WEST:
				west = value;
				break;
			default:
		}
	}

	public ModelRenderState(
			final ModelRenderState sides )
	{
		if ( sides != null )
		{
			north = sides.north;
			south = sides.south;
			east = sides.east;
			west = sides.west;
			up = sides.up;
			down = sides.down;
		}
	}

}
