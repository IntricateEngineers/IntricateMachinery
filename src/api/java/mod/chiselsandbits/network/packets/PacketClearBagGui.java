package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.bitbag.BagContainer;
import mod.chiselsandbits.network.ModPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;

public class PacketClearBagGui extends ModPacket
{
	@Override
	public void server(
			final EntityPlayerMP player )
	{
		if ( player.openContainer instanceof BagContainer )
		{
			( (BagContainer) player.openContainer ).clear();
		}
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
