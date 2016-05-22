package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.client.gui.ModGuiTypes;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.network.ModPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;

public class PacketOpenBagGui extends ModPacket
{
	@Override
	public void server(
			final EntityPlayerMP player )
	{
		player.openGui( ChiselsAndBits.getInstance(), ModGuiTypes.BitBag.ordinal(), player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ );
	}

	@Override
	public void getPayload(
			final PacketBuffer buffer )
	{
		// no data...
	}

	@Override
	public void readPayload(
			final PacketBuffer buffer )
	{
		// no data..
	}

}
