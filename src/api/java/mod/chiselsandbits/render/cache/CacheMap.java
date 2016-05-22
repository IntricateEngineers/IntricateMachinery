package mod.chiselsandbits.render.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * In the end i'm not sure if this helps vs WeakHashMap, but it is cleaner sync
 * wise...
 */
public class CacheMap<K, V>
{

	public static interface EqTest
	{

		boolean doTest(
				Object a,
				Object b );

		int getHash(
				Object referent );

	};

	private static class EqSimple implements EqTest
	{

		@Override
		public boolean doTest(
				final Object a,
				final Object b )
		{
			return a.equals( b );
		}

		@Override
		public int getHash(
				final Object referent )
		{
			return referent.hashCode();
		}

	};

	final private EqTest test;

	private boolean eq(
			final Object a,
			final Object b )
	{
		return test.doTest( a, b );
	}

	private class WeakEntiry extends WeakReference<K>
	{

		final int hashcode;

		public WeakEntiry(
				final K referent,
				final ReferenceQueue<? super K> q )
		{
			super( referent, q );
			hashcode = test.getHash( referent );
		}

		@Override
		public boolean equals(
				final Object obj )
		{
			final K inner = get();

			if ( inner == null )
			{
				return false;
			}

			if ( obj == this || inner == obj )
			{
				return true;
			}

			if ( obj instanceof WeakReference )
			{
				final Object o = ( (WeakReference<?>) obj ).get();

				if ( o == null )
				{
					return false;
				}

				if ( o == inner )
				{
					return true;
				}

				return eq( inner, o );
			}

			return eq( inner, obj );
		}

		@Override
		public int hashCode()
		{
			return hashcode;
		}

	};

	private final ReferenceQueue<K> queue = new ReferenceQueue<K>();
	private final HashMap<Object, V> inner = new HashMap<Object, V>();

	public CacheMap()
	{
		test = new EqSimple();
		ModelCacheCleanup.registerCacheMap( this );
	}

	public CacheMap(
			final EqTest test )
	{
		this.test = test;
		ModelCacheCleanup.registerCacheMap( this );
	}

	@Override
	protected void finalize() throws Throwable
	{
		ModelCacheCleanup.unregisterCacheMap( this );
	}

	public void put(
			final K key,
			final V value )
	{
		synchronized ( this )
		{
			inner.put( new WeakEntiry( key, queue ), value );
		}
	}

	private class EqWrapper
	{

		private int hashcode;
		private K inner;

		@SuppressWarnings( "unchecked" )
		void setObject(
				final K o )
		{
			if ( o instanceof WeakReference )
			{
				inner = (K) ( (WeakReference<?>) o ).get();
				hashcode = test.getHash( inner );
			}
			else
			{
				inner = o;
				hashcode = test.getHash( inner );
			}
		}

		@Override
		public int hashCode()
		{
			return hashcode;
		}

		@Override
		public boolean equals(
				final Object obj )
		{
			if ( inner == null )
			{
				return false;
			}

			if ( obj == this || inner == obj )
			{
				return true;
			}

			if ( obj instanceof WeakReference )
			{
				final Object o = ( (WeakReference<?>) obj ).get();

				if ( o == null )
				{
					return false;
				}

				if ( o == inner )
				{
					return true;
				}

				return eq( inner, o );
			}

			return eq( inner, obj );
		}

	};

	private final EqWrapper lookupHelper = new EqWrapper();

	public V get(
			final K key )
	{
		synchronized ( this )
		{
			lookupHelper.setObject( key );
			return inner.get( lookupHelper );
		}
	}

	public void clear()
	{
		synchronized ( this )
		{
			inner.clear();

			while ( queue.poll() != null )
			{
				// we don't care...
			}
		}
	}

	protected void cleanup()
	{
		Object ref;

		while ( ( ref = queue.poll() ) != null )
		{
			synchronized ( this )
			{
				inner.remove( ref );
			}
		}
	}

}
