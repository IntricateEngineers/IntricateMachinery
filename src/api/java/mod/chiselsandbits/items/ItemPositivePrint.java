package mod.chiselsandbits.items;

import java.util.ArrayList;
import java.util.List;

import mod.chiselsandbits.bitbag.BagInventory;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.chiseledblock.BlockChiseled;
import mod.chiselsandbits.chiseledblock.ItemBlockChiseled;
import mod.chiselsandbits.chiseledblock.NBTBlobConverter;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.ActingPlayer;
import mod.chiselsandbits.helpers.ContinousChisels;
import mod.chiselsandbits.helpers.IContinuousInventory;
import mod.chiselsandbits.helpers.LocalStrings;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.helpers.ModUtil.ItemStackSlot;
import mod.chiselsandbits.integration.mcmultipart.MCMultipartProxy;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemPositivePrint extends ItemNegativePrint
{

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	@Override
	public void addInformation(
			final ItemStack stack,
			final EntityPlayer playerIn,
			final List tooltip,
			final boolean advanced )
	{
		defaultAddInfo( stack, playerIn, tooltip, advanced );
		ChiselsAndBits.getConfig().helpText( LocalStrings.HelpPositivePrint, tooltip );

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
	protected NBTTagCompound getCompoundFromBlock(
			final World world,
			final BlockPos pos,
			final EntityPlayer player )
	{
		final IBlockState state = world.getBlockState( pos );
		final Block blkObj = state.getBlock();

		if ( !( blkObj instanceof BlockChiseled ) && BlockBitInfo.supportsBlock( state ) )
		{
			final NBTBlobConverter tmp = new NBTBlobConverter();

			tmp.fillWith( state );
			final NBTTagCompound comp = new NBTTagCompound();
			tmp.writeChisleData( comp, false );

			comp.setByte( ItemBlockChiseled.NBT_SIDE, (byte) ModUtil.getPlaceFace( player ).ordinal() );
			return comp;
		}

		return super.getCompoundFromBlock( world, pos, player );
	}

	@Override
	protected boolean convertToStone()
	{
		return false;
	}

	@Override
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

		final VoxelBlob filled = new VoxelBlob();
		MCMultipartProxy.proxyMCMultiPart.addFiller( world, pos, filled );

		final List<BagInventory> bags = ModUtil.getBags( player );
		final List<EntityItem> spawnlist = new ArrayList<EntityItem>();

		for ( int y = 0; y < vb.detail; y++ )
		{
			for ( int z = 0; z < vb.detail; z++ )
			{
				for ( int x = 0; x < vb.detail; x++ )
				{
					int inPlace = vb.get( x, y, z );
					final int inPattern = pattern.get( x, y, z );
					if ( inPlace != inPattern )
					{
						if ( inPlace != 0 && selected.isValid() )
						{
							spawnedItem = ItemChisel.chiselBlock( selected, player, vb, world, pos, side, x, y, z, spawnedItem, spawnlist );

							if ( spawnedItem != null )
							{
								inPlace = 0;
							}
						}

						if ( inPlace == 0 && inPattern != 0 && filled.get( x, y, z ) == 0 )
						{
							final ItemStackSlot bit = ModUtil.findBit( player, pos, inPattern );
							if ( ModUtil.consumeBagBit( bags, inPattern ) )
							{
								vb.set( x, y, z, inPattern );
							}
							else if ( bit.isValid() )
							{
								vb.set( x, y, z, inPattern );

								if ( !player.isCreative() )
								{
									bit.consume();
								}
							}
						}
					}
				}
			}
		}

		for ( final EntityItem ei : spawnlist )
		{
			ModUtil.feedPlayer( world, who, ei );
			ItemBitBag.cleanupInventory( who, ei.getEntityItem() );
		}

	}

}
