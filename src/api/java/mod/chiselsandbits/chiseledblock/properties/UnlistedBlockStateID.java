package mod.chiselsandbits.chiseledblock.properties;

import net.minecraftforge.common.property.IUnlistedProperty;

public final class UnlistedBlockStateID implements IUnlistedProperty<Integer>
{
	@Override
	public String getName()
	{
		return "b";
	}

	@Override
	public boolean isValid(
			final Integer value )
	{
		return value != 0;
	}

	@Override
	public Class<Integer> getType()
	{
		return Integer.class;
	}

	@Override
	public String valueToString(
			final Integer value )
	{
		return Integer.toString( value );
	}
}