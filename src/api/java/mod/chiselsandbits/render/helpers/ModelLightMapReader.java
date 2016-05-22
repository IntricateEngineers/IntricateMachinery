package mod.chiselsandbits.render.helpers;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;

public class ModelLightMapReader extends BaseModelReader
{
	public int lv = 0;

	public ModelLightMapReader(
			final int lightValue )
	{
		lv = lightValue;
	}

	@Override
	public VertexFormat getVertexFormat()
	{
		return DefaultVertexFormats.BLOCK;
	}

	@Override
	public void put(
			final int element,
			final float... data )
	{
		final VertexFormatElement e = getVertexFormat().getElement( element );
		final float maxLightmap = 32.0f / 0xffff;

		if ( e.getUsage() == EnumUsage.UV && e.getIndex() == 1 && data.length > 1 )
		{
			final int lvFromData_sky = (int) ( data[0] / maxLightmap );
			final int lvFromData_block = (int) ( data[1] / maxLightmap );

			lv = Math.max( lvFromData_sky, lv );
			lv = Math.max( lvFromData_block, lv );
		}
	}

}