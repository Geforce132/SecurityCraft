package net.geforcemods.securitycraft.blockentities;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.ToIntFunction;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity.TeleportationType;
import net.geforcemods.securitycraft.blocks.RiftStabilizerBlock;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SyncRiftStabilizer;
import net.geforcemods.securitycraft.util.IToggleableEntries;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.EntityTeleportEvent.ChorusFruit;
import net.minecraftforge.event.entity.living.EntityTeleportEvent.EnderEntity;
import net.minecraftforge.event.entity.living.EntityTeleportEvent.EnderPearl;
import net.minecraftforge.event.entity.living.EntityTeleportEvent.SpreadPlayersCommand;
import net.minecraftforge.event.entity.living.EntityTeleportEvent.TeleportCommand;

public class RiftStabilizerBlockEntity extends DisguisableBlockEntity implements ITickableTileEntity, ILockable, IToggleableEntries<TeleportationType> {
	private final IntOption signalLength = new IntOption(this::getBlockPos, "signalLength", 60, 0, 400, 5); //20 seconds max
	private final IntOption range = new IntOption(this::getBlockPos, "range", 5, 1, 15, 1);
	private final DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private final Map<TeleportationType, Boolean> teleportationFilter = new EnumMap<>(TeleportationType.class);
	private double lastTeleportDistance;
	private TeleportationType lastTeleportationType;
	private boolean tracked = false;

	public RiftStabilizerBlockEntity() {
		super(SCContent.RIFT_STABILIZER_BLOCK_ENTITY.get());
		//when adding new types ONLY ADD TO THE END. anything else will break saved data.
		//ordering is done in ToggleListScreen based on the user's current language
		teleportationFilter.put(TeleportationType.CHORUS_FRUIT, true);
		teleportationFilter.put(TeleportationType.ENDER_PEARL, true);
		teleportationFilter.put(TeleportationType.ENDERMAN, false);
		teleportationFilter.put(TeleportationType.SHULKER, false);
		teleportationFilter.put(TeleportationType.MODDED, false);
	}

	@Override
	public void tick() {
		if (getBlockState().getValue(RiftStabilizerBlock.HALF) == DoubleBlockHalf.LOWER && !tracked) {
			BlockEntityTracker.RIFT_STABILIZER.track(this);
			tracked = true;
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		// Stop tracking Rift Stabilizers when they are removed from the world
		BlockEntityTracker.RIFT_STABILIZER.stopTracking(this);
	}

	@Override
	public void setFilter(TeleportationType teleportationType, boolean allowed) {
		if (teleportationFilter.containsKey(teleportationType)) {
			teleportationFilter.put(teleportationType, allowed);
			setChanged();

			if (level.isClientSide)
				SecurityCraft.channel.sendToServer(new SyncRiftStabilizer(worldPosition, teleportationType, allowed));

			RiftStabilizerBlockEntity connectedBlockEntity = RiftStabilizerBlock.getConnectedBlockEntity(level, worldPosition);

			if (connectedBlockEntity != null) {
				connectedBlockEntity.teleportationFilter.put(teleportationType, allowed);
				connectedBlockEntity.setChanged();
			}
		}
	}

	@Override
	public boolean getFilter(TeleportationType teleportationType) {
		return teleportationFilter.containsKey(teleportationType) && teleportationFilter.get(teleportationType);
	}

	@Override
	public ToIntFunction<TeleportationType> getComparatorOutputFunction() {
		return t -> t.ordinal() + 1;
	}

	@Override
	public Map<TeleportationType, Boolean> getFilters() {
		return teleportationFilter;
	}

	@Override
	public TeleportationType getDefaultType() {
		return TeleportationType.MODDED;
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);

		CompoundNBT teleportationNBT = new CompoundNBT();
		int i = 0;

		for (boolean b : teleportationFilter.values()) {
			teleportationNBT.putBoolean("teleportationType" + i, b);
			i++;
		}

		tag.put("teleportationTypes", teleportationNBT);
		tag.putDouble("lastTeleportDistance", lastTeleportDistance);

		if (lastTeleportationType != null)
			tag.putInt("lastTeleportationType", lastTeleportationType.ordinal());

		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);

		if (tag.contains("teleportationTypes", NBT.TAG_COMPOUND)) {
			CompoundNBT teleportationNBT = tag.getCompound("teleportationTypes");
			int i = 0;

			for (TeleportationType teleportationType : teleportationFilter.keySet()) {
				teleportationFilter.put(teleportationType, teleportationNBT.getBoolean("teleportationType" + i));
				i++;
			}
		}

		lastTeleportDistance = tag.getDouble("lastTeleportDistance");

		if (tag.contains("lastTeleportationType"))
			lastTeleportationType = TeleportationType.values()[tag.getInt("lastTeleportationType")];
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		RiftStabilizerBlockEntity connectedBlockEntity = RiftStabilizerBlock.getConnectedBlockEntity(level, worldPosition);

		if (connectedBlockEntity != null && (toggled ? !connectedBlockEntity.isModuleEnabled(module) : !connectedBlockEntity.hasModule(module)))
			connectedBlockEntity.insertModule(stack, toggled);

		if (module == ModuleType.DISGUISE) {
			onInsertDisguiseModule(this, stack);

			if (connectedBlockEntity != null)
				onInsertDisguiseModule(connectedBlockEntity, stack);
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		RiftStabilizerBlockEntity connectedBlockEntity = RiftStabilizerBlock.getConnectedBlockEntity(level, worldPosition);

		if (connectedBlockEntity != null && (toggled ? connectedBlockEntity.isModuleEnabled(module) : connectedBlockEntity.hasModule(module)))
			connectedBlockEntity.removeModule(module, toggled);

		if (module == ModuleType.DISGUISE) {
			onRemoveDisguiseModule(this);

			if (connectedBlockEntity != null)
				onRemoveDisguiseModule(connectedBlockEntity);
		}
		else if (module == ModuleType.SMART) {
			onRemoveSmartModule(this);

			if (connectedBlockEntity != null)
				onRemoveSmartModule(connectedBlockEntity);
		}
	}

	private void onInsertDisguiseModule(TileEntity be, ItemStack stack) {
		if (!be.getLevel().isClientSide)
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
		else
			ClientHandler.putDisguisedBeRenderer(be, stack);
	}

	private void onRemoveDisguiseModule(TileEntity be) {
		if (!be.getLevel().isClientSide)
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
		else
			ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.removeDelegateOf(be);
	}

	private void onRemoveSmartModule(RiftStabilizerBlockEntity be) {
		be.teleportationFilter.put(TeleportationType.CHORUS_FRUIT, true);
		be.teleportationFilter.put(TeleportationType.ENDER_PEARL, true);
		be.teleportationFilter.put(TeleportationType.ENDERMAN, false);
		be.teleportationFilter.put(TeleportationType.SHULKER, false);
		be.teleportationFilter.put(TeleportationType.MODDED, false);
	}

	public void setLastTeleport(double teleportDistance, TeleportationType type) {
		lastTeleportDistance = teleportDistance;
		lastTeleportationType = type;
	}

	public double getLastTeleportDistance() {
		return lastTeleportDistance;
	}

	public TeleportationType getLastTeleportationType() {
		return lastTeleportationType;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DISGUISE, ModuleType.REDSTONE, ModuleType.HARMING, ModuleType.SMART
		};
	}

	@Override
	public void onOptionChanged(Option<?> option) {
		RiftStabilizerBlockEntity connectedBlockEntity = RiftStabilizerBlock.getConnectedBlockEntity(level, worldPosition);

		if (connectedBlockEntity != null) {
			if (option.getName().equals("signalLength"))
				connectedBlockEntity.setSignalLength(((IntOption) option).get());
			else if (option.getName().equals("range"))
				connectedBlockEntity.setRange(((IntOption) option).get());
			else if (option.getName().equals("disabled"))
				connectedBlockEntity.setDisabled(((BooleanOption) option).get());
			else if (option.getName().equals("ignoreOwner"))
				connectedBlockEntity.setIgnoresOwner(((BooleanOption) option).get());
		}

		super.onOptionChanged(option);
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				signalLength, range, disabled, ignoreOwner
		};
	}

	@Override
	public void onOwnerChanged(BlockState state, World level, BlockPos pos, PlayerEntity player) {
		RiftStabilizerBlockEntity be = RiftStabilizerBlock.getConnectedBlockEntity(level, pos);

		if (be != null) {
			be.setOwner(getOwner().getUUID(), getOwner().getName());

			if (!level.isClientSide)
				level.getServer().getPlayerList().broadcastAll(be.getUpdatePacket());
		}

		super.onOwnerChanged(state, level, pos, player);
	}

	public void setSignalLength(int signalLength) {
		if (getSignalLength() != signalLength) {
			this.signalLength.setValue(signalLength);
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BlockStateProperties.POWERED, false));
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
			setChanged();
		}
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public void setRange(int range) {
		if (getRange() != range) {
			this.range.setValue(range);
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
			setChanged();
		}
	}

	public int getRange() {
		return range.get();
	}

	public void setDisabled(boolean disabled) {
		if (isDisabled() != disabled) {
			this.disabled.setValue(disabled);
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
			setChanged();
		}
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	public void setIgnoresOwner(boolean ignoresOwner) {
		if (ignoresOwner() != ignoresOwner) {
			ignoreOwner.setValue(ignoresOwner);
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); //sync option change to client
			setChanged();
		}
	}

	@Override
	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	@Override
	public void setCustomName(ITextComponent customName) {
		super.setCustomName(customName);

		if (getBlockState().getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
			((INameSetter) level.getBlockEntity(worldPosition.above())).setCustomName(customName);
	}

	public enum TeleportationType {
		CHORUS_FRUIT(Items.CHORUS_FRUIT.getDescriptionId()),
		ENDER_PEARL(Items.ENDER_PEARL.getDescriptionId()),
		ENDERMAN(EntityType.ENDERMAN.getDescriptionId()),
		SHULKER(EntityType.SHULKER.getDescriptionId()),
		MODDED("gui.securitycraft:rift_stabilizer.modded");

		public final String label;

		TeleportationType(String label) {
			this.label = label;
		}

		public static TeleportationType getTypeFromEvent(EntityTeleportEvent event) {
			if (event instanceof ChorusFruit)
				return CHORUS_FRUIT;
			else if (event instanceof EnderPearl)
				return ENDER_PEARL;
			else if (event instanceof EnderEntity) {
				if (event.getEntity() instanceof EndermanEntity)
					return ENDERMAN;
				else if (event.getEntity() instanceof ShulkerEntity)
					return SHULKER;

				return MODDED;
			}
			else if (event instanceof TeleportCommand || event instanceof SpreadPlayersCommand)
				return null;

			return MODDED;
		}

		@Override
		public String toString() {
			return label;
		}
	}
}
