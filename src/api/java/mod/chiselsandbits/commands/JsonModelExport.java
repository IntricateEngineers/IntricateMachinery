package mod.chiselsandbits.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mod.chiselsandbits.chiseledblock.ItemBlockChiseled;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.ModUtil;
import mod.chiselsandbits.render.helpers.ModelQuadReader;
import mod.chiselsandbits.render.helpers.ModelUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;

public class JsonModelExport extends CommandBase
{
	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public String getCommandName()
	{
		return "c&b.exportmodel";
	}

	@Override
	public String getCommandUsage(
			final ICommandSender sender )
	{
		return "chiselsandbits.commands.exportjsonmodel.usage";
	}

	@Override
	public void execute(
			final MinecraftServer server,
			final ICommandSender sender,
			final String[] args ) throws CommandException
	{
		final EntityPlayer player = ClientSide.instance.getPlayer();
		final ItemStack is = player.getHeldItemMainhand();
		if ( is != null && is.getItem() != null )
		{
			IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel( is );

			// handle overrides.
			model = model.getOverrides().handleItemState( model, is, player.getEntityWorld(), player );

			final Map<TextureAtlasSprite, String> textures = new HashMap<TextureAtlasSprite, String>();

			for ( final EnumFacing face : EnumFacing.VALUES )
			{
				outputFaces( model.getQuads( null, face, 0 ), face, textures );
			}

			outputFaces( model.getQuads( null, null, 0 ), null, textures );

			String data = "N/A";

			if ( is.getItem() instanceof ItemBlockChiseled && is.hasTagCompound() )
			{
				final VoxelBlob blob = ModUtil.getBlobFromStack( is, null );

				final byte[] bd = blob.blobToBytes( VoxelBlob.VERSION_CROSSWORLD );
				data = Arrays.toString( bd );
			}

			final StringBuilder output = new StringBuilder( "{ \"source\": \"" ).append( data ).append( "\",\n\"textures\": {" );
			for ( final TextureAtlasSprite s : textures.keySet() )
			{
				output.append( "\"" ).append( System.identityHashCode( s ) ).append( "\": \"" ).append( s.getIconName() ).append( "\",\n" );
			}

			if ( !textures.values().isEmpty() )
			{
				// delete line ending + comma.
				output.deleteCharAt( output.length() - 1 );
				output.deleteCharAt( output.length() - 1 );
			}

			output.append( "},\n\"elements\": [\n" );

			for ( final String json : textures.values() )
			{
				output.append( json );
			}

			if ( !textures.values().isEmpty() )
			{
				// delete line ending + comma.
				output.deleteCharAt( output.length() - 1 );
				output.deleteCharAt( output.length() - 1 );
			}

			output.append( "\n],\n\"display\": { \"thirdperson\": { \"rotation\": [ 10, -45, 170 ], \"translation\": [ 0, 1.5, -2.75 ], \"scale\": [ 0.375, 0.375, 0.375 ] } } }" );

			final String modelJSON = output.toString();
			GuiScreen.setClipboardString( modelJSON );
			sender.addChatMessage( new TextComponentString( "Json Posted to Clipboard" ) );
		}
		else
		{
			sender.addChatMessage( new TextComponentString( "No Item in Hand." ) );
		}
	}

	private void outputFaces(
			final List<BakedQuad> faceQuads,
			final EnumFacing cullFace,
			final Map<TextureAtlasSprite, String> textures )
	{
		for ( final BakedQuad quad : faceQuads )
		{
			try
			{
				final TextureAtlasSprite sprite = ModelUtil.findQuadTexture( quad );

				final ModelQuadReader mqr = new ModelQuadReader( "#" + System.identityHashCode( sprite ), sprite, quad.getFace(), cullFace );
				quad.pipe( mqr );
				final String newJSON = mqr.toString();

				String old = textures.get( sprite );
				if ( old == null )
				{
					old = "";
				}

				textures.put( sprite, old + newJSON );
			}
			catch ( final IllegalArgumentException e )
			{
				e.printStackTrace();
			}
			catch ( final IllegalAccessException e )
			{
				e.printStackTrace();
			}
			catch ( final NullPointerException e )
			{
				e.printStackTrace();
			}
		}
	}

}
