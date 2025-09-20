package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.model.data.ModelData;

public abstract class SpecialDoorBlockEntity extends LinkableBlockEntity implements ILockable {
	protected IntOption signalLength = new IntOption("signalLength", defaultSignalLength(), 0, 400, 5); //20 seconds max
	protected DisabledOption disabled = new DisabledOption(false);

	protected SpecialDoorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player, Owner oldOwner, Owner newOwner) {
		pos = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();

		if (level.getBlockEntity(pos) instanceof SpecialDoorBlockEntity be && isLinkedWith(this, be)) {
			be.setOwner(getOwner().getUUID(), getOwner().getName());

			if (!level.isClientSide())
				level.getServer().getPlayerList().broadcastAll(be.getUpdatePacket());
		}

		super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);
	}

	@Override
	protected void onLinkedBlockAction(ILinkedAction action, List<LinkableBlockEntity> excludedBEs) {
		switch (action) {
			case ILinkedAction.OptionChanged<?>(Option<?> option) -> {
				for (Option<?> customOption : customOptions()) {
					if (customOption.getName().equals(option.getName())) {
						customOption.copy(option);
						break;
					}
				}

				setChanged();
			}
			case ILinkedAction.ModuleInserted(ItemStack stack, ModuleItem module, boolean wasModuleToggled) ->
					insertModule(stack, wasModuleToggled);
			case ILinkedAction.ModuleRemoved(ModuleType moduleType, boolean wasModuleToggled) ->
					removeModule(moduleType, wasModuleToggled);
			default -> {
			}
		}
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			DisguisableBlockEntity.onDisguiseModuleInserted(this, stack, toggled);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.DISGUISE)
			DisguisableBlockEntity.onDisguiseModuleRemoved(this, stack, toggled);
	}

	@Override
	public void onDataPacket(Connection net, ValueInput tag) {
		super.onDataPacket(net, tag);
		DisguisableBlockEntity.onHandleUpdateTag(this);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		DisguisableBlockEntity.onHandleUpdateTag(this);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		DisguisableBlockEntity.onSetRemoved(this);
	}

	@Override
	public ModelData getModelData() {
		return DisguisableBlockEntity.getModelData(this);
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	public abstract int defaultSignalLength();
}
