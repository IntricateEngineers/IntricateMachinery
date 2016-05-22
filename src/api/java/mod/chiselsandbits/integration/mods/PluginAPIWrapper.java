package mod.chiselsandbits.integration.mods;

import mod.chiselsandbits.api.IChiselsAndBitsAddon;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.integration.IntegrationBase;

public class PluginAPIWrapper extends IntegrationBase
{
	final IChiselsAndBitsAddon addon;

	public PluginAPIWrapper(
			final IChiselsAndBitsAddon addon )
	{
		this.addon = addon;
	}

	@Override
	public void init()
	{
		addon.onReadyChiselsAndBits( ChiselsAndBits.getApi() );
	}

}
