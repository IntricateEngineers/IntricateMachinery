package mod.chiselsandbits.chiseledblock;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

class ReflectionHelperBlock extends Block
{
	public String MethodName;

	private void markMethod()
	{
		MethodName = new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName();
	}

	protected ReflectionHelperBlock()
	{
		super( Material.AIR );
	}

	@Override
	public float getBlockHardness(
			final IBlockState state,
			final World world,
			final BlockPos pos )
	{
		markMethod();
		return 0;
	}

	@Override
	public void addCollisionBoxToList(
			final IBlockState state,
			final World worldIn,
			final BlockPos pos,
			final AxisAlignedBB p_185477_4_,
			final List<AxisAlignedBB> p_185477_5_,
			final Entity p_185477_6_ )
	{
		markMethod();
	}

	@Override
	public float getPlayerRelativeBlockHardness(
			final IBlockState state,
			final EntityPlayer player,
			final World world,
			final BlockPos pos )
	{
		markMethod();
		return 0;
	}

	@Override
	public float getExplosionResistance(
			final Entity exploder )
	{
		markMethod();
		return 0;
	}

	@Override
	public float getExplosionResistance(
			final World world,
			final BlockPos pos,
			final Entity exploder,
			final Explosion explosion )
	{
		markMethod();
		return 0;
	}

	@Override
	public int quantityDropped(
			final IBlockState state,
			final int fortune,
			final Random random )
	{

		markMethod();
		return 0;
	}

	@Override
	public int quantityDropped(
			final Random random )
	{
		markMethod();
		return 0;
	}

	@Override
	public int quantityDroppedWithBonus(
			final int fortune,
			final Random random )
	{
		markMethod();
		return 0;
	}

	@Override
	public void onEntityCollidedWithBlock(
			final World worldIn,
			final BlockPos pos,
			final IBlockState state,
			final Entity entityIn )
	{
		markMethod();
	}
}