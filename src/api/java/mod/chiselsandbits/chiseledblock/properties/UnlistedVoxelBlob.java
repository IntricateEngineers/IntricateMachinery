package mod.chiselsandbits.chiseledblock.properties;

import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateReference;
import net.minecraftforge.common.property.IUnlistedProperty;

public final class UnlistedVoxelBlob implements IUnlistedProperty<VoxelBlobStateReference>
{
	@Override
	public String getName()
	{
		return "v";
	}

	@Override
	public boolean isValid(
			final VoxelBlobStateReference value )
	{
		return true;
	}

	@Override
	public Class<VoxelBlobStateReference> getType()
	{
		return VoxelBlobStateReference.class;
	}

	@Override
	public String valueToString(
			final VoxelBlobStateReference value )
	{
		return value.toString();
	}
}