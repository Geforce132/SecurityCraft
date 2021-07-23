package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.RequestTEOwnableUpdate;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SecretSignTileEntity extends SignBlockEntity implements IOwnable, IModuleInventory, ICustomizable
{
	private Owner owner = new Owner();
	private BooleanOption isSecret = new BooleanOption("isSecret", true);
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);

	public SecretSignTileEntity(BlockPos pos, BlockState state)
	{
		super(pos, state);
	}

	@Override
	public BlockEntityType<?> getType()
	{
		return SCContent.teTypeSecretSign;
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public CompoundTag save(CompoundTag tag)
	{
		super.save(tag);

		writeModuleInventory(tag);
		writeOptions(tag);

		if(owner != null){
			tag.putString("owner", owner.getName());
			tag.putString("ownerUUID", owner.getUUID());
		}

		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);

		modules = readModuleInventory(tag);
		readOptions(tag);
		owner.setOwnerName(tag.getString("owner"));
		owner.setOwnerUUID(tag.getString("ownerUUID"));
	}

	@Override
	public BlockEntity getTileEntity()
	{
		return this;
	}

	@Override
	public NonNullList<ItemStack> getInventory() {
		return modules;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.ALLOWLIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ isSecret };
	}

	public boolean isSecret() {
		return isSecret.get();
	}

	public boolean isPlayerAllowedToSeeText(Player player) {
		return !isSecret() || getOwner().isOwner(player) || ModuleUtils.isAllowed(this, player);
	}

	@Override
	public void onOptionChanged(Option<?> option)
	{
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag tag = new CompoundTag();
		save(tag);
		return new ClientboundBlockEntityDataPacket(worldPosition, 1, tag);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		load(packet.getTag());
	}

	@Override
	public Owner getOwner(){
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}

	@Override
	public void onLoad()
	{
		if(level.isClientSide)
			SecurityCraft.channel.sendToServer(new RequestTEOwnableUpdate(getBlockPos()));
	}
}
