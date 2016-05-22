package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.core.ChiselMode;
import mod.chiselsandbits.interfaces.IChiselModeItem;
import mod.chiselsandbits.network.ModPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextComponentTranslation;

public class PacketSetChiselMode extends ModPacket
{

	public ChiselMode mode = ChiselMode.SINGLE;
	public boolean chatNotification = false;

	@Override
	public void server(
			final EntityPlayerMP player )
	{
		final ItemStack ei = player.getHeldItemMainhand();
		if ( ei != null && ei.getItem() instanceof IChiselModeItem )
		{
			final ChiselMode originalMode = ChiselMode.getMode( ei );
			mode.setMode( ei );

			if ( originalMode != mode && chatNotification )
			{
				Minecraft.getMinecraft().thePlayer.addChatComponentMessage( new TextComponentTranslation( mode.string.toString() ) );
			}
		}
	}

	@Override
	public void getPayload(
			final PacketBuffer buffer )
	{
		buffer.writeBoolean( chatNotification );
		buffer.writeInt( mode.ordinal() );
	}

	@Override
	public void readPayload(
			final PacketBuffer buffer )
	{
		chatNotification = buffer.readBoolean();
		mode = ChiselMode.getMode( buffer.readInt() );
	}

}
