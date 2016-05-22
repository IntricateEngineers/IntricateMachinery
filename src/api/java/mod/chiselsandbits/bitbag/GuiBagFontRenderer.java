package mod.chiselsandbits.bitbag;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiBagFontRenderer extends FontRenderer
{
	FontRenderer talkto;

	int offsetX, offsetY;
	double scale;

	public GuiBagFontRenderer(
			final FontRenderer src,
			final int bagStackSize )
	{
		super( Minecraft.getMinecraft().gameSettings, new ResourceLocation( "textures/font/ascii.png" ), Minecraft.getMinecraft().getTextureManager(), false );
		talkto = src;

		if ( bagStackSize < 100 )
		{
			scale = 1.0;
		}
		else if ( bagStackSize >= 100 )
		{
			scale = 0.75;
			offsetX = 3;
			offsetY = 2;
		}
	}

	@Override
	public int getStringWidth(
			String text )
	{
		text = convertText( text );
		return talkto.getStringWidth( text );
	}

	@Override
	public int drawString(
			String text,
			float x,
			float y,
			final int color,
			final boolean dropShadow )
	{
		try
		{
			text = convertText( text );
			GlStateManager.pushMatrix();
			GlStateManager.scale( scale, scale, scale );

			x /= scale;
			y /= scale;
			x += offsetX;
			y += offsetY;

			return talkto.drawString( text, x, y, color, dropShadow );
		}
		finally
		{
			GlStateManager.popMatrix();
		}
	}

	private String convertText(
			final String text )
	{
		try
		{
			final int value = Integer.parseInt( text );

			if ( value >= 1000 )
			{
				return value / 1000 + "k";
			}

			return text;
		}
		catch ( final NumberFormatException e )
		{
			return text;
		}
	}
}
