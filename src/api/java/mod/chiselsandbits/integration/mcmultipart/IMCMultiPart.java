package mod.chiselsandbits.integration.mcmultipart;

import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMCMultiPart
{
	void swapRenderIfPossible(
			TileEntity current,
			TileEntityBlockChiseled newTileEntity );

	void removePartIfPossible(
			TileEntity te );

	TileEntityBlockChiseled getPartIfPossible(
			World world,
			BlockPos pos,
			boolean create );

	void triggerPartChange(
			TileEntity te );

	boolean isMultiPart(
			World w,
			BlockPos pos );

	void populateBlobWithUsedSpace(
			World w,
			BlockPos pos,
			VoxelBlob blob );

	boolean rotate(
			World world,
			BlockPos pos,
			EntityPlayer player );

}
