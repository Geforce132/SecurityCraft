package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.KeypadDoorBlock;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fml.network.NetworkHooks;

public class KeypadDoorTileEntity extends SpecialDoorTileEntity implements IPasswordProtected
{
	private String passcode;

	public KeypadDoorTileEntity()
	{
		super(SCContent.teTypeKeypadDoor);
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
	public void load(BlockState state, CompoundTag tag)
	{
		super.load(state, tag);

		passcode = tag.getString("passcode");
	}

	@Override
	public void activate(Player player) {
		if(!level.isClientSide && getBlockState().getBlock() instanceof KeypadDoorBlock)
			KeypadDoorBlock.activate(level, worldPosition, getBlockState(), getSignalLength());
	}

	@Override
	public void openPasswordGUI(Player player) {
		if(getPassword() != null)
		{
			if(player instanceof ServerPlayer)
			{
				NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
					{
						return new GenericTEContainer(SCContent.cTypeCheckPassword, windowId, level, worldPosition);
					}

					@Override
					public Component getDisplayName()
					{
						return new TranslatableComponent(SCContent.KEYPAD_DOOR.get().getDescriptionId());
					}
				}, worldPosition);
			}
		}
		else
		{
			if(getOwner().isOwner(player))
			{
				if(player instanceof ServerPlayer)
				{
					NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
						@Override
						public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
						{
							return new GenericTEContainer(SCContent.cTypeSetPassword, windowId, level, worldPosition);
						}

						@Override
						public Component getDisplayName()
						{
							return new TranslatableComponent(SCContent.KEYPAD_DOOR.get().getDescriptionId());
						}
					}, worldPosition);
				}
			}
			else
				PlayerUtils.sendMessageToPlayer(player, new TextComponent("SecurityCraft"), Utils.localize("messages.securitycraft:passwordProtected.notSetUp"), ChatFormatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(BlockState blockState, Player player) {
		if(!blockState.getValue(DoorBlock.OPEN)) {
			activate(player);
			return true;
		}

		return false;
	}

	@Override
	public String getPassword() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		BlockEntity te = null;

		passcode = password;

		if(getBlockState().getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER)
			te = level.getBlockEntity(worldPosition.above());
		else if(getBlockState().getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
			te = level.getBlockEntity(worldPosition.below());

		if(te instanceof KeypadDoorTileEntity)
			((KeypadDoorTileEntity)te).setPasswordExclusively(password);
	}

	//only set the password for this door half
	public void setPasswordExclusively(String password)
	{
		passcode = password;
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[]{ModuleType.ALLOWLIST, ModuleType.DENYLIST};
	}

	@Override
	public int defaultSignalLength()
	{
		return 60;
	}
}
