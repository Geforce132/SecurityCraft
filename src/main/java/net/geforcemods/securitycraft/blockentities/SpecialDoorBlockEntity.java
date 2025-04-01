package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;

public abstract class SpecialDoorBlockEntity extends LinkableBlockEntity implements ILockable {
	protected IntOption signalLength = new IntOption(this::getBlockPos, "signalLength", defaultSignalLength(), 0, 400, 5); //20 seconds max
	protected DisabledOption disabled = new DisabledOption(false);

	protected SpecialDoorBlockEntity(TileEntityType<?> type) {
		super(type);
	}

	@Override
	public void onOwnerChanged(BlockState state, World level, BlockPos pos, PlayerEntity player, Owner oldOwner, Owner newOwner) {
		TileEntity be;

		pos = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos.above();
		be = level.getBlockEntity(pos);

		if (be instanceof SpecialDoorBlockEntity && isLinkedWith(this, (SpecialDoorBlockEntity) be)) {
			((SpecialDoorBlockEntity) be).setOwner(getOwner().getUUID(), getOwner().getName());

			if (!level.isClientSide)
				level.getServer().getPlayerList().broadcastAll(be.getUpdatePacket());
		}

		super.onOwnerChanged(state, level, pos, player, oldOwner, newOwner);
	}

	@Override
	protected void onLinkedBlockAction(ILinkedAction action, List<LinkableBlockEntity> excludedBEs) {
		if (action instanceof ILinkedAction.OptionChanged) {
			Option<?> option = ((ILinkedAction.OptionChanged<?>) action).option;

			for (Option<?> customOption : customOptions()) {
				if (customOption.getName().equals(option.getName())) {
					customOption.copy(option);
					break;
				}
			}

			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
		}
		else if (action instanceof ILinkedAction.ModuleInserted) {
			ILinkedAction.ModuleInserted moduleInserted = (ILinkedAction.ModuleInserted) action;

			insertModule(moduleInserted.stack, moduleInserted.wasModuleToggled);
		}
		else if (action instanceof ILinkedAction.ModuleRemoved) {
			ILinkedAction.ModuleRemoved moduleRemoved = (ILinkedAction.ModuleRemoved) action;

			removeModule(moduleRemoved.moduleType, moduleRemoved.wasModuleToggled);
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
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		super.onDataPacket(net, packet);
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
	public IModelData getModelData() {
		return DisguisableBlockEntity.DEFAULT_MODEL_DATA.get();
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	public abstract int defaultSignalLength();
}
