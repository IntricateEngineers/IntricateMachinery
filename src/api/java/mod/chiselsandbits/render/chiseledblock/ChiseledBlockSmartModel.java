package mod.chiselsandbits.render.chiseledblock;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import mod.chiselsandbits.chiseledblock.BlockChiseled;
import mod.chiselsandbits.chiseledblock.ItemBlockChiseled;
import mod.chiselsandbits.chiseledblock.NBTBlobConverter;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateInstance;
import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateReference;
import mod.chiselsandbits.chiseledblock.data.VoxelNeighborRenderTracker;
import mod.chiselsandbits.interfaces.ICacheClearable;
import mod.chiselsandbits.render.BaseSmartModel;
import mod.chiselsandbits.render.ModelCombined;
import mod.chiselsandbits.render.NullBakedModel;
import mod.chiselsandbits.render.cache.CacheMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.client.FMLClientHandler;

public class ChiseledBlockSmartModel extends BaseSmartModel implements ICacheClearable
{

	static final CacheMap<VoxelBlobStateReference, ChiseledBlockBaked> solidCache = new CacheMap<VoxelBlobStateReference, ChiseledBlockBaked>();
	static final CacheMap<ItemStack, IBakedModel> itemToModel = new CacheMap<ItemStack, IBakedModel>();
	static final CacheMap<VoxelBlobStateInstance, Integer> sideCache = new CacheMap<VoxelBlobStateInstance, Integer>();

	@SuppressWarnings( "unchecked" )
	static private final Map<ModelRenderState, ChiseledBlockBaked>[] modelCache = new Map[5];

	static
	{
		final int count = ChiselLayer.values().length;

		if ( modelCache.length != count )
		{
			throw new RuntimeException( "Invalid Number of BlockRenderLayer" );
		}

		// setup layers.
		for ( final ChiselLayer l : ChiselLayer.values() )
		{
			modelCache[l.ordinal()] = Collections.synchronizedMap( new WeakHashMap<ModelRenderState, ChiseledBlockBaked>() );
		}
	}

	public static int getSides(
			final TileEntityBlockChiseled te )
	{
		final VoxelBlobStateReference ref = te.getBlobStateReference();
		Integer out = null;

		if ( ref == null )
		{
			return 0;
		}

		synchronized ( sideCache )
		{
			out = sideCache.get( ref.getInstance() );
			if ( out == null )
			{
				final VoxelBlob blob = ref.getVoxelBlob();

				// ignore non-solid, and fluids.
				blob.filter( BlockRenderLayer.SOLID );
				blob.filterFluids( false );

				out = blob.getSideFlags( 0, VoxelBlob.dim_minus_one, VoxelBlob.dim2 );
				sideCache.put( ref.getInstance(), out );
			}
		}

		return out;
	}

	public static ChiseledBlockBaked getCachedModel(
			final TileEntityBlockChiseled te,
			final ChiselLayer layer )
	{
		final IExtendedBlockState myState = te.getBasicState();

		final VoxelBlobStateReference data = myState.getValue( BlockChiseled.UProperty_VoxelBlob );
		final VoxelNeighborRenderTracker rTracker = myState.getValue( BlockChiseled.UProperty_VoxelNeighborState );
		Integer blockP = myState.getValue( BlockChiseled.UProperty_Primary_BlockState );

		blockP = blockP == null ? 0 : blockP;

		return getCachedModel( blockP, data, getRenderState( rTracker, data ), layer, getModelFormat() );
	}

	private static VertexFormat getModelFormat()
	{
		return hasOptifine() ? DefaultVertexFormats.ITEM : ChiselsAndBitsBakedQuad.VERTEX_FORMAT;
	}

	private static boolean hasOptifine()
	{
		return FMLClientHandler.instance().hasOptifine();
	}

	private static ChiseledBlockBaked getCachedModel(
			final Integer blockP,
			final VoxelBlobStateReference data,
			final ModelRenderState mrs,
			final ChiselLayer layer,
			final VertexFormat format )
	{
		if ( data == null )
		{
			return new ChiseledBlockBaked( blockP, layer, data, new ModelRenderState( null ), format );
		}

		ChiseledBlockBaked out = null;

		if ( format == getModelFormat() )
		{
			if ( layer == ChiselLayer.SOLID )
			{
				out = solidCache.get( data );
			}
			else
			{
				out = mrs == null ? null : modelCache[layer.ordinal()].get( mrs );
			}
		}

		if ( out == null )
		{
			out = new ChiseledBlockBaked( blockP, layer, data, mrs, format );

			if ( out.isEmpty() )
			{
				out = ChiseledBlockBaked.breakingParticleModel( layer, blockP );
			}

			if ( format == getModelFormat() )
			{
				if ( layer == ChiselLayer.SOLID )
				{
					solidCache.put( data, out );
				}
				else if ( mrs != null )
				{
					modelCache[layer.ordinal()].put( mrs, out );
				}
			}
		}

		return out;
	}

	@Override
	public IBakedModel handleBlockState(
			final IBlockState state,
			final long rand )
	{
		if ( state == null )
		{
			return NullBakedModel.instance;
		}

		final IExtendedBlockState myState = (IExtendedBlockState) state;

		final VoxelBlobStateReference data = myState.getValue( BlockChiseled.UProperty_VoxelBlob );
		final VoxelNeighborRenderTracker rTracker = myState.getValue( BlockChiseled.UProperty_VoxelNeighborState );
		Integer blockP = myState.getValue( BlockChiseled.UProperty_Primary_BlockState );

		blockP = blockP == null ? 0 : blockP;

		final BlockRenderLayer layer = net.minecraftforge.client.MinecraftForgeClient.getRenderLayer();

		if ( layer == null )
		{
			final ChiseledBlockBaked[] models = new ChiseledBlockBaked[ChiselLayer.values().length];
			int o = 0;

			for ( final ChiselLayer l : ChiselLayer.values() )
			{
				models[o++] = getCachedModel( blockP, data, getRenderState( rTracker, data ), l, getModelFormat() );
			}

			return new ModelCombined( models );
		}

		if ( rTracker != null && rTracker.isDynamic() )
		{
			return ChiseledBlockBaked.breakingParticleModel( ChiselLayer.fromLayer( layer, false ), blockP );
		}

		IBakedModel baked = null;
		int faces = 0;

		if ( layer == BlockRenderLayer.SOLID )
		{
			final ChiseledBlockBaked a = getCachedModel( blockP, data, getRenderState( rTracker, data ), ChiselLayer.fromLayer( layer, false ), getModelFormat() );
			final ChiseledBlockBaked b = getCachedModel( blockP, data, getRenderState( rTracker, data ), ChiselLayer.fromLayer( layer, true ), getModelFormat() );

			faces = a.faceCount() + b.faceCount();

			if ( a.isEmpty() )
			{
				baked = b;
			}
			else if ( b.isEmpty() )
			{
				baked = a;
			}
			else
			{
				baked = new ModelCombined( a, b );
			}
		}
		else
		{
			final ChiseledBlockBaked t = getCachedModel( blockP, data, getRenderState( rTracker, data ), ChiselLayer.fromLayer( layer, false ), getModelFormat() );
			faces = t.faceCount();
			baked = t;
		}

		if ( rTracker != null )
		{
			rTracker.setAbovelimit( layer, faces );
		}

		return baked;
	}

	private static ModelRenderState getRenderState(
			final VoxelNeighborRenderTracker renderTracker,
			final VoxelBlobStateReference data )
	{
		if ( renderTracker != null )
		{
			return renderTracker.getRenderState( data );
		}

		return null;
	}

	@Override
	public IBakedModel handleItemState(
			final IBakedModel originalModel,
			final ItemStack stack,
			final World world,
			final EntityLivingBase entity )
	{
		IBakedModel mdl = itemToModel.get( stack );

		if ( mdl != null )
		{
			return mdl;
		}

		NBTTagCompound c = stack.getTagCompound();
		if ( c == null )
		{
			return this;
		}

		c = c.getCompoundTag( ItemBlockChiseled.NBT_CHISELED_DATA );
		if ( c == null )
		{
			return this;
		}

		final byte[] data = c.getByteArray( NBTBlobConverter.NBT_LEGACY_VOXEL );
		byte[] vdata = c.getByteArray( NBTBlobConverter.NBT_VERSIONED_VOXEL );
		final Integer blockP = c.getInteger( NBTBlobConverter.NBT_PRIMARY_STATE );

		if ( ( vdata == null || vdata.length == 0 ) && data != null && data.length > 0 )
		{
			final VoxelBlob xx = new VoxelBlob();

			try
			{
				xx.fromLegacyByteArray( data );
			}
			catch ( final IOException e )
			{
				// :_(
			}

			vdata = xx.blobToBytes( VoxelBlob.VERSION_COMPACT );
		}

		final IBakedModel[] models = new IBakedModel[ChiselLayer.values().length];
		for ( final ChiselLayer l : ChiselLayer.values() )
		{
			models[l.ordinal()] = getCachedModel( blockP, new VoxelBlobStateReference( vdata, 0L ), null, l, DefaultVertexFormats.ITEM );
		}

		mdl = new ModelCombined( models );

		itemToModel.put( stack, mdl );

		return mdl;
	}

	@Override
	public void clearCache()
	{
		for ( final ChiselLayer l : ChiselLayer.values() )
		{
			modelCache[l.ordinal()].clear();
		}

		solidCache.clear();
		itemToModel.clear();
	}

}
