package mod.chiselsandbits.debug;

import mod.chiselsandbits.api.ChiselsAndBitsAddon;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.IChiselsAndBitsAddon;

@ChiselsAndBitsAddon
public class ApiDebug implements IChiselsAndBitsAddon
{

	@Override
	public void onReadyChiselsAndBits(
			final IChiselAndBitsAPI api )
	{
		DebugAction.api = api;
	}

}
