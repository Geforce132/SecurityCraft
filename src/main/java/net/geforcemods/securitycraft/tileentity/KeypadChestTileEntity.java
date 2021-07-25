package net.geforcemods.securitycraft.tileentity;

import java.util.stream.Collectors;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.RequestTEOwnableUpdate;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class KeypadChestTileEntity extends ChestBlockEntity implements IPasswordProtected, IOwnable, IModuleInventory, ICustomizable {

	private LazyOptional<IItemHandler> insertOnlyHandler;
	private String passcode;
	private Owner owner = new Owner();
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);

	public KeypadChestTileEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.teTypeKeypadChest, pos, state);
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

		if(passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);

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
		passcode = tag.getString("passcode");
		owner.setOwnerName(tag.getString("owner"));
		owner.setOwnerUUID(tag.getString("ownerUUID"));
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
	public Component getDefaultName()
	{
		return Utils.localize("block.securitycraft.keypad_chest");
	}

	@Override
	protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int i, int j) {
		super.signalOpenCount(level, pos, state, i, j);

		if(hasModule(ModuleType.REDSTONE))
			level.updateNeighborsAt(worldPosition.below(), getBlockState().getBlock());
	}

	public int getNumPlayersUsing() {
		return openersCounter.getOpenerCount();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return BlockUtils.getProtectedCapability(side, this, () -> super.getCapability(cap, side), () -> getInsertOnlyHandler()).cast();
		else return super.getCapability(cap, side);
	}

	private LazyOptional<IItemHandler> getInsertOnlyHandler()
	{
		if(insertOnlyHandler == null)
			insertOnlyHandler = LazyOptional.of(() -> new InsertOnlyInvWrapper(KeypadChestTileEntity.this));

		return insertOnlyHandler;
	}

	public LazyOptional<IItemHandler> getHandlerForSentry(SentryEntity entity)
	{
		if(entity.getOwner().owns(this))
			return super.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);
		else
			return LazyOptional.empty();
	}

	@Override
	public boolean enableHack()
	{
		return true;
	}

	@Override
	public ItemStack getItem(int slot)
	{
		return slot >= 100 ? getModuleInSlot(slot) : super.getItem(slot);
	}

	@Override
	public void activate(Player player) {
		if(!level.isClientSide && getBlockState().getBlock() instanceof KeypadChestBlock && !isBlocked())
			KeypadChestBlock.activate(level, worldPosition, player);
	}

	@Override
	public void openPasswordGUI(Player player) {
		if(isBlocked())
			return;

		if(getPassword() != null)
		{
			if(player instanceof ServerPlayer)
			{
				NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
					{
						return new GenericTEContainer(SCContent.cTypeCheckPassword, windowId, level, worldPosition);
					}

					@Override
					public Component getDisplayName()
					{
						return new TranslatableComponent(SCContent.KEYPAD_CHEST.get().getDescriptionId());
					}
				}, worldPosition);
			}
		}
		else
		{
			if(getOwner().isOwner(player))
			{
				if(player instanceof ServerPlayer)
				{
					NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
						@Override
						public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
						{
							return new GenericTEContainer(SCContent.cTypeSetPassword, windowId, level, worldPosition);
						}

						@Override
						public Component getDisplayName()
						{
							return new TranslatableComponent(SCContent.KEYPAD_CHEST.get().getDescriptionId());
						}
					}, worldPosition);
				}
			}
			else
				PlayerUtils.sendMessageToPlayer(player, new TextComponent("SecurityCraft"), Utils.localize("messages.securitycraft:passwordProtected.notSetUp"), ChatFormatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(BlockState blockState, Player player) {
		activate(player);
		return true;
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module)
	{
		IModuleInventory.super.onModuleInserted(stack, module);

		addOrRemoveModuleFromAttached(stack, false);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		IModuleInventory.super.onModuleRemoved(stack, module);

		addOrRemoveModuleFromAttached(stack, true);
	}

	@Override
	public void onOptionChanged(Option<?> option)
	{
		if(option instanceof BooleanOption)
		{
			KeypadChestTileEntity offsetTe = findOther();

			if(offsetTe != null)
				offsetTe.setSendsMessages(((BooleanOption)option).get());
		}
	}

	public void addOrRemoveModuleFromAttached(ItemStack module, boolean remove)
	{
		if(module.isEmpty() || !(module.getItem() instanceof ModuleItem))
			return;

		KeypadChestTileEntity offsetTe = findOther();

		if(offsetTe != null)
		{
			if(remove)
				offsetTe.removeModule(((ModuleItem)module.getItem()).getModuleType());
			else
				offsetTe.insertModule(module);
		}
	}

	public KeypadChestTileEntity findOther()
	{
		BlockState state = getBlockState();
		ChestType type = state.getValue(KeypadChestBlock.TYPE);

		if(type != ChestType.SINGLE)
		{
			BlockPos offsetPos = worldPosition.relative(ChestBlock.getConnectedDirection(state));
			BlockState offsetState = level.getBlockState(offsetPos);

			if(state.getBlock() == offsetState.getBlock())
			{
				ChestType offsetType = offsetState.getValue(KeypadChestBlock.TYPE);

				if(offsetType != ChestType.SINGLE && type != offsetType && state.getValue(KeypadChestBlock.FACING) == offsetState.getValue(KeypadChestBlock.FACING))
				{
					BlockEntity offsetTe = level.getBlockEntity(offsetPos);

					if(offsetTe instanceof KeypadChestTileEntity)
						return (KeypadChestTileEntity)offsetTe;
				}
			}
		}

		return null;
	}

	@Override
	public String getPassword() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		passcode = password;
	}

	@Override
	public Owner getOwner(){
		return owner;
	}

	@Override
	public void setOwner(String uuid, String name) {
		owner.set(uuid, name);
	}

	public boolean isBlocked()
	{
		for(Direction dir : Direction.Plane.HORIZONTAL.stream().collect(Collectors.toList()))
		{
			BlockPos pos = getBlockPos().relative(dir);

			if(level.getBlockState(pos).getBlock() instanceof KeypadChestBlock && KeypadChestBlock.isBlocked(level, pos))
				return true;
		}

		return isSingleBlocked();
	}

	public boolean isSingleBlocked()
	{
		return KeypadChestBlock.isBlocked(getLevel(), getBlockPos());
	}

	@Override
	public void onLoad()
	{
		if(level.isClientSide)
			SecurityCraft.channel.sendToServer(new RequestTEOwnableUpdate(worldPosition));
	}

	@Override
	public BlockEntity getTileEntity()
	{
		return this;
	}

	@Override
	public NonNullList<ItemStack> getInventory()
	{
		return modules;
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[] {ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.REDSTONE};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[]{sendMessage};
	}

	public boolean sendsMessages()
	{
		return sendMessage.get();
	}

	public void setSendsMessages(boolean value)
	{
		sendMessage.setValue(value);
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
	}
}
