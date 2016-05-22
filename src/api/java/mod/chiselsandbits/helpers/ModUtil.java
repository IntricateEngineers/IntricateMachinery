package mod.chiselsandbits.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import mod.chiselsandbits.bitbag.BagInventory;
import mod.chiselsandbits.chiseledblock.ItemBlockChiseled;
import mod.chiselsandbits.chiseledblock.NBTBlobConverter;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.data.IntegerBox;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.integration.mcmultipart.MCMultipartProxy;
import mod.chiselsandbits.items.ItemBitBag;
import mod.chiselsandbits.items.ItemBitBag.BagPos;
import mod.chiselsandbits.items.ItemChiseledBit;
import mod.chiselsandbits.items.ItemNegativePrint;
import mod.chiselsandbits.items.ItemPositivePrint;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ModUtil
{

	private final static Random RAND = new Random();
	private final static float DEG_TO_RAD = 0.017453292f;

	static public EnumFacing getPlaceFace(
			final EntityLivingBase placer )
	{
		return EnumFacing.getHorizontal( MathHelper.floor_double( placer.rotationYaw * 4.0F / 360.0F + 0.5D ) & 3 ).getOpposite();
	}

	static public Pair<Vec3d, Vec3d> getPlayerRay(
			final EntityPlayer playerIn )
	{
		double reachDistance = 5.0d;

		final double x = playerIn.prevPosX + ( playerIn.posX - playerIn.prevPosX );
		final double y = playerIn.prevPosY + ( playerIn.posY - playerIn.prevPosY ) + playerIn.getEyeHeight();
		final double z = playerIn.prevPosZ + ( playerIn.posZ - playerIn.prevPosZ );

		final float playerPitch = playerIn.prevRotationPitch + ( playerIn.rotationPitch - playerIn.prevRotationPitch );
		final float playerYaw = playerIn.prevRotationYaw + ( playerIn.rotationYaw - playerIn.prevRotationYaw );

		final float yawRayX = MathHelper.sin( -playerYaw * DEG_TO_RAD - (float) Math.PI );
		final float yawRayZ = MathHelper.cos( -playerYaw * DEG_TO_RAD - (float) Math.PI );

		final float pitchMultiplier = -MathHelper.cos( -playerPitch * DEG_TO_RAD );
		final float eyeRayY = MathHelper.sin( -playerPitch * DEG_TO_RAD );
		final float eyeRayX = yawRayX * pitchMultiplier;
		final float eyeRayZ = yawRayZ * pitchMultiplier;

		if ( playerIn instanceof EntityPlayerMP )
		{
			reachDistance = ( (EntityPlayerMP) playerIn ).interactionManager.getBlockReachDistance();
		}

		final Vec3d from = new Vec3d( x, y, z );
		final Vec3d to = from.addVector( eyeRayX * reachDistance, eyeRayY * reachDistance, eyeRayZ * reachDistance );

		return Pair.of( from, to );
	}

	static public class ItemStackSlot
	{
		private final IInventory inv;
		private final int slot;
		private final ItemStack stack;
		private final boolean isCreative;
		private final boolean isEditable;
		private final int toolSlot;

		ItemStackSlot(
				final IInventory i,
				final int s,
				final ItemStack st,
				final ActingPlayer player,
				final boolean canEdit )
		{
			inv = i;
			slot = s;
			stack = st;
			toolSlot = player.getCurrentItem();
			isCreative = player.isCreative();
			isEditable = canEdit;
		}

		public boolean isValid()
		{
			return isEditable && ( isCreative || stack != null && stack.stackSize > 0 );
		}

		public void damage(
				final ActingPlayer who )
		{
			if ( isCreative )
			{
				return;
			}

			who.damageItem( stack, 1 );
			if ( stack.stackSize <= 0 )
			{
				who.playerDestroyItem( stack, who.getHand() );
				inv.setInventorySlotContents( slot, null );
			}
		}

		public void consume()
		{
			if ( isCreative )
			{
				return;
			}

			stack.stackSize--;
			if ( stack.stackSize <= 0 )
			{
				inv.setInventorySlotContents( slot, null );
			}
		}

		public ItemStack getStack()
		{
			return stack;
		}

		public void swapWithWeapon()
		{
			final ItemStack it = inv.getStackInSlot( toolSlot );
			inv.setInventorySlotContents( toolSlot, inv.getStackInSlot( slot ) );
			inv.setInventorySlotContents( slot, it );
		}
	};

	static public ItemStackSlot findBit(
			final ActingPlayer who,
			final BlockPos pos,
			final int StateID )
	{
		final ItemStack inHand = who.getCurrentEquippedItem();
		final IInventory inv = who.getInventory();
		final boolean canEdit = who.canPlayerManipulate( pos, EnumFacing.UP, inHand );

		if ( inHand != null && inHand.stackSize > 0 && inHand.getItem() instanceof ItemChiseledBit && ItemChiseledBit.getStackState( inHand ) == StateID )
		{
			return new ItemStackSlot( inv, who.getCurrentItem(), inHand, who, canEdit );
		}

		for ( int x = 0; x < inv.getSizeInventory(); x++ )
		{
			final ItemStack is = inv.getStackInSlot( x );
			if ( is != null && is.stackSize > 0 && is.getItem() instanceof ItemChiseledBit && ItemChiseledBit.sameBit( is, StateID ) )
			{
				return new ItemStackSlot( inv, x, is, who, canEdit );
			}
		}

		return new ItemStackSlot( null, -1, null, who, canEdit );
	}

	public static boolean isHoldingPattern(
			final EntityPlayer player )
	{
		final ItemStack inHand = player.getHeldItemMainhand();

		if ( inHand != null && inHand.getItem() instanceof ItemPositivePrint )
		{
			return true;
		}

		if ( inHand != null && inHand.getItem() instanceof ItemNegativePrint )
		{
			return true;
		}

		return false;
	}

	public static boolean isHoldingChiseledBlock(
			final EntityPlayer player )
	{
		final ItemStack inHand = player.getHeldItemMainhand();

		if ( inHand != null && inHand.getItem() instanceof ItemBlockChiseled )
		{
			return true;
		}

		return false;
	}

	public static int getRotationIndex(
			final EnumFacing face )
	{
		return face.getHorizontalIndex();
	}

	public static int getRotations(
			final EntityLivingBase placer,
			final byte side )
	{
		final EnumFacing newFace = ModUtil.getPlaceFace( placer );
		final EnumFacing oldYaw = EnumFacing.VALUES[side];

		int rotations = getRotationIndex( newFace ) - getRotationIndex( oldYaw );

		// work out the rotation math...
		while ( rotations < 0 )
		{
			rotations = 4 + rotations;
		}
		while ( rotations > 4 )
		{
			rotations = rotations - 4;
		}

		return 4 - rotations;
	}

	public static BlockPos getPartialOffset(
			final EnumFacing side,
			final BlockPos partial,
			final IntegerBox modelBounds )
	{
		int offset_x = modelBounds.minX;
		int offset_y = modelBounds.minY;
		int offset_z = modelBounds.minZ;

		final int partial_x = partial.getX();
		final int partial_y = partial.getY();
		final int partial_z = partial.getZ();

		int middle_x = ( modelBounds.maxX - modelBounds.minX ) / -2;
		int middle_y = ( modelBounds.maxY - modelBounds.minY ) / -2;
		int middle_z = ( modelBounds.maxZ - modelBounds.minZ ) / -2;

		switch ( side )
		{
			case DOWN:
				offset_y = modelBounds.maxY;
				middle_y = 0;
				break;
			case EAST:
				offset_x = modelBounds.minX;
				middle_x = 0;
				break;
			case NORTH:
				offset_z = modelBounds.maxZ;
				middle_z = 0;
				break;
			case SOUTH:
				offset_z = modelBounds.minZ;
				middle_z = 0;
				break;
			case UP:
				offset_y = modelBounds.minY;
				middle_y = 0;
				break;
			case WEST:
				offset_x = modelBounds.maxX;
				middle_x = 0;
				break;
			default:
				throw new NullPointerException();
		}

		final int t_x = -offset_x + middle_x + partial_x;
		final int t_y = -offset_y + middle_y + partial_y;
		final int t_z = -offset_z + middle_z + partial_z;

		return new BlockPos( t_x, t_y, t_z );
	}

	static public <T> T firstNonNull(
			final T... options )
	{
		for ( final T i : options )
		{
			if ( i != null )
			{
				return i;
			}
		}

		throw new NullPointerException( "Unable to find a non null item." );
	}

	public static TileEntityBlockChiseled getChiseledTileEntity(
			final World world,
			final BlockPos pos,
			final boolean create )
	{
		final TileEntity te = world.getTileEntity( pos );
		if ( te instanceof TileEntityBlockChiseled )
		{
			return (TileEntityBlockChiseled) te;
		}

		return MCMultipartProxy.proxyMCMultiPart.getChiseledTileEntity( world, pos, create );
	}

	public static void removeChisledBlock(
			final World world,
			final BlockPos pos )
	{
		final TileEntity te = world.getTileEntity( pos );

		if ( te instanceof TileEntityBlockChiseled )
		{
			world.setBlockToAir( pos ); // no physical matter left...
			return;
		}

		MCMultipartProxy.proxyMCMultiPart.removeChisledBlock( te );
	}

	private final static Random itemRand = new Random();

	public static void feedPlayer(
			final World world,
			final EntityPlayer player,
			final EntityItem ei )
	{
		ItemStack is = ei.getEntityItem();

		final List<BagPos> bags = ItemBitBag.getBags( player.inventory );

		if ( !containsAtLeastOneOf( player.inventory, is ) )
		{
			final ItemStack minSize = is.copy();

			if ( minSize.stackSize > minSize.getMaxStackSize() )
			{
				minSize.stackSize = minSize.getMaxStackSize();
			}

			is.stackSize -= minSize.stackSize;
			player.inventory.addItemStackToInventory( minSize );
			is.stackSize += minSize.stackSize;
		}

		for ( final BagPos bp : bags )
		{
			is = bp.inv.insertItem( is );
		}

		if ( is != null && !player.inventory.addItemStackToInventory( is ) )
		{
			ei.setEntityItemStack( is );
			world.spawnEntityInWorld( ei );
		}
		else
		{
			if ( !ei.isSilent() )
			{
				ei.worldObj.playSound( (EntityPlayer) null, ei.posX, ei.posY, ei.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ( ( itemRand.nextFloat() - itemRand.nextFloat() ) * 0.7F + 1.0F ) * 2.0F );
			}
		}

		player.inventory.markDirty();

		if ( player.inventoryContainer != null )
		{
			player.inventoryContainer.detectAndSendChanges();
		}
	}

	public static boolean containsAtLeastOneOf(
			final IInventory inv,
			final ItemStack is )
	{
		boolean seen = false;
		for ( int x = 0; x < inv.getSizeInventory(); x++ )
		{
			final ItemStack which = inv.getStackInSlot( x );

			if ( which != null && which.getItem() == is.getItem() && ItemChiseledBit.sameBit( which, ItemChiseledBit.getStackState( is ) ) )
			{
				if ( !seen )
				{
					seen = true;
				}
			}
		}
		return seen;
	}

	public static List<BagInventory> getBags(
			final ActingPlayer player )
	{
		if ( player.isCreative() )
		{
			return java.util.Collections.emptyList();
		}

		final List<BagInventory> bags = new ArrayList<BagInventory>();
		final IInventory inv = player.getInventory();

		for ( int zz = 0; zz < inv.getSizeInventory(); zz++ )
		{
			final ItemStack which = inv.getStackInSlot( zz );
			if ( which != null && which.getItem() instanceof ItemBitBag )
			{
				bags.add( new BagInventory( which ) );
			}
		}

		return bags;
	}

	public static boolean consumeBagBit(
			final List<BagInventory> bags,
			final int inPattern )
	{
		for ( final BagInventory inv : bags )
		{
			if ( inv.extractBit( inPattern, 1 ) == 1 )
			{
				return true;
			}
		}

		return false;
	}

	public static VoxelBlob getBlobFromStack(
			final ItemStack stack,
			final EntityLivingBase rotationPlayer )
	{
		if ( stack.hasTagCompound() )
		{
			final NBTBlobConverter tmp = new NBTBlobConverter();

			NBTTagCompound cData = stack.getSubCompound( ItemBlockChiseled.NBT_CHISELED_DATA, false );

			if ( cData == null )
			{
				cData = stack.getTagCompound();
			}

			tmp.readChisleData( cData );
			VoxelBlob blob = tmp.getBlob();

			if ( rotationPlayer != null )
			{
				int xrotations = ModUtil.getRotations( rotationPlayer, ModUtil.getItemRotation( stack ) );
				while ( xrotations-- > 0 )
				{
					blob = blob.spin( Axis.Y );
				}
			}

			return blob;
		}

		return new VoxelBlob();
	}

	public static byte getItemRotation(
			final ItemStack stack )
	{
		final NBTTagCompound cData = stack.getSubCompound( ItemBlockChiseled.NBT_CHISELED_DATA, false );
		final NBTTagCompound rotationSrc = cData != null && cData.hasKey( ItemBlockChiseled.NBT_SIDE ) ? cData : stack.getTagCompound();
		return rotationSrc.getByte( ItemBlockChiseled.NBT_SIDE );
	}

	public static void sendUpdate(
			final World worldObj,
			final BlockPos pos )
	{
		final IBlockState state = worldObj.getBlockState( pos );
		worldObj.notifyBlockUpdate( pos, state, state, 0 );
	}

	public static ItemStack getItemFromBlock(
			final IBlockState state )
	{
		final Block blk = state.getBlock();

		final Item i = blk.getItemDropped( state, RAND, 0 );
		final int meta = blk.getMetaFromState( state );
		final int damage = blk.damageDropped( state );
		final Item blockVarient = Item.getItemFromBlock( blk );

		if ( i == null )
		{
			return null;
		}

		if ( blockVarient == null )
		{
			return null;
		}

		if ( blockVarient != i )
		{
			return null;
		}

		if ( blockVarient instanceof ItemBlock )
		{
			final ItemBlock ib = (ItemBlock) blockVarient;
			if ( meta != ib.getMetadata( damage ) )
			{
				// this item dosn't drop itself... BAIL!
				return null;
			}
		}

		return new ItemStack( i, 1, damage );
	}
}
