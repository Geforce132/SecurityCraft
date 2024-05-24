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
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SecretSignBlockEntity extends SignTileEntity implements IOwnable, IModuleInventory, ICustomizable {
	private Owner owner = new Owner();
	private BooleanOption isSecret = new BooleanOption("isSecret", true) {
		@Override
		public String getKey(String denotation) {
			return "option.generic.secret_sign.isSecret";
		}
	};
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);

	@Override
	public TileEntityType<?> getType() {
		return SCContent.SECRET_SIGN_BLOCK_ENTITY.get();
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		writeModuleInventory(tag);
		writeModuleStates(tag);
		writeOptions(tag);

		if (owner != null)
			owner.save(tag, needsValidation());

		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		modules = readModuleInventory(tag);
		moduleStates = readModuleStates(tag);
		readOptions(tag);
		owner.load(tag);
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
				isSecret
		};
	}

	public boolean isSecret() {
		return isSecret.get();
	}

	public boolean isPlayerAllowedToSeeText(PlayerEntity player) {
		return !isSecret() || isOwnedBy(player) || isAllowed(player);
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT tag = new CompoundNBT();
		save(tag);
		return new SUpdateTileEntityPacket(worldPosition, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		load(getBlockState(), packet.getTag());
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}

	@Override
	public boolean isModuleEnabled(ModuleType module) {
		return hasModule(module) && moduleStates.get(module) == Boolean.TRUE; //prevent NPE
	}

	@Override
	public void toggleModuleState(ModuleType module, boolean shouldBeEnabled) {
		moduleStates.put(module, shouldBeEnabled);

		if (shouldBeEnabled)
			onModuleInserted(getModule(module), module, true);
		else
			onModuleRemoved(getModule(module), module, true);
	}

	@Override
	public String getModuleDescriptionId(String denotation, ModuleType module) {
		return IModuleInventory.super.getModuleDescriptionId("generic.secret_sign", module);
	}

	@Override
	public World myLevel() {
		return level;
	}

	@Override
	public BlockPos myPos() {
		return worldPosition;
	}
}
