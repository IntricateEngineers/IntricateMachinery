package mod.chiselsandbits.render.helpers;

import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;

public abstract class BaseModelReader implements IVertexConsumer
{

	@Override
	public VertexFormat getVertexFormat()
	{
		return DefaultVertexFormats.ITEM;
	}

	@Override
	public void setQuadTint(
			final int tint )
	{
	}

	@Override
	public void setQuadOrientation(
			final EnumFacing orientation )
	{
	}

	@Override
	public void setApplyDiffuseLighting(
			final boolean diffuse )
	{

	}

}
