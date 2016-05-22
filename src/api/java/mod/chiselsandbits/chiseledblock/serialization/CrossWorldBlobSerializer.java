package mod.chiselsandbits.chiseledblock.serialization;

import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class CrossWorldBlobSerializer extends BlobSerializer
{

	public CrossWorldBlobSerializer(
			final PacketBuffer toInflate )
	{
		super( toInflate );
	}

	public CrossWorldBlobSerializer(
			final VoxelBlob toDeflate )
	{
		super( toDeflate );
	}

	@Override
	protected int readStateID(
			final PacketBuffer buffer )
	{
		final String name = buffer.readStringFromBuffer( 512 );
		final int meta = buffer.readVarIntFromBuffer();

		final Block blk = Block.REGISTRY.getObject( new ResourceLocation( name ) );

		if ( blk == null )
		{
			return 0;
		}

		final IBlockState state = blk.getStateFromMeta( meta );
		if ( state == null )
		{
			return 0;
		}

		return Block.getStateId( state );
	}

	@Override
	protected void writeStateID(
			final PacketBuffer buffer,
			final int key )
	{
		final IBlockState state = Block.getStateById( key );
		final Block blk = state.getBlock();

		final String name = Block.REGISTRY.getNameForObject( blk ).toString();
		final int meta = blk.getMetaFromState( state );

		buffer.writeString( name );
		buffer.writeVarIntToBuffer( meta );
	}

	@Override
	public int getVersion()
	{
		return VoxelBlob.VERSION_CROSSWORLD;
	}
}
