package net.geforcemods.securitycraft.entity;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCTags;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.EntityDataWrappedOption;
import net.geforcemods.securitycraft.api.Option.SendAllowlistMessageOption;
import net.geforcemods.securitycraft.api.Option.SendDenylistMessageOption;
import net.geforcemods.securitycraft.api.Option.SmartModuleCooldownOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.inventory.CustomizeBlockMenu;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

public class SecuritySeaBoat extends ChestBoat implements IOwnable, IPasscodeProtected, IModuleInventory, ICustomizable {
	private static final EntityDataAccessor<Owner> OWNER = SynchedEntityData.<Owner>defineId(SecuritySeaBoat.class, Owner.getSerializer());
	private static final EntityDataAccessor<Boolean> SEND_ALLOWLIST_MESSAGE = SynchedEntityData.<Boolean>defineId(SecuritySeaBoat.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> SEND_DENYLIST_MESSAGE = SynchedEntityData.<Boolean>defineId(SecuritySeaBoat.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> SMART_MODULE_COOLDOWN = SynchedEntityData.<Integer>defineId(SecuritySeaBoat.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Long> COOLDOWN_END = SynchedEntityData.<Long>defineId(SecuritySeaBoat.class, EntityDataSerializers.LONG);
	private static final EntityDataAccessor<Map<ModuleType, Boolean>> MODULE_STATES = SynchedEntityData.<Map<ModuleType, Boolean>>defineId(SecuritySeaBoat.class, SCContent.MODULE_STATES_SERIALIZER.get());
	private static final EntityDataAccessor<NonNullList<ItemStack>> MODULES = SynchedEntityData.<NonNullList<ItemStack>>defineId(SecuritySeaBoat.class, SCContent.ITEM_STACK_LIST_SERIALIZER.get());
	private byte[] passcode;
	private UUID saltKey;
	private EntityDataWrappedOption<Boolean, Option<Boolean>> sendAllowlistMessage = new SendAllowlistMessageOption(false).wrapForEntityData(SEND_ALLOWLIST_MESSAGE, () -> entityData);
	private EntityDataWrappedOption<Boolean, Option<Boolean>> sendDenylistMessage = new SendDenylistMessageOption(true).wrapForEntityData(SEND_DENYLIST_MESSAGE, () -> entityData);
	private EntityDataWrappedOption<Integer, Option<Integer>> smartModuleCooldown = new SmartModuleCooldownOption().wrapForEntityData(SMART_MODULE_COOLDOWN, () -> entityData);
	private boolean isInLava = false;
	private LazyOptional<IItemHandler> insertOnlyHandler;

	public SecuritySeaBoat(EntityType<? extends Boat> type, Level level) {
		super(SCContent.SECURITY_SEA_BOAT_ENTITY.get(), level);
	}

	public SecuritySeaBoat(Level level, double x, double y, double z) {
		super(SCContent.SECURITY_SEA_BOAT_ENTITY.get(), level);
		setPos(x, y, z);
		xo = y;
		yo = y;
		zo = z;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(OWNER, new Owner());
		entityData.define(SEND_ALLOWLIST_MESSAGE, false);
		entityData.define(SEND_DENYLIST_MESSAGE, true);
		entityData.define(SMART_MODULE_COOLDOWN, 100);
		entityData.define(COOLDOWN_END, 0L);
		entityData.define(MODULE_STATES, new EnumMap<>(ModuleType.class));
		entityData.define(MODULES, NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY));
	}

	@Override
	public boolean canAddPassenger(Entity passenger) {
		return super.canAddPassenger(passenger) && (isOwnedBy(passenger) || isAllowed(passenger)) && !isDenied(passenger);
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		Level level = player.level;

		if (isDenied(player) && !isOwnedBy(player) && !(player.isSecondaryUseActive() && stack.is(SCContent.CODEBREAKER.get()))) {
			if (sendsDenylistMessage())
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(getType().getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);

			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		if (player.isSecondaryUseActive()) {
			if (stack.is(SCContent.CODEBREAKER.get())) {
				if (!level.isClientSide)
					handleCodebreaking(player, player.getMainHandItem().is(SCContent.CODEBREAKER.get()) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);

				return InteractionResult.sidedSuccess(level.isClientSide);
			}
			else if (stack.is(SCContent.UNIVERSAL_KEY_CHANGER.get())) {
				if (!level.isClientSide) {
					if (isOwnedBy(player) || player.isCreative())
						SecurityCraft.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(DataType.CHANGE_PASSCODE_FOR_ENTITY, getId()));
					else
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(getOwner())), ChatFormatting.RED);
				}

				return InteractionResult.sidedSuccess(level.isClientSide);
			}
			else if (stack.is(SCContent.UNIVERSAL_OWNER_CHANGER.get()) && isOwnedBy(player)) {
				if (!level.isClientSide) {
					String newOwner = stack.getHoverName().getString();

					//disable this in a development environment
					if (FMLEnvironment.production)
						dropAllModules();

					setOwner(PlayerUtils.isPlayerOnline(newOwner) ? PlayerUtils.getPlayerFromName(newOwner).getUUID().toString() : "ownerUUID", newOwner);
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.changed", newOwner), ChatFormatting.GREEN);
				}

				return InteractionResult.sidedSuccess(level.isClientSide);
			}
			else if (stack.is(SCContent.UNIVERSAL_BLOCK_MODIFIER.get())) {
				if (isOwnedBy(player)) {
					if (!level.isClientSide) {
						BlockPos pos = blockPosition();

						NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
							@Override
							public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
								return new CustomizeBlockMenu(windowId, level, pos, SecuritySeaBoat.super.getId(), inv);
							}

							@Override
							public Component getDisplayName() {
								return SecuritySeaBoat.super.getDisplayName();
							}
						}, data -> {
							data.writeBlockPos(pos);
							data.writeVarInt(SecuritySeaBoat.super.getId());
						});
					}
				}
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(getOwner())), ChatFormatting.RED);

				return InteractionResult.sidedSuccess(level.isClientSide);
			}
			else if (stack.is(SCContent.UNIVERSAL_BLOCK_REMOVER.get())) {
				if (isOwnedBy(player) || player.isCreative())
					destroy(damageSources().playerAttack(player));
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_BLOCK_REMOVER.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(getOwner())), ChatFormatting.RED);
			}
		}
		else if (!canAddPassenger(player)) {
			if (isDenied(player))
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(getType().getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);
			else
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(getType().getDescriptionId()), Utils.localize("messages.securitycraft:security_sea_boat.cant_enter", PlayerUtils.getOwnerComponent(getOwner())), ChatFormatting.RED);

			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		return super.interact(player, hand);
	}

	@Override
	public InteractionResult interactWithContainerVehicle(Player player) {
		BlockPos pos = blockPosition();

		if (!level.isClientSide && verifyPasscodeSet(level, pos, this, player)) {
			if (isDenied(player)) {
				if (sendsDenylistMessage())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getType().getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);
			}
			else if (isAllowed(player)) {
				if (sendsAllowlistMessage())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getType().getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), ChatFormatting.GREEN);

				activate(player);
			}
			else
				openPasscodeGUI(level, pos, player);
		}

		return !level.isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
	}

	@Override
	public void openCustomInventoryScreen(Player player) {
		interactWithContainerVehicle(player);
	}

	@Override
	public void openPasscodeGUI(Level level, BlockPos pos, Player player) {
		if (!level.isClientSide && getPasscode() != null)
			SecurityCraft.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(DataType.CHECK_PASSCODE_FOR_ENTITY, getId()));
	}

	@Override
	public void openSetPasscodeScreen(ServerPlayer player, BlockPos pos) {
		SecurityCraft.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new OpenScreen(DataType.SET_PASSCODE_FOR_ENTITY, getId()));
	}

	@Override
	public boolean canBoatInFluid(FluidState state) {
		return super.canBoatInFluid(state) || state.is(Fluids.LAVA);
	}

	@Override
	public boolean canBoatInFluid(FluidType type) {
		return super.canBoatInFluid(type) || type == ForgeMod.LAVA_TYPE.get();
	}

	@Override
	public boolean checkInWater() {
		isInLava = level.getFluidState(blockPosition()).is(Fluids.LAVA);
		return super.checkInWater();
	}

	@Override
	public void setDeltaMovement(Vec3 deltaMovement) {
		if (isInLava)
			super.setDeltaMovement(deltaMovement.scale(0.5F));
		else
			super.setDeltaMovement(deltaMovement);
	}

	@Override
	public void tick() {
		super.tick();

		if (!level.isClientSide && isInLava) {
			Entity passenger = getFirstPassenger();

			if (passenger != null) {
				if (!passenger.fireImmune()) {
					passenger.setRemainingFireTicks(passenger.getRemainingFireTicks() + 1);

					if (passenger.getRemainingFireTicks() == 0)
						passenger.setSecondsOnFire(8);
				}

				passenger.hurt(level.damageSources().inFire(), 1.0F);
			}
		}
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		Entity entity = source.getEntity();

		if (!(entity instanceof Player player) || isOwnedBy(player) || player.isCreative())
			return super.hurt(source, amount);
		else
			return false;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource source) {
		return !source.is(SCTags.DamageTypes.SECURITY_SEA_BOAT_VULNERABLE_TO) || super.isInvulnerableTo(source);
	}

	@Override
	public void chestVehicleDestroyed(DamageSource damageSource, Level level, Entity entity) {
		super.chestVehicleDestroyed(damageSource, level, entity);
		SaltData.removeSalt(getSaltKey());
	}

	@Override
	public Item getDropItem() {
		return (switch (getVariant()) {
			case SPRUCE -> SCContent.SPRUCE_SECURITY_SEA_BOAT;
			case BIRCH -> SCContent.BIRCH_SECURITY_SEA_BOAT;
			case JUNGLE -> SCContent.JUNGLE_SECURITY_SEA_BOAT;
			case ACACIA -> SCContent.ACACIA_SECURITY_SEA_BOAT;
			case DARK_OAK -> SCContent.DARK_OAK_SECURITY_SEA_BOAT;
			case MANGROVE -> SCContent.MANGROVE_SECURITY_SEA_BOAT;
			case CHERRY -> SCContent.CHERRY_SECURITY_SEA_BOAT;
			case BAMBOO -> SCContent.BAMBOO_SECURITY_SEA_RAFT;
			default -> SCContent.OAK_SECURITY_SEA_BOAT;
		}).get();
	}

	@Override
	public void remove(RemovalReason reason) {
		if (!level.isClientSide && reason.shouldDestroy())
			Containers.dropContents(level, blockPosition(), getInventory());

		super.remove(reason);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (isAlive() && cap == ForgeCapabilities.ITEM_HANDLER)
			return BlockUtils.isAllowedToExtractFromProtectedBlock(side, this, level, blockPosition()) ? super.getCapability(cap, side) : getInsertOnlyHandler().cast();
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
			insertOnlyHandler = LazyOptional.of(() -> new InsertOnlyInvWrapper(SecuritySeaBoat.this));

		return insertOnlyHandler;
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		CompoundTag ownerTag = new CompoundTag();
		long cooldownLeft;

		super.addAdditionalSaveData(tag);
		writeModuleInventory(tag);
		writeModuleStates(tag);
		writeOptions(tag);
		cooldownLeft = getCooldownEnd() - System.currentTimeMillis();
		tag.putLong("cooldownLeft", cooldownLeft <= 0 ? -1 : cooldownLeft);
		getOwner().save(ownerTag, needsValidation());
		tag.put("owner", ownerTag);

		if (saltKey != null)
			tag.putUUID("saltKey", saltKey);

		if (passcode != null)
			tag.putString("passcode", PasscodeUtils.bytesToString(passcode));
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		entityData.set(MODULES, readModuleInventory(tag));
		entityData.set(MODULE_STATES, readModuleStates(tag));
		readOptions(tag);
		entityData.set(COOLDOWN_END, System.currentTimeMillis() + tag.getLong("cooldownLeft"));
		entityData.set(OWNER, Owner.fromCompound(tag.getCompound("owner")));
		loadSaltKey(tag);
		loadPasscode(tag);
	}

	@Override
	public void onOptionChanged(Option<?> option) {
		if (!level.isClientSide) {
			if (option == sendAllowlistMessage)
				entityData.set(SEND_ALLOWLIST_MESSAGE, sendAllowlistMessage.get());
			else if (option == sendDenylistMessage)
				entityData.set(SEND_DENYLIST_MESSAGE, sendDenylistMessage.get());
			else if (option == smartModuleCooldown)
				entityData.set(SMART_MODULE_COOLDOWN, smartModuleCooldown.get());
		}
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (level.isClientSide) {
			if (key == SEND_ALLOWLIST_MESSAGE)
				sendAllowlistMessage.setValue(entityData.get(SEND_ALLOWLIST_MESSAGE));
			else if (key == SEND_DENYLIST_MESSAGE)
				sendDenylistMessage.setValue(entityData.get(SEND_DENYLIST_MESSAGE));
			else if (key == SMART_MODULE_COOLDOWN)
				smartModuleCooldown.setValue(entityData.get(SMART_MODULE_COOLDOWN));
		}

		super.onSyncedDataUpdated(key);
	}

	public void setOwner(Player player) {
		if (player != null)
			setOwner(player.getGameProfile().getId().toString(), player.getName().getString());
	}

	@Override
	public void setOwner(String uuid, String name) {
		entityData.set(OWNER, new Owner(name, uuid));
	}

	@Override
	public Owner getOwner() {
		return entityData.get(OWNER);
	}

	@Override
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player) {}

	@Override
	public void activate(Player player) {
		//super is necessary here, because the override doesn't open the screen directly and instead opens the passcode screens
		super.openCustomInventoryScreen(player);
	}

	@Override
	public byte[] getPasscode() {
		return passcode == null || passcode.length == 0 ? null : passcode;
	}

	@Override
	public void setPasscode(byte[] passcode) {
		this.passcode = passcode;
	}

	@Override
	public UUID getSaltKey() {
		return saltKey;
	}

	@Override
	public void setSaltKey(UUID saltKey) {
		this.saltKey = saltKey;
	}

	@Override
	public void startCooldown() {
		if (!isOnCooldown())
			entityData.set(COOLDOWN_END, System.currentTimeMillis() + smartModuleCooldown.get() * 50);
	}

	@Override
	public boolean isOnCooldown() {
		return System.currentTimeMillis() < getCooldownEnd();
	}

	@Override
	public long getCooldownEnd() {
		return entityData.get(COOLDOWN_END);
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				sendAllowlistMessage, sendDenylistMessage, smartModuleCooldown
		};
	}

	@Override
	public NonNullList<ItemStack> getInventory() {
		return entityData.get(MODULES);
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DENYLIST, ModuleType.SMART, ModuleType.HARMING
		};
	}

	@Override
	public boolean isModuleEnabled(ModuleType module) {
		return hasModule(module) && entityData.get(MODULE_STATES).get(module) == Boolean.TRUE; //prevent NPE
	}

	@Override
	public void toggleModuleState(ModuleType module, boolean shouldBeEnabled) {
		Map<ModuleType, Boolean> moduleStates = entityData.get(MODULE_STATES);

		moduleStates.put(module, shouldBeEnabled);
		entityData.set(MODULE_STATES, moduleStates);
	}

	@Override
	public String getModuleDescriptionId(String denotation, ModuleType module) {
		return IModuleInventory.super.getModuleDescriptionId("generic." + denotation, module);
	}

	@Override
	public Level myLevel() {
		return level;
	}

	@Override
	public BlockPos myPos() {
		return blockPosition();
	}

	public boolean sendsAllowlistMessage() {
		return sendAllowlistMessage.get();
	}

	public void setSendsAllowlistMessage(boolean value) {
		sendAllowlistMessage.setValue(value);
	}

	public boolean sendsDenylistMessage() {
		return sendDenylistMessage.get();
	}

	public void setSendsDenylistMessage(boolean value) {
		sendDenylistMessage.setValue(value);
	}
}
