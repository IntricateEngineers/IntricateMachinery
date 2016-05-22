package mod.chiselsandbits.render.chiseledblock.tesr;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Stopwatch;

import mod.chiselsandbits.chiseledblock.EnumTESRRenderState;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseledTESR;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.Log;
import mod.chiselsandbits.render.chiseledblock.ChiselLayer;
import mod.chiselsandbits.render.chiseledblock.ChiseledBlockBaked;
import mod.chiselsandbits.render.chiseledblock.ChiseledBlockSmartModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChisledBlockRenderChunkTESR extends TileEntitySpecialRenderer<TileEntityBlockChiseledTESR>
{
	public final static AtomicInteger pendingTess = new AtomicInteger( 0 );
	public final static AtomicInteger activeTess = new AtomicInteger( 0 );

	private final static ThreadPoolExecutor pool;
	private static ChisledBlockRenderChunkTESR instance;

	public static ChisledBlockRenderChunkTESR getInstance()
	{
		return instance;
	}

	private final LinkedList<FutureTracker> futureTrackers = new LinkedList<FutureTracker>();
	private final Queue<UploadTracker> uploaders = new ConcurrentLinkedQueue<UploadTracker>();
	private final static Queue<Runnable> nextFrameTasks = new ConcurrentLinkedQueue<Runnable>();

	public static void addTask(
			final Runnable r )
	{
		nextFrameTasks.offer( r );
	}

	private static class FutureTracker
	{
		final TileLayerRenderCache tlrc;
		final TileRenderCache renderCache;
		final BlockRenderLayer layer;
		final FutureTask<Tessellator> future;

		public FutureTracker(
				final TileLayerRenderCache tlrc,
				final TileRenderCache renderCache,
				final BlockRenderLayer layer )
		{
			this.tlrc = tlrc;
			this.renderCache = renderCache;
			this.layer = layer;
			future = tlrc.future;
		}

		public void done()
		{
			pendingTess.decrementAndGet();
		}
	};

	private void addFutureTracker(
			final TileLayerRenderCache tlrc,
			final TileRenderCache renderCache,
			final BlockRenderLayer layer )
	{
		futureTrackers.add( new FutureTracker( tlrc, renderCache, layer ) );
	}

	private boolean handleFutureTracker(
			final FutureTracker ft )
	{
		// next frame..?
		if ( ft.future != null && ft.future.isDone() )
		{
			try
			{
				final Tessellator t = ft.future.get();

				if ( ft.future == ft.tlrc.future )
				{
					ft.tlrc.waiting = true;
					uploaders.offer( new UploadTracker( ft.renderCache, ft.layer, t ) );
				}
				else
				{
					try
					{
						t.getBuffer().finishDrawing();
					}
					catch ( final IllegalStateException e )
					{
						Log.logError( "Bad Tessellator Behavior.", e );
					}

					ChisledBlockBackgroundRender.submitTessellator( t );
				}
			}
			catch ( final InterruptedException e )
			{
				Log.logError( "Failed to get TESR Future - C", e );
			}
			catch ( final ExecutionException e )
			{
				Log.logError( "Failed to get TESR Future - D", e );
			}
			catch ( final CancellationException e )
			{
				// no issues here.
			}
			finally
			{
				if ( ft.future == ft.tlrc.future )
				{
					ft.tlrc.future = null;
				}
			}

			ft.done();
			return true;
		}

		return false;
	}

	@SubscribeEvent
	void uploadDisplaylists(
			final RenderWorldLastEvent e )
	{
		do
		{
			final Runnable x = nextFrameTasks.poll();

			if ( x == null )
			{
				break;
			}

			x.run();
		}
		while ( true );

		final Iterator<FutureTracker> i = futureTrackers.iterator();
		while ( i.hasNext() )
		{
			if ( handleFutureTracker( i.next() ) )
			{
				i.remove();
			}
		}

		final Stopwatch w = Stopwatch.createStarted();
		do

		{
			final UploadTracker t = uploaders.poll();

			if ( t == null )
			{
				return;
			}

			if ( t.trc instanceof TileRenderChunk )
			{
				final Stopwatch sw = Stopwatch.createStarted();
				uploadDisplayList( t );

				if ( sw.elapsed( TimeUnit.MILLISECONDS ) > 10 )
				{
					( (TileRenderChunk) t.trc ).singleInstanceMode = true;
				}
			}
			else
			{
				uploadDisplayList( t );
			}

			t.trc.getLayer( t.layer ).waiting = false;
		}
		while ( w.elapsed( TimeUnit.MILLISECONDS ) < 1 );

	}

	private void uploadDisplayList(
			final UploadTracker t )
	{
		final BlockRenderLayer layer = t.layer;
		final TileLayerRenderCache tlrc = t.trc.getLayer( layer );

		if ( tlrc.displayList == 0 )
		{
			tlrc.displayList = GLAllocation.generateDisplayLists( 1 );
		}

		try
		{
			GL11.glNewList( tlrc.displayList, GL11.GL_COMPILE );
			t.getTessellator().draw();
		}
		catch ( final IllegalStateException e )
		{
			Log.logError( "Erratic Tessellator Behavior", e );
			tlrc.rebuild = true;
		}
		finally
		{
			GL11.glEndList();
		}

		t.submitForReuse();
	}

	public ChisledBlockRenderChunkTESR()
	{
		instance = this;
		ChiselsAndBits.registerWithBus( this );
	}

	static
	{
		final ThreadFactory threadFactory = new ThreadFactory() {

			@Override
			public Thread newThread(
					final Runnable r )
			{
				final Thread t = new Thread( r );
				t.setPriority( Thread.NORM_PRIORITY - 1 );
				t.setName( "C&B Dynamic Render Thread" );
				return t;
			}
		};

		int processors = Runtime.getRuntime().availableProcessors();
		if ( ChiselsAndBits.getConfig().lowMemoryMode )
		{
			processors = 1;
		}

		pool = new ThreadPoolExecutor( 1, processors, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>( 64 ), threadFactory );
		pool.allowCoreThreadTimeOut( false );
	}

	public void renderBreakingEffects(
			final TileEntityBlockChiseled te,
			final double x,
			final double y,
			final double z,
			final float partialTicks,
			final int destroyStage )
	{
		bindTexture( TextureMap.LOCATION_BLOCKS_TEXTURE );
		final String file = DESTROY_STAGES[destroyStage].toString().replace( "textures/", "" ).replace( ".png", "" );
		final TextureAtlasSprite damageTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite( file );

		GlStateManager.pushMatrix();
		GlStateManager.depthFunc( GL11.GL_LEQUAL );
		final BlockPos cp = te.getPos();
		GlStateManager.translate( x - cp.getX(), y - cp.getY(), z - cp.getZ() );

		final Tessellator tessellator = Tessellator.getInstance();
		final VertexBuffer worldrenderer = tessellator.getBuffer();

		worldrenderer.begin( GL11.GL_QUADS, DefaultVertexFormats.BLOCK );
		worldrenderer.setTranslation( 0, 0, 0 );

		final BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
		final IExtendedBlockState estate = te.getRenderState();

		for ( final ChiselLayer lx : ChiselLayer.values() )
		{
			final ChiseledBlockBaked model = ChiseledBlockSmartModel.getCachedModel( te, lx );

			if ( !model.isEmpty() )
			{
				final IBakedModel damageModel = new SimpleBakedModel.Builder( estate, model, damageTexture, cp ).makeBakedModel();
				blockRenderer.getBlockModelRenderer().renderModel( te.getWorld(), damageModel, estate, te.getPos(), worldrenderer, false );
			}
		}

		tessellator.draw();
		worldrenderer.setTranslation( 0.0D, 0.0D, 0.0D );

		GlStateManager.resetColor();
		GlStateManager.popMatrix();
		return;
	}

	@Override
	public void renderTileEntityFast(
			final TileEntityBlockChiseledTESR te,
			final double x,
			final double y,
			final double z,
			final float partialTicks,
			final int destroyStage,
			final VertexBuffer worldRenderer )
	{
		if ( destroyStage > 0 )
		{
			renderLogic( te, x, y, z, partialTicks, destroyStage, false );
			return;
		}

		renderLogic( te, x, y, z, partialTicks, destroyStage, true );
	}

	private void renderLogic(
			final TileEntityBlockChiseledTESR te,
			final double x,
			final double y,
			final double z,
			final float partialTicks,
			final int destroyStage,
			final boolean groupLogic )
	{
		final BlockRenderLayer layer = MinecraftForgeClient.getRenderPass() == 0 ? BlockRenderLayer.SOLID : BlockRenderLayer.TRANSLUCENT;
		final TileRenderChunk renderChunk = te.getRenderChunk();
		TileRenderCache renderCache = renderChunk;

		/// how????
		if ( renderChunk == null )
		{
			return;
		}

		if ( destroyStage >= 0 )
		{
			if ( layer == BlockRenderLayer.SOLID )
			{
				return;
			}

			renderBreakingEffects( te, x, y, z, partialTicks, destroyStage );
			return;
		}

		// cache at the tile level rather than the chunk level.
		if ( renderChunk.singleInstanceMode )
		{
			if ( groupLogic )
			{
				final EnumTESRRenderState state = renderCache.update( layer, 0 );
				if ( renderCache == null || state == EnumTESRRenderState.SKIP )
				{
					return;
				}

				final TileList tiles = renderChunk.getTiles();
				tiles.getReadLock().lock();

				try
				{
					for ( final TileEntityBlockChiseledTESR e : tiles )
					{
						configureGLState( layer );
						renderLogic( e, x, y, z, partialTicks, destroyStage, false );
						unconfigureGLState();
					}
				}
				finally
				{
					tiles.getReadLock().unlock();
				}

				return;
			}

			renderCache = te.getCache();
		}

		final EnumTESRRenderState state = renderCache.update( layer, 0 );
		if ( renderCache == null || state == EnumTESRRenderState.SKIP )
		{
			return;
		}

		final BlockPos chunkOffset = renderChunk.chunkOffset();

		final TileLayerRenderCache tlrc = renderCache.getLayer( layer );
		final boolean isNew = tlrc.isNew();
		boolean hasSubmitted = false;

		if ( tlrc.displayList == 0 || tlrc.rebuild )
		{
			final int dynamicTess = getMaxTessalators();

			if ( pendingTess.get() < dynamicTess && tlrc.future == null && !tlrc.waiting || isNew )
			{
				// copy the tiles for the thread..
				final ChunkCache cache = new ChunkCache( getWorld(), chunkOffset, chunkOffset.add( 16, 16, 16 ), 1 );
				final FutureTask<Tessellator> newFuture = new FutureTask<Tessellator>( new ChisledBlockBackgroundRender( cache, chunkOffset, renderCache.getTiles(), layer ) );

				try
				{
					pool.submit( newFuture );
					hasSubmitted = true;

					if ( tlrc.future != null )
					{
						tlrc.future.cancel( true );
					}

					tlrc.rebuild = false;
					tlrc.future = newFuture;
					pendingTess.incrementAndGet();
				}
				catch ( final RejectedExecutionException err )
				{
					// Yar...
				}
			}
		}

		// now..
		if ( tlrc.future != null && isNew && hasSubmitted )
		{
			try
			{
				final Tessellator tess = tlrc.future.get( 5, TimeUnit.MILLISECONDS );
				tlrc.future = null;
				pendingTess.decrementAndGet();

				uploadDisplayList( new UploadTracker( renderCache, layer, tess ) );

				tlrc.waiting = false;
			}
			catch ( final InterruptedException e )
			{
				Log.logError( "Failed to get TESR Future - A", e );
				tlrc.future = null;
			}
			catch ( final ExecutionException e )
			{
				Log.logError( "Failed to get TESR Future - B", e );
				tlrc.future = null;
			}
			catch ( final TimeoutException e )
			{
				addFutureTracker( tlrc, renderCache, layer );
			}
		}
		else if ( tlrc.future != null && hasSubmitted )
		{
			addFutureTracker( tlrc, renderCache, layer );
		}

		final int dl = tlrc.displayList;
		if ( dl != 0 )
		{
			GL11.glPushMatrix();
			GL11.glTranslated( -TileEntityRendererDispatcher.staticPlayerX + chunkOffset.getX(),
					-TileEntityRendererDispatcher.staticPlayerY + chunkOffset.getY(),
					-TileEntityRendererDispatcher.staticPlayerZ + chunkOffset.getZ() );

			configureGLState( layer );
			GL11.glCallList( dl );
			unconfigureGLState();

			GL11.glPopMatrix();
		}
	}

	public static int getMaxTessalators()
	{
		int dynamicTess = ChiselsAndBits.getConfig().dynamicMaxConcurrentTessalators;

		if ( ChiselsAndBits.getConfig().lowMemoryMode )
		{
			dynamicTess = Math.min( 2, dynamicTess );
		}

		return dynamicTess;
	}

	int isConfigured = 0;

	private void configureGLState(
			final BlockRenderLayer layer )
	{
		isConfigured++;

		if ( isConfigured == 1 )
		{
			OpenGlHelper.setLightmapTextureCoords( OpenGlHelper.lightmapTexUnit, 0, 0 );

			GlStateManager.color( 1.0f, 1.0f, 1.0f, 1.0f );
			bindTexture( TextureMap.LOCATION_BLOCKS_TEXTURE );

			RenderHelper.disableStandardItemLighting();
			GlStateManager.blendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
			GlStateManager.color( 1.0f, 1.0f, 1.0f, 1.0f );

			if ( layer == BlockRenderLayer.TRANSLUCENT )
			{
				GlStateManager.enableBlend();
				GlStateManager.disableAlpha();
			}
			else
			{
				GlStateManager.disableBlend();
				GlStateManager.enableAlpha();
			}

			GlStateManager.enableCull();
			GlStateManager.enableTexture2D();

			if ( Minecraft.isAmbientOcclusionEnabled() )
			{
				GlStateManager.shadeModel( GL11.GL_SMOOTH );
			}
			else
			{
				GlStateManager.shadeModel( GL11.GL_FLAT );
			}
		}
	}

	private void unconfigureGLState()
	{
		isConfigured--;

		if ( isConfigured > 0 )
		{
			return;
		}

		GlStateManager.resetColor(); // required to be called after drawing the
										// display list cause the post render
										// method usually calls it.

		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();

		RenderHelper.enableStandardItemLighting();
	}

	@Override
	public void renderTileEntityAt(
			final TileEntityBlockChiseledTESR te,
			final double x,
			final double y,
			final double z,
			final float partialTicks,
			final int destroyStage )
	{
		if ( destroyStage > 0 )
		{
			renderTileEntityFast( te, x, y, z, partialTicks, destroyStage, null );
		}
	}

}
