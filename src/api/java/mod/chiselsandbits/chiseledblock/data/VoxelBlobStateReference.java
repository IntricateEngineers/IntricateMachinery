package mod.chiselsandbits.chiseledblock.data;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import mod.chiselsandbits.chiseledblock.BoxType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class VoxelBlobStateReference implements Comparable<VoxelBlobStateReference>
{

	private static Map<VoxelBlobStateInstance, WeakReference<VoxelBlobStateInstance>> serverRefs = Collections.synchronizedMap( new WeakHashMap<VoxelBlobStateInstance, WeakReference<VoxelBlobStateInstance>>() );
	private static Map<VoxelBlobStateInstance, WeakReference<VoxelBlobStateInstance>> clientRefs = Collections.synchronizedMap( new WeakHashMap<VoxelBlobStateInstance, WeakReference<VoxelBlobStateInstance>>() );

	// optimize air.
	private static byte[] airBlob;

	private static Map<VoxelBlobStateInstance, WeakReference<VoxelBlobStateInstance>> getRefs()
	{
		if ( FMLCommonHandler.instance().getEffectiveSide().isClient() )
		{
			return clientRefs;
		}

		return serverRefs;
	}

	private static VoxelBlobStateInstance lookupRef(
			final VoxelBlobStateInstance inst )
	{
		final WeakReference<VoxelBlobStateInstance> ref = getRefs().get( inst );

		if ( ref != null )
		{
			return ref.get();
		}

		return null;
	}

	private static byte[] findBytesFor(
			final int stateId )
	{
		if ( stateId == 0 )
		{
			if ( airBlob == null )
			{
				final VoxelBlob vb = new VoxelBlob();
				airBlob = vb.blobToBytes( VoxelBlob.VERSION_COMPACT );
			}

			return airBlob;
		}

		final VoxelBlob vb = new VoxelBlob();
		vb.fill( stateId );
		return vb.blobToBytes( VoxelBlob.VERSION_COMPACT );
	}

	private static void addRef(
			final VoxelBlobStateInstance inst )
	{
		getRefs().put( inst, new WeakReference<VoxelBlobStateInstance>( inst ) );
	}

	private static VoxelBlobStateInstance FindRef(
			final byte[] v )
	{
		final VoxelBlobStateInstance t = new VoxelBlobStateInstance( v );
		VoxelBlobStateInstance ref = null;

		ref = lookupRef( t );

		if ( ref == null )
		{
			ref = t;
			addRef( t );
		}

		return ref;
	}

	private final VoxelBlobStateInstance data;
	public final long weight;

	public VoxelBlobStateInstance getInstance()
	{
		return data;
	}

	public byte[] getByteArray()
	{
		return data.voxelBytes;
	}

	public VoxelBlob getVoxelBlob()
	{
		return data.getBlob();
	}

	public VoxelBlobStateReference(
			final VoxelBlob blob,
			final long weight )
	{
		this( blob.blobToBytes( VoxelBlob.VERSION_COMPACT ), weight );
		data.blob = new SoftReference<VoxelBlob>( new VoxelBlob( blob ) );
	}

	public VoxelBlobStateReference(
			final int stateId,
			final long weight )
	{
		this( findBytesFor( stateId ), weight );
	}

	public VoxelBlobStateReference(
			final byte[] v,
			final long weight )
	{
		data = FindRef( v );
		this.weight = weight;
	}

	@Override
	public boolean equals(
			final Object obj )
	{
		if ( !( obj instanceof VoxelBlobStateReference ) )
		{
			return false;
		}

		final VoxelBlobStateReference second = (VoxelBlobStateReference) obj;
		return data.equals( second.data ) && second.weight == weight;
	}

	@Override
	public int hashCode()
	{
		return data.hash ^ (int) ( weight ^ weight >>> 32 );
	}

	@Override
	public int compareTo(
			final VoxelBlobStateReference o )
	{
		final int comp = data.compareTo( o.data );
		if ( comp == 0 )
		{
			if ( weight == o.weight )
			{
				return 0;
			}

			return weight < o.weight ? -1 : 1;
		}
		return comp;
	}

	public Collection<AxisAlignedBB> getBoxes(
			final BoxType type )
	{
		return data.getBoxes( type );
	}

}
