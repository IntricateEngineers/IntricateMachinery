package mod.chiselsandbits.bittank;

import net.minecraft.util.EnumFacing;

class FluidModelVertex
{
	final EnumFacing face;
	final double x, yMultiplier, z;
	final double u, v;
	final double uMultiplier, vMultiplier;

	public FluidModelVertex(
			final EnumFacing side,
			final double x,
			final double y,
			final double z,
			final double u1,
			final double v1,
			final double u2,
			final double v2 )
	{
		face = side;
		this.x = x;
		yMultiplier = y;
		this.z = z;

		final double texMultiplier = side.getFrontOffsetY() == 0 ? 8 : 16;

		u = u1 * texMultiplier;
		v = v1 * texMultiplier;
		uMultiplier = u2 * texMultiplier;
		vMultiplier = v2 * texMultiplier;
	}
}