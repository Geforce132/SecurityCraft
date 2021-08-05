package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedHopperTileEntity extends HopperBlockEntity implements IOwnable, IModuleInventory
{
	private NonNullList<ItemStack> modules = NonNullList.withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private Owner owner = new Owner();

	public ReinforcedHopperTileEntity(BlockPos pos, BlockState state)
	{
		super(pos, state);
	}

	@Override
	public BlockEntityType<?> getType()
	{
		return SCContent.teTypeReinforcedHopper;
	}

	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);

		owner.setOwnerName(tag.getString("owner"));
		owner.setOwnerUUID(tag.getString("ownerUUID"));
		modules = readModuleInventory(tag);
	}

	@Override
	public CompoundTag save(CompoundTag tag)
	{
		super.save(tag);

		if(owner != null)
		{
			tag.putString("owner", owner.getName());
			tag.putString("ownerUUID", owner.getUUID());
		}

		writeModuleInventory(tag);
		return tag;
	}

	@Override
	public CompoundTag getUpdateTag()
	{
		return save(new CompoundTag());
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return new ClientboundBlockEntityDataPacket(worldPosition, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		load(packet.getTag());
	}

	@Override
	public Owner getOwner()
	{
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name)
	{
		owner.set(uuid, name);
	}

	@Override
	public boolean enableHack()
	{
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return slot >= 100 ? getModuleInSlot(slot) : super.getItem(slot);
	}

	@Override
	public ItemStack getItem(int slot)
	{
		return getStackInSlot(slot);
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[] {ModuleType.ALLOWLIST};
	}

	@Override
	public NonNullList<ItemStack> getInventory()
	{
		return modules;
	}

	@Override
	public BlockEntity getTileEntity()
	{
		return this;
	}
}