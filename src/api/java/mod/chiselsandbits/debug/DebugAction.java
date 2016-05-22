package mod.chiselsandbits.debug;

import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IBitLocation;
import mod.chiselsandbits.api.IBitVisitor;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.ItemType;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.Log;
import mod.chiselsandbits.integration.mcmultipart.MCMultipartProxy;
import mod.chiselsandbits.items.ItemChiseledBit;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public abstract class DebugAction
{

	public static IChiselAndBitsAPI api;

	static enum Tests
	{
		canBeChiseled( new DebugAction.canBeChiseled() ),
		createBitItem( new DebugAction.createBitItem() ),
		getBit( new DebugAction.getBit() ),
		getBitAccess( new DebugAction.getBitAccess() ),
		setBitAccess( new DebugAction.setBitAccess() ),
		isBlockChiseled( new DebugAction.isBlockChiseled() ),
		ItemTests( new DebugAction.ItemTests() ),
		Randomize( new DebugAction.Randomize() ),
		getTileClass( new DebugAction.getTileClass() ),
		occusionTest( new DebugAction.occlusionTest() );

		final DebugAction which;

		private Tests(
				final DebugAction action )
		{
			which = action;
		}
	};

	protected static void Msg(
			final EntityPlayer player,
			final String msg )
	{
		final String side = FMLCommonHandler.instance().getEffectiveSide().name() + ": ";
		player.addChatComponentMessage( new TextComponentString( side + msg ) );
	}

	private static void apiAssert(
			final String name,
			final EntityPlayer player,
			final boolean must_be_true )
	{
		if ( must_be_true != true )
		{
			Msg( player, name + " = false" );
		}
	}

	public abstract void run(
			final World w,
			final BlockPos pos,
			final EnumFacing side,
			final float hitX,
			final float hitY,
			final float hitZ,
			EntityPlayer player );

	static class ItemTests extends DebugAction
	{

		@Override
		public void run(
				final World w,
				final BlockPos pos,
				final EnumFacing side,
				final float hitX,
				final float hitY,
				final float hitZ,
				final EntityPlayer player )
		{
			final IBitAccess access = api.createBitItem( null );

			apiAssert( "BIT_BAG", player, api.getItemType( new ItemStack( ChiselsAndBits.getItems().itemBitBag ) ) == ItemType.BIT_BAG );
			apiAssert( "CHISEL", player, api.getItemType( new ItemStack( ChiselsAndBits.getItems().itemChiselDiamond ) ) == ItemType.CHISEL );
			apiAssert( "MIRROR_DESIGN 1", player, api.getItemType( new ItemStack( ChiselsAndBits.getItems().itemMirrorprint ) ) == ItemType.MIRROR_DESIGN );
			apiAssert( "NEGATIVE_DESIGN 1", player, api.getItemType( new ItemStack( ChiselsAndBits.getItems().itemNegativeprint ) ) == ItemType.NEGATIVE_DESIGN );
			apiAssert( "POSITIVE_DESIGN 1", player, api.getItemType( new ItemStack( ChiselsAndBits.getItems().itemPositiveprint ) ) == ItemType.POSITIVE_DESIGN );
			apiAssert( "WRENCH", player, api.getItemType( new ItemStack( ChiselsAndBits.getItems().itemWrench ) ) == ItemType.WRENCH );
			apiAssert( "CHISLED_BIT-cobblestone", player, api.getItemType( ItemChiseledBit.createStack( Block.getStateId( Blocks.COBBLESTONE.getDefaultState() ), 1, true ) ) == ItemType.CHISLED_BIT );
			apiAssert( "CHISLED_BLOCK", player, api.getItemType( access.getBitsAsItem( null, ItemType.CHISLED_BLOCK, false ) ) == null );
			apiAssert( "MIRROR_DESIGN 2", player, api.getItemType( access.getBitsAsItem( null, ItemType.MIRROR_DESIGN, false ) ) == null );
			apiAssert( "NEGATIVE_DESIGN 2", player, api.getItemType( access.getBitsAsItem( null, ItemType.NEGATIVE_DESIGN, false ) ) == null );
			apiAssert( "POSITIVE_DESIGN 2", player, api.getItemType( access.getBitsAsItem( null, ItemType.POSITIVE_DESIGN, false ) ) == null );

			try
			{
				final ItemStack bitItem = api.getBitItem( Blocks.COBBLESTONE.getDefaultState() );
				final IBitBrush brush = api.createBrush( bitItem );
				access.setBitAt( 0, 0, 0, brush );
			}
			catch ( final InvalidBitItem e )
			{
				apiAssert( "createBrush/getBitItem", player, false );
			}
			catch ( final SpaceOccupied e )
			{
				apiAssert( "setBitAt", player, false );
			}

			apiAssert( "CHISLED_BLOCK 2", player, api.getItemType( access.getBitsAsItem( null, ItemType.CHISLED_BLOCK, false ) ) == ItemType.CHISLED_BLOCK );
			apiAssert( "MIRROR_DESIGN 3", player, api.getItemType( access.getBitsAsItem( null, ItemType.MIRROR_DESIGN, false ) ) == ItemType.MIRROR_DESIGN );
			apiAssert( "NEGATIVE_DESIGN 3", player, api.getItemType( access.getBitsAsItem( null, ItemType.NEGATIVE_DESIGN, false ) ) == ItemType.NEGATIVE_DESIGN );
			apiAssert( "POSITIVE_DESIGN 3", player, api.getItemType( access.getBitsAsItem( null, ItemType.POSITIVE_DESIGN, false ) ) == ItemType.POSITIVE_DESIGN );
			apiAssert( "WRENCH", player, api.getItemType( access.getBitsAsItem( null, ItemType.WRENCH, false ) ) == null );
		}

	};

	static class getTileClass extends DebugAction
	{

		@Override
		public void run(
				final World w,
				final BlockPos pos,
				final EnumFacing side,
				final float hitX,
				final float hitY,
				final float hitZ,
				final EntityPlayer player )
		{
			final TileEntity te = w.getTileEntity( pos );
			if ( te != null )
			{
				Msg( player, te.getClass().getName() );
			}
		}

	};

	static class canBeChiseled extends DebugAction
	{

		@Override
		public void run(
				final World w,
				final BlockPos pos,
				final EnumFacing side,
				final float hitX,
				final float hitY,
				final float hitZ,
				final EntityPlayer player )
		{
			Msg( player, "canBeChiseled = " + ( api.canBeChiseled( w, pos ) ? "true" : "false" ) );
		}

	};

	static class isBlockChiseled extends DebugAction
	{

		@Override
		public void run(
				final World w,
				final BlockPos pos,
				final EnumFacing side,
				final float hitX,
				final float hitY,
				final float hitZ,
				final EntityPlayer player )
		{
			Msg( player, "isBlockChiseled = " + ( api.isBlockChiseled( w, pos ) ? "true" : "false" ) );
		}

	};

	static class getBitAccess extends DebugAction
	{

		@Override
		public void run(
				final World w,
				final BlockPos pos,
				final EnumFacing side,
				final float hitX,
				final float hitY,
				final float hitZ,
				final EntityPlayer player )
		{
			final IBitLocation loc = api.getBitPos( hitX, hitY, hitZ, side, pos, true );

			try
			{
				final IBitAccess access = api.getBitAccess( w, loc.getBlockPos() );
				final IBitBrush brush = api.createBrush( api.getBitItem( Blocks.COBBLESTONE.getDefaultState() ) );

				access.setBitAt( loc.getBitX(), loc.getBitY(), loc.getBitZ(), brush );
				access.commitChanges( true );
			}
			catch ( final CannotBeChiseled e )
			{
				Log.logError( "FAIL", e );
			}
			catch ( final SpaceOccupied e )
			{
				Log.logError( "FAIL", e );
			}
			catch ( final InvalidBitItem e )
			{
				Log.logError( "FAIL", e );
			}
		}

	};

	static class setBitAccess extends DebugAction
	{

		@Override
		public void run(
				final World w,
				final BlockPos pos,
				final EnumFacing side,
				final float hitX,
				final float hitY,
				final float hitZ,
				final EntityPlayer player )
		{
			final IBitLocation loc = api.getBitPos( hitX, hitY, hitZ, side, pos, false );

			try
			{
				final IBitAccess access = api.getBitAccess( w, loc.getBlockPos() );
				final IBitBrush brush = api.createBrush( null );

				access.setBitAt( loc.getBitX(), loc.getBitY(), loc.getBitZ(), brush );
				access.commitChanges( true );
			}
			catch ( final CannotBeChiseled e )
			{
				Log.logError( "FAIL", e );
			}
			catch ( final SpaceOccupied e )
			{
				Log.logError( "FAIL", e );
			}
			catch ( final InvalidBitItem e )
			{
				Log.logError( "FAIL", e );
			}
		}

	};

	static class Randomize extends DebugAction
	{

		@Override
		public void run(
				final World w,
				final BlockPos pos,
				final EnumFacing side,
				final float hitX,
				final float hitY,
				final float hitZ,
				final EntityPlayer player )
		{
			final IBitLocation loc = api.getBitPos( hitX, hitY, hitZ, side, pos, false );

			try
			{
				final IBitAccess access = api.getBitAccess( w, loc.getBlockPos() );

				access.visitBits( new IBitVisitor() {

					@Override
					public IBitBrush visitBit(
							final int x,
							final int y,
							final int z,
							final IBitBrush currentValue )
					{
						IBitBrush bit = null;
						final IBlockState state = Blocks.WOOL.getStateFromMeta( 3 );

						try
						{
							bit = api.createBrush( api.getBitItem( state ) );
						}
						catch ( final InvalidBitItem e )
						{
						}

						return y % 2 == 0 ? currentValue : bit;
					}
				} );

				access.commitChanges( true );
			}
			catch ( final CannotBeChiseled e )
			{
				Log.logError( "FAIL", e );
			}
		}

	};

	static class getBit extends DebugAction
	{

		@Override
		public void run(
				final World w,
				final BlockPos pos,
				final EnumFacing side,
				final float hitX,
				final float hitY,
				final float hitZ,
				final EntityPlayer player )
		{
			final IBitLocation loc = api.getBitPos( hitX, hitY, hitZ, side, pos, false );

			try
			{
				final IBitAccess access = api.getBitAccess( w, loc.getBlockPos() );
				final IBitBrush brush = access.getBitAt( loc.getBitX(), loc.getBitY(), loc.getBitZ() );

				if ( brush == null )
				{
					Msg( player, "AIR!" );
				}
				else
				{
					final IBlockState state = brush.getState();
					final Block blk = state.getBlock();

					final ItemStack it = brush.getItemStack( 1 );

					if ( it.getItem() != null )
					{
						player.inventory.addItemStackToInventory( it );
					}

					player.inventory.addItemStackToInventory( new ItemStack( blk, 1, blk.getMetaFromState( state ) ) );
				}
			}
			catch ( final CannotBeChiseled e )
			{
				Log.logError( "FAIL", e );
			}
		}

	};

	static class occlusionTest extends DebugAction
	{

		@Override
		public void run(
				final World w,
				final BlockPos pos,
				final EnumFacing side,
				final float hitX,
				final float hitY,
				final float hitZ,
				final EntityPlayer player )
		{
			final IBitLocation loc = api.getBitPos( hitX, hitY, hitZ, side, pos, false );

			final VoxelBlob out = new VoxelBlob();
			MCMultipartProxy.proxyMCMultiPart.addFiller( w, loc.getBlockPos(), out );

			player.addChatComponentMessage( new TextComponentString( out.filled() + " blocked" ) );
			player.addChatComponentMessage( new TextComponentString( out.air() + " not-blocked" ) );

			final boolean isMultiPart = MCMultipartProxy.proxyMCMultiPart.isMultiPartTileEntity( w, loc.getBlockPos() );
			player.addChatComponentMessage( new TextComponentString( isMultiPart ? "Multipart" : "Not-Multipart" ) );
		}

	};

	static class createBitItem extends DebugAction
	{

		@Override
		public void run(
				final World w,
				final BlockPos pos,
				final EnumFacing side,
				final float hitX,
				final float hitY,
				final float hitZ,
				final EntityPlayer player )
		{
			final IBitLocation loc = api.getBitPos( hitX, hitY, hitZ, side, pos, false );

			try
			{
				final IBitAccess access = api.getBitAccess( w, loc.getBlockPos() );

				player.inventory.addItemStackToInventory( access.getBitsAsItem( side, ItemType.CHISLED_BLOCK, false ) );
				player.inventory.addItemStackToInventory( access.getBitsAsItem( side, ItemType.MIRROR_DESIGN, false ) );
				player.inventory.addItemStackToInventory( access.getBitsAsItem( side, ItemType.NEGATIVE_DESIGN, false ) );
				player.inventory.addItemStackToInventory( access.getBitsAsItem( side, ItemType.POSITIVE_DESIGN, false ) );
			}
			catch ( final CannotBeChiseled e )
			{
				Log.logError( "FAIL", e );
			}
		}

	};

}
