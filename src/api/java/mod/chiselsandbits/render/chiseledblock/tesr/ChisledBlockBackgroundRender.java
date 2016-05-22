package mod.chiselsandbits.render.chiseledblock.tesr;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

import org.lwjgl.opengl.GL11;

import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseledTESR;
import mod.chiselsandbits.core.Log;
import mod.chiselsandbits.render.chiseledblock.ChiselLayer;
import mod.chiselsandbits.render.chiseledblock.ChiseledBlockBaked;
import mod.chiselsandbits.render.chiseledblock.ChiseledBlockSmartModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraftforge.common.property.IExtendedBlockState;

public class ChisledBlockBackgroundRender implements Callable<Tessellator>
{

	private final List<TileEntityBlockChiseledTESR> myPrivateList;
	private final BlockRenderLayer layer;
	private final BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
	private final static Queue<SoftReference<Tessellator>> previousTessellators = new LinkedBlockingQueue<SoftReference<Tessellator>>();

	private final ChunkCache cache;
	private final BlockPos chunkOffset;

	static class CBTessellator extends Tessellator
	{

		public CBTessellator(
				final int bufferSize )
		{
			super( bufferSize );
			ChisledBlockRenderChunkTESR.activeTess.incrementAndGet();
		}

		@Override
		protected void finalize() throws Throwable
		{
			ChisledBlockRenderChunkTESR.activeTess.decrementAndGet();
		}

	};

	public ChisledBlockBackgroundRender(
			final ChunkCache cache,
			final BlockPos chunkOffset,
			final List<TileEntityBlockChiseledTESR> tiles,
			final BlockRenderLayer layer )
	{
		myPrivateList = new ArrayList<TileEntityBlockChiseledTESR>( tiles );
		this.layer = layer;
		this.cache = cache;
		this.chunkOffset = chunkOffset;
	}

	public static void submitTessellator(
			final Tessellator t )
	{
		previousTessellators.add( new SoftReference<Tessellator>( t ) );
	}

	@Override
	public Tessellator call() throws Exception
	{
		Tessellator tessellator = null;

		do
		{
			do
			{
				final SoftReference<Tessellator> softTessellator = previousTessellators.poll();

				if ( softTessellator != null )
				{
					tessellator = softTessellator.get();
				}
			}
			while ( tessellator == null && !previousTessellators.isEmpty() );

			// no previous queues?
			if ( tessellator == null )
			{
				synchronized ( CBTessellator.class )
				{
					if ( ChisledBlockRenderChunkTESR.activeTess.get() < ChisledBlockRenderChunkTESR.getMaxTessalators() )
					{
						tessellator = new CBTessellator( 2109952 );
					}
					else
					{
						Thread.sleep( 10 );
					}
				}
			}
		}
		while ( tessellator == null );

		final VertexBuffer worldrenderer = tessellator.getBuffer();

		try
		{
			worldrenderer.begin( GL11.GL_QUADS, DefaultVertexFormats.BLOCK );
			worldrenderer.setTranslation( -chunkOffset.getX(), -chunkOffset.getY(), -chunkOffset.getZ() );
		}
		catch ( final IllegalStateException e )
		{
			Log.logError( "Invalid Tessellator Behavior", e );
		}

		final EnumSet<ChiselLayer> layers = layer == BlockRenderLayer.TRANSLUCENT ? EnumSet.of( ChiselLayer.TRANSLUCENT ) : EnumSet.complementOf( EnumSet.of( ChiselLayer.TRANSLUCENT ) );
		for ( final TileEntityBlockChiseled tx : myPrivateList )
		{
			if ( tx instanceof TileEntityBlockChiseledTESR && !tx.isInvalid() )
			{
				final IExtendedBlockState estate = ( (TileEntityBlockChiseledTESR) tx ).getTileRenderState();

				for ( final ChiselLayer lx : layers )
				{
					final ChiseledBlockBaked model = ChiseledBlockSmartModel.getCachedModel( tx, lx );

					if ( !model.isEmpty() )
					{
						blockRenderer.getBlockModelRenderer().renderModel( cache, model, estate, tx.getPos(), worldrenderer, true );

						if ( Thread.interrupted() )
						{
							worldrenderer.finishDrawing();
							submitTessellator( tessellator );
							return null;
						}
					}
				}
			}
		}

		if ( Thread.interrupted() )
		{
			worldrenderer.finishDrawing();
			submitTessellator( tessellator );
			return null;
		}

		return tessellator;
	}

}
