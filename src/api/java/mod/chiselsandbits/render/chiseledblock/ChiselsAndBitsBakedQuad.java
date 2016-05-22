package mod.chiselsandbits.render.chiseledblock;

import mod.chiselsandbits.render.cache.InMemoryQuadCompressor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class ChiselsAndBitsBakedQuad extends BakedQuad
{

	private static InMemoryQuadCompressor inMemoryCompressor = new InMemoryQuadCompressor();

	public static final VertexFormat VERTEX_FORMAT = new VertexFormat();

	static
	{
		for ( final VertexFormatElement element : DefaultVertexFormats.ITEM.getElements() )
		{
			VERTEX_FORMAT.addElement( element );
		}

		// add lightmap ;)
		VERTEX_FORMAT.addElement( DefaultVertexFormats.TEX_2S );
	}

	protected final float[][][] rawVertData;

	@Override
	public void pipe(
			final IVertexConsumer consumer )
	{
		final int[] eMap = LightUtil.mapFormats( consumer.getVertexFormat(), format );

		consumer.setQuadTint( getTintIndex() );
		consumer.setQuadOrientation( getFace() );
		consumer.setApplyDiffuseLighting( true );

		for ( int v = 0; v < 4; v++ )
		{
			for ( int e = 0; e < consumer.getVertexFormat().getElementCount(); e++ )
			{
				if ( eMap[e] != format.getElementCount() )
				{
					consumer.put( e, rawVertData[v][eMap[e]] );
				}
				else
				{
					consumer.put( e );
				}
			}
		}
	}

	@Override
	public int[] getVertexData() // anyone asking this will expect ITEM.
	{
		final int[] tmpData = new int[format.getNextOffset() /* / 4 * 4 */];

		for ( int v = 0; v < 4; v++ )
		{
			for ( int e = 0; e < format.getElementCount(); e++ )
			{
				LightUtil.pack( rawVertData[v][e], tmpData, format, v, e );
			}
		}

		return tmpData;
	}

	static int[] OPTIFINE_WORKAROUND = new int[1];

	public ChiselsAndBitsBakedQuad(
			final float[][][] unpackedData,
			final int tint,
			final EnumFacing orientation,
			final TextureAtlasSprite sprite )
	{
		super( OPTIFINE_WORKAROUND, tint, orientation, sprite, true, VERTEX_FORMAT );
		rawVertData = inMemoryCompressor.compress( unpackedData );
	}

	public static class Colored extends ChiselsAndBitsBakedQuad
	{
		public Colored(
				final float[][][] unpackedData,
				final int tint,
				final EnumFacing orientation,
				final TextureAtlasSprite sprite )
		{
			super( unpackedData, tint, orientation, sprite );
		}
	}

	public static class Builder implements IVertexConsumer, IFaceBuilder
	{
		private float[][][] unpackedData;
		private int tint = -1;
		private EnumFacing orientation;
		private final boolean isColored = false;

		private int vertices = 0;
		private int elements = 0;

		@Override
		public VertexFormat getVertexFormat()
		{
			return VERTEX_FORMAT;
		}

		@Override
		public void setQuadTint(
				final int tint )
		{
			this.tint = tint;
		}

		@Override
		public void setQuadOrientation(
				final EnumFacing orientation )
		{
			this.orientation = orientation;
		}

		@Override
		public void put(
				final int element,
				final float... data )
		{
			for ( int i = 0; i < 4; i++ )
			{
				if ( i < data.length )
				{
					unpackedData[vertices][element][i] = data[i];
				}
				else
				{
					unpackedData[vertices][element][i] = 0;
				}
			}

			elements++;

			if ( elements == getVertexFormat().getElementCount() )
			{
				vertices++;
				elements = 0;
			}
		}

		@Override
		public void begin(
				final VertexFormat format )
		{
			if ( format != getVertexFormat() )
			{
				throw new RuntimeException( "Bad format, can only be CNB." );
			}

			unpackedData = new float[4][getVertexFormat().getElementCount()][4];
			tint = -1;
			orientation = null;

			vertices = 0;
			elements = 0;
		}

		@Override
		public BakedQuad create(
				final TextureAtlasSprite sprite )
		{
			if ( isColored )
			{
				return new Colored( unpackedData, tint, orientation, sprite );
			}

			return new ChiselsAndBitsBakedQuad( unpackedData, tint, orientation, sprite );
		}

		@Override
		public void setFace(
				final EnumFacing myFace,
				final int tintIndex )
		{
			setQuadOrientation( myFace );
			setQuadTint( tintIndex );
		}

		@Override
		public void setApplyDiffuseLighting(
				final boolean diffuse )
		{
		}
	}
}
