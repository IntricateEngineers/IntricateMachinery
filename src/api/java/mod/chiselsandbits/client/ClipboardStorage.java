package mod.chiselsandbits.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.Unpooled;
import mod.chiselsandbits.core.ChiselsAndBits;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ClipboardStorage extends Configuration
{

	public ClipboardStorage(
			final File file )
	{
		super( file );
	}

	public void write(
			final List<ItemStack> myitems )
	{
		if ( !ChiselsAndBits.getConfig().persistCreativeClipboard )
		{
			return;
		}

		for ( final String name : getCategoryNames() )
		{
			removeCategory( getCategory( name ) );
		}

		int idx = 0;
		for ( final ItemStack i : myitems )
		{
			if ( i.hasTagCompound() )
			{
				final NBTTagCompound nbt = i.getTagCompound();
				final PacketBuffer b = new PacketBuffer( Unpooled.buffer() );

				b.writeString( Item.REGISTRY.getNameForObject( i.getItem() ).toString() );
				b.writeNBTTagCompoundToBuffer( nbt );

				final int[] o = new int[b.writerIndex()];
				for ( int x = 0; x < b.writerIndex(); x++ )
				{
					o[x] = b.getByte( x );
				}

				get( "clipboard", "" + idx++, o ).set( o );
			}
		}

		save();
	}

	public List<ItemStack> read()
	{
		final List<ItemStack> myItems = new ArrayList<ItemStack>();

		if ( !ChiselsAndBits.getConfig().persistCreativeClipboard )
		{
			return myItems;
		}

		for ( final Property p : getCategory( "clipboard" ).values() )
		{
			final int[] bytes = p.getIntList();
			final byte[] o = new byte[bytes.length];

			for ( int x = 0; x < bytes.length; x++ )
			{
				o[x] = (byte) bytes[x];
			}

			try
			{
				final PacketBuffer b = new PacketBuffer( Unpooled.wrappedBuffer( o ) );

				final String item = b.readStringFromBuffer( 127 );
				final NBTTagCompound c = b.readNBTTagCompoundFromBuffer();

				final ItemStack stack = new ItemStack( Item.getByNameOrId( item ) );
				stack.setTagCompound( c );

				myItems.add( stack );
			}
			catch ( final IOException e )
			{
				// :_ (
			}

		}

		return myItems;
	}
}
