package mod.chiselsandbits.render.bit;

import java.util.HashMap;

import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateReference;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.interfaces.ICacheClearable;
import mod.chiselsandbits.items.ItemChiseledBit;
import mod.chiselsandbits.render.BaseSmartModel;
import mod.chiselsandbits.render.ModelCombined;
import mod.chiselsandbits.render.chiseledblock.ChiselLayer;
import mod.chiselsandbits.render.chiseledblock.ChiseledBlockBaked;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BitItemSmartModel extends BaseSmartModel implements ICacheClearable
{
	static private final HashMap<Integer, IBakedModel> modelCache = new HashMap<Integer, IBakedModel>();
	static private final HashMap<Integer, IBakedModel> largeModelCache = new HashMap<Integer, IBakedModel>();

	private IBakedModel getCachedModel(
			final int stateID,
			final boolean large )
	{
		final HashMap<Integer, IBakedModel> target = large ? largeModelCache : modelCache;
		IBakedModel out = target.get( stateID );

		if ( out == null )
		{
			if ( large )
			{
				final VoxelBlob blob = new VoxelBlob();
				blob.fill( stateID );
				final VoxelBlobStateReference ref = new VoxelBlobStateReference( blob, 0 );
				final IBakedModel a = new ChiseledBlockBaked( stateID, ChiselLayer.SOLID, ref, null, DefaultVertexFormats.ITEM );
				final IBakedModel b = new ChiseledBlockBaked( stateID, ChiselLayer.SOLID_FLUID, ref, null, DefaultVertexFormats.ITEM );
				final IBakedModel c = new ChiseledBlockBaked( stateID, ChiselLayer.CUTOUT_MIPPED, ref, null, DefaultVertexFormats.ITEM );
				final IBakedModel d = new ChiseledBlockBaked( stateID, ChiselLayer.CUTOUT, ref, null, DefaultVertexFormats.ITEM );
				final IBakedModel e = new ChiseledBlockBaked( stateID, ChiselLayer.TRANSLUCENT, ref, null, DefaultVertexFormats.ITEM );
				out = new ModelCombined( a, b, c, d, e );
			}
			else
			{
				out = new BitItemBaked( stateID );
			}

			target.put( stateID, out );
		}

		return out;
	}

	@Override
	public IBakedModel handleItemState(
			final IBakedModel originalModel,
			final ItemStack stack,
			final World world,
			final EntityLivingBase entity )
	{
		return getCachedModel( ItemChiseledBit.getStackState( stack ), ClientSide.instance.holdingShift() );
	}

	@Override
	public void clearCache()
	{
		modelCache.clear();
		largeModelCache.clear();
	}
}
