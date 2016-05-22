package mod.chiselsandbits.chiseledblock;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.util.math.AxisAlignedBB;

public class BoxCollection implements Collection<AxisAlignedBB>
{

	private final AxisAlignedBB[][] arrays;

	static class BoxIterator implements Iterator<AxisAlignedBB>
	{

		private int arrayOffset = 0, idx = -1;
		private final AxisAlignedBB[][] arrays;

		public BoxIterator(
				final AxisAlignedBB[][] a )
		{
			arrays = a;
			findNextItem();
		}

		private void findNextItem()
		{
			++idx;

			if ( arrays[arrayOffset] == null || idx >= arrays[arrayOffset].length )
			{
				idx = -1;
				++arrayOffset;

				if ( hasNext() )
				{
					findNextItem();
				}
			}
		}

		@Override
		public boolean hasNext()
		{
			return arrays.length > arrayOffset;
		}

		@Override
		public AxisAlignedBB next()
		{
			final AxisAlignedBB box = arrays[arrayOffset][idx];

			findNextItem();

			return box;
		}

		@Override
		public void remove()
		{
			throw new RuntimeException( "Not Implemented." );
		}

	};

	public BoxCollection(
			final AxisAlignedBB[] a )
	{
		arrays = new AxisAlignedBB[1][];
		arrays[0] = a;
	}

	public BoxCollection(
			final AxisAlignedBB[] a,
			final AxisAlignedBB[] b )
	{
		arrays = new AxisAlignedBB[2][];
		arrays[0] = a;
		arrays[1] = b;
	}

	public BoxCollection(
			final AxisAlignedBB[] a,
			final AxisAlignedBB[] b,
			final AxisAlignedBB[] c )
	{
		arrays = new AxisAlignedBB[3][];
		arrays[0] = a;
		arrays[1] = b;
		arrays[2] = c;
	}

	@Override
	public boolean add(
			final AxisAlignedBB e )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(
			final Collection<? extends AxisAlignedBB> c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(
			final Object o )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(
			final Collection<?> c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<AxisAlignedBB> iterator()
	{
		return new BoxIterator( arrays );
	}

	@Override
	public boolean remove(
			final Object o )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(
			final Collection<?> c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(
			final Collection<?> c )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int size()
	{
		int size = 0;

		for ( final AxisAlignedBB[] bb : arrays )
		{
			if ( bb != null )
			{
				size += bb.length;
			}
		}

		return size;
	}

	@Override
	public Object[] toArray()
	{
		int s = size();
		final AxisAlignedBB[] storage = new AxisAlignedBB[s];

		s = 0;
		for ( final AxisAlignedBB bb : this )
		{
			storage[s++] = bb;
		}

		return storage;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public <T> T[] toArray(
			final T[] a )
	{
		int s = 0;
		for ( final AxisAlignedBB bb : this )
		{
			a[s++] = (T) bb;
		}

		return a;
	}

}
