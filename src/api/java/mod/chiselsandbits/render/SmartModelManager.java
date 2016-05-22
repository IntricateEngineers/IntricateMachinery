package mod.chiselsandbits.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.interfaces.ICacheClearable;
import mod.chiselsandbits.render.bit.BitItemSmartModel;
import mod.chiselsandbits.render.chiseledblock.ChiseledBlockSmartModel;
import mod.chiselsandbits.render.patterns.PrintSmartModel;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SmartModelManager
{

	private final HashMap<ResourceLocation, IBakedModel> models = new HashMap<ResourceLocation, IBakedModel>();
	private final List<ModelResourceLocation> res = new ArrayList<ModelResourceLocation>();
	private final List<ICacheClearable> clearable = new ArrayList<ICacheClearable>();

	public SmartModelManager()
	{
		add( new ResourceLocation( ChiselsAndBits.MODID, "chiseled_iron" ), new ChiseledBlockSmartModel() );
		add( new ResourceLocation( ChiselsAndBits.MODID, "chiseled_clay" ), new ChiseledBlockSmartModel() );
		add( new ResourceLocation( ChiselsAndBits.MODID, "chiseled_cloth" ), new ChiseledBlockSmartModel() );
		add( new ResourceLocation( ChiselsAndBits.MODID, "chiseled_packedIce" ), new ChiseledBlockSmartModel() );
		add( new ResourceLocation( ChiselsAndBits.MODID, "chiseled_ice" ), new ChiseledBlockSmartModel() );
		add( new ResourceLocation( ChiselsAndBits.MODID, "chiseled_wood" ), new ChiseledBlockSmartModel() );
		add( new ResourceLocation( ChiselsAndBits.MODID, "chiseled_rock" ), new ChiseledBlockSmartModel() );
		add( new ResourceLocation( ChiselsAndBits.MODID, "chiseled_glass" ), new ChiseledBlockSmartModel() );
		add( new ResourceLocation( ChiselsAndBits.MODID, "chiseled_ground" ), new ChiseledBlockSmartModel() );
		add( new ResourceLocation( ChiselsAndBits.MODID, "chiseled_sand" ), new ChiseledBlockSmartModel() );
		add( new ResourceLocation( ChiselsAndBits.MODID, "chiseled_grass" ), new ChiseledBlockSmartModel() );
		add( new ResourceLocation( ChiselsAndBits.MODID, "chiseled_fluid" ), new ChiseledBlockSmartModel() );
		add( new ResourceLocation( ChiselsAndBits.MODID, "chiseled_snow" ), new ChiseledBlockSmartModel() );
		add( new ResourceLocation( ChiselsAndBits.MODID, "chiseled_leaves" ), new ChiseledBlockSmartModel() );

		add( new ResourceLocation( ChiselsAndBits.MODID, "models/item/block_chiseled" ), new ChiseledBlockSmartModel() );

		add( new ResourceLocation( ChiselsAndBits.MODID, "models/item/block_bit" ), new BitItemSmartModel() );
		add( new ResourceLocation( ChiselsAndBits.MODID, "models/item/positiveprint_written_preview" ), new PrintSmartModel( "positiveprint", ChiselsAndBits.getItems().itemPositiveprint ) );
		add( new ResourceLocation( ChiselsAndBits.MODID, "models/item/negativeprint_written_preview" ), new PrintSmartModel( "negativeprint", ChiselsAndBits.getItems().itemNegativeprint ) );
		add( new ResourceLocation( ChiselsAndBits.MODID, "models/item/mirrorprint_written_preview" ), new PrintSmartModel( "mirrorprint", ChiselsAndBits.getItems().itemMirrorprint ) );
	}

	private void add(
			final ResourceLocation modelLocation,
			final IBakedModel modelGen )
	{
		final ResourceLocation second = new ResourceLocation( modelLocation.getResourceDomain(), modelLocation.getResourcePath().substring( 1 + modelLocation.getResourcePath().lastIndexOf( '/' ) ) );

		if ( modelGen instanceof ICacheClearable )
		{
			clearable.add( (ICacheClearable) modelGen );
		}

		res.add( new ModelResourceLocation( modelLocation, null ) );
		res.add( new ModelResourceLocation( second, null ) );

		res.add( new ModelResourceLocation( modelLocation, "inventory" ) );
		res.add( new ModelResourceLocation( second, "inventory" ) );

		res.add( new ModelResourceLocation( modelLocation, "multipart" ) );
		res.add( new ModelResourceLocation( second, "multipart" ) );

		models.put( modelLocation, modelGen );
		models.put( second, modelGen );

		models.put( new ModelResourceLocation( modelLocation, null ), modelGen );
		models.put( new ModelResourceLocation( second, null ), modelGen );

		models.put( new ModelResourceLocation( modelLocation, "inventory" ), modelGen );
		models.put( new ModelResourceLocation( second, "inventory" ), modelGen );

		models.put( new ModelResourceLocation( modelLocation, "multipart" ), modelGen );
		models.put( new ModelResourceLocation( second, "multipart" ), modelGen );
	}

	@SubscribeEvent
	public void textureStichEvent(
			final TextureStitchEvent.Post stitch )
	{
		ChiselsAndBits.getInstance().clearCache();
	}

	@SubscribeEvent
	public void onModelBakeEvent(
			final ModelBakeEvent event )
	{
		for ( final ICacheClearable c : clearable )
		{
			c.clearCache();
		}

		for ( final ModelResourceLocation rl : res )
		{
			event.getModelRegistry().putObject( rl, getModel( rl ) );
		}
	}

	private IBakedModel getModel(
			final ResourceLocation modelLocation )
	{
		try
		{
			return models.get( modelLocation );
		}
		catch ( final Exception e )
		{
			throw new RuntimeException( "The Model: " + modelLocation.toString() + " was not available was requested." );
		}
	}

}
