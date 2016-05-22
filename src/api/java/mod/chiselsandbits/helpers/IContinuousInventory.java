package mod.chiselsandbits.helpers;

import mod.chiselsandbits.helpers.ModUtil.ItemStackSlot;

public interface IContinuousInventory
{

	void useItem(
			int blockId );

	void fail(
			int blockId );

	boolean isValid();

	ItemStackSlot getItem(
			int blockId );

}
