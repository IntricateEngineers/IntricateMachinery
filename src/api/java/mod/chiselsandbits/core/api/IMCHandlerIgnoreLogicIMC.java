package mod.chiselsandbits.core.api;

import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.core.Log;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

public class IMCHandlerIgnoreLogicIMC implements IMCMessageHandler
{

	@Override
	public void excuteIMC(
			final IMCMessage message )
	{
		try
		{
			final String name = message.getStringValue();

			Block blk = Block.REGISTRY.getObject( new ResourceLocation( name ) );

			// try finding the block in the mod instead...
			if ( blk == null )
			{
				blk = Block.REGISTRY.getObject( new ResourceLocation( message.getSender(), name ) );
			}

			if ( blk != null )
			{
				BlockBitInfo.ignoreBlockLogic( blk );
			}
			else
			{
				throw new RuntimeException( "Unable to locate block " + message.getSender() + ":" + message.getStringValue() );
			}
		}
		catch ( final Throwable e )
		{
			Log.logError( "IMC ignoreblocklogic From " + message.getSender(), e );
		}
	}
}
