package mod.chiselsandbits.integration.mcmultipart;

import java.util.Collection;

import com.google.common.base.Predicate;

import mcmultipart.block.TileMultipartContainer;
import mcmultipart.client.multipart.MultipartRegistryClient;
import mcmultipart.microblock.MicroblockRegistry;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.MultipartRegistry;
import mcmultipart.multipart.OcclusionHelper;
import mcmultipart.raytrace.RayTraceUtils;
import mcmultipart.raytrace.RayTraceUtils.AdvancedRayTraceResultPart;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.data.BitCollisionIterator;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.integration.ChiselsAndBitsIntegration;
import mod.chiselsandbits.integration.IntegrationBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

@ChiselsAndBitsIntegration( "mcmultipart" )
public class MCMultiPart extends IntegrationBase implements IMCMultiPart
{

	public final static String block_name = ChiselsAndBits.MODID + ":chisledblock";

	@Override
	public void preinit()
	{
		MCMultipartProxy.proxyMCMultiPart.setRelay( this );

		MultipartRegistry.registerPart( ChiseledBlockPart.class, block_name );
		MicroblockRegistry.registerMicroClass( ChiseledMicroblock.instance );

		final ChiseledBlockConverter converter = new ChiseledBlockConverter();
		MultipartRegistry.registerPartConverter( converter );
		MultipartRegistry.registerReversePartConverter( converter );

		if ( FMLCommonHandler.instance().getSide() == Side.CLIENT )
		{
			MultipartRegistryClient.bindMultipartSpecialRenderer( ChiseledBlockPart.class, new ChiseledBlockRenderChunkMPSR() );
		}
	}

	@Override
	public void removePartIfPossible(
			final TileEntity te )
	{
		if ( te instanceof IMultipartContainer && !te.getWorld().isRemote )
		{
			final IMultipartContainer container = (IMultipartContainer) te;
			for ( final IMultipart part : container.getParts() )
			{
				if ( part instanceof ChiseledBlockPart )
				{
					container.removePart( part );
					return;
				}
			}
		}

	}

	@Override
	public void triggerPartChange(
			final TileEntity te )
	{
		if ( te instanceof IMultipartContainer && !te.getWorld().isRemote )
		{
			for ( final IMultipart part : ( (IMultipartContainer) te ).getParts() )
			{
				if ( part instanceof ChiseledBlockPart )
				{
					( (ChiseledBlockPart) part ).notifyPartUpdate();
				}
			}
		}
	}

	@Override
	public void swapRenderIfPossible(
			final TileEntity current,
			final TileEntityBlockChiseled newTileEntity )
	{
		if ( current instanceof IMultipartContainer )
		{
			final IMultipartContainer container = (IMultipartContainer) current;
			for ( final IMultipart part : container.getParts() )
			{
				if ( part instanceof ChiseledBlockPart )
				{
					( (ChiseledBlockPart) part ).swapTile( newTileEntity );
					return;
				}
			}
		}
	}

	@Override
	public TileEntityBlockChiseled getPartIfPossible(
			final World w,
			final BlockPos pos,
			final boolean create )
	{
		final IMultipartContainer container = MultipartHelper.getOrConvertPartContainer( w, pos, false );

		if ( container != null )
		{
			for ( final IMultipart part : container.getParts() )
			{
				if ( part instanceof ChiseledBlockPart )
				{
					return ( (ChiseledBlockPart) part ).getTile();
				}
			}

			final ChiseledBlockPart part = new ChiseledBlockPart();
			if ( MultipartHelper.canAddPart( w, pos, part ) )
			{
				if ( create && !w.isRemote )
				{
					final TileEntityBlockChiseled tx = part.getTile();
					tx.occlusionState = new MultipartContainerBuilder( w, pos, part, container );
					tx.setWorldObj( w );
					tx.setPos( pos );
					return tx;
				}
				else if ( create )
				{
					final TileEntityBlockChiseled tx = part.getTile();
					part.setContainer( container );
					tx.setWorldObj( w );
					tx.setPos( pos );					
					return tx;
				}
			}
		}

		return null;
	}

	@Override
	public boolean isMultiPart(
			final World w,
			final BlockPos pos )
	{
		return MultipartHelper.getPartContainer( w, pos ) != null // is it a
																	// container?
				|| MultipartHelper.canAddPart( w, pos, new ChiseledBlockPart() ); // or
																					// can
																					// it
																					// be
																					// converted?
	}

	private static class IgnorePred implements Predicate<IMultipart>
	{

		final IMultipart ignore;

		public IgnorePred(
				final IMultipart i )
		{
			ignore = i;
		}

		@Override
		public boolean apply(
				final IMultipart input )
		{
			return ignore == input;
		}

	};

	@Override
	public void populateBlobWithUsedSpace(
			final World w,
			final BlockPos pos,
			final VoxelBlob vb )
	{
		if ( isMultiPart( w, pos ) )
		{
			final IMultipartContainer mc = MultipartHelper.getOrConvertPartContainer( w, pos, false );// get
			// container..

			final BitCollisionIterator bci = new BitCollisionIterator();
			final Collection<? extends IMultipart> parts = mc.getParts();
			IMultipart ignore = null;

			for ( final IMultipart part : parts )
			{
				if ( part instanceof ChiseledBlockPart )
				{
					ignore = part;
				}
			}

			while ( bci.hasNext() )
			{
				final AxisAlignedBB aabb = new AxisAlignedBB( bci.physicalX, bci.physicalY, bci.physicalZ, bci.physicalX + BitCollisionIterator.One16thf, bci.physicalYp1, bci.physicalZp1 );

				if ( !OcclusionHelper.occlusionTest( parts, new IgnorePred( ignore ), aabb ) )
				{
					bci.setNext( vb, 1 );
				}
			}
		}
	}

	@Override
	public boolean rotate(
			final World world,
			final BlockPos pos,
			final EntityPlayer player )
	{
		final IMultipartContainer container = MultipartHelper.getPartContainer( world, pos );
		if ( container != null )
		{
			final Vec3d start = RayTraceUtils.getStart( player );
			final Vec3d end = RayTraceUtils.getEnd( player );
			final AdvancedRayTraceResultPart result = ( (TileMultipartContainer) world.getTileEntity( pos ) ).getPartContainer().collisionRayTrace( start, end );
			if ( result != null && result.hit != null && result.hit.partHit != null )
			{
				return result.hit.partHit.rotatePart( result.hit.sideHit );
			}
		}
		return false;
	}

}
