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
import net.geforcemods.securitycraft.inventory.ReinforcedLecternMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ReinforcedLecternBlockEntity extends LecternTileEntity implements IOwnable, IModuleInventory, ICustomizable {
	private Owner owner = new Owner();
	private NonNullList<ItemStack> modules = NonNullList.withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);
	private BooleanOption lockPage = new BooleanOption("lockPage", false);

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);
		owner.load(tag);
		modules = readModuleInventory(tag);
		moduleStates = readModuleStates(tag);
		readOptions(tag);
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		if (owner != null)
			owner.save(tag, needsValidation());

		writeModuleInventory(tag);
		writeModuleStates(tag);
		writeOptions(tag);
		return tag;
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return save(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
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
		setChanged();
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
				lockPage
		};
	}

	public boolean isPageLocked() {
		return lockPage.get();
	}

	@Override
	public NonNullList<ItemStack> getInventory() {
		return modules;
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
	public TileEntityType<?> getType() {
		return SCContent.REINFORCED_LECTERN_BLOCK_ENTITY.get();
	}

	@Override
	public Container createMenu(int containerId, PlayerInventory playerInventory, PlayerEntity player) {
		return new ReinforcedLecternMenu(containerId, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(SCContent.REINFORCED_LECTERN.get().getDescriptionId());
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
