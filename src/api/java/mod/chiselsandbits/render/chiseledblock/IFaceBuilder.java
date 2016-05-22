package mod.chiselsandbits.render.chiseledblock;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

public interface IFaceBuilder
{

	void setFace(
			EnumFacing myFace,
			int tintIndex );

	void put(
			int element,
			float... args );

	void begin(
			VertexFormat format );

	BakedQuad create(
			TextureAtlasSprite sprite );

}
