package mod.chiselsandbits.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mod.chiselsandbits.helpers.ExceptionNoTileEntity;

public class Log
{

	private Log()
	{

	}

	private static Logger getLogger()
	{
		return LogManager.getLogger( ChiselsAndBits.MODID );
	}

	public static void logError(
			final String message,
			final Throwable e )
	{
		getLogger().error( message, e );
	}

	public static void info(
			final String message )
	{
		getLogger().info( message );
	}

	public static void noTileError(
			final ExceptionNoTileEntity e )
	{
		if ( ChiselsAndBits.getConfig().logTileErrors )
		{
			getLogger().error( "Unable to find TileEntity while interacting with block.", e );
		}
	}

}
