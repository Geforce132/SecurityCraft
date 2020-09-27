package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.BlockKeypadChest;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedHopper;
import net.geforcemods.securitycraft.compat.inventory.InsertOnlyDoubleChestHandler;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

public class TileEntityKeypadChest extends TileEntityChest implements IPasswordProtected, IOwnable, IModuleInventory, ICustomizable {

	private static final EmptyHandler EMPTY_INVENTORY = new EmptyHandler();
	private InsertOnlyDoubleChestHandler insertOnlyHandler;
	private String passcode;
	private Owner owner = new Owner();
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private OptionBoolean sendMessage = new OptionBoolean("sendMessage", true);

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		writeModuleInventory(tag);
		writeOptions(tag);

		if(passcode != null && !passcode.isEmpty())
			tag.setString("passcode", passcode);

		if(owner != null){
			tag.setString("owner", owner.getName());
			tag.setString("ownerUUID", owner.getUUID());
		}

		return tag;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
	{
		return oldState.getBlock() != newState.getBlock();
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		modules = readModuleInventory(tag);
		readOptions(tag);

		if (tag.hasKey("passcode"))
			if(tag.getInteger("passcode") != 0)
				passcode = String.valueOf(tag.getInteger("passcode"));
			else
				passcode = tag.getString("passcode");

		if (tag.hasKey("owner"))
			owner.setOwnerName(tag.getString("owner"));

		if (tag.hasKey("ownerUUID"))
			owner.setOwnerUUID(tag.getString("ownerUUID"));
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		readFromNBT(pkt.getNbtCompound());
	}

	/**
	 * Returns the name of the inventory
	 */
	@Override
	public String getName()
	{
		return "Protected chest";
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			BlockPos offsetPos = pos.offset(facing);

			if(world.getBlockState(offsetPos).getBlock() == SCContent.reinforcedHopper)
			{
				if(!BlockReinforcedHopper.canExtract(this, world, offsetPos))
					return (T) EMPTY_INVENTORY;
				else return super.getCapability(capability, facing);
			}
			else return (T) getInsertOnlyHandler();
		}
		else return super.getCapability(capability, facing);
	}

	private InsertOnlyDoubleChestHandler getInsertOnlyHandler()
	{
		if(insertOnlyHandler == null || insertOnlyHandler.needsRefresh())
			insertOnlyHandler = InsertOnlyDoubleChestHandler.get(this);

		return insertOnlyHandler;
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
	public void activate(EntityPlayer player) {
		if(!world.isRemote && BlockUtils.getBlock(getWorld(), getPos()) instanceof BlockKeypadChest && !isBlocked())
			BlockKeypadChest.activate(world, pos, player);
	}

	@Override
	public void openPasswordGUI(EntityPlayer player) {
		if(isBlocked())
			return;

		if(getPassword() != null)
			player.openGui(SecurityCraft.instance, GuiHandler.INSERT_PASSWORD_ID, world, pos.getX(), pos.getY(), pos.getZ());
		else
		{
			if(getOwner().isOwner(player))
				player.openGui(SecurityCraft.instance, GuiHandler.SETUP_PASSWORD_ID, world, pos.getX(), pos.getY(), pos.getZ());
			else
				PlayerUtils.sendMessageToPlayer(player, "SecurityCraft", ClientUtils.localize("messages.securitycraft:passwordProtected.notSetUp"), TextFormatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(IBlockState blockState, EntityPlayer player, boolean isCodebreakerDisabled) {
		if(isCodebreakerDisabled)
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("tile.securitycraft:keypadChest.name"), ClientUtils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
		else {
			activate(player);
			return true;
		}

		return false;
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumModuleType module)
	{
		IModuleInventory.super.onModuleInserted(stack, module);

		addOrRemoveModuleFromAttached(stack, false);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module)
	{
		IModuleInventory.super.onModuleRemoved(stack, module);

		addOrRemoveModuleFromAttached(stack, true);
	}

	@Override
	public void onOptionChanged(Option<?> option)
	{
		if(option instanceof OptionBoolean)
		{
			TileEntityKeypadChest offsetTe = findOther();

			if(offsetTe != null)
				offsetTe.setSendsMessages(((OptionBoolean)option).get());
		}
	}

	public void addOrRemoveModuleFromAttached(ItemStack module, boolean remove)
	{
		if(module.isEmpty() || !(module.getItem() instanceof ItemModule))
			return;

		TileEntityKeypadChest offsetTe = findOther();

		if(offsetTe != null)
		{
			if(remove)
				offsetTe.removeModule(((ItemModule)module.getItem()).getModule());
			else
				offsetTe.insertModule(module);
		}
	}

	public TileEntityKeypadChest findOther()
	{
		IBlockState state = world.getBlockState(pos);

		for(EnumFacing facing : EnumFacing.HORIZONTALS)
		{
			BlockPos offsetPos = pos.offset(facing);
			IBlockState offsetState = world.getBlockState(offsetPos);

			if(state.getBlock() == offsetState.getBlock())
			{
				TileEntity offsetTe = world.getTileEntity(offsetPos);

				if(offsetTe instanceof TileEntityKeypadChest)
					return (TileEntityKeypadChest)offsetTe;
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

		if(east instanceof BlockKeypadChest && BlockKeypadChest.isBlocked(getWorld(), getPos().east()))
			return true;
		else if(south instanceof BlockKeypadChest && BlockKeypadChest.isBlocked(getWorld(), getPos().south()))
			return true;
		else if(west instanceof BlockKeypadChest && BlockKeypadChest.isBlocked(getWorld(), getPos().west()))
			return true;
		else if(north instanceof BlockKeypadChest && BlockKeypadChest.isBlocked(getWorld(), getPos().north()))
			return true;
		else return isSingleBlocked();
	}

	public boolean isSingleBlocked()
	{
		return BlockKeypadChest.isBlocked(getWorld(), getPos());
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
	public EnumModuleType[] acceptedModules()
	{
		return new EnumModuleType[] {EnumModuleType.WHITELIST, EnumModuleType.BLACKLIST};
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
		IBlockState state = world.getBlockState(pos);

		sendMessage.setValue(value);
		world.notifyBlockUpdate(pos, state, state, 3); //sync option change to client
	}
}
