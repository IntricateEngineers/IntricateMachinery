package mod.chiselsandbits.chiseledblock;

import java.io.IOException;

import io.netty.buffer.Unpooled;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob.BlobStats;
import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateReference;
import mod.chiselsandbits.core.ChiselsAndBits;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class NBTBlobConverter
{

	public static final String NBT_SIDE_FLAGS = "s";
	public static final String NBT_NORMALCUBE_FLAG = "nc";
	public static final String NBT_LIGHTVALUE = "lv";

	public static final String NBT_PRIMARY_STATE = "b";
	public static final String NBT_LEGACY_VOXEL = "v";
	public static final String NBT_VERSIONED_VOXEL = "X";

	TileEntityBlockChiseled tile;

	private int sideState;
	private int lightValue;
	private boolean isNormalCube;
	private int primaryBlockState;
	private VoxelBlobStateReference voxelBlobRef;

	private int format = -1;
	private final boolean triggerUpdates;

	public int getSideState()
	{
		return sideState;
	}

	public int getLightValue()
	{
		return lightValue;
	}

	public boolean isNormalCube()
	{
		return isNormalCube;
	}

	public int getPrimaryBlockStateID()
	{
		return primaryBlockState;
	}

	public IBlockState getPrimaryBlockState()
	{
		return Block.getStateById( primaryBlockState );
	}

	public VoxelBlobStateReference getVoxelRef(
			final int version,
			final long weight )
	{
		final VoxelBlobStateReference voxelRef = getRef();

		if ( format == version )
		{
			return new VoxelBlobStateReference( voxelRef.getByteArray(), weight );
		}

		return new VoxelBlobStateReference( voxelRef.getVoxelBlob().blobToBytes( version ), weight );
	}

	public NBTBlobConverter()
	{
		triggerUpdates = false;
	}

	public NBTBlobConverter(
			final boolean triggerBlockUpdates,
			final TileEntityBlockChiseled tile )
	{
		this.tile = tile;

		triggerUpdates = triggerBlockUpdates;
		sideState = tile.sideState;
		lightValue = tile.getLightValue();
		isNormalCube = tile.isNormalCube;
		primaryBlockState = Block.getStateId( tile.getBlockState( Blocks.COBBLESTONE ) );
		voxelBlobRef = tile.getBlobStateReference();
		format = getFormat( voxelBlobRef );
	}

	public void fillWith(
			final IBlockState state )
	{
		voxelBlobRef = new VoxelBlobStateReference( Block.getStateId( state ), 0 );
		updateFromBlob();
	}

	public void setBlob(
			final VoxelBlob vb )
	{
		voxelBlobRef = new VoxelBlobStateReference( vb, 0 );
		format = getFormat( voxelBlobRef );
		updateFromBlob();
	}

	public final void writeChisleData(
			final NBTTagCompound compound,
			final boolean crossWorld )
	{
		final VoxelBlobStateReference voxelRef = getRef();

		if ( primaryBlockState == 0 )
		{
			return;
		}

		final int newFormat = crossWorld ? VoxelBlob.VERSION_CROSSWORLD : VoxelBlob.VERSION_COMPACT;
		final byte[] voxelBytes = newFormat == format ? voxelRef.getByteArray() : voxelRef.getVoxelBlob().blobToBytes( newFormat );

		compound.setInteger( NBT_LIGHTVALUE, lightValue );
		compound.setInteger( NBT_PRIMARY_STATE, primaryBlockState );
		compound.setInteger( NBT_SIDE_FLAGS, sideState );
		compound.setBoolean( NBT_NORMALCUBE_FLAG, isNormalCube );
		compound.setByteArray( NBT_VERSIONED_VOXEL, voxelBytes );
	}

	public final void readChisleData(
			final NBTTagCompound compound )
	{
		sideState = compound.getInteger( NBT_SIDE_FLAGS );
		primaryBlockState = compound.getInteger( NBT_PRIMARY_STATE );
		lightValue = compound.getInteger( NBT_LIGHTVALUE );
		isNormalCube = compound.getBoolean( NBT_NORMALCUBE_FLAG );
		byte[] v = compound.getByteArray( NBT_VERSIONED_VOXEL );

		if ( v == null || v.length == 0 )
		{
			final byte[] vx = compound.getByteArray( NBT_LEGACY_VOXEL );
			if ( v != null && vx.length > 0 )
			{
				final VoxelBlob bx = new VoxelBlob();

				try
				{
					bx.fromLegacyByteArray( vx );
				}
				catch ( final IOException e )
				{
				}

				v = bx.blobToBytes( VoxelBlob.VERSION_COMPACT );
				format = VoxelBlob.VERSION_COMPACT;
			}
		}

		if ( primaryBlockState == 0 )
		{
			// if load fails default to cobble stone...
			primaryBlockState = Block.getStateId( Blocks.COBBLESTONE.getDefaultState() );
		}

		voxelBlobRef = new VoxelBlobStateReference( v, 0 );
		format = getFormat( voxelBlobRef );

		if ( tile != null )
		{
			tile.updateBlob( this, triggerUpdates );
		}
	}

	private int getFormat(
			final VoxelBlobStateReference v )
	{
		if ( v == null || v.getByteArray() == null || v.getByteArray().length == 0 )
		{
			return -1;
		}

		final PacketBuffer header = new PacketBuffer( Unpooled.wrappedBuffer( v.getByteArray() ) );
		return header.readVarIntFromBuffer();
	}

	public void updateFromBlob()
	{
		final VoxelBlob vb = getRef().getVoxelBlob();

		final BlobStats common = vb.getVoxelStats();
		final float floatLight = common.blockLight;

		isNormalCube = common.isNormalBlock;
		lightValue = Math.max( 0, Math.min( 15, (int) ( floatLight * 15 ) ) );
		sideState = vb.getSideFlags( 5, 11, 4 * 4 );
		primaryBlockState = common.mostCommonState;
	}

	public ItemStack getItemStack(
			final boolean crossWorld )
	{
		final Block blk = ChiselsAndBits.getBlocks().getConversion( getPrimaryBlockState() );

		if ( blk != null )
		{
			final ItemStack is = new ItemStack( blk );
			final NBTTagCompound compound = is.getSubCompound( ItemBlockChiseled.NBT_CHISELED_DATA, true );
			writeChisleData( compound, crossWorld );

			if ( !compound.hasNoTags() )
			{
				return is;
			}
		}

		return null;
	}

	private VoxelBlobStateReference getRef()
	{
		if ( voxelBlobRef == null )
		{
			voxelBlobRef = new VoxelBlobStateReference( 0, 0 );
		}

		return voxelBlobRef;
	}

	public VoxelBlob getBlob()
	{
		return getRef().getVoxelBlob();
	}

}
