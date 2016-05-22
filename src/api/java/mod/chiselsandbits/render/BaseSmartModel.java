package mod.chiselsandbits.render;

import java.util.ArrayList;
import java.util.List;

import mod.chiselsandbits.core.ClientSide;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public abstract class BaseSmartModel implements IBakedModel
{

	private final ItemOverrideList overrides;

	private static class OverrideHelper extends ItemOverrideList
	{
		final BaseSmartModel parent;

		public OverrideHelper(
				final BaseSmartModel p )
		{
			super( new ArrayList<ItemOverride>() );
			parent = p;
		}

		@Override
		public IBakedModel handleItemState(
				final IBakedModel originalModel,
				final ItemStack stack,
				final World world,
				final EntityLivingBase entity )
		{
			return parent.handleItemState( originalModel, stack, world, entity );
		}

	};

	public BaseSmartModel()
	{
		overrides = new OverrideHelper( this );
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return true;
	}

	@Override
	public boolean isGui3d()
	{
		return true;
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		final TextureAtlasSprite sprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture( Blocks.STONE.getDefaultState() );

		if ( sprite == null )
		{
			return ClientSide.instance.getMissingIcon();
		}

		return sprite;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms()
	{
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public List<BakedQuad> getQuads(
			final IBlockState state,
			final EnumFacing side,
			final long rand )
	{
		final IBakedModel model = handleBlockState( state, rand );
		return model.getQuads( state, side, rand );
	}

	public IBakedModel handleBlockState(
			final IBlockState state,
			final long rand )
	{
		return NullBakedModel.instance;
	}

	@Override
	public ItemOverrideList getOverrides()
	{
		return overrides;
	}

	public IBakedModel handleItemState(
			final IBakedModel originalModel,
			final ItemStack stack,
			final World world,
			final EntityLivingBase entity )
	{
		return originalModel;
	}

}
