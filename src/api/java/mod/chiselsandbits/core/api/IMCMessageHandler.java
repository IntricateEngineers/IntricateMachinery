package mod.chiselsandbits.core.api;

import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

public interface IMCMessageHandler
{

	public void excuteIMC(
			IMCMessage message );

}
