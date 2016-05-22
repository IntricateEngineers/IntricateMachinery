package mod.chiselsandbits.api;

import net.minecraftforge.items.IItemHandler;

public interface IBitBag extends IItemHandler
{

	/**
	 * @return get max stack size of bits inside the bag.
	 */
	int getBitbagStackSize();

	/**
	 * @return how many slots contain bits.
	 */
	int getSlotsUsed();

}
