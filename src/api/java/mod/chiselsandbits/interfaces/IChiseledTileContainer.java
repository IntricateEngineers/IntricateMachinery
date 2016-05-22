package mod.chiselsandbits.interfaces;

import mod.chiselsandbits.chiseledblock.data.VoxelBlob;

public interface IChiseledTileContainer
{

	public boolean isBlobOccluded(
			VoxelBlob blob );

	public void sendUpdate();

	public void saveData();

}
