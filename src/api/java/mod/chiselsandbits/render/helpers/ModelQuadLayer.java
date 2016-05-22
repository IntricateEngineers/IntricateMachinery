package mod.chiselsandbits.render.helpers;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class ModelQuadLayer
{

	public float[] uvs;
	public TextureAtlasSprite sprite;
	public int light;
	public int color;
	public int tint;

	public static class ModelQuadLayerBuilder
	{
		public final ModelQuadLayer cache = new ModelQuadLayer();
		public final ModelLightMapReader lv = new ModelLightMapReader( 0 );
		public ModelUVReader uvr;

		public ModelQuadLayerBuilder(
				final TextureAtlasSprite sprite,
				final int uCoord,
				final int vCoord )
		{
			cache.sprite = sprite;
			uvr = new ModelUVReader( sprite, uCoord, vCoord );
		}

		public ModelQuadLayer build(
				final int stateid,
				final int color,
				final int lightValue,
				final boolean isGrass )
		{
			cache.light = Math.max( lightValue, lv.lv );
			cache.uvs = uvr.quadUVs;
			cache.color = cache.tint != -1 ? color : 0xffffffff;

			if ( isGrass )
			{
				cache.color = 0xffffffff;
				cache.tint = cache.tint == -1 ? -1 : stateid;
			}
			else
			{
				// remove tints, I'm only support grass for the time being.
				cache.tint = -1;
			}

			return cache;
		}
	};

}
