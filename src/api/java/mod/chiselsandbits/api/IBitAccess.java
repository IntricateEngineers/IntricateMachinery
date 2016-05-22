package mod.chiselsandbits.api;

import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

/**
 * Do not implement, acquire from {@link IChiselAndBitsAPI}
 */
public interface IBitAccess
{

	/**
	 * Process each bit in the {@link IBitAccess} and return a new bit in its
	 * place, can be used to optimize large changes, or iteration.
	 *
	 * @param visitor
	 */
	void visitBits(
			IBitVisitor visitor );

	/**
	 * Returns the bit at the specific location, this should always return a
	 * valid IBitBrush, never null.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	IBitBrush getBitAt(
			int x,
			int y,
			int z );

	/**
	 * Sets the bit at the specific location, if you pass null it will use use
	 * air.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param bit
	 * @throws SpaceOccupied
	 */
	void setBitAt(
			int x,
			int y,
			int z,
			IBitBrush bit ) throws SpaceOccupied;

	/**
	 * Any time you modify a block you must commit your changes for them to take
	 * affect, optionally you can trigger updates or not.
	 *
	 * If the {@link IBitAccess} is not in the world this method does nothing.
	 *
	 * @param triggerUpdates
	 *            normally true, only use false if your doing something special.
	 */
	void commitChanges(
			boolean triggerUpdates );

	/**
	 * Any time you modify a block you must commit your changes for them to take
	 * affect, please move to above method and specify true to prepare for
	 * future removal.
	 *
	 * If the {@link IBitAccess} is not in the world this method does nothing.
	 */
	@Deprecated
	void commitChanges();

	/**
	 * Returns an item for the {@link IBitAccess}
	 *
	 * Usable for any {@link IBitAccess}
	 *
	 * @param side
	 *            angle the player is looking at, can be null.
	 * @param type
	 *            what type of item to give.
	 * @param crossWorld
	 *            determines if the NBT for the item is specific to this world
	 *            or if it is portable, cross world NBT is larger and slower,
	 *            you should only request cross world NBT if you specifically
	 *            need it.
	 * @return an Item for bits, null if there are no bits.
	 */
	ItemStack getBitsAsItem(
			EnumFacing side,
			ItemType type,
			boolean crossWorld );

	/**
	 * Returns an item for the {@link IBitAccess}, this method all ways returns
	 * non-cross world NBT, please move to the above method and specify false to
	 * prepare for future removal.
	 *
	 * Usable for any {@link IBitAccess}
	 *
	 * @param side
	 *            angle the player is looking at, can be null.
	 * @param type
	 *            what type of item to give.
	 * @return an Item for bits, null if there are no bits.
	 */
	@Deprecated
	ItemStack getBitsAsItem(
			EnumFacing side,
			ItemType type );

}
