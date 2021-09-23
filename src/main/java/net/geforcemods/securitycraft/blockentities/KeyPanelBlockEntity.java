package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.KeyPanelBlock;
import net.geforcemods.securitycraft.inventory.GenericTEMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class KeyPanelBlockEntity extends CustomizableBlockEntity implements IPasswordProtected
{
	private String passcode;
	private BooleanOption isAlwaysActive = new BooleanOption("isAlwaysActive", false) {
		@Override
		public void toggle() {
			super.toggle();

			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(KeyPanelBlock.POWERED, get()));
			level.updateNeighborsAt(worldPosition, SCContent.KEY_PANEL_BLOCK.get());
		}
	};
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private IntOption signalLength = new IntOption(this::getBlockPos, "signalLength", 60, 5, 400, 5, true); //20 seconds max

	public KeyPanelBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.beTypeKeyPanel, pos, state);
	}

	@Override
	public CompoundTag save(CompoundTag tag)
	{
		super.save(tag);

		if(passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);

		return tag;
	}

	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);

		passcode = tag.getString("passcode");
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[]{ModuleType.ALLOWLIST, ModuleType.DENYLIST};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[]{ isAlwaysActive, sendMessage, signalLength };
	}

	@Override
	public void activate(Player player)
	{
		if(!level.isClientSide && getBlockState().getBlock() instanceof KeyPanelBlock block)
			block.activate(getBlockState(), level, worldPosition, signalLength.get());
	}

	@Override
	public void openPasswordGUI(Player player)
	{
		if(getPassword() != null)
		{
			if(player instanceof ServerPlayer serverPlayer)
			{
				NetworkHooks.openGui(serverPlayer, new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
					{
						return new GenericTEMenu(SCContent.mTypeCheckPassword, windowId, level, worldPosition);
					}

					@Override
					public Component getDisplayName()
					{
						return new TranslatableComponent(SCContent.KEY_PANEL_BLOCK.get().getDescriptionId());
					}
				}, worldPosition);
			}
		}
		else
		{
			if(getOwner().isOwner(player))
			{
				if(player instanceof ServerPlayer serverPlayer)
				{
					NetworkHooks.openGui(serverPlayer, new MenuProvider() {
						@Override
						public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
						{
							return new GenericTEMenu(SCContent.mTypeSetPassword, windowId, level, worldPosition);
						}

						@Override
						public Component getDisplayName()
						{
							return new TranslatableComponent(SCContent.KEY_PANEL_BLOCK.get().getDescriptionId());
						}
					}, worldPosition);
				}
			}
			else
				PlayerUtils.sendMessageToPlayer(player, new TextComponent("SecurityCraft"), Utils.localize("messages.securitycraft:passwordProtected.notSetUp"), ChatFormatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(BlockState state, Player player)
	{
		if(!state.getValue(KeyPanelBlock.POWERED))
		{
			activate(player);
			return true;
		}

		return false;
	}

	@Override
	public String getPassword()
	{
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password)
	{
		passcode = password;
	}

	public boolean sendsMessages()
	{
		return sendMessage.get();
	}

	public int getSignalLength()
	{
		return signalLength.get();
	}
}
