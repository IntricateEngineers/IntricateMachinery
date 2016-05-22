package mod.chiselsandbits.render.helpers;

import java.util.Arrays;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;

public class ModelUVReader extends BaseModelReader
{

	final float minU;
	final float maxUMinusMin;

	final float minV;
	final float maxVMinusMin;

	public final float[] quadUVs = new float[] { 0, 0, 0, 1, 1, 0, 1, 1 };

	int uCoord, vCoord;

	public ModelUVReader(
			final TextureAtlasSprite texture,
			final int uFaceCoord,
			final int vFaceCoord )
	{
		minU = texture.getMinU();
		maxUMinusMin = texture.getMaxU() - minU;

		minV = texture.getMinV();
		maxVMinusMin = texture.getMaxV() - minV;

		uCoord = uFaceCoord;
		vCoord = vFaceCoord;
	}

	private float pos[];
	private float uv[];
	public int corners;

	@Override
	public void put(
			final int element,
			final float... data )
	{
		final VertexFormat format = getVertexFormat();
		final VertexFormatElement ele = format.getElement( element );

		if ( ele.getUsage() == EnumUsage.UV && ele.getIndex() != 1 )
		{
			uv = Arrays.copyOf( data, data.length );
		}

		else if ( ele.getUsage() == EnumUsage.POSITION )
		{
			pos = Arrays.copyOf( data, data.length );
		}

		if ( element == format.getElementCount() - 1 )
		{
			if ( ModelUtil.isZero( pos[uCoord] ) && ModelUtil.isZero( pos[vCoord] ) )
			{
				corners = corners | 0x1;
				quadUVs[0] = ( uv[0] - minU ) / maxUMinusMin;
				quadUVs[1] = ( uv[1] - minV ) / maxVMinusMin;
			}
			else if ( ModelUtil.isZero( pos[uCoord] ) && ModelUtil.isOne( pos[vCoord] ) )
			{
				corners = corners | 0x2;
				quadUVs[4] = ( uv[0] - minU ) / maxUMinusMin;
				quadUVs[5] = ( uv[1] - minV ) / maxVMinusMin;
			}
			else if ( ModelUtil.isOne( pos[uCoord] ) && ModelUtil.isZero( pos[vCoord] ) )
			{
				corners = corners | 0x4;
				quadUVs[2] = ( uv[0] - minU ) / maxUMinusMin;
				quadUVs[3] = ( uv[1] - minV ) / maxVMinusMin;
			}
			else if ( ModelUtil.isOne( pos[uCoord] ) && ModelUtil.isOne( pos[vCoord] ) )
			{
				corners = corners | 0x8;
				quadUVs[6] = ( uv[0] - minU ) / maxUMinusMin;
				quadUVs[7] = ( uv[1] - minV ) / maxVMinusMin;
			}
		}
	}
}