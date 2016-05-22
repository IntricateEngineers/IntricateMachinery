package mod.chiselsandbits.render.patterns;

import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.interfaces.IPatternItem;
import mod.chiselsandbits.render.BaseBakedItemModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class PrintBaked extends BaseBakedItemModel
{

	final String itemName;

	public PrintBaked(
			final String itname,
			final IPatternItem item,
			final ItemStack stack )
	{
		itemName = itname;

		final ItemStack blockItem = item.getPatternedItem( stack );
		IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel( blockItem );

		model = model.getOverrides().handleItemState( model, blockItem, null, null );

		for ( final EnumFacing face : EnumFacing.VALUES )
		{
			list.addAll( model.getQuads( null, face, 0 ) );
		}

		list.addAll( model.getQuads( null, null, 0 ) );
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite( ChiselsAndBits.MODID + ":item/" + itemName );
	}
}
