package mod.chiselsandbits.client.gui;

import java.lang.reflect.Constructor;

import mod.chiselsandbits.bitbag.BagContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings( "unused" )
public enum ModGuiTypes
{

	BitBag( BagContainer.class );

	private final Class<? extends Container> container;
	private final Class<?> gui;

	public final Constructor<?> container_construtor;
	public final Constructor<?> gui_construtor;

	private ModGuiTypes(
			final Class<? extends Container> c )
	{
		try
		{
			container = c;
			container_construtor = container.getConstructor( EntityPlayer.class, World.class, int.class, int.class, int.class );
		}
		catch ( final Exception e )
		{
			throw new RuntimeException( e );
		}

		// by default...
		Class<?> g = null;
		Constructor<?> g_construtor = null;

		// attempt to get gui class/constructor...
		try
		{
			g = (Class<?>) container.getMethod( "getGuiClass" ).invoke( null );
			g_construtor = g.getConstructor( EntityPlayer.class, World.class, int.class, int.class, int.class );
		}
		catch ( final Exception e )
		{
			// Only throw error if this is a client...
			if ( FMLCommonHandler.instance().getSide() == Side.CLIENT )
			{
				throw new RuntimeException( e );
			}

		}

		gui = g;
		gui_construtor = g_construtor;

	}
}
