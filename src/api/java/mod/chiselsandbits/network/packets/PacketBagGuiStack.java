package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.bitbag.BagContainer;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.items.ItemChiseledBit;
import mod.chiselsandbits.network.ModPacket;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class PacketBagGuiStack extends ModPacket
{
	public int index = -1;
	public ItemStack is;

	@Override
	public void client()
	{
		final Container cc = ClientSide.instance.getPlayer().openContainer;
		if ( cc instanceof BagContainer )
		{
			( (BagContainer) cc ).customSlots.get( index ).putStack( is );
		}
	}

	@Override
	public void getPayload(
			final PacketBuffer buffer )
	{
		buffer.writeVarIntToBuffer( index );

		if ( is == null )
		{
			buffer.writeVarIntToBuffer( 0 );
		}
		else
		{
			buffer.writeVarIntToBuffer( is.stackSize );
			buffer.writeVarIntToBuffer( ItemChiseledBit.getStackState( is ) );
		}
	}

	@Override
	public void readPayload(
			final PacketBuffer buffer )
	{
		index = buffer.readVarIntFromBuffer();

		final int size = buffer.readVarIntFromBuffer();

		if ( size <= 0 )
		{
			is = null;
		}
		else
		{
			is = ItemChiseledBit.createStack( buffer.readVarIntFromBuffer(), size, false );
		}
	}

}
