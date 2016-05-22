package mod.chiselsandbits.core.api;

import java.util.HashMap;
import java.util.Map;

import mod.chiselsandbits.core.Log;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

public class IMCHandler
{

	private final Map<String, IMCMessageHandler> processors = new HashMap<String, IMCMessageHandler>();

	public IMCHandler()
	{
		processors.put( "ignoreblocklogic", new IMCHandlerIgnoreLogicIMC() );
	}

	public void handleIMCEvent(
			final IMCEvent event )
	{
		for ( final FMLInterModComms.IMCMessage message : event.getMessages() )
		{
			executeIMC( message );
		}
	}

	private void executeIMC(
			final IMCMessage message )
	{
		final IMCMessageHandler handler = processors.get( message.key );

		if ( handler != null )
		{
			handler.excuteIMC( message );
		}
		else
		{
			Log.logError( "Invalid IMC: " + message.key + " from " + message.getSender(), new RuntimeException( "Invalid IMC Type." ) );
		}
	}

}
