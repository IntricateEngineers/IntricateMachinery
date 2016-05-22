package mod.chiselsandbits.chiseledblock.data;

public enum VoxelType
{
	AIR, SOLID, FLUID;

	public boolean shouldShow(
			final VoxelType secondVoxelType )
	{
		return this != AIR && this != secondVoxelType;
	}
}
