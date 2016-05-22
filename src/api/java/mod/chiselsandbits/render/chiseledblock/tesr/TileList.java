package mod.chiselsandbits.render.chiseledblock.tesr;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseledTESR;

public class TileList extends ArrayList<TileEntityBlockChiseledTESR>
{

	private static final long serialVersionUID = -1489262087068588997L;

	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();

	public Lock getReadLock()
	{
		return r;
	}

	public Lock getWriteLock()
	{
		return w;
	}
}
