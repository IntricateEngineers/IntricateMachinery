package mod.chiselsandbits.integration.mcmultipart;

import mcmultipart.microblock.IMicroMaterial;
import mcmultipart.microblock.IMicroblockPlacementGrid;
import mcmultipart.microblock.MicroblockClass;
import mcmultipart.microblock.MicroblockPlacement;
import mcmultipart.multipart.IMultipart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ChiseledMicroblock extends MicroblockClass
{

	final public static MicroblockClass instance = new ChiseledMicroblock();

	@Override
	public IMultipart create(
			final boolean clientside )
	{
		return new ChiseledBlockPart();
	}

	@Override
	public ItemStack createStack(
			final IMicroMaterial material,
			final int arg1,
			final int arg2 )
	{
		final ChiseledBlockPart part = (ChiseledBlockPart) material;
		return part.getTile().getItemStack( null );
	}

	@Override
	public String getLocalizedName(
			final IMicroMaterial material,
			final int arg1 )
	{
		final ChiseledBlockPart part = (ChiseledBlockPart) material;
		return part.getBlock().getUnlocalizedName();
	}

	@Override
	public MicroblockPlacement getPlacement(
			final World world,
			final BlockPos pos,
			final IMicroMaterial material,
			final int size,
			final RayTraceResult hit,
			final EntityPlayer player )
	{
		return null;
	}

	@Override
	public IMicroblockPlacementGrid getPlacementGrid()
	{
		return null;
	}

	@Override
	public String getType()
	{
		return MCMultiPart.block_name;
	}

}
