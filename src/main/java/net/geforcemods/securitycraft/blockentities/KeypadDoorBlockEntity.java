package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.KeypadDoorBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class KeypadDoorBlockEntity extends SpecialDoorBlockEntity implements IPasswordProtected {
	private String passcode;

	public KeypadDoorBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.KEYPAD_DOOR_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		super.save(tag);

		if (passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);

		return tag;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		passcode = tag.getString("passcode");
	}

	@Override
	public void activate(Player player) {
		if (!level.isClientSide && getBlockState().getBlock() instanceof KeypadDoorBlock block)
			block.activate(getBlockState(), level, worldPosition, player, getSignalLength());
	}

	@Override
	public boolean shouldAttemptCodebreak(BlockState state, Player player) {
		if (isDisabled()) {
			player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			return false;
		}

		return !state.getValue(DoorBlock.OPEN) && IPasswordProtected.super.shouldAttemptCodebreak(state, player);
	}

	@Override
	public String getPassword() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		BlockEntity be = null;

		passcode = password;

		if (getBlockState().getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER)
			be = level.getBlockEntity(worldPosition.above());
		else if (getBlockState().getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
			be = level.getBlockEntity(worldPosition.below());

		if (be instanceof KeypadDoorBlockEntity doorTe)
			doorTe.setPasswordExclusively(password);

		setChanged();
	}

	//only set the password for this door half
	public void setPasswordExclusively(String password) {
		passcode = password;
		setChanged();
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DENYLIST
		};
	}

	@Override
	public int defaultSignalLength() {
		return 60;
	}
}
