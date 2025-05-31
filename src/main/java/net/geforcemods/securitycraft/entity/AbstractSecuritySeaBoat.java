package net.geforcemods.securitycraft.entity;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
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
import net.geforcemods.securitycraft.components.OwnerData;
import net.geforcemods.securitycraft.components.PasscodeData;
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
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.vehicle.AbstractChestBoat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.network.PacketDistributor;

public abstract class AbstractSecuritySeaBoat extends AbstractChestBoat implements IOwnable, IPasscodeProtected, IModuleInventory, ICustomizable {
	private static final EntityDataAccessor<Owner> OWNER = SynchedEntityData.<Owner>defineId(AbstractSecuritySeaBoat.class, Owner.getSerializer());
	private static final EntityDataAccessor<Boolean> SEND_ALLOWLIST_MESSAGE = SynchedEntityData.<Boolean>defineId(AbstractSecuritySeaBoat.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> SEND_DENYLIST_MESSAGE = SynchedEntityData.<Boolean>defineId(AbstractSecuritySeaBoat.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> SMART_MODULE_COOLDOWN = SynchedEntityData.<Integer>defineId(AbstractSecuritySeaBoat.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Long> COOLDOWN_END = SynchedEntityData.<Long>defineId(AbstractSecuritySeaBoat.class, EntityDataSerializers.LONG);
	private static final EntityDataAccessor<Map<ModuleType, Boolean>> MODULE_STATES = SynchedEntityData.<Map<ModuleType, Boolean>>defineId(AbstractSecuritySeaBoat.class, SCContent.MODULE_STATES_SERIALIZER.get());
	private static final EntityDataAccessor<NonNullList<ItemStack>> MODULES = SynchedEntityData.<NonNullList<ItemStack>>defineId(AbstractSecuritySeaBoat.class, SCContent.ITEM_STACK_LIST_SERIALIZER.get());
	private byte[] passcode;
	private UUID saltKey;
	private boolean saveSalt;
	private EntityDataWrappedOption<Boolean> sendAllowlistMessage = new SendAllowlistMessageOption(false).wrapForEntityData(SEND_ALLOWLIST_MESSAGE, () -> entityData);
	private EntityDataWrappedOption<Boolean> sendDenylistMessage = new SendDenylistMessageOption(true).wrapForEntityData(SEND_DENYLIST_MESSAGE, () -> entityData);
	private EntityDataWrappedOption<Integer> smartModuleCooldown = new SmartModuleCooldownOption().wrapForEntityData(SMART_MODULE_COOLDOWN, () -> entityData);
	private boolean isInLava = false;

	protected AbstractSecuritySeaBoat(EntityType<? extends AbstractSecuritySeaBoat> type, Level level, Supplier<Item> dropItem) {
		super(type, level, dropItem);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(OWNER, new Owner());
		builder.define(SEND_ALLOWLIST_MESSAGE, false);
		builder.define(SEND_DENYLIST_MESSAGE, true);
		builder.define(SMART_MODULE_COOLDOWN, 100);
		builder.define(COOLDOWN_END, 0L);
		builder.define(MODULE_STATES, new EnumMap<>(ModuleType.class));
		builder.define(MODULES, NonNullList.<ItemStack>withSize(getMaxNumberOfModules(), ItemStack.EMPTY));
	}

	@Override
	public boolean canAddPassenger(Entity passenger) {
		return super.canAddPassenger(passenger) && (isOwnedBy(passenger) || isAllowed(passenger)) && !isDenied(passenger);
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		Level level = player.level();

		if (isDenied(player) && !isOwnedBy(player) && !(player.isSecondaryUseActive() && stack.is(SCContent.CODEBREAKER.get()))) {
			if (sendsDenylistMessage())
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(getType().getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);

			return InteractionResult.SUCCESS;
		}

		if (player.isSecondaryUseActive()) {
			if (stack.is(SCContent.CODEBREAKER.get())) {
				if (!level.isClientSide)
					handleCodebreaking(player, player.getMainHandItem().is(SCContent.CODEBREAKER.get()) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);

				return InteractionResult.SUCCESS;
			}
			else if (stack.is(SCContent.UNIVERSAL_KEY_CHANGER.get())) {
				if (!level.isClientSide) {
					if (isOwnedBy(player) || player.isCreative())
						PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenScreen(DataType.CHANGE_PASSCODE_FOR_ENTITY, getId()));
					else
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(getOwner())), ChatFormatting.RED);
				}

				return InteractionResult.SUCCESS;
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

				return InteractionResult.SUCCESS;
			}
			else if (stack.is(SCContent.UNIVERSAL_BLOCK_MODIFIER.get())) {
				if (isOwnedBy(player)) {
					if (!level.isClientSide) {
						BlockPos pos = blockPosition();

						player.openMenu(new MenuProvider() {
							@Override
							public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
								return new CustomizeBlockMenu(windowId, level, pos, AbstractSecuritySeaBoat.super.getId(), inv);
							}

							@Override
							public Component getDisplayName() {
								return AbstractSecuritySeaBoat.super.getDisplayName();
							}

							@Override
							public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
								buffer.writeBlockPos(pos);
								buffer.writeVarInt(AbstractSecuritySeaBoat.super.getId());
							}
						});
					}
				}
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_OWNER_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(getOwner())), ChatFormatting.RED);

				return InteractionResult.SUCCESS;
			}
			else if (stack.is(SCContent.UNIVERSAL_BLOCK_REMOVER.get()) && !ConfigHandler.SERVER.vanillaToolBlockBreaking.get() && !level.isClientSide) {
				if (isOwnedBy(player) || player.isCreative())
					destroy((ServerLevel) level, damageSources().playerAttack(player));
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_BLOCK_REMOVER.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(getOwner())), ChatFormatting.RED);
			}
		}
		else if (!canAddPassenger(player)) {
			if (isDenied(player))
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(getType().getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);
			else
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(getType().getDescriptionId()), Utils.localize("messages.securitycraft:security_sea_boat.cant_enter", PlayerUtils.getOwnerComponent(getOwner())), ChatFormatting.RED);

			return InteractionResult.SUCCESS;
		}

		return super.interact(player, hand);
	}

	@Override
	public InteractionResult interactWithContainerVehicle(Player player) {
		Level level = level();
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
			PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenScreen(DataType.CHECK_PASSCODE_FOR_ENTITY, getId()));
	}

	@Override
	public void openSetPasscodeScreen(ServerPlayer player, BlockPos pos) {
		PacketDistributor.sendToPlayer(player, new OpenScreen(DataType.SET_PASSCODE_FOR_ENTITY, getId()));
	}

	@Override
	public boolean canBoatInFluid(FluidState state) {
		return super.canBoatInFluid(state) || state.is(Fluids.LAVA);
	}

	@Override
	public boolean canBoatInFluid(FluidType type) {
		return super.canBoatInFluid(type) || type == NeoForgeMod.LAVA_TYPE;
	}

	@Override
	public boolean checkInWater() {
		isInLava = level().getFluidState(blockPosition()).is(Fluids.LAVA);
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

		if (!level().isClientSide && isInLava) {
			Entity passenger = getFirstPassenger();

			if (passenger != null) {
				if (!passenger.fireImmune()) {
					passenger.setRemainingFireTicks(passenger.getRemainingFireTicks() + 1);

					if (passenger.getRemainingFireTicks() == 0)
						passenger.igniteForSeconds(8);
				}

				passenger.hurt(level().damageSources().inFire(), 1.0F);
			}
		}
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		return source.getEntity() instanceof Player player && isOwnedBy(player) && super.hurtServer(level, source, amount);
	}

	@Override
	public void remove(RemovalReason reason) {
		if (!level().isClientSide && reason.shouldDestroy()) {
			Containers.dropContents(level(), blockPosition(), getInventory());
			SaltData.removeSalt(getSaltKey());
		}

		super.remove(reason);
	}

	public static IItemHandler getCapability(AbstractSecuritySeaBoat boat, Direction direction) {
		return BlockUtils.isAllowedToExtractFromProtectedObject(direction, boat, boat.level(), boat.blockPosition()) ? new InvWrapper(boat) : new InsertOnlyInvWrapper(boat);
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput tag) {
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
		savePasscodeAndSalt(tag);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput tag) {
		super.readAdditionalSaveData(tag);
		entityData.set(MODULES, readModuleInventory(tag));
		entityData.set(MODULE_STATES, readModuleStates(tag));
		readOptions(tag);
		entityData.set(COOLDOWN_END, System.currentTimeMillis() + tag.getLongOr("cooldownLeft", 0));
		entityData.set(OWNER, Owner.fromCompound(tag.getCompoundOrEmpty("owner")));
		loadSaltKey(tag);
		loadPasscode(tag);
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (!level().isClientSide)
			entityData.set(((EntityDataWrappedOption<T>) option).getEntityDataKey(), option.get());
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (level().isClientSide) {
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
	public void onOwnerChanged(BlockState state, Level level, BlockPos pos, Player player, Owner oldOwner, Owner newOwner) {}

	@Override
	protected void applyImplicitComponents(DataComponentGetter getter) {
		super.applyImplicitComponents(getter);
		applyImplicitComponentIfPresent(getter, SCContent.OWNER_DATA.get());
		applyImplicitComponentIfPresent(getter, SCContent.PASSCODE_DATA.get());
	}

	@Override
	protected <T> boolean applyImplicitComponent(DataComponentType<T> type, T value) {
		if (type == SCContent.OWNER_DATA.get()) {
			OwnerData ownerData = castComponentValue(SCContent.OWNER_DATA.get(), value);

			setOwner(ownerData.uuid(), ownerData.name());
			return true;
		}
		else if (type == SCContent.PASSCODE_DATA.get()) {
			PasscodeData passcodeData = castComponentValue(SCContent.PASSCODE_DATA.get(), value);

			setPasscode(PasscodeUtils.stringToBytes(passcodeData.passcode()));
			setSaltKey(passcodeData.saltKey());
			return true;
		}

		return super.applyImplicitComponent(type, value);
	}

	@Override
	public <T> T get(DataComponentType<? extends T> type) {
		if (type == SCContent.OWNER_DATA.get())
			return (T) OwnerData.fromOwner(getOwner());
		else if (type == SCContent.PASSCODE_DATA.get())
			return (T) new PasscodeData(PasscodeUtils.bytesToString(getPasscode()), getSaltKey());
		else
			return super.get(type);
	}

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
	public void setSaveSalt(boolean saveSalt) {
		this.saveSalt = saveSalt;
	}

	@Override
	public boolean shouldSaveSalt() {
		return saveSalt;
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
		return IModuleInventory.super.getModuleDescriptionId("generic.security_sea_boat", module);
	}

	@Override
	public Level myLevel() {
		return level();
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
