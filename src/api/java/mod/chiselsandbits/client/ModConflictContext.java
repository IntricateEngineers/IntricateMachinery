package mod.chiselsandbits.client;

import mod.chiselsandbits.core.ClientSide;
import mod.chiselsandbits.interfaces.IVoxelBlobItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;

public enum ModConflictContext implements IKeyConflictContext
{

	HOLDING_ROTATEABLE
	{
		@Override
		public boolean isActive()
		{
			final ItemStack held = ClientSide.instance.getPlayer().getHeldItemMainhand();
			return held != null && held.getItem() instanceof IVoxelBlobItem;
		}

		@Override
		public boolean conflicts(
				final IKeyConflictContext other )
		{
			return this == other || other == KeyConflictContext.IN_GAME;
		}
	},

	HOLDING_CHISEL
	{
		@Override
		public boolean isActive()
		{
			return ClientSide.instance.getHeldToolType() != null;
		}

		@Override
		public boolean conflicts(
				final IKeyConflictContext other )
		{
			return this == other || other == KeyConflictContext.IN_GAME;
		}
	};

}
