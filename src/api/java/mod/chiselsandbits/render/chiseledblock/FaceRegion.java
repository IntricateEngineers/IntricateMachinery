package mod.chiselsandbits.render.chiseledblock;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

class FaceRegion
{
	public FaceRegion(
			final EnumFacing myFace,
			final Vec3i center,
			final int blockStateID,
			final boolean isEdgeFace )
	{
		face = myFace;
		this.blockStateID = blockStateID;
		isEdge = isEdgeFace;
		min = center;
		max = new Vec3i( center.getX(), center.getY(), center.getZ() );
	}

	final public EnumFacing face;
	final int blockStateID;
	final boolean isEdge;

	public Vec3i min, max;

	public boolean extend(
			final FaceRegion currentFace )
	{
		if ( currentFace.blockStateID != blockStateID )
		{
			return false;
		}

		switch ( face )
		{
			case DOWN:
			case UP:
			{
				final boolean a = max.getX() == currentFace.min.getX() - 2 && max.getZ() == currentFace.max.getZ() && min.getZ() == currentFace.min.getZ();
				final boolean b = min.getX() == currentFace.max.getX() + 2 && max.getZ() == currentFace.max.getZ() && min.getZ() == currentFace.min.getZ();
				final boolean c = max.getZ() == currentFace.min.getZ() - 2 && max.getX() == currentFace.max.getX() && min.getX() == currentFace.min.getX();
				final boolean d = min.getZ() == currentFace.max.getZ() + 2 && max.getX() == currentFace.max.getX() && min.getX() == currentFace.min.getX();

				if ( a || b || c || d )
				{
					min = new Vec3i( Math.min( currentFace.min.getX(), min.getX() ), Math.min( currentFace.min.getY(), min.getY() ), Math.min( currentFace.min.getZ(), min.getZ() ) );
					max = new Vec3i( Math.max( currentFace.max.getX(), max.getX() ), Math.max( currentFace.max.getY(), max.getY() ), Math.max( currentFace.max.getZ(), max.getZ() ) );
					return true;
				}

				return false;
			}

			case WEST:
			case EAST:
			{
				final boolean a = max.getY() == currentFace.min.getY() - 2 && max.getZ() == currentFace.max.getZ() && min.getZ() == currentFace.min.getZ();
				final boolean b = min.getY() == currentFace.max.getY() + 2 && max.getZ() == currentFace.max.getZ() && min.getZ() == currentFace.min.getZ();
				final boolean c = max.getZ() == currentFace.min.getZ() - 2 && max.getY() == currentFace.max.getY() && min.getY() == currentFace.min.getY();
				final boolean d = min.getZ() == currentFace.max.getZ() + 2 && max.getY() == currentFace.max.getY() && min.getY() == currentFace.min.getY();

				if ( a || b || c || d )
				{
					min = new Vec3i( Math.min( currentFace.min.getX(), min.getX() ), Math.min( currentFace.min.getY(), min.getY() ), Math.min( currentFace.min.getZ(), min.getZ() ) );
					max = new Vec3i( Math.max( currentFace.max.getX(), max.getX() ), Math.max( currentFace.max.getY(), max.getY() ), Math.max( currentFace.max.getZ(), max.getZ() ) );
					return true;
				}

				return false;
			}

			case NORTH:
			case SOUTH:
			{
				final boolean a = max.getY() == currentFace.min.getY() - 2 && max.getX() == currentFace.max.getX() && min.getX() == currentFace.min.getX();
				final boolean b = min.getY() == currentFace.max.getY() + 2 && max.getX() == currentFace.max.getX() && min.getX() == currentFace.min.getX();
				final boolean c = max.getX() == currentFace.min.getX() - 2 && max.getY() == currentFace.max.getY() && min.getY() == currentFace.min.getY();
				final boolean d = min.getX() == currentFace.max.getX() + 2 && max.getY() == currentFace.max.getY() && min.getY() == currentFace.min.getY();

				if ( a || b || c || d )
				{
					min = new Vec3i( Math.min( currentFace.min.getX(), min.getX() ), Math.min( currentFace.min.getY(), min.getY() ), Math.min( currentFace.min.getZ(), min.getZ() ) );
					max = new Vec3i( Math.max( currentFace.max.getX(), max.getX() ), Math.max( currentFace.max.getY(), max.getY() ), Math.max( currentFace.max.getZ(), max.getZ() ) );
					return true;
				}

				return false;
			}

			default:
				return false;
		}
	}
}