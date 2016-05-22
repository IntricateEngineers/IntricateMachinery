package mod.chiselsandbits.render.chiseledblock;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;

public class UnpackedQuadBuilderWrapper implements IFaceBuilder
{

	UnpackedBakedQuad.Builder builder;

	@Override
	public void begin(
			final VertexFormat format )
	{
		builder = new UnpackedBakedQuad.Builder( format );
	}

	@Override
	public BakedQuad create(
			final TextureAtlasSprite sprite )
	{
		builder.setTexture( sprite );
		return builder.build();
	}

	@Override
	public void setFace(
			final EnumFacing myFace,
			final int tintIndex )
	{
		builder.setQuadOrientation( myFace );
		builder.setQuadTint( tintIndex );
	}

	@Override
	public void put(
			final int element,
			final float... args )
	{
		builder.put( element, args );
	}

}
