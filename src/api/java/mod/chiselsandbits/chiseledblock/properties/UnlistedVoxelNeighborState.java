package mod.chiselsandbits.chiseledblock.properties;

import mod.chiselsandbits.chiseledblock.data.VoxelNeighborRenderTracker;
import net.minecraftforge.common.property.IUnlistedProperty;

public final class UnlistedVoxelNeighborState implements IUnlistedProperty<VoxelNeighborRenderTracker>
{
	@Override
	public String getName()
	{
		return "vb";
	}

	@Override
	public boolean isValid(
			final VoxelNeighborRenderTracker value )
	{
		return true;
	}

	@Override
	public Class<VoxelNeighborRenderTracker> getType()
	{
		return VoxelNeighborRenderTracker.class;
	}

	@Override
	public String valueToString(
			final VoxelNeighborRenderTracker value )
	{
		return value.toString();
	}
}