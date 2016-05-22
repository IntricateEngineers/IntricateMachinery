package mod.chiselsandbits.events;

import mod.chiselsandbits.items.ItemChisel;
import mod.chiselsandbits.items.ItemChiseledBit;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Disable breaking blocks when using a chisel / bit, some items break too fast
 * for the other code to prevent which is where this comes in.
 *
 * This manages survival chisel actions, creative some how skips this and calls
 * onBlockStartBreak on its own, but when in creative this is called on the
 * server... which still needs to be canceled or it will break the block.
 *
 * The whole things, is very strange.
 */
public class EventPlayerInteract
{

	@SubscribeEvent
	public void interaction(
			final LeftClickBlock event )
	{
		if ( event.getEntityPlayer() != null )
		{
			final ItemStack is = event.getItemStack();
			if ( is != null && ( is.getItem() instanceof ItemChisel || is.getItem() instanceof ItemChiseledBit ) )
			{
				if ( event.getWorld().isRemote )
				{
					// this is called when the player is survival - client side.
					is.getItem().onBlockStartBreak( is, event.getPos(), event.getEntityPlayer() );
				}

				// cancel all interactions, creative is magic.
				event.setCanceled( true );
			}
		}
	}
}
