package mod.chiselsandbits.chiseledblock;

import java.util.ArrayList;
import java.util.List;

import mod.chiselsandbits.chiseledblock.data.BitLocation;
import mod.chiselsandbits.chiseledblock.data.IntegerBox;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.core.Log;
import mod.chiselsandbits.helpers.ChiselToolType;
import mod.chiselsandbits.helpers.ExceptionNoTileEntity;
import mod.chiselsandbits.helpers.LocalStrings;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.interfaces.IItemScrollWheel;
import mod.chiselsandbits.interfaces.IVoxelBlobItem;
import mod.chiselsandbits.network.NetworkRouter;
import mod.chiselsandbits.network.packets.PacketRotateVoxelBlob;
import mod.chiselsandbits.render.helpers.SimpleInstanceCache;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockChiseled extends ItemBlock implements IVoxelBlobItem, IItemScrollWheel
{

	public static final String NBT_CHISELED_DATA = "BlockEntityTag";
	public static final String NBT_SIDE = "side";

	SimpleInstanceCache<ItemStack, List<String>> tooltipCache = new SimpleInstanceCache<ItemStack, List<String>>( null, new ArrayList<String>() );

	public ItemBlockChiseled(
			final Block block )
	{
		super( block );
	}

	@Override
	public void addInformation(
			final ItemStack stack,
			final EntityPlayer playerIn,
			final List<String> tooltip,
			final boolean advanced )
	{
		super.addInformation( stack, playerIn, tooltip, advanced );
		ChiselsAndBits.getConfig().helpText( LocalStrings.HelpChiseledBlock, tooltip );

		if ( stack.hasTagCompound() )
		{
			if ( ClientSide.instance.holdingShift() )
			{
				if ( tooltipCache.needsUpdate( stack ) )
				{
					final VoxelBlob blob = ModUtil.getBlobFromStack( stack, null );
					tooltipCache.updateCachedValue( blob.listContents( new ArrayList<String>() ) );
				}

				tooltip.addAll( tooltipCache.getCached() );
			}
			else
			{
				tooltip.add( LocalStrings.ShiftDetails.getLocal() );
			}
		}
	}

	@Override
	@SideOnly( Side.CLIENT )
	public boolean canPlaceBlockOnSide(
			final World worldIn,
			final BlockPos pos,
			final EnumFacing side,
			final EntityPlayer player,
			final ItemStack stack )
	{
		return canPlaceBlockHere( worldIn, pos, side, player, stack );
	}

	public boolean vanillaStylePlacementTest(
			final World worldIn,
			BlockPos pos,
			EnumFacing side,
			final EntityPlayer player,
			final ItemStack stack )
	{
		final Block block = worldIn.getBlockState( pos ).getBlock();

		if ( block == Blocks.SNOW_LAYER )
		{
			side = EnumFacing.UP;
		}
		else if ( !block.isReplaceable( worldIn, pos ) )
		{
			pos = pos.offset( side );
		}

		return worldIn.canBlockBePlaced( this.block, pos, false, side, (Entity) null, stack );
	}

	public boolean canPlaceBlockHere(
			final World worldIn,
			final BlockPos pos,
			final EnumFacing side,
			final EntityPlayer player,
			final ItemStack stack )
	{
		if ( vanillaStylePlacementTest( worldIn, pos, side, player, stack ) )
		{
			return true;
		}

		if ( player.isSneaking() )
		{
			return true;
		}

		if ( tryPlaceBlockAt( block, stack, player, worldIn, pos, side, null, false ) )
		{
			return true;
		}

		return tryPlaceBlockAt( block, stack, player, worldIn, pos.offset( side ), side, null, false );
	}

	@Override
	public EnumActionResult onItemUse(
			final ItemStack stack,
			final EntityPlayer playerIn,
			final World worldIn,
			BlockPos pos,
			final EnumHand hand,
			EnumFacing side,
			final float hitX,
			final float hitY,
			final float hitZ )
	{
		final IBlockState state = worldIn.getBlockState( pos );
		final Block block = state.getBlock();

		if ( block == Blocks.SNOW_LAYER && state.getValue( BlockSnow.LAYERS ).intValue() < 1 )
		{
			side = EnumFacing.UP;
		}
		else
		{
			boolean canMerge = false;
			if ( stack.hasTagCompound() )
			{
				final TileEntityBlockChiseled tebc = ModUtil.getChiseledTileEntity( worldIn, pos, true );

				if ( tebc != null )
				{
					final VoxelBlob blob = ModUtil.getBlobFromStack( stack, playerIn );
					canMerge = tebc.canMerge( blob );
				}
			}

			if ( !canMerge && !playerIn.isSneaking() && !block.isReplaceable( worldIn, pos ) )
			{
				pos = pos.offset( side );
			}
		}

		if ( stack.stackSize == 0 )
		{
			return EnumActionResult.FAIL;
		}
		else if ( !playerIn.canPlayerEdit( pos, side, stack ) )
		{
			return EnumActionResult.FAIL;
		}
		else if ( pos.getY() == 255 && this.block.getMaterial( this.block.getStateFromMeta( stack.getMetadata() ) ).isSolid() )
		{
			return EnumActionResult.FAIL;
		}
		else if ( canPlaceBlockHere( worldIn, pos, side, playerIn, stack ) )
		{
			final int i = this.getMetadata( stack.getMetadata() );
			final IBlockState iblockstate1 = this.block.onBlockPlaced( worldIn, pos, side, hitX, hitY, hitZ, i, playerIn );

			if ( placeBlockAt( stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ, iblockstate1 ) )
			{
				worldIn.playSound( pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, this.block.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, ( this.block.getSoundType().getVolume() + 1.0F ) / 2.0F,
						this.block.getSoundType().getPitch() * 0.8F, false );
				--stack.stackSize;
			}

			return EnumActionResult.SUCCESS;
		}
		else
		{
			return EnumActionResult.FAIL;
		}
	}

	@Override
	public boolean placeBlockAt(
			final ItemStack stack,
			final EntityPlayer player,
			final World world,
			final BlockPos pos,
			final EnumFacing side,
			final float hitX,
			final float hitY,
			final float hitZ,
			final IBlockState newState )
	{
		if ( player.isSneaking() )
		{
			final BitLocation bl = new BitLocation( new RayTraceResult( RayTraceResult.Type.BLOCK, new Vec3d( hitX, hitY, hitZ ), side, pos ), false, ChiselToolType.BIT );
			return tryPlaceBlockAt( block, stack, player, world, bl.blockPos, side, new BlockPos( bl.bitX, bl.bitY, bl.bitZ ), true );
		}
		else
		{
			return tryPlaceBlockAt( block, stack, player, world, pos, side, null, true );
		}
	}

	static public boolean tryPlaceBlockAt(
			final Block block,
			final ItemStack stack,
			final EntityLivingBase player,
			final World world,
			BlockPos pos,
			final EnumFacing side,
			final BlockPos partial,
			final boolean modulateWorld )
	{
		try
		{
			final VoxelBlob[][][] blobs = new VoxelBlob[2][2][2];

			// you can't place empty blocks...
			if ( !stack.hasTagCompound() )
			{
				return false;
			}

			final VoxelBlob source = ModUtil.getBlobFromStack( stack, player );

			final IntegerBox modelBounds = source.getBounds();
			BlockPos offset = partial == null ? new BlockPos( 0, 0, 0 ) : ModUtil.getPartialOffset( side, partial, modelBounds );
			final BlockChiseled myBlock = (BlockChiseled) block;

			if ( offset.getX() < 0 )
			{
				pos = pos.add( -1, 0, 0 );
				offset = offset.add( VoxelBlob.dim, 0, 0 );
			}

			if ( offset.getY() < 0 )
			{
				pos = pos.add( 0, -1, 0 );
				offset = offset.add( 0, VoxelBlob.dim, 0 );
			}

			if ( offset.getZ() < 0 )
			{
				pos = pos.add( 0, 0, -1 );
				offset = offset.add( 0, 0, VoxelBlob.dim );
			}

			for ( int x = 0; x < 2; x++ )
			{
				for ( int y = 0; y < 2; y++ )
				{
					for ( int z = 0; z < 2; z++ )
					{
						blobs[x][y][z] = source.offset( offset.getX() - source.detail * x, offset.getY() - source.detail * y, offset.getZ() - source.detail * z );
						final int solids = blobs[x][y][z].filled();
						if ( solids > 0 )
						{
							final BlockPos bp = pos.add( x, y, z );

							if ( world.isAirBlock( bp ) || world.getBlockState( bp ).getBlock().isReplaceable( world, bp ) )
							{
								continue;
							}

							final TileEntityBlockChiseled target = ModUtil.getChiseledTileEntity( world, bp, true );
							if ( target != null )
							{
								if ( !target.canMerge( blobs[x][y][z] ) )
								{
									return false;
								}

								blobs[x][y][z] = blobs[x][y][z].merge( target.getBlob() );
								continue;
							}

							return false;
						}
					}
				}
			}

			if ( modulateWorld )
			{
				for ( int x = 0; x < 2; x++ )
				{
					for ( int y = 0; y < 2; y++ )
					{
						for ( int z = 0; z < 2; z++ )
						{
							if ( blobs[x][y][z].filled() > 0 )
							{
								final BlockPos bp = pos.add( x, y, z );
								final IBlockState state = world.getBlockState( bp );

								if ( world.getBlockState( bp ).getBlock().isReplaceable( world, bp ) )
								{
									// clear it...
									world.setBlockToAir( bp );
								}

								if ( world.isAirBlock( bp ) )
								{
									final int commonBlock = blobs[x][y][z].getVoxelStats().mostCommonState;
									if ( BlockChiseled.replaceWithChisled( world, bp, state, commonBlock, true ) )
									{
										final TileEntityBlockChiseled target = myBlock.getTileEntity( world, bp );
										target.setBlob( blobs[x][y][z] );
									}

									continue;
								}

								final TileEntityBlockChiseled target = ModUtil.getChiseledTileEntity( world, bp, true );
								if ( target != null )
								{
									target.setBlob( blobs[x][y][z] );

									continue;
								}

								return false;
							}
						}
					}
				}
			}

			return true;
		}
		catch ( final ExceptionNoTileEntity e )
		{
			Log.noTileError( e );
			return false;
		}
	}

	@Override
	public String getItemStackDisplayName(
			final ItemStack stack )
	{
		final NBTTagCompound comp = stack.getTagCompound();

		if ( comp != null )
		{
			final NBTTagCompound BlockEntityTag = comp.getCompoundTag( NBT_CHISELED_DATA );
			if ( BlockEntityTag != null )
			{
				final NBTBlobConverter c = new NBTBlobConverter();
				c.readChisleData( BlockEntityTag );

				final IBlockState state = c.getPrimaryBlockState();
				final Block blk = state.getBlock();

				final ItemStack target = new ItemStack( blk, 1, blk.getMetaFromState( state ) );

				if ( target.getItem() != null )
				{
					return new StringBuilder().append( super.getItemStackDisplayName( stack ) ).append( " - " ).append( target.getDisplayName() ).toString();
				}
			}
		}

		return super.getItemStackDisplayName( stack );
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
		EnumFacing side = EnumFacing.VALUES[blueprintTag.getByte( NBT_SIDE )];

		if ( side == EnumFacing.DOWN || side == EnumFacing.UP )
		{
			side = EnumFacing.NORTH;
		}

		side = rotationDirection > 0 ? side.rotateY() : side.rotateYCCW();
		blueprintTag.setInteger( NBT_SIDE, +side.ordinal() );
	}

}
