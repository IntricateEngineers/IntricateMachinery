package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.bitbag.BagContainer;
import mod.chiselsandbits.network.ModPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.PacketBuffer;

public class PacketBagGui extends ModPacket
{
	public int slotNumber = -1;
	public int mouseButton = -1;
	public boolean duplicateButton = false;
	public boolean holdingShift = false;

	@Override
	public void server(
			final EntityPlayerMP player )
	{
		doAction( player );
	}

	public void doAction(
			final EntityPlayer player )
	{
		final Container c = player.openContainer;
		if ( c instanceof BagContainer )
		{
			final BagContainer bc = (BagContainer) c;
			bc.handleCustomSlotAction( slotNumber, mouseButton, duplicateButton, holdingShift );
		}
	}

	@Override
	public void getPayload(
			final PacketBuffer buffer )
	{
		buffer.writeInt( slotNumber );
		buffer.writeInt( mouseButton );
		buffer.writeBoolean( duplicateButton );
		buffer.writeBoolean( holdingShift );
	}

	@Override
	public void readPayload(
			final PacketBuffer buffer )
	{
		slotNumber = buffer.readInt();
		mouseButton = buffer.readInt();
		duplicateButton = buffer.readBoolean();
		holdingShift = buffer.readBoolean();
	}

}
