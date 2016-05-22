package mod.chiselsandbits.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateReference;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.helpers.ActingPlayer;
import mod.chiselsandbits.interfaces.ICacheClearable;
import mod.chiselsandbits.network.NetworkRouter;
import mod.chiselsandbits.network.packets.PacketUndo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UndoTracker implements ICacheClearable
{

	private final static UndoTracker instance = new UndoTracker();

	public static UndoTracker getInstance()
	{
		return instance;
	}

	public UndoTracker()
	{
		ChiselsAndBits.getInstance().addClearable( this );
	}

	private int level = -1; // the current undo level.
	private boolean recording = true; // is the system currently recording?
	private boolean grouping = false; // is a group active?
	private boolean hasCreatedGroup = false; // did we add an item yet?

	private final List<UndoStep> undoLevels = new ArrayList<UndoStep>();

	// errors produced by operations are accumulated for display.
	private final Set<String> errors = new HashSet<String>();

	/**
	 * capture stack trace from whoever opened the undo group, for display
	 * later.
	 */
	private RuntimeException groupStarted;

	public void add(
			final World world,
			final BlockPos pos,
			final VoxelBlobStateReference before,
			final VoxelBlobStateReference after )
	{
		// servers don't track undo's
		if ( world.isRemote && recording )
		{
			if ( undoLevels.size() > level && !undoLevels.isEmpty() )
			{
				final int end = Math.max( -1, level );
				for ( int x = undoLevels.size() - 1; x > end; --x )
				{
					undoLevels.remove( x );
				}
			}

			if ( undoLevels.size() > ChiselsAndBits.getConfig().maxUndoLevel )
			{
				undoLevels.remove( 0 );
			}

			if ( level >= undoLevels.size() )
			{
				level = undoLevels.size() - 1;
			}

			if ( grouping && hasCreatedGroup )
			{
				final UndoStep current = undoLevels.get( undoLevels.size() - 1 );
				final UndoStep newest = new UndoStep( world.provider.getDimension(), pos, before, after );
				undoLevels.set( undoLevels.size() - 1, newest );
				newest.next = current;
				return;
			}

			undoLevels.add( new UndoStep( world.provider.getDimension(), pos, before, after ) );
			hasCreatedGroup = true;
			level = undoLevels.size() - 1;
		}
	}

	public void undo()
	{
		if ( level > -1 )
		{
			final UndoStep step = undoLevels.get( level );
			final EntityPlayer who = ClientSide.instance.getPlayer();

			if ( correctWorld( who, step ) )
			{
				final ActingPlayer testPlayer = ActingPlayer.testingAs( who, EnumHand.MAIN_HAND );
				final boolean result = replayChanges( testPlayer, step, true, false );

				if ( result )
				{
					final ActingPlayer player = ActingPlayer.actingAs( who, EnumHand.MAIN_HAND );
					if ( replayChanges( player, step, true, true ) )
					{
						level--;
					}
				}

				displayError();
			}
		}
		else
		{
			ClientSide.instance.getPlayer().addChatMessage( new TextComponentTranslation( "mod.chiselsandbits.result.nothing_to_undo" ) );
		}
	}

	public void redo()
	{
		if ( level + 1 < undoLevels.size() )
		{
			final UndoStep step = undoLevels.get( level + 1 );
			final EntityPlayer who = ClientSide.instance.getPlayer();

			if ( correctWorld( who, step ) )
			{
				final ActingPlayer testPlayer = ActingPlayer.testingAs( who, EnumHand.MAIN_HAND );
				final boolean result = replayChanges( testPlayer, step, false, false );

				if ( result )
				{
					final ActingPlayer player = ActingPlayer.actingAs( who, EnumHand.MAIN_HAND );
					if ( replayChanges( player, step, false, true ) )
					{
						level++;
					}
				}

				displayError();
			}
		}
		else
		{
			ClientSide.instance.getPlayer().addChatMessage( new TextComponentTranslation( "mod.chiselsandbits.result.nothing_to_redo" ) );
		}
	}

	private boolean replayChanges(
			final ActingPlayer player,
			UndoStep step,
			final boolean backwards,
			final boolean spawnItemsAndCommitWorldChanges )
	{
		boolean done = false;

		while ( step != null && replaySingleAction( player, step.pos, backwards ? step.after : step.before, backwards ? step.before : step.after, spawnItemsAndCommitWorldChanges ) )
		{
			step = step.next;
			if ( step == null )
			{
				done = true;
			}
		}

		return done;
	}

	private boolean correctWorld(
			final EntityPlayer player,
			final UndoStep step )
	{
		return player.dimension == step.dimensionId;
	}

	private boolean replaySingleAction(
			final ActingPlayer player,
			final BlockPos pos,
			final VoxelBlobStateReference before,
			final VoxelBlobStateReference after,
			final boolean spawnItemsAndCommitWorldChanges )
	{
		try
		{
			recording = false;
			final PacketUndo packet = new PacketUndo( pos, before, after );
			if ( packet.preformAction( player, spawnItemsAndCommitWorldChanges ) )
			{
				NetworkRouter.instance.sendToServer( packet );
				return true;
			}

			return false;
		}
		finally
		{
			recording = true;
		}
	}

	public void beginGroup(
			final EntityPlayer player )
	{
		if ( grouping )
		{
			throw new RuntimeException( "Opening a new group, previous group already started.", groupStarted );
		}

		// capture stack...
		groupStarted = new RuntimeException( "Group was not closed properly." );
		groupStarted.fillInStackTrace();

		grouping = true;
		hasCreatedGroup = false;
	}

	public void endGroup(
			final EntityPlayer player )
	{
		if ( !grouping )
		{
			throw new RuntimeException( "Closing undo group, but no undogroup was started." );
		}

		groupStarted = null;
		grouping = false;
	}

	@SideOnly( Side.CLIENT )
	private void displayError()
	{
		for ( final String err : errors )
		{
			ClientSide.instance.getPlayer().addChatMessage( new TextComponentTranslation( err ) );
		}

		errors.clear();
	}

	public void addError(
			final ActingPlayer player,
			final String string )
	{
		// servers don't care about this...
		if ( !player.isReal() && player.getWorld().isRemote )
		{
			errors.add( string );
		}
	}

	@Override
	public void clearCache()
	{
		level = -1;
		undoLevels.clear();
	}

}
