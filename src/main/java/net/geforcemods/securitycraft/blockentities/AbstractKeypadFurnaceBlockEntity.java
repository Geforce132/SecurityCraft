package net.geforcemods.securitycraft.blockentities;

import java.util.EnumMap;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.AbstractKeypadFurnaceBlock;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.inventory.AbstractKeypadFurnaceMenu;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.DisguisableDynamicBakedModel;
import net.geforcemods.securitycraft.network.client.RefreshDisguisableModel;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.PacketDistributor;

public abstract class AbstractKeypadFurnaceBlockEntity extends AbstractFurnaceBlockEntity implements IPasswordProtected, MenuProvider, IOwnable, INameSetter, IModuleInventory, ICustomizable, ILockable {
	private LazyOptional<IItemHandler> insertOnlyHandler;
	private Owner owner = new Owner();
	private String passcode;
	private NonNullList<ItemStack> modules = NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private DisabledOption disabled = new DisabledOption(false);
	private EnumMap<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);
	private ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
		@Override
		protected void onOpen(Level level, BlockPos pos, BlockState state) {
			if (level.isClientSide)
				return;

			level.playSound(null, pos, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.setBlockAndUpdate(pos, state.setValue(AbstractKeypadFurnaceBlock.OPEN, true));
		}

		@Override
		protected void onClose(Level level, BlockPos pos, BlockState state) {
			if (level.isClientSide)
				return;

			level.playSound(null, pos, SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.setBlockAndUpdate(pos, state.setValue(AbstractKeypadFurnaceBlock.OPEN, false));
		}

		@Override
		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {}

		@Override
		protected boolean isOwnContainer(Player player) {
			if (player.containerMenu instanceof AbstractKeypadFurnaceMenu menu) {
				Container container = menu.be;

				return container == AbstractKeypadFurnaceBlockEntity.this;
			}
			else
				return false;
		}
	};

	public AbstractKeypadFurnaceBlockEntity(BlockEntityType<?> beType, BlockPos pos, BlockState state, RecipeType<? extends AbstractCookingRecipe> recipeType) {
		super(beType, pos, state, recipeType);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, AbstractKeypadFurnaceBlockEntity be) {
		if (!be.isDisabled())
			AbstractFurnaceBlockEntity.serverTick(level, pos, state, be);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);

		writeModuleInventory(tag);
		writeModuleStates(tag);
		writeOptions(tag);

		if (owner != null)
			owner.save(tag, false);

		if (passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		modules = readModuleInventory(tag);
		moduleStates = readModuleStates(tag);
		readOptions(tag);
		owner.load(tag);
		passcode = tag.getString("passcode");
	}

	@Override
	public CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		super.onDataPacket(net, packet);
		handleUpdateTag(packet.getTag());
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		load(tag);

		if (level != null && level.isClientSide) {
			ItemStack stack = getModule(ModuleType.DISGUISE);

			if (!stack.isEmpty())
				ClientHandler.putDisguisedBeRenderer(this, stack);
			else
				ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(this);
		}
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
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER)
			return BlockUtils.getProtectedCapability(side, this, () -> super.getCapability(cap, side), () -> getInsertOnlyHandler()).cast();
		else
			return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		if (insertOnlyHandler != null)
			insertOnlyHandler.invalidate();

		super.invalidateCaps();
	}

	@Override
	public void reviveCaps() {
		insertOnlyHandler = null; //recreated in getInsertOnlyHandler
		super.reviveCaps();
	}

	private LazyOptional<IItemHandler> getInsertOnlyHandler() {
		if (insertOnlyHandler == null)
			insertOnlyHandler = LazyOptional.of(() -> new InsertOnlyInvWrapper(AbstractKeypadFurnaceBlockEntity.this));

		return insertOnlyHandler;
	}

	@Override
	public boolean enableHack() {
		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		return slot >= 100 ? getModuleInSlot(slot) : items.get(slot);
	}

	@Override
	public boolean shouldAttemptCodebreak(BlockState state, Player player) {
		if (isDisabled()) {
			player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			return false;
		}

		return IPasswordProtected.super.shouldAttemptCodebreak(state, player);
	}

	@Override
	public void activate(Player player) {
		if (!level.isClientSide && getBlockState().getBlock() instanceof AbstractKeypadFurnaceBlock block)
			block.activate(this, getBlockState(), level, worldPosition, player);
	}

	@Override
	public void startOpen(Player player) {
		if (!remove && !player.isSpectator())
			openersCounter.incrementOpeners(player, this.getLevel(), getBlockPos(), getBlockState());
	}

	@Override
	public void stopOpen(Player player) {
		if (!remove && !player.isSpectator())
			openersCounter.decrementOpeners(player, getLevel(), getBlockPos(), getBlockState());
	}

	public void recheckOpen() {
		if (!remove)
			openersCounter.recheckOpeners(getLevel(), getBlockPos(), getBlockState());
	}

	@Override
	public String getPassword() {
		return passcode != null && !passcode.isEmpty() ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		passcode = password;
		setChanged();
	}

	public ContainerData getFurnaceData() {
		return dataAccess;
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable(getBlockState().getBlock().getDescriptionId());
	}

	@Override
	public NonNullList<ItemStack> getInventory() {
		return modules;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendMessage, disabled
		};
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		IModuleInventory.super.onModuleInserted(stack, module, toggled);

		if (module == ModuleType.DISGUISE) {
			BlockState state = getBlockState();

			if (!level.isClientSide) {
				SecurityCraft.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new RefreshDisguisableModel(worldPosition, true, stack, toggled));

				if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
					level.scheduleTick(worldPosition, Fluids.WATER, Fluids.WATER.getTickDelay(level));
					level.updateNeighborsAt(worldPosition, state.getBlock());
				}
			}
			else {
				ClientHandler.putDisguisedBeRenderer(this, stack);

				if (state.getLightEmission(level, worldPosition) > 0)
					level.getChunkSource().getLightEngine().checkBlock(worldPosition);
			}
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		if (level.isClientSide)
			ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(getBlockEntity());
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		IModuleInventory.super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.DISGUISE) {
			if (!level.isClientSide) {
				BlockState state = getBlockState();

				SecurityCraft.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new RefreshDisguisableModel(worldPosition, false, stack, toggled));

				if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
					level.scheduleTick(worldPosition, Fluids.WATER, Fluids.WATER.getTickDelay(level));
					level.updateNeighborsAt(worldPosition, state.getBlock());
				}
			}
			else {
				ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(this);
				DisguisableBlock.getDisguisedBlockStateFromStack(level, stack).ifPresent(disguisedState -> {
					if (disguisedState.getLightEmission(level, worldPosition) > 0)
						level.getChunkSource().getLightEngine().checkBlock(worldPosition);
				});
			}
		}
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
	public ModelData getModelData() {
		BlockState disguisedState = DisguisableBlock.getDisguisedStateOrDefault(Blocks.AIR.defaultBlockState(), level, worldPosition);

		return ModelData.builder().with(DisguisableDynamicBakedModel.DISGUISED_STATE, disguisedState).build();
	}

	public boolean sendsMessages() {
		return sendMessage.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}
}
