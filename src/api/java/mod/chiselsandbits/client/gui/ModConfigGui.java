package mod.chiselsandbits.client.gui;

import java.util.ArrayList;
import java.util.List;

import mod.chiselsandbits.config.ModConfig;
import mod.chiselsandbits.core.ChiselsAndBits;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ModConfigGui extends GuiConfig
{

	public ModConfigGui(
			final GuiScreen parent )
	{
		super( parent, getConfigElements(), ChiselsAndBits.MODID, false, false, GuiConfig.getAbridgedConfigPath( ChiselsAndBits.getConfig().getFilePath() ) );
	}

	private static List<IConfigElement> getConfigElements()
	{
		final List<IConfigElement> list = new ArrayList<IConfigElement>();

		final ModConfig config = ChiselsAndBits.getConfig();

		for ( final String cat : config.getCategoryNames() )
		{
			final ConfigCategory cc = config.getCategory( cat );

			if ( cc.isChild() )
			{
				continue;
			}

			final ConfigElement ce = new ConfigElement( cc );
			list.add( ce );
		}

		return list;
	}

}
