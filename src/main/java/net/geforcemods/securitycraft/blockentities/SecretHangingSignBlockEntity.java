package net.geforcemods.securitycraft.blockentities;

import java.util.EnumMap;
import java.util.Map;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SecretHangingSignBlockEntity extends HangingSignBlockEntity implements IOwnable, IModuleInventory, ICustomizable {
	private Owner owner = new Owner();
	private BooleanOption isFrontSecret = new BooleanOption("isFrontSecret", true) {
		@Override
		public String getKey(String denotation) {
			return "option.generic.secret_sign.isFrontSecret";
		}
	};
	private BooleanOption isBackSecret = new BooleanOption("isBackSecret", true) {
		@Override
		public String getKey(String denotation) {
			return "option.generic.secret_sign.isBackSecret";
		}
	};
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);

	public SecretHangingSignBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public BlockEntityType<?> getType() {
		return SCContent.SECRET_HANGING_SIGN_BLOCK_ENTITY.get();
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		writeModuleInventory(tag);
		writeModuleStates(tag);
		writeOptions(tag);

		if (owner != null)
			owner.save(tag, needsValidation());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		modules = readModuleInventory(tag);
		moduleStates = readModuleStates(tag);
		readOptions(tag);
		owner.load(tag);
	}

	@Override
	public void readOptions(CompoundTag tag) {
		if (tag.contains("isSecret")) {
			tag.putBoolean(isFrontSecret.getName(), tag.getBoolean("isSecret"));
			tag.remove("isSecret");
		}

		ICustomizable.super.readOptions(tag);
	}

	@Override
	public NonNullList<ItemStack> getInventory() {
		return modules;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				isFrontSecret, isBackSecret
		};
	}

	public boolean isFrontSecret() {
		return isFrontSecret.get();
	}

	public boolean isBackSecret() {
		return isBackSecret.get();
	}

	public boolean isPlayerAllowedToSeeText(Player player, boolean isFront) {
		return !(isFront ? isFrontSecret() : isBackSecret()) || isOwnedBy(player) || isAllowed(player);
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
		ICustomizable.super.onOptionChanged(option);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		super.onDataPacket(net, packet);
		handleUpdateTag(packet.getTag());
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		load(tag);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
		setChanged();
	}

	@Override
	public boolean isModuleEnabled(ModuleType module) {
		return hasModule(module) && moduleStates.get(module) == Boolean.TRUE; //prevent NPE
	}

	@Override
	public void toggleModuleState(ModuleType module, boolean shouldBeEnabled) {
		moduleStates.put(module, shouldBeEnabled);
	}

	@Override
	public String getModuleDescriptionId(String denotation, ModuleType module) {
		return IModuleInventory.super.getModuleDescriptionId("generic.secret_sign", module);
	}

	@Override
	public Level myLevel() {
		return level;
	}

	@Override
	public BlockPos myPos() {
		return worldPosition;
	}
}
