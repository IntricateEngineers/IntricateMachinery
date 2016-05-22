package mod.chiselsandbits.core;

import mod.chiselsandbits.helpers.LocalStrings;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;

public enum ChiselMode
{
	SINGLE( LocalStrings.ChiselModeSingle ),
	SNAP2( LocalStrings.ChiselModeSnap2 ),
	SNAP4( LocalStrings.ChiselModeSnap4 ),
	SNAP8( LocalStrings.ChiselModeSnap8 ),
	LINE( LocalStrings.ChiselModeLine ),
	PLANE( LocalStrings.ChiselModePlane ),
	CONNECTED_PLANE( LocalStrings.ChiselModeConnectedPlane ),
	CUBE_SMALL( LocalStrings.ChiselModeCubeSmall ),
	CUBE_MEDIUM( LocalStrings.ChiselModeCubeMedium ),
	CUBE_LARGE( LocalStrings.ChiselModeCubeLarge ),
	DRAWN_REGION( LocalStrings.ChiselModeDrawnRegion );

	public final LocalStrings string;

	public boolean isDisabled = false;

	public Object binding;

	private ChiselMode(
			final LocalStrings str )
	{
		string = str;
	}

	public static ChiselMode getMode(
			final ItemStack stack )
	{
		if ( stack != null )
		{
			try
			{
				final NBTTagCompound nbt = stack.getTagCompound();
				if ( nbt != null && nbt.hasKey( "mode" ) )
				{
					return valueOf( nbt.getString( "mode" ) );
				}
			}
			catch ( final Exception e )
			{
				Log.logError( "Unable to determine mode.", e );
			}
		}

		return SINGLE;
	}

	public void setMode(
			final ItemStack stack )
	{
		if ( stack != null )
		{
			stack.setTagInfo( "mode", new NBTTagString( name() ) );
		}
	}

	public static ChiselMode getMode(
			final int offset )
	{
		return values()[offset % values().length];
	}

}
