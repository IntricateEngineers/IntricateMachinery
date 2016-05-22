package mod.chiselsandbits.chiseledblock.data;

public class BitIterator
{

	private static final int zInc = 1 << 8;
	private static final int yInc = 1 << 4;

	private static final int yMax = yInc * VoxelBlob.dim;
	private static final int zMax = zInc * VoxelBlob.dim;

	private int zOffset = 0;
	private int yOffset = 0;
	private int combined = 0;

	private int bit;

	// read-only outputs.
	public int x = -1;
	public int y;
	public int z;

	protected void yPlus()
	{
		x = 0;

		++y;
		yOffset += yInc;
	}

	protected void zPlus()
	{
		y = 0;
		yOffset = 0;

		++z;
		zOffset += zInc;
	}

	public boolean hasNext()
	{
		++x;

		if ( x >= VoxelBlob.dim )
		{
			yPlus();

			if ( yOffset >= yMax )
			{
				zPlus();

				if ( zOffset >= zMax )
				{
					done();
					return false;
				}
			}

			combined = zOffset | yOffset;
		}

		bit = combined | x;
		return true;
	}

	protected void done()
	{

	}

	public int getNext(
			final VoxelBlob blob )
	{
		return blob.getBit( bit );
	}

	public void setNext(
			final VoxelBlob blob,
			final int value )
	{
		blob.putBit( bit, value );
	}

}
