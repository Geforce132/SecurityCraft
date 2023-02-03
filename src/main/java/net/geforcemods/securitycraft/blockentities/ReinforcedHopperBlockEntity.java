package net.geforcemods.securitycraft.blockentities;

import java.util.EnumMap;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReinforcedHopperBlockEntity extends TileEntityHopper implements IOwnable, IModuleInventory {
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private Owner owner = new Owner();
	private EnumMap<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		if (owner != null)
			owner.writeToNBT(tag, false);

		writeModuleInventory(tag);
		writeModuleStates(tag);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		owner.readFromNBT(tag);
		modules = readModuleInventory(tag);
		moduleStates = readModuleStates(tag);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock() && oldState.getBlock() != SCContent.reinforcedMovingPiston; //prevent this TileEntity from getting removed when the previous block was a Reinforced Moving Piston to make Reinforced Pistons work
	}

	@Override
	public boolean enableHack() {
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot >= 100 ? getModuleInSlot(slot) : super.getStackInSlot(slot);
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST
		};
	}

	@Override
	public NonNullList<ItemStack> getInventory() {
		return modules;
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
