package mod.chiselsandbits.core;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ReflectionWrapper
{

	final static public ReflectionWrapper instance = new ReflectionWrapper();

	private Field highlightingItemStack = null;
	private Field mapRegSprites = null;

	private Field findField(
			Class<?> clz,
			final String... methods ) throws Exception
	{
		do
		{
			if ( clz == null || clz == Object.class )
			{
				break;
			}

			for ( final String name : methods )
			{
				try
				{
					final Field f = clz.getDeclaredField( name );
					if ( f != null )
					{
						return f;
					}
				}
				catch ( final Exception e )
				{
					// :__(
				}
			}

			clz = clz.getSuperclass();
		}
		while ( true );

		throw new Exception( "Unable to find field " + methods[0] );
	}

	/**
	 * CLASS: net.minecraft.client.gui.GuiIngame
	 *
	 * SRG: field_92016_l
	 *
	 * NAME: highlightingItemStack
	 */
	@SideOnly( Side.CLIENT )
	public void setHighlightStack(
			final ItemStack is )
	{
		try
		{
			final Object o = Minecraft.getMinecraft().ingameGUI;

			if ( highlightingItemStack == null )
			{
				highlightingItemStack = findField( o.getClass(), "highlightingItemStack", "field_92016_l" );
			}

			highlightingItemStack.setAccessible( true );
			highlightingItemStack.set( o, is );
		}
		catch ( final Throwable t )
		{
			// unable to clear the selected stack.
			notifyDeveloper( t );
		}
	}

	@SideOnly( Side.CLIENT )
	public void clearHighlightedStack()
	{
		setHighlightStack( null );
	}

	@SideOnly( Side.CLIENT )
	public void endHighlightedStack()
	{
		setHighlightStack( Minecraft.getMinecraft().thePlayer.getHeldItemMainhand() );
	}

	/**
	 * CLASS: net.minecraft.client.renderer.texture.TextureMap
	 *
	 * SRG: field_110574_e
	 *
	 * NAME: mapRegisteredSprites
	 */
	@SuppressWarnings( "unchecked" )
	@SideOnly( Side.CLIENT )
	public Map<String, TextureAtlasSprite> getRegSprite(
			final TextureMap map )
	{
		try
		{
			if ( mapRegSprites == null )
			{
				mapRegSprites = findField( map.getClass(), "mapRegisteredSprites", "field_110574_e" );
			}

			mapRegSprites.setAccessible( true );
			return (Map<String, TextureAtlasSprite>) mapRegSprites.get( map );
		}
		catch ( final Throwable t )
		{
			// unable to clear the selected stack.
			notifyDeveloper( t );
		}

		return null;
	}

	private void notifyDeveloper(
			final Throwable t )
	{
		if ( deobfuscatedEnvironment() )
		{
			throw new RuntimeException( t );
		}
	}

	private boolean deobfuscatedEnvironment()
	{
		final Object deObf = Launch.blackboard.get( "fml.deobfuscatedEnvironment" );
		return Boolean.valueOf( String.valueOf( deObf ) );
	}

}
