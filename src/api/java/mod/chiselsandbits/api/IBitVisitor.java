package mod.chiselsandbits.api;

public interface IBitVisitor
{

	/**
	 * Called once for each bit in the block, the return value will be used as
	 * new bit after visitBits returns.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param currentValue
	 * @return newValue
	 */
	IBitBrush visitBit(
			int x,
			int y,
			int z,
			IBitBrush currentValue );

}
