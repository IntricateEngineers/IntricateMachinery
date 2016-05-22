package mod.chiselsandbits.api;

import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

/**
 * Do not implement, is passed to your {@link IChiselsAndBitsAddon}
 */
public interface IChiselAndBitsAPI
{

	/**
	 * Determine the Item Type and return it.
	 *
	 * @param item
	 * @return ItemType of the item, or null if it is not any of them.
	 */
	ItemType getItemType(
			ItemStack item );

	/**
	 * Check if a block can support {@link IBitAccess}
	 *
	 * @param world
	 * @param pos
	 * @return true if the block can be chiseled, this is true for air,
	 *         multi-parts, and blocks which can be chiseled, false otherwise.
	 */
	boolean canBeChiseled(
			World world,
			BlockPos pos );

	/**
	 * is this block already chiseled?
	 *
	 * @param world
	 * @param pos
	 * @return true if the block contains chiseled bits, false otherwise.
	 */
	boolean isBlockChiseled(
			World world,
			BlockPos pos );

	/**
	 * Get Access to the bits for a given block.
	 *
	 * @param world
	 * @param pos
	 * @return A {@link IBitAccess} for the specified location.
	 * @throws CannotBeChiseled
	 *             when the location cannot support bits, or if the parameters
	 *             are invalid.
	 */
	IBitAccess getBitAccess(
			World world,
			BlockPos pos ) throws CannotBeChiseled;

	/**
	 * Create a bit access from an item, passing null creates an empty item,
	 * passing an invalid item returns null.
	 *
	 * @return a {@link IBitAccess} for an item.
	 */
	IBitAccess createBitItem(
			ItemStack BitItemStack );

	/**
	 * Create a brush from an item, once created you can use it many times.
	 *
	 * @param bitItem
	 * @return A brush for the specified item, if null is passed for the item an
	 *         air brush is created.
	 * @throws InvalidBitItem
	 */
	IBitBrush createBrush(
			ItemStack bitItem ) throws InvalidBitItem;

	/**
	 * Create a brush from an state, once created you can use it many times.
	 *
	 * @param state
	 * @return A brush for the specified state, if null is passed for the item
	 *         an air brush is created.
	 * @throws InvalidBitItem
	 */
	IBitBrush createBrushFromState(
			IBlockState state ) throws InvalidBitItem;

	/**
	 * Convert ray trace information into bit location information, note that
	 * the block position can change, be aware.
	 *
	 * @param hitX
	 * @param hitY
	 * @param hitZ
	 * @param side
	 * @param pos
	 * @param placement
	 * @return details about the target bit, if any parameters are missing will
	 *         return null.
	 */
	IBitLocation getBitPos(
			float hitX,
			float hitY,
			float hitZ,
			EnumFacing side,
			BlockPos pos,
			boolean placement );

	/**
	 * Get an ItemStack for the bit type of the state...
	 *
	 * VERY IMPORTANT: C&B lets you disable bits, if this happens the Item in
	 * this ItemStack WILL BE NULL, if you put this item in an inventory, drop
	 * it on the ground, or anything else.. CHECK THIS!!!!!
	 *
	 * @param defaultState
	 * @return the bit.
	 */
	ItemStack getBitItem(
			IBlockState defaultState ) throws InvalidBitItem;

	/**
	 * Give a bit to a player, it will end up in their inventory, a bag, or if
	 * there is no where to put it, on the ground.
	 *
	 * CLIENT: destroys the item.
	 *
	 * SERVER: adds item to inv/bag/spawns entity.
	 *
	 * @param player
	 *            player to give bits to.
	 * @param itemstack
	 *            bits to store.
	 * @param spawnPos
	 *            if null defaults to the players position, absolute position of
	 *            where to spawn bits, should be in the block near where they
	 *            are being extracted from.
	 */
	void giveBitToPlayer(
			EntityPlayer player,
			ItemStack itemstack,
			Vec3d spawnPos );

	/**
	 * Access the contents of a bitbag as if it was a normal
	 * {@link IItemHandler} with a few extra features.
	 *
	 * @return internal object to manipulate bag.
	 */
	IBitBag getBitbag(
			ItemStack itemstack );

	/**
	 * Begins an undo operation, starting two operations without ending the
	 * previous operation will throw a runtime exception.
	 *
	 * @formatter:off
	 *
	 * Example:
	 *
	 * try
	 * {
	 *     api.beginUndoGroup();
	 *     this.manipulateAllTheBlocks();
	 * }
	 * finally
	 * {
	 *     api.endUndoGroup();
	 * }
	 *
	 */
	void beginUndoGroup(
			EntityPlayer player );

	/**
	 * Ends a previously running undo operation, must be called after starting
	 * an undo operation, closing a group without opening one will result in a
	 * runtime exception.
	 */
	void endUndoGroup(
			EntityPlayer player );

}
