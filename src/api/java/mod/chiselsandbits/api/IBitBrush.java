package mod.chiselsandbits.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

/**
 * Do not implement, acquire from {@link IChiselAndBitsAPI}
 */
public interface IBitBrush
{

	/**
	 * @return true when the brush is air...
	 */
	boolean isAir();

	/**
	 * Gets the corresponding block state.
	 *
	 * @return IBlockState of brush, null for air.
	 */
	IBlockState getState();

	/**
	 * Get the ItemStack for a bit, returns null for air.
	 *
	 * VERY IMPORTANT: C&B lets you disable bits, if this happens the Item in
	 * this ItemStack WILL BE NULL, if you put this item in an inventory, drop
	 * it on the ground, or anything else.. CHECK THIS!!!!!
	 *
	 * @param count
	 * @return ItemStack, or null for air.
	 */
	ItemStack getItemStack(
			int count );

	/**
	 * @return the state id for the {@link IBlockState}
	 */
	int getStateID();

}
