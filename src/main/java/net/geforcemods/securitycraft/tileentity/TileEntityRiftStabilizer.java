package net.geforcemods.securitycraft.tileentity;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.ToIntFunction;

import com.google.common.collect.Lists;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.blocks.BlockRiftStabilizer;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.misc.TileEntityTracker;
import net.geforcemods.securitycraft.network.server.SyncRiftStabilizer;
import net.geforcemods.securitycraft.tileentity.TileEntityRiftStabilizer.TeleportationType;
import net.geforcemods.securitycraft.util.IToggleableEntries;
import net.geforcemods.securitycraft.util.TileEntityRenderDelegate;
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

public class TileEntityRiftStabilizer extends TileEntityDisguisable implements ITickable, ILockable, IToggleableEntries<TeleportationType> {
	private final OptionInt signalLength = new OptionInt(this::getPos, "signalLength", 60, 5, 400, 5, true); //20 seconds max
	private final OptionInt range = new OptionInt(this::getPos, "range", 5, 1, 15, 1, true);
	private final DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private final Map<TeleportationType, Boolean> teleportationFilter = new EnumMap<>(TeleportationType.class);
	private double lastTeleportDistance;
	private TeleportationType lastTeleportationType;
	private boolean tracked = false;

	public TileEntityRiftStabilizer() {
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
		if (world.getBlockState(pos).getValue(BlockRiftStabilizer.HALF) == EnumDoorHalf.LOWER && !tracked) {
			TileEntityTracker.RIFT_STABILIZER.track(this);
			tracked = true;
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();

		// Stop tracking Rift Stabilizers when they are removed from the world
		TileEntityTracker.RIFT_STABILIZER.stopTracking(this);
	}

	@Override
	public void setFilter(TeleportationType teleportationType, boolean allowed) {
		if (teleportationFilter.containsKey(teleportationType)) {
			teleportationFilter.put(teleportationType, allowed);

			if (world.isRemote)
				SecurityCraft.network.sendToServer(new SyncRiftStabilizer(pos, teleportationType, allowed));

			TileEntityRiftStabilizer connectedTileEntity = BlockRiftStabilizer.getConnectedTileEntity(world, pos);

			if (connectedTileEntity != null) {
				connectedTileEntity.teleportationFilter.put(teleportationType, allowed);

				if (world.isRemote)
					SecurityCraft.network.sendToServer(new SyncRiftStabilizer(connectedTileEntity.pos, teleportationType, allowed));
			}
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
	public void onModuleInserted(ItemStack stack, EnumModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		TileEntityRiftStabilizer connectedTileEntity = BlockRiftStabilizer.getConnectedTileEntity(world, pos);

		if (connectedTileEntity != null) {
			if (toggled ? !connectedTileEntity.isModuleEnabled(module) : !connectedTileEntity.hasModule(module))
				connectedTileEntity.insertModule(stack, toggled);
		}

		if (module == EnumModuleType.DISGUISE) {
			TileEntityRenderDelegate.putDisguisedTeRenderer(this, stack);

			if (connectedTileEntity != null)
				TileEntityRenderDelegate.putDisguisedTeRenderer(connectedTileEntity, stack);
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		TileEntityRiftStabilizer connectedTileEntity = BlockRiftStabilizer.getConnectedTileEntity(world, pos);

		if (connectedTileEntity != null) {
			if (toggled ? connectedTileEntity.isModuleEnabled(module) : connectedTileEntity.hasModule(module))
				connectedTileEntity.removeModule(module, toggled);
		}

		if (module == EnumModuleType.DISGUISE) {
			TileEntityRenderDelegate.DISGUISED_BLOCK.removeDelegateOf(this);

			if (connectedTileEntity != null)
				TileEntityRenderDelegate.DISGUISED_BLOCK.removeDelegateOf(connectedTileEntity);
		}
		else if (module == EnumModuleType.SMART) {
			onRemoveSmartModule(this);

			if (connectedTileEntity != null)
				onRemoveSmartModule(connectedTileEntity);
		}
	}

	private void onRemoveSmartModule(TileEntityRiftStabilizer te) {
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
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[] {
				EnumModuleType.ALLOWLIST, EnumModuleType.DISGUISE, EnumModuleType.REDSTONE, EnumModuleType.HARMING, EnumModuleType.SMART
		};
	}

	@Override
	public void onOptionChanged(Option<?> option) {
		TileEntityRiftStabilizer connectedTileEntity = BlockRiftStabilizer.getConnectedTileEntity(world, pos);

		if (connectedTileEntity != null) {
			if (option.getName().equals("signalLength"))
				connectedTileEntity.setSignalLength(((OptionInt) option).get());
			else if (option.getName().equals("range"))
				connectedTileEntity.setRange(((OptionInt) option).get());
			else if (option.getName().equals("disabled"))
				connectedTileEntity.setDisabled(((OptionBoolean) option).get());
			else if (option.getName().equals("ignoreOwner"))
				connectedTileEntity.setIgnoresOwner(((OptionBoolean) option).get());
		}

		super.onOptionChanged(option);
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				signalLength, range, disabled
		};
	}

	@Override
	public void onOwnerChanged(IBlockState state, World world, BlockPos pos, EntityPlayer player) {
		TileEntityRiftStabilizer te = BlockRiftStabilizer.getConnectedTileEntity(world, pos);

		if (te != null) {
			te.setOwner(getOwner().getUUID(), getOwner().getName());

			if (!world.isRemote)
				world.getMinecraftServer().getPlayerList().sendPacketToAllPlayers(te.getUpdatePacket());
		}

		super.onOwnerChanged(state, world, pos, player);
	}

	public void setSignalLength(int signalLength) {
		if (getSignalLength() != signalLength) {
			IBlockState state = world.getBlockState(pos);

			this.signalLength.setValue(signalLength);
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

	public boolean ignoresOwner() {
		return ignoreOwner.get();
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
