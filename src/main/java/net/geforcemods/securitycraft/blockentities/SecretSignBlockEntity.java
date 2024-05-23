package net.geforcemods.securitycraft.blockentities;

import java.util.EnumMap;
import java.util.Map;

import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.NonNullList;

public class SecretSignBlockEntity extends TileEntitySign implements IOwnable, IModuleInventory, ICustomizable {
	private Owner owner = new Owner();
	private BooleanOption isSecret = new BooleanOption("isSecret", true);
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		writeModuleInventory(tag);
		writeModuleStates(tag);
		writeOptions(tag);

		if (owner != null)
			owner.save(tag, needsValidation());

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

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

	public boolean isPlayerAllowedToSeeText(EntityPlayer player) {
		return !isSecret() || isOwnedBy(player) || isAllowed(player);
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		IBlockState state = world.getBlockState(pos);

		world.notifyBlockUpdate(pos, state, state, 2);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
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
}
