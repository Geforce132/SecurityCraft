package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.KeypadFurnaceBlock;
import net.geforcemods.securitycraft.inventory.GenericTEMenu;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.inventory.KeypadFurnaceMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
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
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class KeypadFurnaceBlockEntity extends AbstractFurnaceBlockEntity implements IPasswordProtected, MenuProvider, IOwnable, INameable, IModuleInventory, ICustomizable
{
	private LazyOptional<IItemHandler> insertOnlyHandler;
	private Owner owner = new Owner();
	private String passcode;
	private Component furnaceCustomName;
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);

	public KeypadFurnaceBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.beTypeKeypadFurnace, pos, state, RecipeType.SMELTING);
	}

	@Override
	public CompoundTag save(CompoundTag tag)
	{
		super.save(tag);

		writeModuleInventory(tag);
		writeOptions(tag);

		if(owner != null)
		{
			owner.save(tag);
		}

		if(passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);

		if(hasCustomSCName())
			tag.putString("CustomName", furnaceCustomName.getString());
		return tag;
	}

	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);

		modules = readModuleInventory(tag);
		readOptions(tag);
		owner.load(tag);
		passcode = tag.getString("passcode");
		furnaceCustomName = new TextComponent(tag.getString("CustomName"));
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
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return BlockUtils.getProtectedCapability(side, this, () -> super.getCapability(cap, side), () -> getInsertOnlyHandler()).cast();
		else return super.getCapability(cap, side);
	}

	private LazyOptional<IItemHandler> getInsertOnlyHandler()
	{
		if(insertOnlyHandler == null)
			insertOnlyHandler = LazyOptional.of(() -> new InsertOnlyInvWrapper(KeypadFurnaceBlockEntity.this));

		return insertOnlyHandler;
	}

	@Override
	public boolean enableHack()
	{
		return true;
	}

	@Override
	public ItemStack getItem(int slot)
	{
		return slot >= 100 ? getModuleInSlot(slot) : items.get(slot);
	}

	@Override
	public void activate(Player player) {
		if(!level.isClientSide && getBlockState().getBlock() instanceof KeypadFurnaceBlock)
			KeypadFurnaceBlock.activate(level, worldPosition, player);
	}

	@Override
	public void openPasswordGUI(Player player) {
		if(getPassword() != null)
		{
			if(player instanceof ServerPlayer serverPlayer)
			{
				NetworkHooks.openGui(serverPlayer, new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
					{
						return new GenericTEMenu(SCContent.mTypeCheckPassword, windowId, level, worldPosition);
					}

					@Override
					public Component getDisplayName()
					{
						return new TranslatableComponent(SCContent.KEYPAD_FURNACE.get().getDescriptionId());
					}
				}, worldPosition);
			}
		}
		else
		{
			if(getOwner().isOwner(player))
			{
				if(player instanceof ServerPlayer serverPlayer)
				{
					NetworkHooks.openGui(serverPlayer, new MenuProvider() {
						@Override
						public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
						{
							return new GenericTEMenu(SCContent.mTypeSetPassword, windowId, level, worldPosition);
						}

						@Override
						public Component getDisplayName()
						{
							return new TranslatableComponent(SCContent.KEYPAD_FURNACE.get().getDescriptionId());
						}
					}, worldPosition);
				}
			}
			else
				PlayerUtils.sendMessageToPlayer(player, new TextComponent("SecurityCraft"), Utils.localize("messages.securitycraft:passwordProtected.notSetUp"), ChatFormatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(BlockState state, Player player) {
		activate(player);
		return true;
	}

	@Override
	public String getPassword() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		passcode = password;
	}

	public ContainerData getFurnaceData()
	{
		return dataAccess;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
	{
		return new KeypadFurnaceMenu(windowId, level, worldPosition, inv, this, dataAccess);
	}

	@Override
	protected AbstractContainerMenu createMenu(int windowId, Inventory inv)
	{
		return createMenu(windowId, inv, inv.player);
	}

	@Override
	public Component getDisplayName()
	{
		return hasCustomSCName() ? getCustomSCName() : getDefaultName();
	}

	@Override
	protected Component getDefaultName()
	{
		return new TranslatableComponent(SCContent.KEYPAD_FURNACE.get().getDescriptionId());
	}

	@Override
	public Component getCustomSCName()
	{
		return furnaceCustomName;
	}

	@Override
	public void setCustomSCName(Component customName)
	{
		furnaceCustomName = customName;
	}

	@Override
	public boolean hasCustomSCName()
	{
		return furnaceCustomName != null && furnaceCustomName.getString() != null && !furnaceCustomName.getString().isEmpty();
	}

	@Override
	public boolean canBeNamed()
	{
		return true;
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
		return new ModuleType[] {ModuleType.ALLOWLIST, ModuleType.DENYLIST};
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
}
