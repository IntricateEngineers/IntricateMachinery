package mod.chiselsandbits.render.helpers;

import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;

public class ModelUVAverager extends BaseModelReader
{
	private int vertCount = 0;
	private float sumU;
	private float sumV;

	public float getU()
	{
		return sumU / vertCount;
	}

	public float getV()
	{
		return sumV / vertCount;
	}

	@Override
	public void put(
			final int element,
			final float... data )
	{
		final VertexFormatElement e = getVertexFormat().getElement( element );
		if ( e.getUsage() == EnumUsage.UV && e.getIndex() != 1 )
		{
			sumU += data[0];
			sumV += data[1];
			++vertCount;
		}
	}

}