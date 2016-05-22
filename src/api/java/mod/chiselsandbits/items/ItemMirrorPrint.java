package mod.chiselsandbits.items;

import java.util.ArrayList;
import java.util.List;

import mod.chiselsandbits.chiseledblock.ItemBlockChiseled;
import mod.chiselsandbits.chiseledblock.NBTBlobConverter;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob.BlobStats;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.LocalStrings;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.interfaces.IPatternItem;
import mod.chiselsandbits.render.helpers.SimpleInstanceCache;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMirrorPrint extends Item implements IPatternItem
{

	public ItemMirrorPrint()
	{

	}

	SimpleInstanceCache<ItemStack, List<String>> toolTipCache = new SimpleInstanceCache<ItemStack, List<String>>( null, new ArrayList<String>() );

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	@Override
	public void addInformation(
			final ItemStack stack,
			final EntityPlayer playerIn,
			final List tooltip,
			final boolean advanced )
	{
		super.addInformation( stack, playerIn, tooltip, advanced );
		ChiselsAndBits.getConfig().helpText( LocalStrings.HelpMirrorPrint, tooltip );

		if ( stack.hasTagCompound() )
		{
			if ( ClientSide.instance.holdingShift() )
			{
				if ( toolTipCache.needsUpdate( stack ) )
				{
					final VoxelBlob blob = ModUtil.getBlobFromStack( stack, null );
					toolTipCache.updateCachedValue( blob.listContents( new ArrayList<String>() ) );
				}

				tooltip.addAll( toolTipCache.getCached() );
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
		if ( !player.canPlayerEdit( pos, side, stack ) )
		{
			return EnumActionResult.SUCCESS;
		}

		if ( !stack.hasTagCompound() )
		{
			final NBTTagCompound comp = getCompoundFromBlock( world, pos, player, side );
			if ( comp != null )
			{
				stack.setTagCompound( comp );
				return EnumActionResult.SUCCESS;
			}

			return EnumActionResult.FAIL;
		}

		return EnumActionResult.FAIL;
	}

	protected NBTTagCompound getCompoundFromBlock(
			final World world,
			final BlockPos pos,
			final EntityPlayer player,
			final EnumFacing face )
	{
		final TileEntityBlockChiseled te = ModUtil.getChiseledTileEntity( world, pos, false );

		if ( te != null )
		{
			final NBTTagCompound comp = new NBTTagCompound();
			te.writeChisleData( comp );

			final TileEntityBlockChiseled tmp = new TileEntityBlockChiseled();
			tmp.readChisleData( comp );

			final VoxelBlob bestBlob = tmp.getBlob();
			tmp.setBlob( bestBlob.mirror( face.getAxis() ) );
			tmp.writeChisleData( comp );

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

		final BlobStats common = conv.getBlob().getVoxelStats();
		if ( common.isFullBlock )
		{
			final IBlockState state = Block.getStateById( common.mostCommonState );
			return new ItemStack( state.getBlock(), 1, state.getBlock().getMetaFromState( state ) );
		}

		final IBlockState blk = conv.getPrimaryBlockState();
		final ItemStack itemstack = new ItemStack( ChiselsAndBits.getBlocks().getConversionWithDefault( blk ), 1 );

		itemstack.setTagInfo( ItemBlockChiseled.NBT_CHISELED_DATA, tag );
		return itemstack;
	}

}