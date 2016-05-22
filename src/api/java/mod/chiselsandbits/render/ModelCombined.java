package mod.chiselsandbits.render;

import java.util.ArrayList;
import java.util.List;

import mod.chiselsandbits.core.ClientSide;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class ModelCombined extends BaseBakedBlockModel
{

	IBakedModel[] merged;

	List<BakedQuad>[] face;
	List<BakedQuad> generic;

	@SuppressWarnings( "unchecked" )
	public ModelCombined(
			final IBakedModel... args )
	{
		face = new ArrayList[EnumFacing.VALUES.length];

		generic = new ArrayList<BakedQuad>();
		for ( final EnumFacing f : EnumFacing.VALUES )
		{
			face[f.ordinal()] = new ArrayList<BakedQuad>();
		}

		merged = args;

		for ( final IBakedModel m : merged )
		{
			generic.addAll( m.getQuads( null, null, 0 ) );
			for ( final EnumFacing f : EnumFacing.VALUES )
			{
				face[f.ordinal()].addAll( m.getQuads( null, f, 0 ) );
			}
		}
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		for ( final IBakedModel a : merged )
		{
			return a.getParticleTexture();
		}

		return ClientSide.instance.getMissingIcon();
	}

	@Override
	public List<BakedQuad> getQuads(
			final IBlockState state,
			final EnumFacing side,
			final long rand )
	{
		if ( side != null )
		{
			return face[side.ordinal()];
		}

		return generic;
	}

}
