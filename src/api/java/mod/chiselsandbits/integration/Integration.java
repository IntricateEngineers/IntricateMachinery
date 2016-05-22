package mod.chiselsandbits.integration;

import java.util.ArrayList;
import java.util.List;

import mod.chiselsandbits.api.ChiselsAndBitsAddon;
import mod.chiselsandbits.api.IChiselsAndBitsAddon;
import mod.chiselsandbits.core.Log;
import mod.chiselsandbits.integration.mods.PluginAPIWrapper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Integration
{

	private final List<IntegrationBase> integrations = new ArrayList<IntegrationBase>();

	public void preinit(
			final FMLPreInitializationEvent event )
	{
		// addons/plugins
		for ( final ASMData asmData : event.getAsmData().getAll( ChiselsAndBitsAddon.class.getName() ) )
		{
			try
			{
				final Class<?> asmClass = Class.forName( asmData.getClassName() );
				final Class<? extends IChiselsAndBitsAddon> asmInstanceClass = asmClass.asSubclass( IChiselsAndBitsAddon.class );
				final IChiselsAndBitsAddon instance = asmInstanceClass.newInstance();
				integrations.add( new PluginAPIWrapper( instance ) );
			}
			catch ( final Exception e )
			{
				Log.logError( "Failed to create instance of " + asmData.getClassName() + " as IChiselsAndBitsAddon, Please forward this to the developer of " + asmData.getClassName(), e );
			}
		}

		// integration
		for ( final ASMData asmData : event.getAsmData().getAll( ChiselsAndBitsIntegration.class.getName() ) )
		{
			final Object modID = asmData.getAnnotationInfo().get( "value" );
			if ( modID instanceof String && Loader.isModLoaded( (String) modID ) )
			{
				try
				{
					final Class<?> asmClass = Class.forName( asmData.getClassName() );
					final Class<? extends IntegrationBase> asmInstanceClass = asmClass.asSubclass( IntegrationBase.class );
					final IntegrationBase instance = asmInstanceClass.newInstance();
					integrations.add( instance );
				}
				catch ( final Exception e )
				{
					Log.logError( "Failed to load: " + asmData.getClassName(), e );
				}
			}
		}

		for ( final IntegrationBase i : integrations )
		{
			i.preinit();
		}
	}

	public void init()
	{
		for ( final IntegrationBase i : integrations )
		{
			i.init();
		}
	}

	public void postinit()
	{
		for ( final IntegrationBase i : integrations )
		{
			i.postinit();
		}
	}
}
