package mod.chiselsandbits.items;

import java.util.ArrayList;
import java.util.List;

import mod.chiselsandbits.chiseledblock.BlockChiseled;
import mod.chiselsandbits.chiseledblock.ItemBlockChiseled;
import mod.chiselsandbits.chiseledblock.NBTBlobConverter;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.ActingPlayer;
import mod.chiselsandbits.helpers.ContinousChisels;
import mod.chiselsandbits.helpers.IContinuousInventory;
import mod.chiselsandbits.helpers.LocalStrings;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.integration.mcmultipart.MCMultipartProxy;
import mod.chiselsandbits.interfaces.IItemScrollWheel;
import mod.chiselsandbits.interfaces.IPatternItem;
import mod.chiselsandbits.interfaces.IVoxelBlobItem;
import mod.chiselsandbits.network.NetworkRouter;
import mod.chiselsandbits.network.packets.PacketRotateVoxelBlob;
import mod.chiselsandbits.render.helpers.SimpleInstanceCache;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemNegativePrint extends Item implements IVoxelBlobItem, IItemScrollWheel, IPatternItem
{

	public ItemNegativePrint()
	{

	}

	protected void defaultAddInfo(
			final ItemStack stack,
			final EntityPlayer playerIn,
			final List<String> tooltip,
			final boolean advanced )
	{
		super.addInformation( stack, playerIn, tooltip, advanced );
	}

	// add info cached info
	SimpleInstanceCache<ItemStack, List<String>> toolTipCache = new SimpleInstanceCache<ItemStack, List<String>>( null, new ArrayList<String>() );

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	@Override
	public void addInformation(
			final ItemStack stack,
			final EntityPlayer playerIn,
			final List tooltip,
			final boolean advanced )
	{
		defaultAddInfo( stack, playerIn, tooltip, advanced );
		ChiselsAndBits.getConfig().helpText( LocalStrings.HelpNegativePrint, tooltip );

		if ( stack.hasTagCompound() )
		{
			if ( ClientSide.instance.holdingShift() )
			{
				final List<String> details = toolTipCache.getCached();

				if ( toolTipCache.needsUpdate( stack ) )
				{
					details.clear();

					final VoxelBlob blob = ModUtil.getBlobFromStack( stack, null );

					final int solid = blob.filled();
					final int air = blob.air();

					if ( solid > 0 )
					{
						details.add( solid + " " + LocalStrings.Empty.getLocal() );
					}

					if ( air > 0 )
					{
						details.add( air + " " + LocalStrings.Filled.getLocal() );
					}
				}

				tooltip.addAll( details );
			}
			else
			{
				tooltip.add( LocalStrings.ShiftDetails.getLocal() );
			}
		}
	}

	@Override
	public String getUnlocalizedName(
			final ItemStack stack )
	{
		if ( stack.hasTagCompound() )
		{
			return super.getUnlocalizedName( stack ) + "_written";
		}
		return super.getUnlocalizedName( stack );
	}

	@Override
	public EnumActionResult onItemUse(
			final ItemStack stack,
			final EntityPlayer player,
			final World world,
			final BlockPos pos,
			final EnumHand hand,
			final EnumFacing side,
			final float hitX,
			final float hitY,
			final float hitZ )
	{
		final IBlockState blkstate = world.getBlockState( pos );

		if ( !player.canPlayerEdit( pos, side, stack ) || !world.isBlockModifiable( player, pos ) )
		{
			return EnumActionResult.FAIL;
		}

		if ( !stack.hasTagCompound() )
		{
			final NBTTagCompound comp = getCompoundFromBlock( world, pos, player );
			if ( comp != null )
			{
				stack.setTagCompound( comp );
				return EnumActionResult.SUCCESS;
			}

			return EnumActionResult.FAIL;
		}

		final TileEntityBlockChiseled te = ModUtil.getChiseledTileEntity( world, pos, false );
		if ( te != null )
		{
			// we can do this!
		}
		else if ( !BlockChiseled.replaceWithChisled( world, pos, blkstate, true ) && !MCMultipartProxy.proxyMCMultiPart.isMultiPartTileEntity( world, pos ) )
		{
			return EnumActionResult.FAIL;
		}

		final TileEntityBlockChiseled tec = ModUtil.getChiseledTileEntity( world, pos, true );
		if ( tec != null )
		{
			final VoxelBlob vb = tec.getBlob();

			final VoxelBlob pattern = ModUtil.getBlobFromStack( stack, player );

			applyPrint( world, pos, side, vb, pattern, player, hand );

			tec.setBlob( vb );
			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.FAIL;
	}

	protected boolean convertToStone()
	{
		return true;
	}

	protected NBTTagCompound getCompoundFromBlock(
			final World world,
			final BlockPos pos,
			final EntityPlayer player )
	{

		final TileEntityBlockChiseled te = ModUtil.getChiseledTileEntity( world, pos, false );
		if ( te != null )
		{
			final NBTTagCompound comp = new NBTTagCompound();
			te.writeChisleData( comp );

			if ( convertToStone() )
			{
				final TileEntityBlockChiseled tmp = new TileEntityBlockChiseled();
				tmp.readChisleData( comp );

				final VoxelBlob bestBlob = tmp.getBlob();
				bestBlob.binaryReplacement( 0, Block.getStateId( Blocks.STONE.getDefaultState() ) );

				tmp.setBlob( bestBlob );
				tmp.writeChisleData( comp );
			}

			comp.setByte( ItemBlockChiseled.NBT_SIDE, (byte) ModUtil.getPlaceFace( player ).ordinal() );
			return comp;
		}

		return null;
	}

	@Override
	public ItemStack getPatternedItem(
			final ItemStack stack )
	{
		if ( !stack.hasTagCompound() )
		{
			return null;
		}

		final NBTTagCompound tag = stack.getTagCompound();

		// Detect and provide full blocks if pattern solid full and solid.
		final NBTBlobConverter conv = new NBTBlobConverter();
		conv.readChisleData( tag );

		final IBlockState state = conv.getPrimaryBlockState();
		final ItemStack itemstack = new ItemStack( ChiselsAndBits.getBlocks().getConversionWithDefault( state ), 1 );

		itemstack.setTagInfo( ItemBlockChiseled.NBT_CHISELED_DATA, tag );
		return itemstack;
	}

	protected void applyPrint(
			final World world,
			final BlockPos pos,
			final EnumFacing side,
			final VoxelBlob vb,
			final VoxelBlob pattern,
			final EntityPlayer who,
			final EnumHand hand )
	{
		// snag a tool...
		final ActingPlayer player = ActingPlayer.actingAs( who, hand );
		final IContinuousInventory selected = new ContinousChisels( player, pos, side );
		ItemStack spawnedItem = null;

		final List<EntityItem> spawnlist = new ArrayList<EntityItem>();

		for ( int z = 0; z < vb.detail && selected.isValid(); z++ )
		{
			for ( int y = 0; y < vb.detail && selected.isValid(); y++ )
			{
				for ( int x = 0; x < vb.detail && selected.isValid(); x++ )
				{
					final int blkID = vb.get( x, y, z );
					if ( blkID != 0 && pattern.get( x, y, z ) == 0 )
					{
						spawnedItem = ItemChisel.chiselBlock( selected, player, vb, world, pos, side, x, y, z, spawnedItem, spawnlist );
					}
				}
			}
		}

		for ( final EntityItem ei : spawnlist )
		{
			ModUtil.feedPlayer( world, who, ei );
		}
	}

	@Override
	public void scroll(
			final EntityPlayer player,
			final ItemStack stack,
			final int dwheel )
	{
		final PacketRotateVoxelBlob p = new PacketRotateVoxelBlob();
		p.rotationDirection = dwheel;
		NetworkRouter.instance.sendToServer( p );
	}

	@Override
	public void rotate(
			final ItemStack stack,
			final int rotationDirection )
	{
		final NBTTagCompound blueprintTag = stack.getTagCompound();
		EnumFacing side = EnumFacing.VALUES[blueprintTag.getByte( ItemBlockChiseled.NBT_SIDE )];

		if ( side.getAxis() == Axis.Y )
		{
			side = EnumFacing.NORTH;
		}

		side = rotationDirection > 0 ? side.rotateY() : side.rotateYCCW();
		blueprintTag.setInteger( ItemBlockChiseled.NBT_SIDE, +side.ordinal() );
	}

}