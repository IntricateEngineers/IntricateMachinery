package mod.chiselsandbits.core;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Stopwatch;

import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.ItemType;
import mod.chiselsandbits.bittank.BlockBitTank;
import mod.chiselsandbits.bittank.TileEntityBitTank;
import mod.chiselsandbits.bittank.TileEntitySpecialRenderBitTank;
import mod.chiselsandbits.chiseledblock.BlockBitInfo;
import mod.chiselsandbits.chiseledblock.BlockChiseled;
import mod.chiselsandbits.chiseledblock.ChiselTypeIterator;
import mod.chiselsandbits.chiseledblock.ItemBlockChiseled;
import mod.chiselsandbits.chiseledblock.NBTBlobConverter;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseledTESR;
import mod.chiselsandbits.chiseledblock.data.BitIterator;
import mod.chiselsandbits.chiseledblock.data.BitLocation;
import mod.chiselsandbits.chiseledblock.data.IntegerBox;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateReference;
import mod.chiselsandbits.client.BlockColorChisled;
import mod.chiselsandbits.client.CreativeClipboardTab;
import mod.chiselsandbits.client.ItemColorBits;
import mod.chiselsandbits.client.ItemColorChisled;
import mod.chiselsandbits.client.ModConflictContext;
import mod.chiselsandbits.client.UndoTracker;
import mod.chiselsandbits.client.gui.ChiselsAndBitsMenu;
import mod.chiselsandbits.client.gui.SpriteIconPositioning;
import mod.chiselsandbits.commands.JsonModelExport;
import mod.chiselsandbits.helpers.ChiselModeManager;
import mod.chiselsandbits.helpers.ChiselToolType;
import mod.chiselsandbits.helpers.LocalStrings;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.helpers.VoxelRegionSrc;
import mod.chiselsandbits.integration.mcmultipart.MCMultipartProxy;
import mod.chiselsandbits.interfaces.IItemScrollWheel;
import mod.chiselsandbits.interfaces.IPatternItem;
import mod.chiselsandbits.items.ItemChisel;
import mod.chiselsandbits.items.ItemChiseledBit;
import mod.chiselsandbits.network.NetworkRouter;
import mod.chiselsandbits.network.packets.PacketChisel;
import mod.chiselsandbits.network.packets.PacketRotateVoxelBlob;
import mod.chiselsandbits.registry.ModItems;
import mod.chiselsandbits.render.SmartModelManager;
import mod.chiselsandbits.render.chiseledblock.tesr.ChisledBlockRenderChunkTESR;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientSide
{

	private static final Random RANDOM = new Random();
	public static final ClientSide instance = new ClientSide();

	private final HashMap<ChiselMode, SpriteIconPositioning> chiselModeIcons = new HashMap<ChiselMode, SpriteIconPositioning>();
	private KeyBinding rotateCCW;
	private KeyBinding rotateCW;
	private KeyBinding undo;
	private KeyBinding redo;
	private KeyBinding modeMenu;
	private KeyBinding addToClipboard;
	private KeyBinding pickBit;
	private Stopwatch rotateTimer;

	public void preinit(
			final ChiselsAndBits mod )
	{
		ChiselsAndBits.registerWithBus( new SmartModelManager() );

		registerModels();
	}

	public void init(
			final ChiselsAndBits chiselsandbits )
	{
		ClientRegistry.bindTileEntitySpecialRenderer( TileEntityBlockChiseledTESR.class, new ChisledBlockRenderChunkTESR() );
		ClientRegistry.bindTileEntitySpecialRenderer( TileEntityBitTank.class, new TileEntitySpecialRenderBitTank() );

		for ( final ChiselMode mode : ChiselMode.values() )
		{
			mode.binding = registerKeybind( mode.string.toString(), 0, "itemGroup.chiselsandbits", ModConflictContext.HOLDING_CHISEL );
		}

		modeMenu = registerKeybind( "mod.chiselsandbits.other.mode", 56, "itemGroup.chiselsandbits", ModConflictContext.HOLDING_CHISEL );
		rotateCCW = registerKeybind( "mod.chiselsandbits.other.rotate.ccw", 0, "itemGroup.chiselsandbits", ModConflictContext.HOLDING_ROTATEABLE );
		rotateCW = registerKeybind( "mod.chiselsandbits.other.rotate.cw", 0, "itemGroup.chiselsandbits", ModConflictContext.HOLDING_ROTATEABLE );
		pickBit = registerKeybind( "mod.chiselsandbits.other.pickbit", 0, "itemGroup.chiselsandbits", ModConflictContext.HOLDING_ROTATEABLE );
		undo = registerKeybind( "mod.chiselsandbits.other.undo", 0, "itemGroup.chiselsandbits", KeyConflictContext.IN_GAME );
		redo = registerKeybind( "mod.chiselsandbits.other.redo", 0, "itemGroup.chiselsandbits", KeyConflictContext.IN_GAME );
		addToClipboard = registerKeybind( "mod.chiselsandbits.other.add_to_clipboard", 0, "itemGroup.chiselsandbits", KeyConflictContext.IN_GAME );

		ChiselsAndBits.registerWithBus( instance );

		ClientCommandHandler.instance.registerCommand( new JsonModelExport() );
	}

	private KeyBinding registerKeybind(
			final String bindingName,
			final int defaultKey,
			final String groupName,
			final IKeyConflictContext context )
	{
		final KeyBinding kb = new KeyBinding( bindingName, context, defaultKey, groupName );
		ClientRegistry.registerKeyBinding( kb );
		return kb;
	}

	public void postinit(
			final ChiselsAndBits mod )
	{
		final ModItems modItems = ChiselsAndBits.getItems();

		if ( modItems.itemBlockBit != null )
		{
			Minecraft.getMinecraft().getItemColors().registerItemColorHandler( new ItemColorBits(), modItems.itemBlockBit );
		}

		for ( final BlockChiseled blk : ChiselsAndBits.getBlocks().getConversions().values() )
		{
			final Item item = Item.getItemFromBlock( blk );
			Minecraft.getMinecraft().getItemColors().registerItemColorHandler( new ItemColorChisled(), item );
			Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler( new BlockColorChisled(), blk );
		}

	}

	public void registerModels()
	{
		final String modId = ChiselsAndBits.MODID;

		final ModItems modItems = ChiselsAndBits.getItems();

		registerMesh( modItems.itemChiselStone, 0, new ModelResourceLocation( new ResourceLocation( modId, "chisel_stone" ), "inventory" ) );
		registerMesh( modItems.itemChiselIron, 0, new ModelResourceLocation( new ResourceLocation( modId, "chisel_iron" ), "inventory" ) );
		registerMesh( modItems.itemChiselGold, 0, new ModelResourceLocation( new ResourceLocation( modId, "chisel_gold" ), "inventory" ) );
		registerMesh( modItems.itemChiselDiamond, 0, new ModelResourceLocation( new ResourceLocation( modId, "chisel_diamond" ), "inventory" ) );
		registerMesh( modItems.itemBitBag, 0, new ModelResourceLocation( new ResourceLocation( modId, "bit_bag" ), "inventory" ) );
		registerMesh( modItems.itemWrench, 0, new ModelResourceLocation( new ResourceLocation( modId, "wrench_wood" ), "inventory" ) );
		registerMesh( modItems.itemBitSawDiamond, 0, new ModelResourceLocation( new ResourceLocation( modId, "bitsaw_diamond" ), "inventory" ) );

		if ( modItems.itemPositiveprint != null )
		{
			ModelBakery.registerItemVariants( modItems.itemPositiveprint, new ResourceLocation( modId, "positiveprint" ), new ResourceLocation( modId, "positiveprint_written" ) );
			ModelLoader.setCustomMeshDefinition( modItems.itemPositiveprint, new ItemMeshDefinition() {

				@Override
				public ModelResourceLocation getModelLocation(
						final ItemStack stack )
				{
					return new ModelResourceLocation( new ResourceLocation( modId, stack.hasTagCompound() ? "positiveprint_written_preview" : "positiveprint" ), "inventory" );
				}

			} );
		}

		if ( modItems.itemNegativeprint != null )
		{
			ModelBakery.registerItemVariants( modItems.itemNegativeprint, new ResourceLocation( modId, "negativeprint" ), new ResourceLocation( modId, "negativeprint_written" ) );
			ModelLoader.setCustomMeshDefinition( modItems.itemNegativeprint, new ItemMeshDefinition() {

				@Override
				public ModelResourceLocation getModelLocation(
						final ItemStack stack )
				{
					return new ModelResourceLocation( new ResourceLocation( modId, stack.hasTagCompound() ? "negativeprint_written_preview" : "negativeprint" ), "inventory" );
				}

			} );
		}

		if ( modItems.itemMirrorprint != null )
		{
			ModelBakery.registerItemVariants( modItems.itemMirrorprint, new ResourceLocation( modId, "mirrorprint" ), new ResourceLocation( modId, "mirrorprint_written" ) );
			ModelLoader.setCustomMeshDefinition( modItems.itemMirrorprint, new ItemMeshDefinition() {

				@Override
				public ModelResourceLocation getModelLocation(
						final ItemStack stack )
				{
					return new ModelResourceLocation( new ResourceLocation( modId, stack.hasTagCompound() ? "mirrorprint_written_preview" : "mirrorprint" ), "inventory" );
				}

			} );
		}

		if ( modItems.itemBlockBit != null )
		{
			ModelLoader.setCustomMeshDefinition( modItems.itemBlockBit, new ItemMeshDefinition() {

				@Override
				public ModelResourceLocation getModelLocation(
						final ItemStack stack )
				{
					return new ModelResourceLocation( new ResourceLocation( modId, "block_bit" ), "inventory" );
				}

			} );
		}

		for ( final BlockChiseled blk : ChiselsAndBits.getBlocks().getConversions().values() )
		{
			final Item item = Item.getItemFromBlock( blk );
			registerMesh( item, 0, new ModelResourceLocation( new ResourceLocation( modId, "block_chiseled" ), "inventory" ) );
		}

		final BlockBitTank bitTank = ChiselsAndBits.getBlocks().blockBitTank;
		final Item bitTankItem = Item.getItemFromBlock( bitTank );
		final ModelResourceLocation bittank_item = new ModelResourceLocation( new ResourceLocation( modId, "bittank" ), "inventory" );

		registerMesh( bitTankItem, 0, bittank_item );
	}

	private void registerMesh(
			final Item item,
			final int meta,
			final ModelResourceLocation loctaion )
	{
		if ( item != null )
		{
			ModelLoader.setCustomModelResourceLocation( item, meta, loctaion );
		}
	}

	public static TextureAtlasSprite undoIcon;
	public static TextureAtlasSprite redoIcon;
	public static TextureAtlasSprite trashIcon;

	@SubscribeEvent
	void registerIconTextures(
			final TextureStitchEvent.Pre ev )
	{
		final TextureMap map = ev.getMap();
		undoIcon = map.registerSprite( new ResourceLocation( "chiselsandbits", "icons/undo" ) );
		redoIcon = map.registerSprite( new ResourceLocation( "chiselsandbits", "icons/redo" ) );
		trashIcon = map.registerSprite( new ResourceLocation( "chiselsandbits", "icons/trash" ) );

		for ( final ChiselMode mode : ChiselMode.values() )
		{
			final SpriteIconPositioning sip = new SpriteIconPositioning();

			final ResourceLocation sprite = new ResourceLocation( "chiselsandbits", "icons/" + mode.name().toLowerCase() );
			final ResourceLocation png = new ResourceLocation( "chiselsandbits", "textures/icons/" + mode.name().toLowerCase() + ".png" );

			sip.sprite = map.registerSprite( sprite );

			try
			{
				final IResource iresource = Minecraft.getMinecraft().getResourceManager().getResource( png );
				final BufferedImage bi = TextureUtil.readBufferedImage( iresource.getInputStream() );

				int bottom = 0;
				int right = 0;
				sip.left = bi.getWidth();
				sip.top = bi.getHeight();

				for ( int x = 0; x < bi.getWidth(); x++ )
				{
					for ( int y = 0; y < bi.getHeight(); y++ )
					{
						final int color = bi.getRGB( x, y );
						final int a = color >> 24 & 0xff;
						if ( a > 0 )
						{
							sip.left = Math.min( sip.left, x );
							right = Math.max( right, x );

							sip.top = Math.min( sip.top, y );
							bottom = Math.max( bottom, y );
						}
					}
				}

				sip.height = bottom - sip.top + 1;
				sip.width = right - sip.left + 1;

				sip.left /= bi.getWidth();
				sip.width /= bi.getWidth();
				sip.top /= bi.getHeight();
				sip.height /= bi.getHeight();
			}
			catch ( final IOException e )
			{
				sip.height = 1;
				sip.width = 1;
				sip.left = 0;
				sip.top = 0;
			}

			chiselModeIcons.put( mode, sip );
		}
	}

	public SpriteIconPositioning getIconForMode(
			final ChiselMode mode )
	{
		return chiselModeIcons.get( mode );
	}

	@SubscribeEvent
	public void onRenderGUI(
			final RenderGameOverlayEvent.Post event )
	{
		final ChiselToolType tool = getHeldToolType();
		final ElementType type = event.getType();
		if ( type == ElementType.ALL && tool != null )
		{
			final boolean wasVisible = ChiselsAndBitsMenu.instance.isVisible();

			if ( modeMenu.isKeyDown() )
			{
				ChiselsAndBitsMenu.instance.actionUsed = false;
				ChiselsAndBitsMenu.instance.raiseVisibility();
			}
			else
			{
				if ( !ChiselsAndBitsMenu.instance.actionUsed )
				{
					if ( ChiselsAndBitsMenu.instance.switchTo != null )
					{
						ChiselModeManager.changeChiselMode( tool, ChiselModeManager.getChiselMode( getPlayer(), tool ), ChiselsAndBitsMenu.instance.switchTo );
					}

					if ( ChiselsAndBitsMenu.instance.doAction != null )
					{
						switch ( ChiselsAndBitsMenu.instance.doAction )
						{
							case UNDO:
								UndoTracker.getInstance().undo();
								break;

							case REDO:
								UndoTracker.getInstance().redo();
								break;
						}
					}
				}

				ChiselsAndBitsMenu.instance.actionUsed = true;
				ChiselsAndBitsMenu.instance.decreaseVisibility();
			}

			if ( ChiselsAndBitsMenu.instance.isVisible() )
			{
				final ScaledResolution res = event.getResolution();
				ChiselsAndBitsMenu.instance.configure( res.getScaledWidth(), res.getScaledHeight() );

				if ( wasVisible == false )
				{
					ChiselsAndBitsMenu.instance.mc.inGameHasFocus = false;
					ChiselsAndBitsMenu.instance.mc.mouseHelper.ungrabMouseCursor();
				}

				if ( ChiselsAndBitsMenu.instance.mc.inGameHasFocus )
				{
					KeyBinding.unPressAllKeys();
				}

				final int k1 = Mouse.getX() * res.getScaledWidth() / ChiselsAndBitsMenu.instance.mc.displayWidth;
				final int l1 = res.getScaledHeight() - Mouse.getY() * res.getScaledHeight() / ChiselsAndBitsMenu.instance.mc.displayHeight - 1;

				net.minecraftforge.client.ForgeHooksClient.drawScreen( ChiselsAndBitsMenu.instance, k1, l1, event.getPartialTicks() );
			}
			else
			{
				if ( wasVisible )
				{
					ChiselsAndBitsMenu.instance.mc.setIngameFocus();
				}
			}
		}

		if ( undo.isPressed() )
		{
			UndoTracker.getInstance().undo();
		}

		if ( redo.isPressed() )
		{
			UndoTracker.getInstance().redo();
		}

		if ( addToClipboard.isPressed() )
		{
			final Minecraft mc = Minecraft.getMinecraft();
			if ( mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK )
			{
				try
				{
					final IBitAccess access = ChiselsAndBits.getApi().getBitAccess( mc.theWorld, mc.objectMouseOver.getBlockPos() );
					final ItemStack is = access.getBitsAsItem( null, ItemType.CHISLED_BLOCK, false );

					CreativeClipboardTab.addItem( is );
				}
				catch ( final CannotBeChiseled e )
				{
					// nope.
				}
			}
		}

		if ( pickBit.isPressed() )
		{
			final Minecraft mc = Minecraft.getMinecraft();
			if ( mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK )
			{
				try
				{
					final BitLocation bl = new BitLocation( mc.objectMouseOver, true, ChiselToolType.CHISEL );
					final IBitAccess access = ChiselsAndBits.getApi().getBitAccess( mc.theWorld, bl.getBlockPos() );
					final IBitBrush brush = access.getBitAt( bl.getBitX(), bl.getBitY(), bl.getBitZ() );
					if ( brush != null )
					{
						final ItemStack is = brush.getItemStack( 1 );
						doPick( is );
					}
				}
				catch ( final CannotBeChiseled e )
				{
					// nope.
				}
			}
		}

		if ( type == ElementType.HOTBAR && ChiselsAndBits.getConfig().enableToolbarIcons )
		{
			final Minecraft mc = Minecraft.getMinecraft();
			final ScaledResolution res = event.getResolution();

			if ( !mc.thePlayer.isSpectator() )
			{
				final GuiIngame sc = mc.ingameGUI;

				for ( int slot = 0; slot < 9; ++slot )
				{
					final ItemStack stack = mc.thePlayer.inventory.mainInventory[slot];
					if ( stack != null && stack.getItem() instanceof ItemChisel )
					{
						ChiselMode mode = ChiselMode.getMode( stack );

						if ( !ChiselsAndBits.getConfig().perChiselMode )
						{
							mode = ChiselModeManager.getChiselMode( mc.thePlayer, ChiselToolType.CHISEL );
						}

						final int x = res.getScaledWidth() / 2 - 90 + slot * 20 + 2;
						final int y = res.getScaledHeight() - 16 - 3;

						GlStateManager.color( 1, 1, 1, 1.0f );
						Minecraft.getMinecraft().getTextureManager().bindTexture( TextureMap.LOCATION_BLOCKS_TEXTURE );
						final TextureAtlasSprite sprite = chiselModeIcons.get( mode ).sprite;

						GlStateManager.enableBlend();
						sc.drawTexturedModalRect( x + 1, y + 1, sprite, 8, 8 );
						GlStateManager.disableBlend();
					}
				}
			}
		}
	}

	private boolean doPick(
			final ItemStack result )
	{
		final EntityPlayer player = getPlayer();

		for ( int x = 0; x < 9; x++ )
		{
			final ItemStack stack = player.inventory.getStackInSlot( x );
			if ( stack != null && stack.isItemEqual( result ) && ItemStack.areItemStackTagsEqual( stack, result ) )
			{
				player.inventory.currentItem = x;
				return true;
			}
		}

		if ( !player.capabilities.isCreativeMode )
		{
			return false;
		}

		int slot = player.inventory.getFirstEmptyStack();
		if ( slot < 0 || slot >= 9 )
		{
			slot = player.inventory.currentItem;
		}

		// update inventory..
		player.inventory.setInventorySlotContents( slot, result );
		player.inventory.currentItem = slot;

		// update server...
		final int j = player.inventoryContainer.inventorySlots.size() - 9 + player.inventory.currentItem;
		Minecraft.getMinecraft().playerController.sendSlotPacket( player.inventory.getStackInSlot( player.inventory.currentItem ), j );
		return true;
	}

	public ChiselToolType getHeldToolType()
	{
		final EntityPlayer player = getPlayer();

		if ( player == null )
		{
			return null;
		}

		final ItemStack is = player.getHeldItemMainhand();

		if ( is != null && is.getItem() instanceof ItemChisel )
		{
			return ChiselToolType.CHISEL;
		}

		if ( is != null && is.getItem() instanceof ItemChiseledBit )
		{
			return ChiselToolType.BIT;
		}

		return null;
	}

	@SubscribeEvent
	public void interaction(
			final TickEvent.ClientTickEvent event )
	{
		// used to prevent hyper chisels.. its actually far worse then you might
		// think...
		if ( event.side == Side.CLIENT && event.type == Type.CLIENT && event.phase == Phase.START && !Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown() )
		{
			ItemChisel.resetDelay();
		}

		if ( !getToolKey().isKeyDown() )
		{
			if ( loopDeath )
			{
				drawStart = null;
				lastTool = ChiselToolType.CHISEL;
			}
			else
			{
				loopDeath = true;
			}
		}
		else
		{
			loopDeath = false;
		}

		if ( rotateCCW.isKeyDown() )
		{
			if ( rotateTimer == null || rotateTimer.elapsed( TimeUnit.MILLISECONDS ) > 200 )
			{
				rotateTimer = Stopwatch.createStarted();
				final PacketRotateVoxelBlob p = new PacketRotateVoxelBlob();
				p.rotationDirection = 1;
				NetworkRouter.instance.sendToServer( p );
			}
		}

		if ( rotateCW.isKeyDown() )
		{
			if ( rotateTimer == null || rotateTimer.elapsed( TimeUnit.MILLISECONDS ) > 200 )
			{
				rotateTimer = Stopwatch.createStarted();
				final PacketRotateVoxelBlob p = new PacketRotateVoxelBlob();
				p.rotationDirection = -1;
				NetworkRouter.instance.sendToServer( p );
			}
		}

		for ( final ChiselMode mode : ChiselMode.values() )
		{
			final KeyBinding kb = (KeyBinding) mode.binding;
			if ( kb.isKeyDown() )
			{
				final ChiselToolType tool = getHeldToolType();
				ChiselModeManager.changeChiselMode( tool, ChiselModeManager.getChiselMode( getPlayer(), tool ), mode );
			}
		}
	}

	@SubscribeEvent
	@SideOnly( Side.CLIENT )
	public void drawHighlight(
			final DrawBlockHighlightEvent event )
	{
		ChiselToolType tool = getHeldToolType();
		final ChiselMode chMode = ChiselModeManager.getChiselMode( getPlayer(), tool );
		if ( chMode == ChiselMode.DRAWN_REGION )
		{
			tool = lastTool;
		}

		if ( tool != null && chMode != null )
		{
			final EntityPlayer player = event.getPlayer();
			final float partialTicks = event.getPartialTicks();
			final RayTraceResult mop = Minecraft.getMinecraft().objectMouseOver;
			final World theWorld = player.worldObj;

			if ( mop.typeOfHit != RayTraceResult.Type.BLOCK )
			{
				return;
			}

			boolean showBox = false;
			if ( mop.typeOfHit == RayTraceResult.Type.BLOCK )
			{
				final BitLocation location = new BitLocation( mop, true, getDrawnTool() );
				if ( theWorld.getWorldBorder().contains( location.blockPos ) )
				{
					// this logic originated in the vanilla bounding box...
					final IBlockState state = theWorld.getBlockState( location.blockPos );

					final boolean isChisel = getDrawnTool() == ChiselToolType.CHISEL;
					final boolean isBit = getHeldToolType() == ChiselToolType.BIT;
					final TileEntityBlockChiseled data = ModUtil.getChiseledTileEntity( theWorld, location.blockPos, false );

					final VoxelRegionSrc region = new VoxelRegionSrc( theWorld, location.blockPos, 1 );
					final VoxelBlob vb = data != null ? data.getBlob() : new VoxelBlob();

					if ( isChisel && data == null )
					{
						showBox = true;
						vb.fill( 1 );
					}

					final BitLocation other = getStartPos();
					if ( chMode == ChiselMode.DRAWN_REGION && other != null )
					{
						final ChiselTypeIterator oneEnd = new ChiselTypeIterator( VoxelBlob.dim, location.bitX, location.bitY, location.bitZ, VoxelBlob.NULL_BLOB, ChiselMode.SINGLE, EnumFacing.UP );
						final ChiselTypeIterator otherEnd = new ChiselTypeIterator( VoxelBlob.dim, other.bitX, other.bitY, other.bitZ, VoxelBlob.NULL_BLOB, ChiselMode.SINGLE, EnumFacing.UP );

						final AxisAlignedBB a = oneEnd.getBoundingBox( VoxelBlob.NULL_BLOB, false ).offset( location.blockPos.getX(), location.blockPos.getY(), location.blockPos.getZ() );
						final AxisAlignedBB b = otherEnd.getBoundingBox( VoxelBlob.NULL_BLOB, false ).offset( other.blockPos.getX(), other.blockPos.getY(), other.blockPos.getZ() );

						final AxisAlignedBB bb = a.union( b );

						final double maxChiseSize = ChiselsAndBits.getConfig().maxDrawnRegionSize + 0.001;
						if ( bb.maxX - bb.minX <= maxChiseSize && bb.maxY - bb.minY <= maxChiseSize && bb.maxZ - bb.minZ <= maxChiseSize )
						{
							drawSelectionBoundingBoxIfExists( bb, BlockPos.ORIGIN, player, partialTicks, false );

							if ( !getToolKey().isKeyDown() )
							{
								final PacketChisel pc = new PacketChisel( lastTool == ChiselToolType.BIT, location, other, EnumFacing.UP, chMode, EnumHand.MAIN_HAND );

								if ( pc.doAction( getPlayer() ) > 0 )
								{
									NetworkRouter.instance.sendToServer( pc );
									ClientSide.placeSound( theWorld, location.blockPos, 0 );
								}

								drawStart = null;
								lastTool = ChiselToolType.CHISEL;
							}
						}
					}
					else
					{
						final TileEntity te = theWorld.getTileEntity( location.blockPos );
						boolean isBitBlock = te instanceof TileEntityBlockChiseled;
						final boolean isBlockSupported = BlockBitInfo.supportsBlock( state );

						if ( !( isBitBlock || isBlockSupported ) )
						{
							final TileEntityBlockChiseled tebc = ModUtil.getChiseledTileEntity( theWorld, location.blockPos, false );
							if ( tebc != null )
							{
								final VoxelBlob vx = tebc.getBlob();
								if ( vx.get( location.bitX, location.bitY, location.bitZ ) != 0 )
								{
									isBitBlock = true;
								}
							}
						}

						if ( theWorld.isAirBlock( location.blockPos ) || isBitBlock || isBlockSupported )
						{
							final ChiselTypeIterator i = new ChiselTypeIterator( VoxelBlob.dim, location.bitX, location.bitY, location.bitZ, region, chMode, mop.sideHit );
							final AxisAlignedBB bb = i.getBoundingBox( vb, isChisel );
							drawSelectionBoundingBoxIfExists( bb, location.blockPos, player, partialTicks, false );
							showBox = false;
						}
						else if ( isBit )
						{
							final VoxelBlob j = new VoxelBlob();
							j.fill( 1 );
							final ChiselTypeIterator i = new ChiselTypeIterator( VoxelBlob.dim, location.bitX, location.bitY, location.bitZ, j, chMode, mop.sideHit );
							final AxisAlignedBB bb = snapToSide( i.getBoundingBox( j, isChisel ), mop.sideHit );
							drawSelectionBoundingBoxIfExists( bb, location.blockPos, player, partialTicks, false );
						}
					}
				}

				if ( !showBox )
				{
					event.setCanceled( true );
				}

			}
		}
	}

	private AxisAlignedBB snapToSide(
			final AxisAlignedBB boundingBox,
			final EnumFacing sideHit )
	{
		if ( boundingBox != null )
		{
			switch ( sideHit )
			{
				case DOWN:
					return new AxisAlignedBB( boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.minY, boundingBox.maxZ );
				case EAST:
					return new AxisAlignedBB( boundingBox.maxX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ );
				case NORTH:
					return new AxisAlignedBB( boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.minZ );
				case SOUTH:
					return new AxisAlignedBB( boundingBox.minX, boundingBox.minY, boundingBox.maxZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ );
				case UP:
					return new AxisAlignedBB( boundingBox.minX, boundingBox.maxY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ );
				case WEST:
					return new AxisAlignedBB( boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.minX, boundingBox.maxY, boundingBox.maxZ );
				default:
					break;
			}
		}

		return boundingBox;
	}

	private void drawSelectionBoundingBoxIfExists(
			final AxisAlignedBB bb,
			final BlockPos blockPos,
			final EntityPlayer player,
			final float partialTicks,
			final boolean NormalBoundingBox )
	{
		if ( bb != null )
		{
			final double x = player.lastTickPosX + ( player.posX - player.lastTickPosX ) * partialTicks;
			final double y = player.lastTickPosY + ( player.posY - player.lastTickPosY ) * partialTicks;
			final double z = player.lastTickPosZ + ( player.posZ - player.lastTickPosZ ) * partialTicks;

			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0 );
			GlStateManager.color( 0.0F, 0.0F, 0.0F, 0.4F );
			GL11.glLineWidth( 2.0F );
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask( false );

			if ( !NormalBoundingBox )
			{
				RenderGlobal.drawSelectionBoundingBox( bb.expand( 0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D ).offset( -x + blockPos.getX(), -y + blockPos.getY(), -z + blockPos.getZ() ) );
			}

			GlStateManager.disableDepth();
			GlStateManager.color( 0.0F, 0.0F, 0.0F, 0.1F );

			RenderGlobal.drawSelectionBoundingBox( bb.expand( 0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D ).offset( -x + blockPos.getX(), -y + blockPos.getY(), -z + blockPos.getZ() ) );

			GlStateManager.enableDepth();
			GlStateManager.depthMask( true );
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
		}
	}

	@SubscribeEvent
	@SideOnly( Side.CLIENT )
	public void drawLast(
			final RenderWorldLastEvent event )
	{
		// important and used for tesr / block rendering.
		++lastRenderedFrame;

		if ( Minecraft.getMinecraft().gameSettings.hideGUI )
		{
			return;
		}

		// now render the ghosts...
		final EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		final float partialTicks = event.getPartialTicks();
		final RayTraceResult mop = Minecraft.getMinecraft().objectMouseOver;
		final World theWorld = player.worldObj;
		final ItemStack currentItem = player.getHeldItemMainhand();

		final double x = player.lastTickPosX + ( player.posX - player.lastTickPosX ) * partialTicks;
		final double y = player.lastTickPosY + ( player.posY - player.lastTickPosY ) * partialTicks;
		final double z = player.lastTickPosZ + ( player.posZ - player.lastTickPosZ ) * partialTicks;

		if ( mop == null )
		{
			return;
		}

		if ( ModUtil.isHoldingPattern( player ) )
		{
			if ( mop.typeOfHit != RayTraceResult.Type.BLOCK )
			{
				return;
			}

			final IBlockState s = theWorld.getBlockState( mop.getBlockPos() );
			if ( !( s.getBlock() instanceof BlockChiseled ) && !BlockBitInfo.supportsBlock( s ) && !MCMultipartProxy.proxyMCMultiPart.isMultiPartTileEntity( theWorld, mop.getBlockPos() ) )
			{
				return;
			}

			if ( !currentItem.hasTagCompound() )
			{
				return;
			}

			final ItemStack item = ChiselsAndBits.getItems().itemNegativeprint.getPatternedItem( currentItem );
			if ( !item.hasTagCompound() )
			{
				return;
			}

			final int rotations = ModUtil.getRotations( player, ModUtil.getItemRotation( currentItem ) );

			if ( item != null )
			{
				final TileEntityBlockChiseled tebc = ModUtil.getChiseledTileEntity( theWorld, mop.getBlockPos(), false );
				Object cacheRef = tebc != null ? tebc : s;
				if ( cacheRef instanceof TileEntityBlockChiseled )
				{
					cacheRef = ( (TileEntityBlockChiseled) cacheRef ).getBlobStateReference();
				}

				GlStateManager.depthFunc( GL11.GL_ALWAYS );
				showGhost( currentItem, item, mop.getBlockPos(), player, rotations, x, y, z, mop.sideHit, null, cacheRef );
				GlStateManager.depthFunc( GL11.GL_LEQUAL );
			}
		}
		else if ( ModUtil.isHoldingChiseledBlock( player ) )
		{
			if ( mop.typeOfHit != RayTraceResult.Type.BLOCK )
			{
				return;
			}

			final ItemStack item = currentItem;
			if ( !item.hasTagCompound() )
			{
				return;
			}

			final int rotations = ModUtil.getRotations( player, ModUtil.getItemRotation( item ) );
			final BlockPos offset = mop.getBlockPos();

			if ( player.isSneaking() )
			{
				final BitLocation bl = new BitLocation( mop, true, ChiselToolType.BIT );
				showGhost( currentItem, item, bl.blockPos, player, rotations, x, y, z, mop.sideHit, new BlockPos( bl.bitX, bl.bitY, bl.bitZ ), null );
			}
			else
			{
				boolean canMerge = false;
				if ( currentItem.hasTagCompound() )
				{
					final TileEntityBlockChiseled tebc = ModUtil.getChiseledTileEntity( theWorld, offset, true );

					if ( tebc != null )
					{
						final VoxelBlob blob = ModUtil.getBlobFromStack( currentItem, player );
						canMerge = tebc.canMerge( blob );
					}
				}

				BlockPos newOffset = offset;
				final Block block = theWorld.getBlockState( newOffset ).getBlock();
				if ( !canMerge && !player.isSneaking() && !block.isReplaceable( theWorld, newOffset ) )
				{
					newOffset = offset.offset( mop.sideHit );
				}

				final TileEntity newTarget = theWorld.getTileEntity( newOffset );

				if ( theWorld.isAirBlock( newOffset ) || theWorld.getBlockState( newOffset ).getBlock().isReplaceable( theWorld, newOffset ) || newTarget instanceof TileEntityBlockChiseled
						|| MCMultipartProxy.proxyMCMultiPart.isMultiPartTileEntity( theWorld, newOffset ) )
				{
					final TileEntityBlockChiseled test = ModUtil.getChiseledTileEntity( theWorld, newOffset, false );
					showGhost( currentItem, item, newOffset, player, rotations, x, y, z, mop.sideHit, null, test == null ? null : test.getBlobStateReference() );
				}
			}
		}
	}

	private ItemStack previousItem;
	private int previousRotations;
	private Object previousModel;
	private Object previousCacheRef;
	private IntegerBox modelBounds;
	private boolean isVisible = true;
	private BlockPos lastPartial;
	private BlockPos lastPos;

	private void showGhost(
			final ItemStack refItem,
			final ItemStack item,
			final BlockPos blockPos,
			final EntityPlayer player,
			final int rotationCount,
			final double x,
			final double y,
			final double z,
			final EnumFacing side,
			final BlockPos partial,
			final Object cacheRef )
	{
		IBakedModel baked = null;

		if ( previousCacheRef == cacheRef && samePos( lastPos, blockPos ) && previousItem == refItem && previousRotations == rotationCount && previousModel != null && samePos( lastPartial, partial ) )
		{
			baked = (IBakedModel) previousModel;
		}
		else
		{
			int rotations = rotationCount;

			previousItem = refItem;
			previousRotations = rotations;
			previousCacheRef = cacheRef;
			lastPos = blockPos;
			lastPartial = partial;

			final NBTBlobConverter c = new NBTBlobConverter();
			c.readChisleData( item.getSubCompound( ItemBlockChiseled.NBT_CHISELED_DATA, false ) );
			VoxelBlob blob = c.getBlob();

			while ( rotations-- > 0 )
			{
				blob = blob.spin( Axis.Y );
			}

			modelBounds = blob.getBounds();

			fail: if ( refItem.getItem() == ChiselsAndBits.getItems().itemNegativeprint )
			{
				final VoxelBlob pattern = blob;

				if ( cacheRef instanceof VoxelBlobStateReference )
				{
					blob = ( (VoxelBlobStateReference) cacheRef ).getVoxelBlob();
				}
				else if ( cacheRef instanceof IBlockState )
				{
					blob = new VoxelBlob();
					blob.fill( Block.getStateId( (IBlockState) cacheRef ) );
				}
				else
				{
					break fail;
				}

				final BitIterator it = new BitIterator();
				while ( it.hasNext() )
				{
					if ( it.getNext( pattern ) == 0 )
					{
						it.setNext( blob, 0 );
					}
				}
			}

			c.setBlob( blob );

			final Block blk = Block.getBlockFromItem( item.getItem() );
			final ItemStack is = c.getItemStack( false );

			if ( is == null || is.getItem() == null )
			{
				isVisible = false;
			}
			else
			{
				baked = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel( is );
				previousModel = baked = baked.getOverrides().handleItemState( baked, is, player.getEntityWorld(), player );

				if ( refItem.getItem() instanceof IPatternItem )
				{
					isVisible = true;
				}
				else
				{
					isVisible = ItemBlockChiseled.tryPlaceBlockAt( blk, item, player, player.getEntityWorld(), blockPos, side, partial, false );
				}
			}
		}

		if ( !isVisible )
		{
			return;
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate( blockPos.getX() - x, blockPos.getY() - y, blockPos.getZ() - z );
		if ( partial != null )
		{
			final BlockPos t = ModUtil.getPartialOffset( side, partial, modelBounds );
			final double fullScale = 1.0 / VoxelBlob.dim;
			GlStateManager.translate( t.getX() * fullScale, t.getY() * fullScale, t.getZ() * fullScale );
		}

		GlStateManager.bindTexture( Minecraft.getMinecraft().getTextureMapBlocks().getGlTextureId() );
		GlStateManager.color( 1.0f, 1.0f, 1.0f, 0.1f );
		GlStateManager.enableBlend();
		GlStateManager.enableTexture2D();
		GlStateManager.blendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
		GlStateManager.colorMask( false, false, false, false );

		renderModel( baked, player.worldObj, blockPos );
		GlStateManager.colorMask( true, true, true, true );
		GlStateManager.depthFunc( GL11.GL_LEQUAL );
		renderModel( baked, player.worldObj, blockPos );

		GlStateManager.color( 1.0f, 1.0f, 1.0f, 1.0f );
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	private void renderQuads(
			final VertexBuffer renderer,
			final List<BakedQuad> quads,
			final World worldObj,
			final BlockPos blockPos )
	{
		int i = 0;
		for ( final int j = quads.size(); i < j; ++i )
		{
			final BakedQuad bakedquad = quads.get( i );
			final int color = bakedquad.getTintIndex() == -1 ? 0xaaffffff : getTint( bakedquad.getTintIndex(), worldObj, blockPos );
			net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor( renderer, bakedquad, color );
		}
	}

	private int getTint(
			final int tintIndex,
			final World worldObj,
			final BlockPos blockPos )
	{
		return 0xaa000000 | Minecraft.getMinecraft().getBlockColors().colorMultiplier( ChiselsAndBits.getBlocks().getChiseledDefaultState(), worldObj, blockPos, tintIndex );
	}

	private void renderModel(
			final IBakedModel model,
			final World worldObj,
			final BlockPos blockPos )
	{
		final Tessellator tessellator = Tessellator.getInstance();
		final VertexBuffer worldrenderer = tessellator.getBuffer();
		worldrenderer.begin( 7, DefaultVertexFormats.ITEM );

		for ( final EnumFacing enumfacing : EnumFacing.values() )
		{
			renderQuads( worldrenderer, model.getQuads( null, enumfacing, 0 ), worldObj, blockPos );
		}

		renderQuads( worldrenderer, model.getQuads( null, null, 0 ), worldObj, blockPos );
		tessellator.draw();
	}

	private boolean samePos(
			final BlockPos lastPartial2,
			final BlockPos partial )
	{
		if ( lastPartial2 == partial )
		{
			return true;
		}

		if ( lastPartial2 == null || partial == null )
		{
			return false;
		}

		return partial.equals( lastPartial2 );
	}

	public EntityPlayer getPlayer()
	{
		return Minecraft.getMinecraft().thePlayer;
	}

	public boolean addHitEffects(
			final World world,
			final RayTraceResult target,
			final IBlockState state,
			final ParticleManager effectRenderer )
	{
		final ItemStack hitWith = getPlayer().getHeldItemMainhand();
		if ( hitWith != null && ( hitWith.getItem() instanceof ItemChisel || hitWith.getItem() instanceof ItemChiseledBit ) )
		{
			return true; // no
			// effects!
		}

		final BlockPos pos = target.getBlockPos();
		final float boxOffset = 0.1F;

		AxisAlignedBB bb = world.getBlockState( pos ).getBlock().getSelectedBoundingBox( state, world, pos );

		if ( bb == null )
		{
			bb = new AxisAlignedBB( pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1 );
		}

		double x = RANDOM.nextDouble() * ( bb.maxX - bb.minX - boxOffset * 2.0F ) + boxOffset + bb.minX;
		double y = RANDOM.nextDouble() * ( bb.maxY - bb.minY - boxOffset * 2.0F ) + boxOffset + bb.minY;
		double z = RANDOM.nextDouble() * ( bb.maxZ - bb.minZ - boxOffset * 2.0F ) + boxOffset + bb.minZ;

		switch ( target.sideHit )
		{
			case DOWN:
				y = bb.minY - boxOffset;
				break;
			case EAST:
				x = bb.maxX + boxOffset;
				break;
			case NORTH:
				z = bb.minZ - boxOffset;
				break;
			case SOUTH:
				z = bb.maxZ + boxOffset;
				break;
			case UP:
				y = bb.maxY + boxOffset;
				break;
			case WEST:
				x = bb.minX - boxOffset;
				break;
			default:
				break;

		}

		final Particle fx = effectRenderer.spawnEffectParticle( EnumParticleTypes.BLOCK_DUST.getParticleID(), x, y, z, 0.0D, 0.0D, 0.0D, new int[] { Block.getStateId( state ) } );

		if ( fx != null )
		{
			fx.multiplyVelocity( 0.2F ).multipleParticleScaleBy( 0.6F );
		}

		return true;
	}

	@SubscribeEvent
	public void wheelEvent(
			final MouseEvent me )
	{
		final int dwheel = me.getDwheel();
		if ( me.isCanceled() || dwheel == 0 )
		{
			return;
		}

		final EntityPlayer player = ClientSide.instance.getPlayer();
		final ItemStack is = player.getHeldItemMainhand();

		if ( dwheel != 0 && is != null && is.getItem() instanceof IItemScrollWheel && player.isSneaking() )
		{
			( (IItemScrollWheel) is.getItem() ).scroll( player, is, dwheel );
			me.setCanceled( true );
		}
	}

	public static void placeSound(
			final World world,
			final BlockPos pos,
			final int stateID )
	{
		final IBlockState state = Block.getStateById( stateID );
		final Block block = state.getBlock();
		world.playSound( pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, block.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, ( block.getSoundType().getVolume() + 1.0F ) / 16.0F, block.getSoundType().getPitch() * 0.9F, false );
	}

	public static void breakSound(
			final World world,
			final BlockPos pos,
			final int extractedState )
	{
		final IBlockState state = Block.getStateById( extractedState );
		final Block block = state.getBlock();
		world.playSound( pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, block.getSoundType().getBreakSound(), SoundCategory.BLOCKS, ( block.getSoundType().getVolume() + 1.0F ) / 16.0F, block.getSoundType().getPitch() * 0.9F, false );
	}

	private BitLocation drawStart;
	private boolean loopDeath = false;
	private int lastRenderedFrame = Integer.MIN_VALUE;

	public int getLastRenderedFrame()
	{
		return lastRenderedFrame;
	}

	public BitLocation getStartPos()
	{
		return drawStart;
	}

	public void pointAt(
			final ChiselToolType type,
			final BitLocation pos )
	{
		if ( drawStart == null )
		{
			drawStart = pos;
			lastTool = type;
		}
	}

	ChiselToolType lastTool = ChiselToolType.CHISEL;

	KeyBinding getToolKey()
	{
		if ( lastTool == ChiselToolType.CHISEL )
		{
			return Minecraft.getMinecraft().gameSettings.keyBindAttack;
		}
		else
		{
			return Minecraft.getMinecraft().gameSettings.keyBindUseItem;
		}
	}

	public boolean addBlockDestroyEffects(
			final World world,
			final BlockPos pos,
			IBlockState state,
			final ParticleManager effectRenderer )
	{
		if ( !state.getBlock().isAir( state, world, pos ) )
		{
			state = state.getBlock().getActualState( state, world, pos );
			final int StateID = Block.getStateId( state );

			final int i = 4;

			for ( int j = 0; j < i; ++j )
			{
				for ( int k = 0; k < i; ++k )
				{
					for ( int l = 0; l < i; ++l )
					{
						final double d0 = pos.getX() + ( j + 0.5D ) / i;
						final double d1 = pos.getY() + ( k + 0.5D ) / i;
						final double d2 = pos.getZ() + ( l + 0.5D ) / i;
						effectRenderer.spawnEffectParticle( EnumParticleTypes.BLOCK_CRACK.getParticleID(), d0, d1, d2, d0 - pos.getX() - 0.5D, d1 - pos.getY() - 0.5D, d2 - pos.getZ() - 0.5D, StateID );
					}
				}
			}
		}

		return true;
	}

	public TextureAtlasSprite getMissingIcon()
	{
		return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
	}

	public String getModeKey()
	{
		return GameSettings.getKeyDisplayString( modeMenu.getKeyCode() ).replace( "LMENU", LocalStrings.leftAlt.getLocal() ).replace( "RMENU", LocalStrings.rightAlt.getLocal() );
	}

	public ChiselToolType getDrawnTool()
	{
		return lastTool;
	}

	public boolean holdingShift()
	{
		return Keyboard.isKeyDown( Keyboard.KEY_LSHIFT ) || Keyboard.isKeyDown( Keyboard.KEY_RSHIFT );
	}

}
