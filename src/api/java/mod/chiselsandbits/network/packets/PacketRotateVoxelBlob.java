package mod.chiselsandbits.network.packets;

import mod.chiselsandbits.interfaces.IVoxelBlobItem;
import mod.chiselsandbits.network.ModPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class PacketRotateVoxelBlob extends ModPacket
{

	public int rotationDirection;

	@Override
	public void server(
			final EntityPlayerMP player )
	{
		final ItemStack is = player.getHeldItemMainhand();
		if ( is != null && is.getItem() instanceof IVoxelBlobItem )
		{
			( (IVoxelBlobItem) is.getItem() ).rotate( is, rotationDirection );
		}
	}

	@Override
	public void getPayload(
			final PacketBuffer buffer )
	{
		buffer.writeInt( rotationDirection );
	}

	@Override
	public void readPayload(
			final PacketBuffer buffer )
	{
		rotationDirection = buffer.readInt();
	}

}
