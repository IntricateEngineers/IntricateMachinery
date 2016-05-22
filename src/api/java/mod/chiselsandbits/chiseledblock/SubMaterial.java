package mod.chiselsandbits.chiseledblock;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

class SubMaterial extends Material
{

	final private Material c;

	public SubMaterial(
			final Material color )
	{
		super( color.getMaterialMapColor() );
		c = color;
	}

	@Override
	public boolean isLiquid()
	{
		return c.isLiquid();
	}

	@Override
	public boolean isSolid()
	{
		return true;
	}

	@Override
	public boolean blocksLight()
	{
		return false;
	}

	@Override
	public boolean blocksMovement()
	{
		return true;
	}

	@Override
	public boolean getCanBurn()
	{
		return c.getCanBurn();
	}

	@Override
	public boolean isReplaceable()
	{
		return c.isReplaceable();
	}

	@Override
	public boolean isOpaque()
	{
		return false;
	}

	@Override
	public boolean isToolNotRequired()
	{
		return c.isToolNotRequired();
	}

	@Override
	public EnumPushReaction getMobilityFlag()
	{
		return c.getMobilityFlag();
	}

	@Override
	public MapColor getMaterialMapColor()
	{
		return c.getMaterialMapColor();
	}
}