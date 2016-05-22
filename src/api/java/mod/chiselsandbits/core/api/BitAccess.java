package mod.chiselsandbits.core.api;

import java.util.HashMap;
import java.util.Map;

import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IBitVisitor;
import mod.chiselsandbits.api.ItemType;
import mod.chiselsandbits.chiseledblock.BlockChiseled;
import mod.chiselsandbits.chiseledblock.ItemBlockChiseled;
import mod.chiselsandbits.chiseledblock.NBTBlobConverter;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.data.BitIterator;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob.BlobStats;
import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateReference;
import mod.chiselsandbits.client.UndoTracker;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.ModUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BitAccess implements IBitAccess
{

	private final World world;
	private final BlockPos pos;
	private final VoxelBlob blob;
	private final VoxelBlob filler;

	private final Map<Integer, IBitBrush> brushes = new HashMap<Integer, IBitBrush>();

	public VoxelBlob getNativeBlob()
	{
		return blob;
	}

	public BitAccess(
			final World worldIn,
			final BlockPos pos,
			final VoxelBlob blob,
			final VoxelBlob filler )
	{
		world = worldIn;
		this.pos = pos;
		this.blob = blob;
		this.filler = filler;
	}

	@Override
	public IBitBrush getBitAt(
			final int x,
			final int y,
			final int z )
	{
		return getBrushForState( blob.getSafe( x, y, z ) );
	}

	private IBitBrush getBrushForState(
			final int state )
	{
		IBitBrush brush = brushes.get( state );

		if ( brush == null )
		{
			brushes.put( state, brush = new BitBrush( state ) );
		}

		return brush;
	}

	@Override
	public void setBitAt(
			int x,
			int y,
			int z,
			final IBitBrush bit ) throws SpaceOccupied
	{
		int state = 0;

		if ( bit instanceof BitBrush )
		{
			state = bit.getStateID();
		}

		// make sure that they are only 0-15
		x = x & 0xf;
		y = y & 0xf;
		z = z & 0xf;

		if ( filler.get( x, y, z ) == 0 )
		{
			blob.set( x, y, z, state );
		}
		else
		{
			throw new SpaceOccupied();
		}
	}

	@Override
	public void commitChanges(
			final boolean triggerUpdates )
	{
		TileEntityBlockChiseled tile = ModUtil.getChiseledTileEntity( world, pos, true );
		final BlobStats cb = blob.getVoxelStats();

		if ( tile == null && BlockChiseled.replaceWithChisled( world, pos, world.getBlockState( pos ), cb.mostCommonState, false ) )
		{
			tile = ModUtil.getChiseledTileEntity( world, pos, true );
		}

		if ( tile != null )
		{
			final VoxelBlobStateReference before = tile.getBlobStateReference();
			tile.setBlob( blob, triggerUpdates );
			final VoxelBlobStateReference after = tile.getBlobStateReference();

			UndoTracker.getInstance().add( world, pos, before, after );
		}
	}

	@Override
	public void commitChanges()
	{
		commitChanges( true );
	}

	@Override
	public ItemStack getBitsAsItem(
			final EnumFacing side,
			final ItemType type,
			final boolean crossWorld )
	{
		if ( type == null )
		{
			return null;
		}

		final BlobStats cb = blob.getVoxelStats();
		if ( cb.mostCommonState == 0 )
		{
			return null;
		}

		final NBTBlobConverter c = new NBTBlobConverter();
		c.setBlob( blob );

		final NBTTagCompound nbttagcompound = new NBTTagCompound();
		c.writeChisleData( nbttagcompound, crossWorld );

		final ItemStack itemstack;

		if ( type == ItemType.CHISLED_BLOCK )
		{
			final IBlockState state = Block.getStateById( cb.mostCommonState );
			final BlockChiseled blk = ChiselsAndBits.getBlocks().getConversion( state );

			if ( blk == null )
			{
				return null;
			}

			itemstack = new ItemStack( blk, 1 );
			itemstack.setTagInfo( ItemBlockChiseled.NBT_CHISELED_DATA, nbttagcompound );
		}
		else
		{
			switch ( type )
			{
				case MIRROR_DESIGN:
					itemstack = new ItemStack( ChiselsAndBits.getItems().itemMirrorprint );
					break;
				case NEGATIVE_DESIGN:
					itemstack = new ItemStack( ChiselsAndBits.getItems().itemNegativeprint );
					break;
				case POSITIVE_DESIGN:
					itemstack = new ItemStack( ChiselsAndBits.getItems().itemPositiveprint );
					break;
				default:
					return null;
			}

			itemstack.setTagCompound( nbttagcompound );
		}

		if ( side != null )
		{
			itemstack.setTagInfo( ItemBlockChiseled.NBT_SIDE, new NBTTagByte( (byte) side.ordinal() ) );
		}

		return itemstack;
	}

	@Override
	public ItemStack getBitsAsItem(
			final EnumFacing side,
			final ItemType type )
	{
		return getBitsAsItem( side, type, false );
	}

	@Override
	public void visitBits(
			final IBitVisitor visitor )
	{
		final BitIterator bi = new BitIterator();
		IBitBrush brush = getBrushForState( 0 );
		while ( bi.hasNext() )
		{
			if ( bi.getNext( filler ) == 0 )
			{
				final int stateID = bi.getNext( blob );

				// Most blocks are mostly the same bit type, so if it dosn't
				// change just keep the current brush, only if they differ
				// should we bother looking it up again.
				if ( stateID != brush.getStateID() )
				{
					brush = getBrushForState( stateID );
				}

				final IBitBrush after = visitor.visitBit( bi.x, bi.y, bi.z, brush );

				if ( brush != after )
				{
					if ( after == null )
					{
						bi.setNext( blob, 0 );
					}
					else
					{
						bi.setNext( blob, after.getStateID() );
					}
				}
			}
		}
	}

}
