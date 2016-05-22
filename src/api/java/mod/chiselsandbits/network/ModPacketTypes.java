package mod.chiselsandbits.network;

import java.util.HashMap;

import mod.chiselsandbits.network.packets.PacketBagGui;
import mod.chiselsandbits.network.packets.PacketOpenBagGui;
import mod.chiselsandbits.network.packets.PacketBagGuiStack;
import mod.chiselsandbits.network.packets.PacketChisel;
import mod.chiselsandbits.network.packets.PacketClearBagGui;
import mod.chiselsandbits.network.packets.PacketRotateVoxelBlob;
import mod.chiselsandbits.network.packets.PacketSetChiselMode;
import mod.chiselsandbits.network.packets.PacketUndo;

public enum ModPacketTypes
{
	CHISEL( PacketChisel.class ),
	OPEN_BAG_GUI( PacketOpenBagGui.class ),
	SET_CHISEL_MODE( PacketSetChiselMode.class ),
	ROTATE_VOXEL_BLOB( PacketRotateVoxelBlob.class ),
	BAG_GUI( PacketBagGui.class ),
	BAG_GUI_STACK( PacketBagGuiStack.class ),
	UNDO( PacketUndo.class ),
	CLEAR_BAG( PacketClearBagGui.class );

	private final Class<? extends ModPacket> packetClass;

	ModPacketTypes(
			final Class<? extends ModPacket> clz )
	{
		packetClass = clz;
	}

	private static HashMap<Class<? extends ModPacket>, Integer> fromClassToId = new HashMap<Class<? extends ModPacket>, Integer>();
	private static HashMap<Integer, Class<? extends ModPacket>> fromIdToClass = new HashMap<Integer, Class<? extends ModPacket>>();

	public static void init()
	{
		for ( final ModPacketTypes p : ModPacketTypes.values() )
		{
			fromClassToId.put( p.packetClass, p.ordinal() );
			fromIdToClass.put( p.ordinal(), p.packetClass );
		}
	}

	public static int getID(
			final Class<? extends ModPacket> clz )
	{
		return fromClassToId.get( clz );
	}

	public static ModPacket constructByID(
			final int id ) throws InstantiationException, IllegalAccessException
	{
		return fromIdToClass.get( id ).newInstance();
	}

}
