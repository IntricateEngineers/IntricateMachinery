package mod.chiselsandbits.items;

import java.util.List;

import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.LocalStrings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemBitSaw extends Item
{

	public ItemBitSaw()
	{
		setMaxStackSize( 1 );

		final int uses = ChiselsAndBits.getConfig().diamondSawUses;
		setMaxDamage( ChiselsAndBits.getConfig().damageTools ? (int) Math.max( 0, uses ) : 0 );
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	@Override
	public void addInformation(
			final ItemStack stack,
			final EntityPlayer playerIn,
			final List tooltip,
			final boolean advanced )
	{
		super.addInformation( stack, playerIn, tooltip, advanced );
		ChiselsAndBits.getConfig().helpText( LocalStrings.HelpBitSaw, tooltip );
	}

	@Override
	public ItemStack getContainerItem(
			final ItemStack itemStack )
	{
		if ( ChiselsAndBits.getConfig().damageTools )
		{
			itemStack.setItemDamage( itemStack.getItemDamage() + 1 );
		}

		return itemStack.copy();
	}

	@Override
	public boolean hasContainerItem()
	{
		return true;
	}

}
