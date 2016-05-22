package mod.chiselsandbits.core.api;

import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBag;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IBitLocation;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.ItemType;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.chiseledblock.ItemBlockChiseled;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.data.BitLocation;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.client.UndoTracker;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.ChiselToolType;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.integration.mcmultipart.MCMultipartProxy;
import mod.chiselsandbits.items.ItemBitBag;
import mod.chiselsandbits.items.ItemChisel;
import mod.chiselsandbits.items.ItemChiseledBit;
import mod.chiselsandbits.items.ItemMirrorPrint;
import mod.chiselsandbits.items.ItemNegativePrint;
import mod.chiselsandbits.items.ItemPositivePrint;
import mod.chiselsandbits.items.ItemWrench;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

public class ChiselAndBitsAPI implements IChiselAndBitsAPI
{

	@Override
	public boolean canBeChiseled(
			final World world,
			final BlockPos pos )
	{
		if ( world == null || pos == null )
		{
			return false;
		}

		final IBlockState state = world.getBlockState( pos );
		return state.getBlock() == Blocks.AIR || BlockBitInfo.supportsBlock( state ) || ModUtil.getChiseledTileEntity( world, pos, false ) != null;
	}

	@Override
	public boolean isBlockChiseled(
			final World world,
			final BlockPos pos )
	{
		if ( world == null || pos == null )
		{
			return false;
		}

		return ModUtil.getChiseledTileEntity( world, pos, false ) != null;
	}

	@Override
	public IBitAccess getBitAccess(
			final World world,
			final BlockPos pos ) throws CannotBeChiseled
	{
		if ( world == null || pos == null )
		{
			throw new CannotBeChiseled();
		}

		final IBlockState state = world.getBlockState( pos );
		if ( BlockBitInfo.supportsBlock( state ) )
		{
			final VoxelBlob blob = new VoxelBlob();
			blob.fill( Block.getStateId( state ) );
			return new BitAccess( world, pos, blob, VoxelBlob.NULL_BLOB );
		}

		if ( world.isAirBlock( pos ) )
		{
			final VoxelBlob blob = new VoxelBlob();
			return new BitAccess( world, pos, blob, VoxelBlob.NULL_BLOB );
		}

		final TileEntityBlockChiseled te = ModUtil.getChiseledTileEntity( world, pos, true );
		if ( te != null )
		{
			final VoxelBlob mask = new VoxelBlob();
			MCMultipartProxy.proxyMCMultiPart.addFiller( world, pos, mask );

			return new BitAccess( world, pos, te.getBlob(), mask );
		}

		throw new CannotBeChiseled();
	}

	@Override
	public IBitBrush createBrush(
			final ItemStack bitItem ) throws InvalidBitItem
	{
		if ( bitItem == null )
		{
			return new BitBrush( 0 );
		}

		if ( bitItem.getItem() == null || getItemType( bitItem ) == ItemType.CHISLED_BIT )
		{
			final int stateID = ItemChiseledBit.getStackState( bitItem );
			final IBlockState state = Block.getStateById( stateID );

			if ( state != null && BlockBitInfo.supportsBlock( state ) )
			{
				return new BitBrush( stateID );
			}
		}

		throw new InvalidBitItem();
	}

	@Override
	public IBitLocation getBitPos(
			final float hitX,
			final float hitY,
			final float hitZ,
			final EnumFacing side,
			final BlockPos pos,
			final boolean placement )
	{
		if ( side == null || pos == null )
		{
			return null;
		}

		final RayTraceResult mop = new RayTraceResult( RayTraceResult.Type.BLOCK, new Vec3d( hitX, hitY, hitZ ), side, pos );
		return new BitLocation( mop, false, placement ? ChiselToolType.BIT : ChiselToolType.CHISEL );
	}

	@Override
	public ItemType getItemType(
			final ItemStack item )
	{
		if ( item != null && item.getItem() instanceof ItemChiseledBit )
		{
			return ItemType.CHISLED_BIT;
		}

		if ( item != null && item.getItem() instanceof ItemBitBag )
		{
			return ItemType.BIT_BAG;
		}

		if ( item != null && item.getItem() instanceof ItemChisel )
		{
			return ItemType.CHISEL;
		}

		if ( item != null && item.getItem() instanceof ItemBlockChiseled )
		{
			return ItemType.CHISLED_BLOCK;
		}

		if ( item != null && item.getItem() instanceof ItemMirrorPrint )
		{
			return ItemType.MIRROR_DESIGN;
		}

		if ( item != null && item.getItem() instanceof ItemPositivePrint )
		{
			return ItemType.POSITIVE_DESIGN;
		}

		if ( item != null && item.getItem() instanceof ItemNegativePrint )
		{
			return ItemType.NEGATIVE_DESIGN;
		}

		if ( item != null && item.getItem() instanceof ItemWrench )
		{
			return ItemType.WRENCH;
		}

		return null;
	}

	@Override
	public IBitAccess createBitItem(
			final ItemStack bitItemStack )
	{
		if ( bitItemStack == null )
		{
			return new BitAccess( null, null, new VoxelBlob(), VoxelBlob.NULL_BLOB );
		}

		final ItemType type = getItemType( bitItemStack );
		if ( type != null && type.isBitAccess )
		{
			final VoxelBlob blob = ModUtil.getBlobFromStack( bitItemStack, null );
			return new BitAccess( null, null, blob, VoxelBlob.NULL_BLOB );
		}

		if ( bitItemStack != null && bitItemStack.getItem() instanceof ItemBlock )
		{
			final ItemBlock blkItem = (ItemBlock) bitItemStack.getItem();
			final IBlockState state = blkItem.getBlock().getStateFromMeta( blkItem.getMetadata( bitItemStack ) );

			if ( BlockBitInfo.supportsBlock( state ) )
			{
				final VoxelBlob blob = new VoxelBlob();
				blob.fill( Block.getStateId( state ) );
				return new BitAccess( null, null, blob, VoxelBlob.NULL_BLOB );
			}
		}

		return null;
	}

	@Override
	public IBitBrush createBrushFromState(
			final IBlockState state ) throws InvalidBitItem
	{
		if ( !BlockBitInfo.supportsBlock( state ) )
		{
			throw new InvalidBitItem();
		}

		return new BitBrush( Block.getStateId( state ) );
	}

	@Override
	public ItemStack getBitItem(
			final IBlockState state ) throws InvalidBitItem
	{
		if ( !BlockBitInfo.supportsBlock( state ) )
		{
			throw new InvalidBitItem();
		}

		return ItemChiseledBit.createStack( Block.getStateId( state ), 1, true );
	}

	@Override
	public void giveBitToPlayer(
			final EntityPlayer player,
			final ItemStack is,
			Vec3d spawnPos )
	{
		if ( is != null )
		{
			if ( spawnPos == null )
			{
				spawnPos = new Vec3d( player.posX, player.posY, player.posZ );
			}

			final EntityItem ei = new EntityItem( player.getEntityWorld(), spawnPos.xCoord, spawnPos.yCoord, spawnPos.zCoord, is );

			if ( is.getItem() == ChiselsAndBits.getItems().itemBlockBit )
			{
				if ( player.getEntityWorld().isRemote )
				{
					return;
				}

				ModUtil.feedPlayer( player.getEntityWorld(), player, ei );
				return;
			}
			else if ( !player.inventory.addItemStackToInventory( is ) )
			{
				ei.setEntityItemStack( is );
				player.getEntityWorld().spawnEntityInWorld( ei );
			}
		}
	}

	@Override
	public IBitBag getBitbag(
			final ItemStack itemstack )
	{
		if ( itemstack != null )
		{
			final Object o = itemstack.getCapability( CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP );
			if ( o instanceof IBitBag )
			{
				return (IBitBag) o;
			}
		}

		return null;
	}

	@Override
	public void beginUndoGroup(
			final EntityPlayer player )
	{
		UndoTracker.getInstance().beginGroup( player );
	}

	@Override
	public void endUndoGroup(
			final EntityPlayer player )
	{
		UndoTracker.getInstance().endGroup( player );
	}

}
