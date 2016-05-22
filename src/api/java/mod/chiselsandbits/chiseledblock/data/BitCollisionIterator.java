package mod.chiselsandbits.chiseledblock.data;

public class BitCollisionIterator extends BitIterator
{

	public final static float One16thf = 1.0f / VoxelBlob.dim;

	public float physicalX;
	public float physicalY;
	public float physicalZ;

	public float physicalYp1 = One16thf;
	public float physicalZp1 = One16thf;

	@Override
	public boolean hasNext()
	{
		final boolean r = super.hasNext();
		physicalX = x * One16thf;
		return r;
	}

	@Override
	protected void yPlus()
	{
		super.yPlus();
		physicalY = y * One16thf;
		physicalYp1 = physicalY + One16thf;
	}

	@Override
	protected void zPlus()
	{
		super.zPlus();

		physicalZ = z * One16thf;
		physicalZp1 = physicalZ + One16thf;

		physicalY = y * One16thf;
		physicalYp1 = physicalY + One16thf;
	}

}
