package mod.chiselsandbits.helpers;

import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.api.BitAccess;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VoxelRegionSrc implements IVoxelSrc
{

	final BlockPos min;
	final BlockPos max;
	final BlockPos actingCenter;

	final int wrapZ;
	final int wrapY;
	final int wrapX;

	final VoxelBlob blobs[];

	private VoxelRegionSrc(
			final World src,
			final BlockPos min,
			final BlockPos max,
			final BlockPos actingCenter )
	{
		this.min = min;
		this.max = max;
		this.actingCenter = actingCenter.subtract( min );

		wrapX = max.getX() - min.getX() + 1;
		wrapY = max.getY() - min.getY() + 1;
		wrapZ = max.getZ() - min.getZ() + 1;

		blobs = new VoxelBlob[wrapX * wrapY * wrapZ];

		for ( int x = min.getX(); x <= max.getX(); ++x )
		{
			for ( int y = min.getY(); y <= max.getY(); ++y )
			{
				for ( int z = min.getZ(); z <= max.getZ(); ++z )
				{
					final int idx = x - min.getX() + ( y - min.getY() ) * wrapX + ( z - min.getZ() ) * wrapX * wrapY;

					try
					{
						final BitAccess access = (BitAccess) ChiselsAndBits.getApi().getBitAccess( src, new BlockPos( x, y, z ) );
						blobs[idx] = access.getNativeBlob();
					}
					catch ( final CannotBeChiseled e )
					{
						blobs[idx] = new VoxelBlob();
					}
				}
			}
		}
	}

	public VoxelRegionSrc(
			final World theWorld,
			final BlockPos blockPos,
			final int i )
	{
		this( theWorld, blockPos.add( -i, -i, -i ), blockPos.add( i, i, i ), blockPos );
	}

	@Override
	public int getSafe(
			int x,
			int y,
			int z )
	{
		x += actingCenter.getX() * VoxelBlob.dim;
		y += actingCenter.getY() * VoxelBlob.dim;
		z += actingCenter.getZ() * VoxelBlob.dim;

		final int bitPosX = x & 0xf;
		final int bitPosY = y & 0xf;
		final int bitPosZ = z & 0xf;

		final int blkPosX = x >> 4;
		final int blkPosY = y >> 4;
		final int blkPosZ = z >> 4;

		final int idx = blkPosX + blkPosY * wrapX + blkPosZ * wrapX * wrapY;

		if ( blkPosX < 0 || blkPosY < 0 || blkPosZ < 0 || blkPosX >= wrapX || blkPosY >= wrapY || blkPosZ >= wrapZ )
		{
			return 0;
		}

		return blobs[idx].get( bitPosX, bitPosY, bitPosZ );
	}

	public VoxelBlob getBlobAt(
			final BlockPos blockPos )
	{
		final int blkPosX = blockPos.getX() - min.getX();
		final int blkPosY = blockPos.getY() - min.getY();
		final int blkPosZ = blockPos.getZ() - min.getZ();

		final int idx = blkPosX + blkPosY * wrapX + blkPosZ * wrapX * wrapY;

		if ( blkPosX < 0 || blkPosY < 0 || blkPosZ < 0 || blkPosX >= wrapX || blkPosY >= wrapY || blkPosZ >= wrapZ )
		{
			return new VoxelBlob();
		}

		return blobs[idx];
	}
}
