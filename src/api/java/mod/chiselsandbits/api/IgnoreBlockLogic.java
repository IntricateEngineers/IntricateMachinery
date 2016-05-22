package mod.chiselsandbits.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * When checking for blocks to allow for chiseling C&B checks various methods...
 *
 * hasTileEntity, getTickRandomly, quantityDropped, quantityDroppedWithBonus,
 * onEntityCollidedWithBlock, and isFullBlock
 *
 * If you include this annotation or use the IMC below, you can force C&B to
 * overlook these custom implementations, please use with care and test before
 * releasing usage.
 *
 * Put this on the block, or use the IMC,
 *
 * FMLInterModComms.sendMessage( "chiselsandbits", "ignoreblocklogic",
 * "myBlockName" );
 */
@Retention( RetentionPolicy.RUNTIME )
public @interface IgnoreBlockLogic
{

}
