package mod.chiselsandbits.client;

import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateReference;
import net.minecraft.util.math.BlockPos;

public class UndoStep
{
	public final int dimensionId;
	public final BlockPos pos;
	public final VoxelBlobStateReference before;
	public final VoxelBlobStateReference after;
	public UndoStep next = null; // groups form a linked chain.

	public UndoStep(
			final int dimensionId,
			final BlockPos pos,
			final VoxelBlobStateReference before,
			final VoxelBlobStateReference after )
	{
		this.dimensionId = dimensionId;
		this.pos = pos;
		this.before = before;
		this.after = after;
	}

}
