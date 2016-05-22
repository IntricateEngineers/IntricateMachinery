package mod.chiselsandbits.chiseledblock.data;

import mod.chiselsandbits.api.IBitLocation;
import mod.chiselsandbits.helpers.ChiselToolType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class BitLocation implements IBitLocation
{
	private static final double One32nd = 0.5 / VoxelBlob.dim;

	public final BlockPos blockPos;
	public final int bitX, bitY, bitZ;

	@Override
	public BlockPos getBlockPos()
	{
		return blockPos;
	}

	@Override
	public int getBitX()
	{
		return bitX;
	}

	@Override
	public int getBitY()
	{
		return bitY;
	}

	@Override
	public int getBitZ()
	{
		return bitZ;
	}

	public BitLocation(
			final RayTraceResult mop,
			final boolean absHit,
			final ChiselToolType type )
	{
		final BlockPos absOffset = absHit ? mop.getBlockPos() : BlockPos.ORIGIN;

		if ( type == ChiselToolType.CHISEL )
		{
			blockPos = mop.getBlockPos();

			final double xCoord = mop.hitVec.xCoord - absOffset.getX() - mop.sideHit.getFrontOffsetX() * One32nd;
			final double yCoord = mop.hitVec.yCoord - absOffset.getY() - mop.sideHit.getFrontOffsetY() * One32nd;
			final double zCoord = mop.hitVec.zCoord - absOffset.getZ() - mop.sideHit.getFrontOffsetZ() * One32nd;

			bitX = (int) Math.floor( xCoord * VoxelBlob.dim );
			bitY = (int) Math.floor( yCoord * VoxelBlob.dim );
			bitZ = (int) Math.floor( zCoord * VoxelBlob.dim );
		}
		else
		{
			final double xCoord = mop.hitVec.xCoord - absOffset.getX() + mop.sideHit.getFrontOffsetX() * One32nd;
			final double yCoord = mop.hitVec.yCoord - absOffset.getY() + mop.sideHit.getFrontOffsetY() * One32nd;
			final double zCoord = mop.hitVec.zCoord - absOffset.getZ() + mop.sideHit.getFrontOffsetZ() * One32nd;

			final int bitXi = (int) Math.floor( xCoord * VoxelBlob.dim );
			final int bitYi = (int) Math.floor( yCoord * VoxelBlob.dim );
			final int bitZi = (int) Math.floor( zCoord * VoxelBlob.dim );

			if ( bitXi < 0 || bitYi < 0 || bitZi < 0 || bitXi >= VoxelBlob.dim || bitYi >= VoxelBlob.dim || bitZi >= VoxelBlob.dim )
			{
				blockPos = mop.getBlockPos().offset( mop.sideHit );
				bitX = bitXi - mop.sideHit.getFrontOffsetX() * VoxelBlob.dim;
				bitY = bitYi - mop.sideHit.getFrontOffsetY() * VoxelBlob.dim;
				bitZ = bitZi - mop.sideHit.getFrontOffsetZ() * VoxelBlob.dim;
			}
			else
			{
				blockPos = mop.getBlockPos();
				bitX = bitXi;
				bitY = bitYi;
				bitZ = bitZi;
			}
		}
	}

	public BitLocation(
			final BlockPos pos,
			final int x,
			final int y,
			final int z )
	{
		blockPos = pos;
		bitX = x;
		bitY = y;
		bitZ = z;
	}

	public static BitLocation min(
			final BitLocation from,
			final BitLocation to )
	{
		final int bitX = Min( from.blockPos.getX(), to.blockPos.getX(), from.bitX, to.bitX );
		final int bitY = Min( from.blockPos.getY(), to.blockPos.getY(), from.bitY, to.bitY );
		final int bitZ = Min( from.blockPos.getZ(), to.blockPos.getZ(), from.bitZ, to.bitZ );

		return new BitLocation( new BlockPos(
				Math.min( from.blockPos.getX(), to.blockPos.getX() ),
				Math.min( from.blockPos.getY(), to.blockPos.getY() ),
				Math.min( from.blockPos.getZ(), to.blockPos.getZ() ) ),
				bitX, bitY, bitZ );
	}

	public static BitLocation max(
			final BitLocation from,
			final BitLocation to )
	{
		final int bitX = Max( from.blockPos.getX(), to.blockPos.getX(), from.bitX, to.bitX );
		final int bitY = Max( from.blockPos.getY(), to.blockPos.getY(), from.bitY, to.bitY );
		final int bitZ = Max( from.blockPos.getZ(), to.blockPos.getZ(), from.bitZ, to.bitZ );

		return new BitLocation( new BlockPos(
				Math.max( from.blockPos.getX(), to.blockPos.getX() ),
				Math.max( from.blockPos.getY(), to.blockPos.getY() ),
				Math.max( from.blockPos.getZ(), to.blockPos.getZ() ) ),
				bitX, bitY, bitZ );
	}

	private static int Min(
			final int x,
			final int x2,
			final int bitX2,
			final int bitX3 )
	{
		if ( x < x2 )
		{
			return bitX2;
		}
		if ( x2 == x )
		{
			return Math.min( bitX2, bitX3 );
		}

		return bitX3;
	}

	private static int Max(
			final int x,
			final int x2,
			final int bitX2,
			final int bitX3 )
	{
		if ( x > x2 )
		{
			return bitX2;
		}
		if ( x2 == x )
		{
			return Math.max( bitX2, bitX3 );
		}

		return bitX3;
	}

}
