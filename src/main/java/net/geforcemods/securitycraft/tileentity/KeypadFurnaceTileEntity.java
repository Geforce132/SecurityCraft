package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.KeypadFurnaceBlock;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.containers.KeypadFurnaceContainer;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.RequestTEOwnableUpdate;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

public class KeypadFurnaceTileEntity extends AbstractFurnaceTileEntity implements IPasswordProtected, INamedContainerProvider, IOwnable, INameable, IModuleInventory, ICustomizable
{
	private static final LazyOptional<IItemHandler> EMPTY_INVENTORY = LazyOptional.of(() -> new EmptyHandler());
	private LazyOptional<IItemHandler> insertOnlyHandler;
	private Owner owner = new Owner();
	private String passcode;
	private ITextComponent furnaceCustomName;
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);

	public KeypadFurnaceTileEntity()
	{
		super(SCContent.teTypeKeypadFurnace, IRecipeType.SMELTING);
	}

	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);

		writeModuleInventory(tag);
		writeOptions(tag);

		if(owner != null)
		{
			tag.putString("owner", owner.getName());
			tag.putString("ownerUUID", owner.getUUID());
		}

		if(passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);

		if(tag.contains("CustomName", Constants.NBT.TAG_STRING))
			furnaceCustomName = new StringTextComponent(tag.getString("CustomName"));

		return tag;
	}

	@Override
	public void read(BlockState state, CompoundNBT tag)
	{
		super.read(state, tag);

		modules = readModuleInventory(tag);
		readOptions(tag);

		if(tag.contains("owner"))
			owner.setOwnerName(tag.getString("owner"));

		if(tag.contains("ownerUUID"))
			owner.setOwnerUUID(tag.getString("ownerUUID"));

		if(tag.contains("passcode"))
			passcode = tag.getString("passcode");

		if(hasCustomSCName())
			tag.putString("CustomName", furnaceCustomName.getString());
	}

	@Override
	public CompoundNBT getUpdateTag()
	{
		return write(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket()
	{
		return new SUpdateTileEntityPacket(pos, 1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet)
	{
		read(getBlockState(), packet.getNbtCompound());
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
	public void onLoad()
	{
		if(world.isRemote)
			SecurityCraft.channel.sendToServer(new RequestTEOwnableUpdate(pos));
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			BlockPos offsetPos = pos.offset(side);
			BlockState offsetState = world.getBlockState(offsetPos);

			for(IExtractionBlock extractionBlock : SecurityCraft.getRegisteredExtractionBlocks())
			{
				if(offsetState.getBlock() == extractionBlock.getBlock())
				{
					if(!extractionBlock.canExtract(this, world, offsetPos, offsetState))
						return EMPTY_INVENTORY.cast();
					else return super.getCapability(cap, side);
				}
			}

			return getInsertOnlyHandler().cast();
		}
		else return super.getCapability(cap, side);
	}

	private LazyOptional<IItemHandler> getInsertOnlyHandler()
	{
		if(insertOnlyHandler == null)
			insertOnlyHandler = LazyOptional.of(() -> new InsertOnlyInvWrapper(KeypadFurnaceTileEntity.this));

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
		return slot >= 100 ? getModuleInSlot(slot) : items.get(slot);
	}

	@Override
	public void activate(PlayerEntity player) {
		if(!world.isRemote && BlockUtils.getBlock(getWorld(), getPos()) instanceof KeypadFurnaceBlock)
			KeypadFurnaceBlock.activate(world, pos, player);
	}

	@Override
	public void openPasswordGUI(PlayerEntity player) {
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
						return new TranslationTextComponent(SCContent.KEYPAD_FURNACE.get().getTranslationKey());
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
							return new TranslationTextComponent(SCContent.KEYPAD_FURNACE.get().getTranslationKey());
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
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.KEYPAD_FURNACE.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
		else {
			activate(player);
			return true;
		}

		return false;
	}

	@Override
	public String getPassword() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		passcode = password;
	}

	public IIntArray getFurnaceData()
	{
		return furnaceData;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
	{
		return new KeypadFurnaceContainer(windowId, world, pos, inv, this, furnaceData);
	}

	@Override
	protected Container createMenu(int windowId, PlayerInventory inv)
	{
		return createMenu(windowId, inv, inv.player);
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return hasCustomSCName() ? getCustomSCName() : getDefaultName();
	}

	@Override
	protected ITextComponent getDefaultName()
	{
		return new TranslationTextComponent(SCContent.KEYPAD_FURNACE.get().getTranslationKey());
	}

	@Override
	public ITextComponent getCustomSCName()
	{
		return furnaceCustomName;
	}

	@Override
	public void setCustomSCName(ITextComponent customName)
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
}
