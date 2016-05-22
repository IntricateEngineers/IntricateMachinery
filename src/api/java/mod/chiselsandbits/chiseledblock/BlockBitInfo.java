package mod.chiselsandbits.chiseledblock;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Random;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import mod.chiselsandbits.api.IgnoreBlockLogic;
import mod.chiselsandbits.chiseledblock.data.VoxelType;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.Log;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.render.helpers.ModelUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockGlowstone;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.BlockSnowBlock;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class BlockBitInfo
{
	// imc api...
	private static HashMap<Block, Boolean> ignoreLogicBlocks = new HashMap<Block, Boolean>();

	static
	{
		ignoreLogicBlocks.put( Blocks.LEAVES, true );
		ignoreLogicBlocks.put( Blocks.LEAVES2, true );
		ignoreLogicBlocks.put( Blocks.SNOW, true );
	}

	// cache data..
	private static HashMap<IBlockState, BlockBitInfo> stateBitInfo = new HashMap<IBlockState, BlockBitInfo>();
	private static HashMap<Block, Boolean> supportedBlocks = new HashMap<Block, Boolean>();
	private static HashMap<Block, Fluid> fluidBlocks = new HashMap<Block, Fluid>();
	private static TIntObjectMap<Fluid> fluidStates = new TIntObjectHashMap<Fluid>();
	private static HashMap<IBlockState, Integer> bitColor = new HashMap<IBlockState, Integer>();

	public static int getColorFor(
			final IBlockState state,
			final int tint )
	{
		Integer out = bitColor.get( state );

		if ( out == null )
		{
			final Block blk = state.getBlock();

			final Fluid fluid = BlockBitInfo.getFluidFromBlock( blk );
			if ( fluid != null )
			{
				out = fluid.getColor();
			}
			else
			{
				final ItemStack target = ModUtil.getItemFromBlock( state );

				if ( target == null )
				{
					out = 0xffffff;
				}
				else
				{
					out = ModelUtil.getItemStackColor( target, tint );
				}
			}

			bitColor.put( state, out );
		}

		return out;
	}

	public static void addFluidBlock(
			final Block blk,
			final Fluid fluid )
	{
		if ( blk == null )
		{
			return;
		}

		fluidBlocks.put( blk, fluid );

		for ( final IBlockState state : blk.getBlockState().getValidStates() )
		{
			try
			{
				fluidStates.put( Block.getStateId( state ), fluid );
			}
			catch ( final Throwable t )
			{
				Log.logError( "Error while determining fluid state.", t );
			}
		}

		stateBitInfo.clear();
		supportedBlocks.clear();
	}

	static public Fluid getFluidFromBlock(
			final Block blk )
	{
		return fluidBlocks.get( blk );
	}

	public static VoxelType getTypeFromStateID(
			final int bit )
	{
		if ( bit == 0 )
		{
			return VoxelType.AIR;
		}

		return fluidStates.containsKey( bit ) ? VoxelType.FLUID : VoxelType.SOLID;
	}

	public static void ignoreBlockLogic(
			final Block which )
	{
		ignoreLogicBlocks.put( which, true );

		stateBitInfo.clear();
		supportedBlocks.clear();
	}

	public static BlockBitInfo getBlockInfo(
			final IBlockState state )
	{
		BlockBitInfo bit = stateBitInfo.get( state );

		if ( bit == null )
		{
			bit = BlockBitInfo.createFromState( state );
			stateBitInfo.put( state, bit );
		}

		return bit;
	}

	public static boolean supportsBlock(
			final IBlockState state )
	{
		final Block blk = state.getBlock();

		if ( supportedBlocks.containsKey( blk ) )
		{
			return supportedBlocks.get( blk );
		}

		try
		{
			// require basic hardness behavior...
			final ReflectionHelperBlock pb = new ReflectionHelperBlock();
			final Class<? extends Block> blkClass = blk.getClass();

			// custom dropping behavior?
			pb.quantityDropped( null );
			final Class<?> wc = blkClass.getMethod( pb.MethodName, Random.class ).getDeclaringClass();
			final boolean quantityDroppedTest = wc == Block.class || wc == BlockGlowstone.class || wc == BlockStainedGlass.class || wc == BlockGlass.class || wc == BlockSnowBlock.class;

			pb.quantityDroppedWithBonus( 0, null );
			final boolean quantityDroppedWithBonusTest = blkClass.getMethod( pb.MethodName, int.class, Random.class ).getDeclaringClass() == Block.class || wc == BlockGlowstone.class;

			pb.quantityDropped( null, 0, null );
			final boolean quantityDropped2Test = blkClass.getMethod( pb.MethodName, IBlockState.class, int.class, Random.class ).getDeclaringClass() == Block.class;

			final boolean isNotSlab = Item.getItemFromBlock( blk ) != null;
			boolean itemExistsOrNotSpecialDrops = quantityDroppedTest && quantityDroppedWithBonusTest && quantityDropped2Test || isNotSlab;

			// ignore blocks with custom collision.
			pb.onEntityCollidedWithBlock( null, null, null, null );
			boolean noCustomCollision = blkClass.getMethod( pb.MethodName, World.class, BlockPos.class, IBlockState.class, Entity.class ).getDeclaringClass() == Block.class || blkClass == BlockSlime.class;

			// full cube specifically is tied to lighting... so for glass
			// Compatibility use isFullBlock which can be true for glass.
			boolean isFullBlock = blk.isFullBlock( state ) || blkClass == BlockStainedGlass.class || blkClass == BlockGlass.class || blk == Blocks.SLIME_BLOCK || blk == Blocks.ICE;

			final BlockBitInfo info = BlockBitInfo.createFromState( state );

			boolean hasBehavior = ( blk.hasTileEntity( state ) || blk.getTickRandomly() ) && blkClass != BlockGrass.class && blkClass != BlockIce.class;

			final boolean supportedMaterial = ChiselsAndBits.getBlocks().getConversion( state ) != null;

			final Boolean IgnoredLogic = ignoreLogicBlocks.get( blk );
			if ( blkClass.isAnnotationPresent( IgnoreBlockLogic.class ) || IgnoredLogic != null && IgnoredLogic )
			{
				isFullBlock = true;
				noCustomCollision = true;
				hasBehavior = false;
				itemExistsOrNotSpecialDrops = true;
			}

			if ( info.isCompatiable && noCustomCollision && info.hardness >= -0.01f && isFullBlock && supportedMaterial && !hasBehavior && itemExistsOrNotSpecialDrops )
			{
				final boolean result = ChiselsAndBits.getConfig().isEnabled( blkClass.getName() );
				supportedBlocks.put( blk, result );

				if ( result )
				{
					stateBitInfo.put( state, info );
				}

				return result;
			}

			if ( fluidBlocks.containsKey( blk ) )
			{
				stateBitInfo.put( state, info );
				supportedBlocks.put( blk, true );
				return true;
			}

			supportedBlocks.put( blk, false );
			return false;
		}
		catch ( final Throwable t )
		{
			// if the above test fails for any reason, then the block cannot be
			// supported.
			supportedBlocks.put( blk, false );
			return false;
		}
	}

	public final boolean isCompatiable;
	public final float hardness;
	public final float explosionResistance;

	private BlockBitInfo(
			final boolean isCompatiable,
			final float hardness,
			final float explosionResistance )
	{
		this.isCompatiable = isCompatiable;
		this.hardness = hardness;
		this.explosionResistance = explosionResistance;
	}

	public static BlockBitInfo createFromState(
			final IBlockState state )
	{
		try
		{
			// require basic hardness behavior...
			final ReflectionHelperBlock reflectBlock = new ReflectionHelperBlock();
			final Block blk = state.getBlock();
			final Class<? extends Block> blkClass = blk.getClass();

			reflectBlock.getBlockHardness( null, null, null );
			final Method hardnessMethod = blkClass.getMethod( reflectBlock.MethodName, IBlockState.class, World.class, BlockPos.class );
			final boolean test_a = hardnessMethod.getDeclaringClass() == Block.class;

			reflectBlock.getPlayerRelativeBlockHardness( null, null, null, null );
			final boolean test_b = blkClass.getMethod( reflectBlock.MethodName, IBlockState.class, EntityPlayer.class, World.class, BlockPos.class ).getDeclaringClass() == Block.class;

			reflectBlock.getExplosionResistance( null );
			final Method exploResistance = blkClass.getMethod( reflectBlock.MethodName, Entity.class );
			final boolean test_c = exploResistance.getDeclaringClass() == Block.class;

			reflectBlock.getExplosionResistance( null, null, null, null );
			final boolean test_d = blkClass.getMethod( reflectBlock.MethodName, World.class, BlockPos.class, Entity.class, Explosion.class ).getDeclaringClass() == Block.class;

			final boolean isFluid = fluidStates.containsKey( Block.getStateId( state ) );

			// is it perfect?
			if ( test_a && test_b && test_c && test_d && !isFluid )
			{
				final float blockHardness = blk.getBlockHardness( null, null, null );
				final float resistance = blk.getExplosionResistance( null );

				return new BlockBitInfo( true, blockHardness, resistance );
			}
			else
			{
				// less accurate, we can just pretend they are some fixed
				// hardness... say like stone?

				final Block stone = Blocks.STONE;
				return new BlockBitInfo( ChiselsAndBits.getConfig().compatabilityMode, stone.getBlockHardness( null, null, null ), stone.getExplosionResistance( null ) );
			}
		}
		catch ( final Exception err )
		{
			return new BlockBitInfo( false, -1, -1 );
		}
	}

}
