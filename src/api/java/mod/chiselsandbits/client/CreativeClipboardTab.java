package mod.chiselsandbits.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.ItemType;
import mod.chiselsandbits.chiseledblock.ItemBlockChiseled;
import mod.chiselsandbits.chiseledblock.NBTBlobConverter;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.interfaces.ICacheClearable;
import mod.chiselsandbits.registry.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CreativeClipboardTab extends CreativeTabs implements ICacheClearable
{
	static boolean renewMappings = true;
	static private List<ItemStack> myWorldItems = new ArrayList<ItemStack>();
	static private List<ItemStack> myCrossItems = new ArrayList<ItemStack>();
	static private ClipboardStorage clipStorage = null;

	public static void load(
			final File file )
	{
		clipStorage = new ClipboardStorage( file );
		myCrossItems = clipStorage.read();
	}

	static public void addItem(
			final ItemStack iss )
	{
		// this is a client side things.
		if ( FMLCommonHandler.instance().getEffectiveSide().isClient() )
		{
			final IBitAccess bitData = ChiselsAndBits.getApi().createBitItem( iss );

			if ( bitData == null )
			{
				return;
			}

			final ItemStack is = bitData.getBitsAsItem( null, ItemType.CHISLED_BLOCK, true );

			if ( is == null )
			{
				return;
			}

			// remove duplicates if they exist...
			for ( final ItemStack isa : myCrossItems )
			{
				if ( ItemStack.areItemStackTagsEqual( is, isa ) )
				{
					myCrossItems.remove( isa );
					break;
				}
			}

			// add item to front...
			myCrossItems.add( 0, is );

			// remove extra items from back..
			while ( myCrossItems.size() > ChiselsAndBits.getConfig().creativeClipboardSize && !myCrossItems.isEmpty() )
			{
				myCrossItems.remove( myCrossItems.size() - 1 );
			}

			clipStorage.write( myCrossItems );
			myWorldItems.clear();
			renewMappings = true;
		}
	}

	public CreativeClipboardTab()
	{
		super( ChiselsAndBits.MODID + ".Clipboard" );
		ChiselsAndBits.getInstance().addClearable( this );
	}

	@Override
	public Item getTabIconItem()
	{
		final ModItems cbitems = ChiselsAndBits.getItems();
		return ModUtil.firstNonNull(
				cbitems.itemPositiveprint,
				cbitems.itemNegativeprint,
				cbitems.itemBitBag,
				cbitems.itemChiselDiamond,
				cbitems.itemChiselGold,
				cbitems.itemChiselIron,
				cbitems.itemChiselStone,
				cbitems.itemWrench );
	}

	@Override
	public void displayAllRelevantItems(
			final List<ItemStack> itemList )
	{
		if ( renewMappings )
		{
			myWorldItems.clear();
			renewMappings = false;

			for ( final ItemStack is : myCrossItems )
			{
				final NBTBlobConverter c = new NBTBlobConverter();
				c.readChisleData( is.getSubCompound( ItemBlockChiseled.NBT_CHISELED_DATA, true ) );

				// recalculate.
				c.updateFromBlob();

				final ItemStack worldItem = c.getItemStack( false );

				if ( worldItem != null )
				{
					myWorldItems.add( worldItem );
				}
			}
		}

		itemList.addAll( myWorldItems );
	}

	@Override
	public void clearCache()
	{
		renewMappings = true;
	}

}
