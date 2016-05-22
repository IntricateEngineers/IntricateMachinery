package mod.chiselsandbits.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ActingPlayer
{
	private final IInventory storage;

	// used to test permission and stuff...
	private final EntityPlayer innerPlayer;
	private final boolean realPlayer; // are we a real player?
	private final EnumHand hand;

	private ActingPlayer(
			final EntityPlayer player,
			final boolean realPlayer,
			final EnumHand hand )
	{
		innerPlayer = player;
		this.hand = hand;
		this.realPlayer = realPlayer;
		storage = realPlayer ? player.inventory : new PlayerCopiedInventory( player.inventory );
	}

	public IInventory getInventory()
	{
		return storage;
	}

	public int getCurrentItem()
	{
		return innerPlayer.inventory.currentItem;
	}

	public boolean isCreative()
	{
		return innerPlayer.capabilities.isCreativeMode;
	}

	public ItemStack getCurrentEquippedItem()
	{
		return storage.getStackInSlot( getCurrentItem() );
	}

	public boolean canPlayerManipulate(
			final BlockPos pos,
			final EnumFacing side,
			final ItemStack is )
	{
		return innerPlayer.canPlayerEdit( pos, side, is ) && innerPlayer.worldObj.isBlockModifiable( innerPlayer, pos );
	}

	public void damageItem(
			final ItemStack stack,
			final int amount )
	{
		if ( realPlayer )
		{
			stack.damageItem( amount, innerPlayer );
		}
		else
		{
			stack.setItemDamage( stack.getItemDamage() + amount );
		}
	}

	public void playerDestroyItem(
			final ItemStack stack,
			final EnumHand hand )
	{
		if ( realPlayer )
		{
			net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem( innerPlayer, stack, hand );
		}
	}

	public static ActingPlayer actingAs(
			final EntityPlayer player,
			final EnumHand hand )
	{
		return new ActingPlayer( player, true, hand );
	}

	public static ActingPlayer testingAs(
			final EntityPlayer player,
			final EnumHand hand )
	{
		return new ActingPlayer( player, false, hand );
	}

	public World getWorld()
	{
		return innerPlayer.worldObj;
	}

	/**
	 * only call this is you require a player, and only as a last resort.
	 */
	public EntityPlayer getPlayer()
	{
		return innerPlayer;
	}

	public boolean isReal()
	{
		return realPlayer;
	}

	/**
	 * @return the hand
	 */
	public EnumHand getHand()
	{
		return hand;
	}

}
