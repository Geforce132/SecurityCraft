package net.geforcemods.securitycraft.blockentities;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.ToIntFunction;

import com.google.common.collect.Lists;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity.TeleportationType;
import net.geforcemods.securitycraft.blocks.RiftStabilizerBlock;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SyncRiftStabilizer;
import net.geforcemods.securitycraft.util.BlockEntityRenderDelegate;
import net.geforcemods.securitycraft.util.IToggleableEntries;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class RiftStabilizerBlockEntity extends DisguisableBlockEntity implements ITickable, ILockable, IToggleableEntries<TeleportationType> {
	private final IntOption signalLength = new IntOption(this::getPos, "signalLength", 60, 0, 400, 5); //20 seconds max
	private final IntOption range = new IntOption(this::getPos, "range", 5, 1, 15, 1);
	private final DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private final Map<TeleportationType, Boolean> teleportationFilter = new EnumMap<>(TeleportationType.class);
	private double lastTeleportDistance;
	private TeleportationType lastTeleportationType;
	private boolean tracked = false;

	public RiftStabilizerBlockEntity() {
		//when adding new types ONLY ADD TO THE END. anything else will break saved data.
		//ordering is done in GuiToggleList based on the user's current language
		teleportationFilter.put(TeleportationType.CHORUS_FRUIT, true);
		teleportationFilter.put(TeleportationType.ENDER_PEARL, true);
		teleportationFilter.put(TeleportationType.ENDERMAN, false);
		teleportationFilter.put(TeleportationType.SHULKER, false);
		teleportationFilter.put(TeleportationType.MODDED, false);
	}

	@Override
	public void update() {
		if (world.getBlockState(pos).getValue(RiftStabilizerBlock.HALF) == EnumDoorHalf.LOWER && !tracked) {
			BlockEntityTracker.RIFT_STABILIZER.track(this);
			tracked = true;
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();

		// Stop tracking Rift Stabilizers when they are removed from the world
		BlockEntityTracker.RIFT_STABILIZER.stopTracking(this);
	}

	@Override
	public void setFilter(TeleportationType teleportationType, boolean allowed) {
		if (teleportationFilter.containsKey(teleportationType)) {
			teleportationFilter.put(teleportationType, allowed);

			if (world.isRemote)
				SecurityCraft.network.sendToServer(new SyncRiftStabilizer(pos, teleportationType, allowed));

			RiftStabilizerBlockEntity connectedTileEntity = RiftStabilizerBlock.getConnectedTileEntity(world, pos);

			if (connectedTileEntity != null)
				connectedTileEntity.teleportationFilter.put(teleportationType, allowed);
		}
	}

	@Override
	public boolean getFilter(TeleportationType teleportationType) {
		return teleportationFilter.containsKey(teleportationType) && teleportationFilter.get(teleportationType);
	}

	@Override
	public String getTypeName(TeleportationType type) {
		return Utils.localize(type.label).getFormattedText();
	}

	@Override
	public ToIntFunction<TeleportationType> getComparatorOutputFunction() {
		return t -> Lists.newArrayList(TeleportationType.values()).indexOf(t) + 1;
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
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagCompound teleportationNBT = new NBTTagCompound();
		int i = 0;

		for (boolean b : teleportationFilter.values()) {
			teleportationNBT.setBoolean("teleportationType" + i, b);
			i++;
		}

		tag.setTag("teleportationTypes", teleportationNBT);
		tag.setDouble("lastTeleportDistance", lastTeleportDistance);

		if (lastTeleportationType != null)
			tag.setInteger("lastTeleportationType", Lists.newArrayList(TeleportationType.values()).indexOf(lastTeleportationType));

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		if (tag.hasKey("teleportationTypes", NBT.TAG_COMPOUND)) {
			NBTTagCompound teleportationNBT = tag.getCompoundTag("teleportationTypes");
			int i = 0;

			for (TeleportationType teleportationType : teleportationFilter.keySet()) {
				teleportationFilter.put(teleportationType, teleportationNBT.getBoolean("teleportationType" + i));
				i++;
			}
		}

		lastTeleportDistance = tag.getDouble("lastTeleportDistance");

		if (tag.hasKey("lastTeleportationType"))
			lastTeleportationType = TeleportationType.values()[tag.getInteger("lastTeleportationType")];
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		RiftStabilizerBlockEntity connectedTileEntity = RiftStabilizerBlock.getConnectedTileEntity(world, pos);

		if (connectedTileEntity != null && (toggled ? !connectedTileEntity.isModuleEnabled(module) : !connectedTileEntity.hasModule(module)))
			connectedTileEntity.insertModule(stack, toggled);

		if (module == ModuleType.DISGUISE) {
			BlockEntityRenderDelegate.putDisguisedTeRenderer(this, stack);

			if (connectedTileEntity != null)
				BlockEntityRenderDelegate.putDisguisedTeRenderer(connectedTileEntity, stack);
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		RiftStabilizerBlockEntity connectedTileEntity = RiftStabilizerBlock.getConnectedTileEntity(world, pos);

		if (connectedTileEntity != null && (toggled ? connectedTileEntity.isModuleEnabled(module) : connectedTileEntity.hasModule(module)))
			connectedTileEntity.removeModule(module, toggled);

		if (module == ModuleType.DISGUISE) {
			BlockEntityRenderDelegate.DISGUISED_BLOCK.removeDelegateOf(this);

			if (connectedTileEntity != null)
				BlockEntityRenderDelegate.DISGUISED_BLOCK.removeDelegateOf(connectedTileEntity);
		}
		else if (module == ModuleType.SMART) {
			onRemoveSmartModule(this);

			if (connectedTileEntity != null)
				onRemoveSmartModule(connectedTileEntity);
		}
	}

	private void onRemoveSmartModule(RiftStabilizerBlockEntity te) {
		te.teleportationFilter.put(TeleportationType.CHORUS_FRUIT, true);
		te.teleportationFilter.put(TeleportationType.ENDER_PEARL, true);
		te.teleportationFilter.put(TeleportationType.ENDERMAN, false);
		te.teleportationFilter.put(TeleportationType.SHULKER, false);
		te.teleportationFilter.put(TeleportationType.MODDED, false);
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
	public <T> void onOptionChanged(Option<T> option) {
		RiftStabilizerBlockEntity connectedTileEntity = RiftStabilizerBlock.getConnectedTileEntity(world, pos);

		if (connectedTileEntity != null) {
			if (option instanceof IntOption) {
				IntOption io = (IntOption) option;

				if (option == signalLength)
					connectedTileEntity.setSignalLength(io.get());
				else if (option == range)
					connectedTileEntity.setRange(io.get());
				else
					throw new UnsupportedOperationException("Unhandled option synchronization in rift stabilizer! " + option.getName());
			}
			else if (option instanceof BooleanOption) {
				BooleanOption bo = (BooleanOption) option;

				if (option == disabled)
					connectedTileEntity.setDisabled(bo.get());
				else if (option == ignoreOwner)
					connectedTileEntity.setIgnoresOwner(bo.get());
				else
					throw new UnsupportedOperationException("Unhandled option synchronization in rift stabilizer! " + option.getName());
			}
			else
				throw new UnsupportedOperationException("Unhandled option synchronization in rift stabilizer! " + option.getName());
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
	public void onOwnerChanged(IBlockState state, World world, BlockPos pos, EntityPlayer player, Owner oldOwner, Owner newOwner) {
		RiftStabilizerBlockEntity te = RiftStabilizerBlock.getConnectedTileEntity(world, pos);

		if (te != null) {
			te.setOwner(getOwner().getUUID(), getOwner().getName());

			if (!world.isRemote)
				world.getMinecraftServer().getPlayerList().sendPacketToAllPlayers(te.getUpdatePacket());
		}

		super.onOwnerChanged(state, world, pos, player, oldOwner, newOwner);
	}

	public void setSignalLength(int signalLength) {
		if (getSignalLength() != signalLength) {
			IBlockState state = world.getBlockState(pos);

			this.signalLength.setValue(signalLength);
			world.setBlockState(pos, world.getBlockState(pos).withProperty(RiftStabilizerBlock.POWERED, false));
			world.notifyBlockUpdate(pos, state, state, 3); //sync option change to client
		}
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public void setRange(int range) {
		if (getRange() != range) {
			IBlockState state = world.getBlockState(pos);

			this.range.setValue(range);
			world.notifyBlockUpdate(pos, state, state, 3); //sync option change to client
		}
	}

	public int getRange() {
		return range.get();
	}

	public void setDisabled(boolean disabled) {
		if (isDisabled() != disabled) {
			IBlockState state = world.getBlockState(pos);

			this.disabled.setValue(disabled);
			world.notifyBlockUpdate(pos, state, state, 3); //sync option change to client
		}
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	public void setIgnoresOwner(boolean ignoresOwner) {
		if (ignoresOwner() != ignoresOwner) {
			IBlockState state = world.getBlockState(pos);

			ignoreOwner.setValue(ignoresOwner);
			world.notifyBlockUpdate(pos, state, state, 3); //sync option change to client
			markDirty();
		}
	}

	@Override
	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	@Override
	public void setCustomName(String customName) {
		super.setCustomName(customName);

		if (world.getBlockState(pos).getValue(RiftStabilizerBlock.HALF) == EnumDoorHalf.LOWER)
			((INameSetter) world.getTileEntity(pos.up())).setCustomName(customName);
	}

	public enum TeleportationType {
		CHORUS_FRUIT(Items.CHORUS_FRUIT.getTranslationKey() + ".name"),
		ENDER_PEARL(Items.ENDER_PEARL.getTranslationKey() + ".name"),
		ENDERMAN("entity." + ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft:enderman")).getName() + ".name"),
		SHULKER("entity." + ForgeRegistries.ENTITIES.getValue(new ResourceLocation("minecraft:shulker")).getName() + ".name"),
		MODDED("gui.securitycraft:rift_stabilizer.modded");

		public final String label;

		TeleportationType(String label) {
			this.label = label;
		}

		public static TeleportationType getTypeFromEvent(EntityLivingBase teleported, Vec3d target) {
			Optional<EntityEnderPearl> pearl = teleported.world.getEntities(EntityEnderPearl.class, e -> e.getPositionVector().equals(target)).stream().findFirst();

			if (teleported instanceof EntityEnderman)
				return ENDERMAN;
			else if (teleported instanceof EntityShulker)
				return SHULKER;
			else if (teleported instanceof EntityPlayer && pearl.isPresent())
				return ENDER_PEARL;

			return MODDED;
		}
	}
}
