package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;

import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public abstract class SpecialDoorBlockEntity extends LinkableBlockEntity {
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private IntOption signalLength = new IntOption(this::getBlockPos, "signalLength", defaultSignalLength(), 0, 400, 5, true); //20 seconds max

	public SpecialDoorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player) {
		pos = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();

		if (level.getBlockEntity(pos) instanceof SpecialDoorBlockEntity be && isLinkedWith(this, be)) {
			be.setOwner(getOwner().getUUID(), getOwner().getName());

			if (!level.isClientSide)
				level.getServer().getPlayerList().broadcastAll(be.getUpdatePacket());
		}

		super.onOwnerChanged(state, level, pos, player);
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		handleModule(stack, module, false, toggled);
		super.onModuleInserted(stack, module, toggled);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		handleModule(stack, module, true, toggled);
		super.onModuleRemoved(stack, module, toggled);
	}

	private void handleModule(ItemStack stack, ModuleType module, boolean removed, boolean toggled) {
		DoubleBlockHalf myHalf = getBlockState().getValue(DoorBlock.HALF);
		BlockPos otherPos;

		if (myHalf == DoubleBlockHalf.UPPER)
			otherPos = getBlockPos().below();
		else
			otherPos = getBlockPos().above();

		BlockState other = level.getBlockState(otherPos);

		if (other.getValue(DoorBlock.HALF) != myHalf) {
			if (level.getBlockEntity(otherPos) instanceof SpecialDoorBlockEntity otherDoorBe) {
				if (!removed && !otherDoorBe.hasModule(module))
					otherDoorBe.insertModule(stack, toggled);
				else if (removed && otherDoorBe.hasModule(module))
					otherDoorBe.removeModule(module, toggled);
			}
		}
	}

	@Override
	protected void onLinkedBlockAction(LinkedAction action, Object[] parameters, ArrayList<LinkableBlockEntity> excludedBEs) {
		if (action == LinkedAction.OPTION_CHANGED) {
			Option<?> option = (Option<?>) parameters[0];

			if (option.getName().equals(sendMessage.getName()))
				sendMessage.copy(option);
			else if (option.getName().equals(signalLength.getName()))
				signalLength.copy(option);

			setChanged();
		}
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendMessage, signalLength
		};
	}

	public boolean sendsMessages() {
		return sendMessage.get();
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public abstract int defaultSignalLength();
}
