package mod.chiselsandbits.registry;

import java.util.HashMap;
import java.util.Map;

import mod.chiselsandbits.bittank.BlockBitTank;
import mod.chiselsandbits.bittank.ItemBlockBitTank;
import mod.chiselsandbits.bittank.TileEntityBitTank;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.chiseledblock.BlockChiseled;
import mod.chiselsandbits.chiseledblock.ItemBlockChiseled;
import mod.chiselsandbits.chiseledblock.MaterialType;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseledTESR;
import mod.chiselsandbits.config.ModConfig;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ModBlocks extends ModRegistry
{

	// TE Registration names.
	private static String TE_BIT_TANK = "mod.chiselsandbits.TileEntityBitTank";
	private static String TE_CHISELEDBLOCK = "mod.chiselsandbits.TileEntityChiseled";
	private static String TE_CHISELEDBLOCK_TESR = "mod.chiselsandbits.TileEntityChiseled.tesr";

	private final HashMap<Material, BlockChiseled> conversions = new HashMap<Material, BlockChiseled>();

	public final BlockBitTank blockBitTank;

	public static final MaterialType[] validMaterials = new MaterialType[] {
			new MaterialType( "wood", Material.WOOD ),
			new MaterialType( "rock", Material.ROCK ),
			new MaterialType( "iron", Material.IRON ),
			new MaterialType( "cloth", Material.CLOTH ),
			new MaterialType( "ice", Material.ICE ),
			new MaterialType( "packedIce", Material.PACKED_ICE ),
			new MaterialType( "clay", Material.CLAY ),
			new MaterialType( "glass", Material.GLASS ),
			new MaterialType( "sand", Material.SAND ),
			new MaterialType( "ground", Material.GROUND ),
			new MaterialType( "grass", Material.GRASS ),
			new MaterialType( "snow", Material.CRAFTED_SNOW ),
			new MaterialType( "fluid", Material.WATER ),
			new MaterialType( "leaves", Material.LEAVES ),
	};

	public ModBlocks(
			final ModConfig config,
			final Side side )
	{
		// register tile entities.
		GameRegistry.registerTileEntity( TileEntityBlockChiseled.class, TE_CHISELEDBLOCK );

		/**
		 * register the TESR name either way, but if its a dedicated server
		 * register the normal class under the same name.
		 */
		if ( side == Side.CLIENT )
		{
			GameRegistry.registerTileEntity( TileEntityBlockChiseledTESR.class, TE_CHISELEDBLOCK_TESR );
		}
		else
		{
			GameRegistry.registerTileEntity( TileEntityBlockChiseled.class, TE_CHISELEDBLOCK_TESR );
		}

		if ( config.enableBitTank )
		{
			blockBitTank = new BlockBitTank();
			registerBlock( blockBitTank, ItemBlockBitTank.class, "bittank" );
			GameRegistry.registerTileEntity( TileEntityBitTank.class, TE_BIT_TANK );
		}
		else
		{
			blockBitTank = null;
		}

		// register blocks...
		for ( final MaterialType mat : validMaterials )
		{
			final BlockChiseled blk = new BlockChiseled( mat.type, "chiseled_" + mat.name );
			getConversions().put( mat.type, blk );
			registerBlock( blk, ItemBlockChiseled.class, blk.name );
		}
	}

	public void addRecipes()
	{
		ShapedOreRecipe( blockBitTank, " G ", "GOG", " I ", 'G', "blockGlass", 'O', "logWood", 'I', "ingotIron" );
	}

	public IBlockState getChiseledDefaultState()
	{
		for ( final BlockChiseled bc : getConversions().values() )
		{
			return bc.getDefaultState();
		}
		return null;
	}

	public BlockChiseled getConversion(
			final IBlockState material )
	{
		final Fluid f = BlockBitInfo.getFluidFromBlock( material.getBlock() );

		if ( f != null )
		{
			return getConversions().get( Material.WATER );
		}

		return getConversions().get( material.getMaterial() );
	}

	public BlockChiseled getConversionWithDefault(
			final IBlockState material )
	{
		final BlockChiseled bcX = getConversion( material );

		if ( bcX == null )
		{
			for ( final BlockChiseled bc : getConversions().values() )
			{
				return bc;
			}
		}

		return bcX;
	}

	public Map<Material, BlockChiseled> getConversions()
	{
		return conversions;
	}

}
