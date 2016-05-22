package mod.chiselsandbits.render.cache;

import java.util.ArrayList;
import java.util.List;

public class ModelCacheCleanup implements Runnable
{

	private static final ModelCacheCleanup instance = new ModelCacheCleanup();
	private final List<CacheMap<?, ?>> maps = new ArrayList<CacheMap<?, ?>>();

	private ModelCacheCleanup()
	{
		final Thread t = new Thread( this );
		t.setName( "C&B Model Cache Cleanup" );
		t.setPriority( ( Thread.MAX_PRIORITY + Thread.NORM_PRIORITY ) / 2 );
		t.start();
	}

	public static void registerCacheMap(
			final CacheMap<?, ?> map )
	{
		synchronized ( instance )
		{
			instance.maps.add( map );
		}
	}

	public static void unregisterCacheMap(
			final CacheMap<?, ?> map )
	{
		synchronized ( instance )
		{
			instance.maps.remove( map );
		}
	}

	@Override
	public void run()
	{
		while ( true )
		{
			try
			{
				Thread.sleep( 5000 );
			}
			catch ( final InterruptedException e )
			{
				// :P
			}

			synchronized ( this )
			{
				for ( final CacheMap<?, ?> map : maps )
				{
					map.cleanup();
				}
			}
		}
	}

}