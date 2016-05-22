package mod.chiselsandbits.items;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import mod.chiselsandbits.bittank.BlockBitTank;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.chiseledblock.BlockChiseled;
import mod.chiselsandbits.chiseledblock.ChiselTypeIterator;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.data.BitLocation;
import mod.chiselsandbits.chiseledblock.data.IntegerBox;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.core.ChiselMode;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.core.Log;
import mod.chiselsandbits.helpers.ActingPlayer;
import mod.chiselsandbits.helpers.ChiselModeManager;
import mod.chiselsandbits.helpers.ChiselToolType;
import mod.chiselsandbits.helpers.IContinuousInventory;
import mod.chiselsandbits.helpers.LocalStrings;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.helpers.ModUtil.ItemStackSlot;
import mod.chiselsandbits.helpers.VoxelRegionSrc;
import mod.chiselsandbits.interfaces.ICacheClearable;
import mod.chiselsandbits.interfaces.IChiselModeItem;
import mod.chiselsandbits.interfaces.IItemScrollWheel;
import mod.chiselsandbits.network.NetworkRouter;
import mod.chiselsandbits.network.packets.PacketChisel;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class ItemChiseledBit extends Item implements IItemScrollWheel, IChiselModeItem, ICacheClearable
{

	public static boolean bitBagStackLimitHack;

	private ArrayList<ItemStack> bits;

	public ItemChiseledBit()
	{
		setHasSubtypes( true );
		ChiselsAndBits.getInstance().addClearable( this );
	}

	@Override
	public void addInformation(
			final ItemStack stack,
			final EntityPlayer playerIn,
			final List<String> tooltip,
			final boolean advanced )
	{
		super.addInformation( stack, playerIn, tooltip, advanced );
		ChiselsAndBits.getConfig().helpText( LocalStrings.HelpBit, tooltip, ClientSide.instance.getModeKey() );
	}

	@Override
	public String getHighlightTip(
			final ItemStack item,
			final String displayName )
	{
		if ( ChiselsAndBits.getConfig().itemNameModeDisplay && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT )
		{
			return displayName + " - " + ChiselModeManager.getChiselMode( ClientSide.instance.getPlayer(), ChiselToolType.BIT ).string.getLocal();
		}

		return displayName;
	}

	@Override
	/**
	 * alter digging behavior to chisel, uses packets to enable server to stay
	 * in-sync.
	 */
	public boolean onBlockStartBreak(
			final ItemStack itemstack,
			final BlockPos pos,
			final EntityPlayer player )
	{
		return ItemChisel.fromBreakToChisel( ChiselModeManager.getChiselMode( player, ChiselToolType.BIT ), itemstack, pos, player, EnumHand.MAIN_HAND );
	}

	public String getBitTypeName(
			final ItemStack stack )
	{
		ItemStack target = null;

		try
		{
			// for an unknown reason its possible to generate mod blocks without
			// proper state here...
			final IBlockState state = Block.getStateById( ItemChiseledBit.getStackState( stack ) );
			final Block blk = state.getBlock();

			final Item item = Item.getItemFromBlock( blk );
			if ( item == null )
			{
				final Fluid f = BlockBitInfo.getFluidFromBlock( blk );
				if ( f != null )
				{
					return f.getLocalizedName( new FluidStack( f, 10 ) );
				}
			}
			else
			{
				target = new ItemStack( blk, 1, blk.getMetaFromState( state ) );
			}
		}
		catch ( final IllegalArgumentException e )
		{
			Log.logError( "Unable to get Item Details for Bit.", e );
		}

		if ( target == null || target.getItem() == null )
		{
			return null;
		}

		try
		{
			return target.getDisplayName();
		}
		catch ( final Exception e )
		{
			return "ERROR";
		}
	}

	@Override
	public String getItemStackDisplayName(
			final ItemStack stack )
	{
		final String typeName = getBitTypeName( stack );

		if ( typeName == null )
		{
			return super.getItemStackDisplayName( stack );
		}

		return new StringBuilder().append( super.getItemStackDisplayName( stack ) ).append( " - " ).append( typeName ).toString();
	}

	@SuppressWarnings( "deprecation" )
	@Override
	public int getItemStackLimit()
	{
		return bitBagStackLimitHack ? ChiselsAndBits.getConfig().bagStackSize : super.getItemStackLimit();
	}

	@Override
	public EnumActionResult onItemUse(
			final ItemStack stack,
			final EntityPlayer player,
			final World world,
			final BlockPos usedBlock,
			final EnumHand hand,
			final EnumFacing side,
			final float hitX,
			final float hitY,
			final float hitZ )
	{
		if ( !player.canPlayerEdit( usedBlock, side, stack ) )
		{
			return EnumActionResult.FAIL;
		}

		// forward interactions to tank...
		final IBlockState usedState = world.getBlockState( usedBlock );
		final Block blk = usedState.getBlock();
		if ( blk instanceof BlockBitTank )
		{
			if ( blk.onBlockActivated( world, usedBlock, usedState, player, hand, player.getHeldItem( hand ), side, hitX, hitY, hitZ ) )
			{
				return EnumActionResult.SUCCESS;
			}
			return EnumActionResult.FAIL;
		}

		if ( world.isRemote )
		{
			final ChiselMode mode = ChiselModeManager.getChiselMode( player, ClientSide.instance.getHeldToolType() );
			final BitLocation bitLocation = new BitLocation( new RayTraceResult( RayTraceResult.Type.BLOCK, new Vec3d( hitX, hitY, hitZ ), side, usedBlock ), false, ChiselToolType.BIT );
			final BitLocation chiselLocation = new BitLocation( new RayTraceResult( RayTraceResult.Type.BLOCK, new Vec3d( hitX, hitY, hitZ ), side, usedBlock ), false, ChiselToolType.CHISEL );

			IBlockState blkstate = world.getBlockState( bitLocation.blockPos );
			TileEntityBlockChiseled tebc = ModUtil.getChiseledTileEntity( world, bitLocation.blockPos, true );
			if ( tebc == null && BlockChiseled.replaceWithChisled( world, bitLocation.blockPos, blkstate, ItemChiseledBit.getStackState( stack ), true ) )
			{
				blkstate = world.getBlockState( bitLocation.blockPos );
				tebc = ModUtil.getChiseledTileEntity( world, bitLocation.blockPos, true );
			}

			if ( tebc != null )
			{
				PacketChisel pc = null;

				switch ( mode )
				{
					case DRAWN_REGION:
						if ( world.isRemote )
						{
							ClientSide.instance.pointAt( ChiselToolType.BIT, bitLocation );
						}
						return EnumActionResult.FAIL;

					case CONNECTED_PLANE:

						final VoxelRegionSrc region = new VoxelRegionSrc( world, chiselLocation.blockPos, 1 );

						final ChiselTypeIterator i = new ChiselTypeIterator( VoxelBlob.dim, chiselLocation.bitX, chiselLocation.bitY, chiselLocation.bitZ, region, ChiselMode.CONNECTED_PLANE, side );
						final IntegerBox connectedBox = i.getVoxelBox( region.getBlobAt( chiselLocation.blockPos ), true );

						if ( connectedBox == null )
						{
							return EnumActionResult.FAIL;
						}

						BlockPos targetBlock = bitLocation.blockPos;

						connectedBox.move( side, 1 );
						if ( connectedBox.isBadBitPositions() )
						{
							final EnumFacing reverse = side.getOpposite();
							connectedBox.move( reverse, 16 );
							targetBlock = targetBlock.offset( side );
						}

						final BitLocation from = new BitLocation( bitLocation.blockPos, connectedBox.minX, connectedBox.minY, connectedBox.minZ );
						final BitLocation to = new BitLocation( bitLocation.blockPos, connectedBox.maxX, connectedBox.maxY, connectedBox.maxZ );
						pc = new PacketChisel( true, from, to, side, ChiselMode.DRAWN_REGION, hand );
						break;
					default:
						pc = new PacketChisel( true, bitLocation, side, mode, hand );
						break;
				}

				final int result = pc.doAction( player );

				if ( result > 0 )
				{
					NetworkRouter.instance.sendToServer( pc );
				}
			}
		}

		return EnumActionResult.SUCCESS;
	}

	@Override
	public void clearCache()
	{
		bits = null;
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	@Override
	public void getSubItems(
			final Item itemIn,
			final CreativeTabs tab,
			final List subItems )
	{
		if ( bits == null )
		{
			bits = new ArrayList<ItemStack>();

			final ArrayList<ItemStack> List = new ArrayList<ItemStack>();
			final HashSet<IBlockState> used = new HashSet();

			for ( final Object obj : Item.REGISTRY )
			{
				if ( !( obj instanceof ItemBlock ) )
				{
					continue;
				}

				try
				{
					Item it = (Item) obj;
					it.getSubItems( it, it.getCreativeTab(), List );

					for ( final ItemStack out : List )
					{
						it = out.getItem();

						if ( !( it instanceof ItemBlock ) )
						{
							continue;
						}

						final ItemBlock ib = (ItemBlock) it;
						final IBlockState state = ib.block.getStateFromMeta( out.getMetadata() );

						if ( state != null && BlockBitInfo.supportsBlock( state ) )
						{
							used.add( state );
							bits.add( ItemChiseledBit.createStack( Block.getStateId( state ), 1, false ) );
						}
					}

				}
				catch ( final Throwable t )
				{
					// a mod did something that isn't acceptable, let them crash
					// in their own code...
				}

				List.clear();
			}

			for ( final Fluid o : FluidRegistry.getRegisteredFluids().values() )
			{
				if ( o.canBePlacedInWorld() && o.getBlock() != null )
				{
					if ( used.contains( o.getBlock().getDefaultState() ) )
					{
						continue;
					}

					bits.add( ItemChiseledBit.createStack( Block.getStateId( o.getBlock().getDefaultState() ), 1, false ) );
				}
			}
		}

		subItems.addAll( bits );
	}

	public static boolean sameBit(
			final ItemStack output,
			final int blk )
	{
		return output.hasTagCompound() ? getStackState( output ) == blk : false;
	}

	public static ItemStack createStack(
			final int id,
			final int count,
			final boolean RequireStack )
	{
		if ( ChiselsAndBits.getItems().itemBlockBit == null )
		{
			if ( !RequireStack )
			{
				return null;
			}
		}

		final ItemStack out = new ItemStack( ChiselsAndBits.getItems().itemBlockBit, count );
		out.setTagInfo( "id", new NBTTagInt( id ) );
		return out;
	}

	@Override
	public void scroll(
			final EntityPlayer player,
			final ItemStack stack,
			final int dwheel )
	{
		final ChiselMode mode = ChiselModeManager.getChiselMode( player, ChiselToolType.BIT );
		ChiselModeManager.scrollOption( ChiselToolType.BIT, mode, mode, dwheel );
	}

	public static int getStackState(
			final ItemStack inHand )
	{
		return inHand != null && inHand.hasTagCompound() ? inHand.getTagCompound().getInteger( "id" ) : 0;
	}

	public static boolean placeBit(
			final IContinuousInventory bits,
			final ActingPlayer player,
			final VoxelBlob vb,
			final int x,
			final int y,
			final int z )
	{
		if ( vb.get( x, y, z ) == 0 )
		{
			final ItemStackSlot slot = bits.getItem( 0 );
			final int stateID = ItemChiseledBit.getStackState( slot.getStack() );

			if ( slot.isValid() )
			{
				vb.set( x, y, z, stateID );

				if ( !player.isCreative() )
				{
					bits.useItem( stateID );
				}
			}

			return true;
		}

		return false;
	}
}
