package mod.chiselsandbits.chiseledblock.data;

import java.lang.ref.WeakReference;

import mod.chiselsandbits.chiseledblock.BlockChiseled;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.render.chiseledblock.ModelRenderState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class VoxelNeighborRenderTracker
{
	private WeakReference<VoxelBlobStateReference> lastCenter;
	private ModelRenderState lrs = null;

	private boolean isDynamic;
	private boolean shouldUpdate;
	Integer[] faceCount = new Integer[4];

	public VoxelNeighborRenderTracker()
	{
		faceCount = new Integer[BlockRenderLayer.values().length];
	}

	private final ModelRenderState sides = new ModelRenderState( null );

	public boolean isAboveLimit()
	{
		int faces = 0;

		for ( int x = 0; x < faceCount.length; ++x )
		{
			if ( faceCount[x] == null )
			{
				return false;
			}

			faces += faceCount[x];
		}

		return faces >= ChiselsAndBits.getConfig().dynamicModelFaceCount;
	}

	public void setAbovelimit(
			final BlockRenderLayer layer,
			final int fc )
	{
		faceCount[layer.ordinal()] = fc;
	}

	public boolean isShouldUpdate()
	{
		final boolean out = shouldUpdate;
		shouldUpdate = false;
		return out;
	}

	public boolean isDynamic()
	{
		shouldUpdate = true;
		return isDynamic;
	}

	public void update(
			final boolean isDynamic,
			final World worldObj,
			final BlockPos pos )
	{
		if ( worldObj == null || pos == null )
		{
			return;
		}

		this.isDynamic = isDynamic;

		for ( final EnumFacing f : EnumFacing.VALUES )
		{
			final TileEntityBlockChiseled tebc = ModUtil.getChiseledTileEntity( worldObj, pos.offset( f ), false );
			if ( tebc != null )
			{
				update( f, tebc.getBasicState().getValue( BlockChiseled.UProperty_VoxelBlob ) );
			}
			else
			{
				update( f, null );
			}
		}
	}

	private void update(
			final EnumFacing f,
			final VoxelBlobStateReference value )
	{
		if ( sides.get( f ) == value )
		{
			return;
		}

		synchronized ( this )
		{
			sides.put( f, value );
			lrs = null;
		}
	}

	public ModelRenderState getRenderState(
			final VoxelBlobStateReference data )
	{
		if ( lrs == null || lastCenter == null )
		{
			lrs = new ModelRenderState( sides );
			updateCenter( data );
		}
		else if ( lastCenter.get() != data )
		{
			updateCenter( data );
			lrs = new ModelRenderState( sides );
		}

		return lrs;
	}

	private void updateCenter(
			final VoxelBlobStateReference data )
	{
		lastCenter = new WeakReference<VoxelBlobStateReference>( data );
	}

}
