package mod.chiselsandbits.render.chiseledblock.tesr;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.BlockRenderLayer;

class UploadTracker
{
	final TileRenderCache trc;
	final BlockRenderLayer layer;
	private Tessellator src;

	public Tessellator getTessellator()
	{
		if ( src == null )
		{
			throw new NullPointerException();
		}
		return src;
	}

	public UploadTracker(
			final TileRenderCache t,
			final BlockRenderLayer l,
			final Tessellator tess )
	{
		trc = t;
		layer = l;
		src = tess;
	}

	public void submitForReuse()
	{
		ChisledBlockBackgroundRender.submitTessellator( src );
		src = null;
	}
}