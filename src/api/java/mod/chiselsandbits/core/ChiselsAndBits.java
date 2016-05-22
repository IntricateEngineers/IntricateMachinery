package mod.chiselsandbits.core;

import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.client.CreativeClipboardTab;
import mod.chiselsandbits.client.UndoTracker;
import mod.chiselsandbits.client.gui.ModGuiRouter;
import mod.chiselsandbits.config.ModConfig;
import mod.chiselsandbits.core.api.ChiselAndBitsAPI;
import mod.chiselsandbits.core.api.IMCHandler;
import mod.chiselsandbits.crafting.*;
import mod.chiselsandbits.events.EventPlayerInteract;
import mod.chiselsandbits.integration.Integration;
import mod.chiselsandbits.interfaces.ICacheClearable;
import mod.chiselsandbits.network.NetworkRouter;
import mod.chiselsandbits.registry.ModBlocks;
import mod.chiselsandbits.registry.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mod(
		name = ChiselsAndBits.MODNAME,
		modid = ChiselsAndBits.MODID,
		version = ChiselsAndBits.VERSION,
		acceptedMinecraftVersions = "[1.9.4]",
		dependencies = ChiselsAndBits.DEPENDENCIES,
		guiFactory = "mod.chiselsandbits.client.gui.ModConfigGuiFactory" )
public class ChiselsAndBits
{
	public static final String MODNAME = "Chisels & Bits";
	public static final String MODID = "chiselsandbits";
	public static final String VERSION = "@VERSION@";

	public static final String DEPENDENCIES = "required-after:Forge@[" // forge.
			+ net.minecraftforge.common.ForgeVersion.majorVersion + '.' // majorVersion
			+ net.minecraftforge.common.ForgeVersion.minorVersion + '.' // minorVersion
			+ net.minecraftforge.common.ForgeVersion.revisionVersion + '.' // revisionVersion
			+ net.minecraftforge.common.ForgeVersion.buildVersion + ",);after:mcmultipart;after:jei@[11.15.0.1697,)"; // buildVersion

	private static ChiselsAndBits instance;
	private ModConfig config;
	private ModItems items;
	private ModBlocks blocks;
	private final Integration integration = new Integration();
	private final IChiselAndBitsAPI api = new ChiselAndBitsAPI();

	List<ICacheClearable> cacheClearables = new ArrayList<ICacheClearable>();

	public ChiselsAndBits()
	{
		instance = this;
	}

	public static ChiselsAndBits getInstance()
	{
		return instance;
	}

	public static ModBlocks getBlocks()
	{
		return instance.blocks;
	}

	public static ModItems getItems()
	{
		return instance.items;
	}

	public static ModConfig getConfig()
	{
		return instance.config;
	}

	public static IChiselAndBitsAPI getApi()
	{
		return instance.api;
	}

	@EventHandler
	private void handleIMCEvent(
			final FMLInterModComms.IMCEvent event )
	{
		final IMCHandler imcHandler = new IMCHandler();
		imcHandler.handleIMCEvent( event );
	}

	@EventHandler
	public void preinit(
			final FMLPreInitializationEvent event )
	{
		// load config...
		final File configFile = event.getSuggestedConfigurationFile();
		config = new ModConfig( configFile );

		items = new ModItems( getConfig() );
		blocks = new ModBlocks( getConfig(), event.getSide() );

		integration.preinit( event );

		// loader must be added here to prevent missing models, the rest of the
		// model/textures must be configured later.
		if ( event.getSide() == Side.CLIENT )
		{
			// load this after items are created...
			CreativeClipboardTab.load( new File( configFile.getParent(), MODID + "_clipboard.cfg" ) );

			ClientSide.instance.preinit( this );
		}
	}

	@EventHandler
	public void init(
			final FMLInitializationEvent event )
	{
		if ( event.getSide() == Side.CLIENT )
		{
			ClientSide.instance.init( this );
		}

		integration.init();

		// registerWithBus( new EventBreakSpeed() );
		registerWithBus( new EventPlayerInteract() );

		// add recipes to game...
		getBlocks().addRecipes();
		getItems().addRecipes();

		final String craftingOrder = "after:minecraft:shapeless";

		// add special recipes...
		if ( getConfig().enablePositivePrintCrafting )
		{
			GameRegistry.addRecipe( new ChiselCrafting() );
			RecipeSorter.register( MODID + ":chiselcrafting", ChiselCrafting.class, Category.UNKNOWN, craftingOrder );
		}

		if ( getConfig().enableStackableCrafting )
		{
			GameRegistry.addRecipe( new StackableCrafting() );
			RecipeSorter.register( MODID + ":stackablecrafting", StackableCrafting.class, Category.UNKNOWN, craftingOrder );
		}

		if ( getConfig().enableNegativePrintInversionCrafting )
		{
			GameRegistry.addRecipe( new NegativeInversionCrafting() );
			RecipeSorter.register( MODID + ":negativepatterncrafting", NegativeInversionCrafting.class, Category.UNKNOWN, craftingOrder );
		}

		if ( getConfig().enableMirrorPrint )
		{
			GameRegistry.addRecipe( new MirrorTransferCrafting() );
			RecipeSorter.register( MODID + ":mirrorpatterncrafting", MirrorTransferCrafting.class, Category.UNKNOWN, craftingOrder );
		}

		if ( getConfig().enableBitSaw )
		{
			GameRegistry.addRecipe( new BitSawCrafting() );
			RecipeSorter.register( MODID + ":bitsawcrafting", BitSawCrafting.class, Category.UNKNOWN, craftingOrder );
		}
	}

	@EventHandler
	public void postinit(
			final FMLPostInitializationEvent event )
	{
		if ( event.getSide() == Side.CLIENT )
		{
			ClientSide.instance.postinit( this );
		}

		integration.postinit();

		for ( final Fluid o : FluidRegistry.getRegisteredFluids().values() )
		{
			if ( o.canBePlacedInWorld() )
			{
				BlockBitInfo.addFluidBlock( o.getBlock(), o );
			}
		}

		NetworkRouter.instance = new NetworkRouter();
		NetworkRegistry.INSTANCE.registerGuiHandler( this, new ModGuiRouter() );
	}

	@EventHandler
	public void idsMapped(
			final FMLModIdMappingEvent event )
	{
		clearCache();
	}

	public void clearCache()
	{
		for ( final ICacheClearable clearable : cacheClearables )
		{
			clearable.clearCache();
		}

		addClearable( UndoTracker.getInstance() );
		VoxelBlob.clearCache();
	}

	public static void registerWithBus(
			final Object obj )
	{
		MinecraftForge.EVENT_BUS.register( obj );
	}

	public void addClearable(
			final ICacheClearable cache )
	{
		cacheClearables.add( cache );
	}

}
