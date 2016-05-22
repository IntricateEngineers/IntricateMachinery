package mod.chiselsandbits.integration.mcmultipart;

import java.util.ArrayList;
import java.util.List;

import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.INormallyOccludingPart;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.interfaces.IChiseledTileContainer;
import net.minecraft.util.math.AxisAlignedBB;

class MultipartContainerWrapper implements IChiseledTileContainer
{

	final ChiseledBlockPart container;

	public MultipartContainerWrapper(
			final ChiseledBlockPart chisledBlockPart )
	{
		container = chisledBlockPart;
	}

	@Override
	public void sendUpdate()
	{
		container.sendUpdatePacket( true );
	}

	@Override
	public void saveData()
	{
		container.saveChanges();
	}

	@Override
	public boolean isBlobOccluded(
			final VoxelBlob blob )
	{
		final ChiseledBlockPart part = new ChiseledBlockPart();
		part.getTile().setBlob( blob );

		if ( container.getContainer() == null )
		{
			return false;
		}

		// get new occlusion...
		final List<AxisAlignedBB> selfBoxes = new ArrayList<AxisAlignedBB>();
		part.addOcclusionBoxes( selfBoxes );

		// test occlusion...
		for ( final IMultipart comparePart : container.getContainer().getParts() )
		{
			if ( comparePart instanceof INormallyOccludingPart )
			{
				final List<AxisAlignedBB> partBoxes = new ArrayList<AxisAlignedBB>();
				( (INormallyOccludingPart) comparePart ).addOcclusionBoxes( partBoxes );

				for ( final AxisAlignedBB a : selfBoxes )
				{
					for ( final AxisAlignedBB b : partBoxes )
					{
						if ( a.intersectsWith( b ) )
						{
							return true;
						}
					}
				}
			}
		}

		return false;
	}

}
