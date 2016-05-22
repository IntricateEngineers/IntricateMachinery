package mod.chiselsandbits.integration.mcmultipart;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import mcmultipart.microblock.IMicroMaterial;
import mcmultipart.microblock.IMicroblock;
import mcmultipart.microblock.MicroblockClass;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.INormallyOccludingPart;
import mcmultipart.multipart.ISolidPart;
import mcmultipart.multipart.Multipart;
import mcmultipart.multipart.OcclusionHelper;
import mcmultipart.multipart.PartSlot;
import mcmultipart.raytrace.PartMOP;
import mcmultipart.raytrace.RayTraceUtils.AdvancedRayTraceResultPart;
import mod.chiselsandbits.chiseledblock.BlockChiseled;
import mod.chiselsandbits.chiseledblock.BoxType;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.Log;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ChiseledBlockPart extends Multipart implements INormallyOccludingPart, ISolidPart, IMicroblock
{
	protected TileEntityBlockChiseled inner; // never use directly..
	protected BlockChiseled bc; // never use directly..

	@Override
	public void onAdded()
	{
		if ( inner != null )
		{
			inner.validate();
		}
	}

	@Override
	public void onRemoved()
	{
		if ( inner != null )
		{
			inner.invalidate();
		}
	}

	public void saveChanges()
	{
		super.markDirty();
	}

	public void swapTile(
			final TileEntityBlockChiseled newTileEntity )
	{
		newTileEntity.copyFrom( getTile() );
		inner.invalidate();
		inner = newTileEntity;
		markRenderUpdate();
	}

	@Override
	public boolean occlusionTest(
			final IMultipart part )
	{
		return OcclusionHelper.defaultOcclusionTest( this, part ) && !( part instanceof ChiseledBlockPart );
	}

	@Override
	public float getHardness(
			final PartMOP hit )
	{
		return getTile().getBlockInfo( getBlock() ).hardness;
	}

	@Override
	public void addCollisionBoxes(
			final AxisAlignedBB mask,
			final List<AxisAlignedBB> list,
			final Entity collidingEntity )
	{
		getBlock().addCollisionBoxesToList( getTile(), BlockPos.ORIGIN, mask, list, collidingEntity );
	}

	@Override
	public AdvancedRayTraceResultPart collisionRayTrace(
			final Vec3d start,
			final Vec3d end )
	{
		final RayTraceResult mop = getBlock().collisionRayTrace( getTile(), getPos(), start, end, true );

		if ( mop == null )
		{
			return null;
		}

		final BlockPos myPos = getPos() == null ? BlockPos.ORIGIN : getPos();
		final AxisAlignedBB bb = getBlock().getSelectedBoundingBox( getTile(), myPos );
		return new AdvancedRayTraceResultPart( new PartMOP( mop, this ), bb == null ? null : bb.offset( -myPos.getX(), -myPos.getY(), -myPos.getZ() ) );
	}

	@Override
	public void addOcclusionBoxes(
			final List<AxisAlignedBB> list )
	{
		list.addAll( getTile().getBoxes( BoxType.OCCLUSION ) );
	}

	public TileEntityBlockChiseled getTile()
	{
		if ( inner == null )
		{
			inner = new TileEntityBlockChiseled();
		}

		// update tile stats..
		inner.setWorldObj( getWorld() );
		inner.setPos( getPos() );

		if ( !( inner.occlusionState instanceof MultipartContainerWrapper ) )
		{
			inner.occlusionState = new MultipartContainerWrapper( this );
		}

		return inner;
	}

	public BlockChiseled getBlock()
	{
		if ( bc == null )
		{
			bc = (BlockChiseled) ChiselsAndBits.getBlocks().getChiseledDefaultState().getBlock();
		}

		return bc;
	}

	@Override
	public ResourceLocation getModelPath()
	{
		return getBlock().getModel();
	}

	@Override
	public int getLightValue()
	{
		final int lv = getTile().getLightValue();
		return lv;
	}

	@Override
	public ItemStack getPickBlock(
			final EntityPlayer player,
			final PartMOP hit )
	{
		return getBlock().getPickBlock( hit, hit.getBlockPos(), getTile() );
	}

	@Override
	public boolean canRenderInLayer(
			final BlockRenderLayer layer )
	{
		return true;
	}

	@Override
	public IBlockState getExtendedState(
			final IBlockState state )
	{
		return getTile().getRenderState();
	}

	@Override
	public List<ItemStack> getDrops()
	{
		return Collections.singletonList( getTile().getItemStack( null ) );
	}

	@Override
	public void harvest(
			final EntityPlayer player,
			final PartMOP hit )
	{

		final World world = getWorld();
		final BlockPos pos = getPos();
		final double x = pos.getX() + 0.5, y = pos.getY() + 0.5, z = pos.getZ() + 0.5;

		if ( ( player == null || !player.capabilities.isCreativeMode ) && !world.isRemote && world.getGameRules().getBoolean( "doTileDrops" )
				&& !world.restoringBlockSnapshots )
		{

			final ItemStack stack = getTile().getItemStack( player );
			final EntityItem item = new EntityItem( world, x, y, z, stack );
			item.setDefaultPickupDelay();
			world.spawnEntityInWorld( item );
		}

		getContainer().removePart( this );
	}

	@Override
	public BlockStateContainer createBlockState()
	{
		return getBlock().getBlockState();
	}

	@Override
	public boolean rotatePart(
			final EnumFacing axis )
	{
		getTile().rotateBlock( axis );
		return true;
	}

	@Override
	public EnumFacing[] getValidRotations()
	{
		return EnumFacing.VALUES;
	}

	@Override
	public NBTTagCompound writeToNBT(
			final NBTTagCompound tag )
	{
		getTile().writeChisleData( tag );
		return tag;
	}

	@Override
	public void readFromNBT(
			final NBTTagCompound tag )
	{
		if ( tag.hasNoTags() )
		{
			return;
		}

		getTile().readChisleData( tag );
	}

	@Override
	public void writeUpdatePacket(
			final PacketBuffer buf )
	{
		final NBTTagCompound tag = new NBTTagCompound();
		getTile().writeChisleData( tag );
		buf.writeNBTTagCompoundToBuffer( tag );
	}

	@Override
	public void readUpdatePacket(
			final PacketBuffer buf )
	{
		try
		{
			final NBTTagCompound tag = buf.readNBTTagCompoundFromBuffer();
			getTile().readChisleData( tag );
		}
		catch ( final IOException e )
		{
			Log.logError( "Invalid Chisled Block Packet.", e );
		}
	}

	@Override
	public void notifyPartUpdate()
	{
		super.notifyPartUpdate();
	}

	@Override
	public boolean isSideSolid(
			final EnumFacing side )
	{
		return getTile().isSideSolid( side );
	}

	// IF ANY ONE ASKS, Amadornes told me too... :D

	@Override
	public EnumSet<PartSlot> getSlotMask()
	{
		return EnumSet.noneOf( PartSlot.class );
	}

	@Override
	public AxisAlignedBB getBounds()
	{
		return getBlock().getSelectedBoundingBox( getTile(), getPos() );
	}

	@Override
	public MicroblockClass getMicroClass()
	{
		return ChiseledMicroblock.instance;
	}

	@Override
	public IMicroMaterial getMicroMaterial()
	{
		return (IMicroMaterial) this;
	}

	@Override
	public int getSize()
	{
		return 0;
	}

	@Override
	public PartSlot getSlot()
	{
		return null;
	}

	@Override
	public void setSize(
			final int arg0 )
	{
	}

	@Override
	public void setSlot(
			final PartSlot arg0 )
	{
	}

	@Override
	public Boolean isAABBInsideMaterial(
			final AxisAlignedBB bx,
			final Material materialIn )
	{
		return BlockChiseled.sharedIsAABBInsideMaterial( getTile(), bx, materialIn );
	}

	@Override
	public Boolean isEntityInsideMaterial(
			final Entity entity,
			final double yToTest,
			final Material materialIn,
			final boolean testingHead )
	{
		return BlockChiseled.sharedIsEntityInsideMaterial( getTile(), getPos(), entity, yToTest, materialIn, testingHead );
	}
}
