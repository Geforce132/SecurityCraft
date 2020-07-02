package net.geforcemods.securitycraft.tileentity;

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
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedHopperBlock;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.RequestTEOwnableUpdate;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

public class KeypadChestTileEntity extends ChestTileEntity implements IPasswordProtected, IOwnable, IModuleInventory, ICustomizable {

	private static final LazyOptional<IItemHandler> EMPTY_INVENTORY = LazyOptional.of(() -> new EmptyHandler());
	private String passcode;
	private Owner owner = new Owner();
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);

	public KeypadChestTileEntity()
	{
		super(SCContent.teTypeKeypadChest);
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);

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
	public void func_230337_a_(BlockState state, CompoundNBT tag)
	{
		super.func_230337_a_(state, tag);

		modules = readModuleInventory(tag);
		readOptions(tag);

		if (tag.contains("passcode"))
			if(tag.getInt("passcode") != 0)
				passcode = String.valueOf(tag.getInt("passcode"));
			else
				passcode = tag.getString("passcode");

		if (tag.contains("owner"))
			owner.setOwnerName(tag.getString("owner"));

		if (tag.contains("ownerUUID"))
			owner.setOwnerUUID(tag.getString("ownerUUID"));
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT tag = new CompoundNBT();
		write(tag);
		return new SUpdateTileEntityPacket(pos, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		func_230337_a_(getBlockState(), packet.getNbtCompound());
	}

	/**
	 * Returns the name of the inventory
	 */
	@Override
	public ITextComponent getName()
	{
		return new StringTextComponent("Protected chest");
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side == Direction.DOWN)
		{
			BlockPos offsetPos = pos.offset(side);

			if(world.getBlockState(offsetPos).getBlock() != SCContent.REINFORCED_HOPPER.get() || !ReinforcedHopperBlock.canExtract(this, world, offsetPos))
				return EMPTY_INVENTORY.cast();
		}

		return super.getCapability(cap, side);
	}

	@Override
	public boolean enableHack()
	{
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return slot >= 100 ? getModuleInSlot(slot) : super.getStackInSlot(slot);
	}

	@Override
	public void activate(PlayerEntity player) {
		if(!world.isRemote && BlockUtils.getBlock(getWorld(), getPos()) instanceof KeypadChestBlock && !isBlocked())
			KeypadChestBlock.activate(world, pos, player);
	}

	@Override
	public void openPasswordGUI(PlayerEntity player) {
		if(isBlocked())
			return;

		if(getPassword() != null)
		{
			if(player instanceof ServerPlayerEntity)
			{
				NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
					@Override
					public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
					{
						return new GenericTEContainer(SCContent.cTypeCheckPassword, windowId, world, pos);
					}

					@Override
					public ITextComponent getDisplayName()
					{
						return new TranslationTextComponent(SCContent.KEYPAD_CHEST.get().getTranslationKey());
					}
				}, pos);
			}
		}
		else
		{
			if(getOwner().isOwner(player))
			{
				if(player instanceof ServerPlayerEntity)
				{
					NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
						@Override
						public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
						{
							return new GenericTEContainer(SCContent.cTypeSetPassword, windowId, world, pos);
						}

						@Override
						public ITextComponent getDisplayName()
						{
							return new TranslationTextComponent(SCContent.KEYPAD_CHEST.get().getTranslationKey());
						}
					}, pos);
				}
			}
			else
				PlayerUtils.sendMessageToPlayer(player, new StringTextComponent("SecurityCraft"), ClientUtils.localize("messages.securitycraft:passwordProtected.notSetUp"), TextFormatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(BlockState blockState, PlayerEntity player, boolean isCodebreakerDisabled) {
		if(isCodebreakerDisabled)
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYPAD_CHEST.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
		else {
			activate(player);
			return true;
		}

		return false;
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
				offsetTe.removeModule(((ModuleItem)module.getItem()).getModule());
			else
				offsetTe.insertModule(module);
		}
	}

	public KeypadChestTileEntity findOther()
	{
		BlockState state = getBlockState();
		ChestType type = state.get(KeypadChestBlock.TYPE);

		if(type != ChestType.SINGLE)
		{
			BlockPos offsetPos = pos.offset(ChestBlock.getDirectionToAttached(state));
			BlockState offsetState = world.getBlockState(offsetPos);

			if(state.getBlock() == offsetState.getBlock())
			{
				ChestType offsetType = offsetState.get(KeypadChestBlock.TYPE);

				if(offsetType != ChestType.SINGLE && type != offsetType && state.get(KeypadChestBlock.FACING) == offsetState.get(KeypadChestBlock.FACING))
				{
					TileEntity offsetTe = world.getTileEntity(offsetPos);

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
		Block east = BlockUtils.getBlock(getWorld(), getPos().east());
		Block south = BlockUtils.getBlock(getWorld(), getPos().south());
		Block west = BlockUtils.getBlock(getWorld(), getPos().west());
		Block north = BlockUtils.getBlock(getWorld(), getPos().north());

		if(east instanceof KeypadChestBlock && KeypadChestBlock.isBlocked(getWorld(), getPos().east()))
			return true;
		else if(south instanceof KeypadChestBlock && KeypadChestBlock.isBlocked(getWorld(), getPos().south()))
			return true;
		else if(west instanceof KeypadChestBlock && KeypadChestBlock.isBlocked(getWorld(), getPos().west()))
			return true;
		else if(north instanceof KeypadChestBlock && KeypadChestBlock.isBlocked(getWorld(), getPos().north()))
			return true;
		else return isSingleBlocked();
	}

	public boolean isSingleBlocked()
	{
		return KeypadChestBlock.isBlocked(getWorld(), getPos());
	}

	@Override
	public void onLoad()
	{
		if(world.isRemote)
			SecurityCraft.channel.sendToServer(new RequestTEOwnableUpdate(pos, world.getDimension().getType().getId()));
	}

	@Override
	public TileEntity getTileEntity()
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
		return new ModuleType[] {ModuleType.WHITELIST, ModuleType.BLACKLIST};
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
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3); //sync option change to client
	}
}
