package net.geforcemods.securitycraft.blockentities;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IViewActivated;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.RespectInvisibilityOption;
import net.geforcemods.securitycraft.api.Option.SignalLengthOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.ITickable;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class RetinalScannerBlockEntity extends DisguisableBlockEntity implements IViewActivated, ITickable, ILockable {
	private static PlayerProfileCache profileCache;
	private static MinecraftSessionService sessionService;
	private BooleanOption activatedByEntities = new BooleanOption("activatedByEntities", false);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private IntOption signalLength = new SignalLengthOption(this::getPos, 60);
	private DoubleOption maximumDistance = new DoubleOption(this::getPos, "maximumDistance", 5.0D, 0.1D, 25.0D, 0.1D) {
		@Override
		public String getKey(String denotation) {
			return "option.generic.viewActivated.maximumDistance";
		}
	};
	private DisabledOption disabled = new DisabledOption(false);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
	private GameProfile ownerProfile;
	private int viewCooldown = 0;

	@Override
	public void update() {
		checkView(world, pos);
	}

	@Override
	public boolean onEntityViewed(EntityLivingBase entity, RayTraceResult rayTraceResult) {
		if (!isLocked() && !isDisabled()) {
			IBlockState state = world.getBlockState(pos);

			if (state.getValue(RetinalScannerBlock.FACING) != rayTraceResult.sideHit)
				return false;

			int signalLength = getSignalLength();

			if ((!state.getValue(RetinalScannerBlock.POWERED) || signalLength == 0) && !respectInvisibility.isConsideredInvisible(entity)) {
				if (entity instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) entity;
					Owner viewingPlayer;

					if (ConfigHandler.trickScannersWithPlayerHeads && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.SKULL)
						viewingPlayer = PlayerUtils.getSkullOwner(player);
					else
						viewingPlayer = isOwnedBy(player, true) ? PlayerUtils.getOwnerFromPlayerOrMask(player) : new Owner(player);

					if (!isOwnedBy(viewingPlayer) && !isAllowed(viewingPlayer.getName())) {
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.retinalScanner), Utils.localize("messages.securitycraft:retinalScanner.notOwner", PlayerUtils.getOwnerComponent(getOwner())), TextFormatting.RED);
						return true;
					}

					if (sendMessage.get())
						PlayerUtils.sendMessageToPlayer((EntityPlayer) entity, Utils.localize("tile.securitycraft:retinalScanner.name"), Utils.localize("messages.securitycraft:retinalScanner.hello", viewingPlayer.getName()), TextFormatting.GREEN);
				}
				else if (activatedOnlyByPlayer())
					return false;

				world.setBlockState(pos, state.cycleProperty(RetinalScannerBlock.POWERED));
				BlockUtils.updateIndirectNeighbors(world, pos, SCContent.retinalScanner);

				if (signalLength > 0)
					world.scheduleUpdate(pos, SCContent.retinalScanner, signalLength);

				return true;
			}
		}
		else if (entity instanceof EntityPlayer) {
			if (isLocked() && sendMessage.get())
				PlayerUtils.sendMessageToPlayer((EntityPlayer) entity, Utils.localize(SCContent.retinalScanner), Utils.localize("messages.securitycraft:sonic_security_system.locked", Utils.localize(SCContent.retinalScanner)), TextFormatting.DARK_RED, false);
			else if (isDisabled())
				((EntityPlayer) entity).sendStatusMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);

			return true;
		}

		return false;
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (option == signalLength) {
			world.setBlockState(pos, world.getBlockState(pos).withProperty(RetinalScannerBlock.POWERED, false));
			BlockUtils.updateIndirectNeighbors(world, pos, SCContent.retinalScanner);
		}
	}

	@Override
	public int getDefaultViewCooldown() {
		return getSignalLength() + 30;
	}

	@Override
	public int getViewCooldown() {
		return viewCooldown;
	}

	@Override
	public void setViewCooldown(int viewCooldown) {
		this.viewCooldown = viewCooldown;
	}

	@Override
	public boolean activatedOnlyByPlayer() {
		return !activatedByEntities.get();
	}

	public int getSignalLength() {
		return signalLength.get();
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public double getMaximumDistance() {
		return maximumDistance.get();
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				activatedByEntities, sendMessage, signalLength, disabled, maximumDistance, respectInvisibility
		};
	}

	public static void setProfileCache(PlayerProfileCache profileCacheIn) {
		profileCache = profileCacheIn;
	}

	public static void setSessionService(MinecraftSessionService sessionServiceIn) {
		sessionService = sessionServiceIn;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (!StringUtils.isNullOrEmpty(getOwner().getName()) && !(getOwner().getName().equals("owner"))) {
			if (ownerProfile == null || !getOwner().getName().equals(ownerProfile.getName()))
				setPlayerProfile(new GameProfile((UUID) null, getOwner().getName()));

			updatePlayerProfile();
			NBTTagCompound ownerProfileTag = new NBTTagCompound();
			NBTUtil.writeGameProfile(ownerProfileTag, ownerProfile);
			tag.setTag("ownerProfile", ownerProfileTag);
			return tag;
		}
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		ownerProfile = NBTUtil.readGameProfileFromNBT(tag.getCompoundTag("ownerProfile"));
	}

	@Nullable
	public GameProfile getPlayerProfile() {
		return ownerProfile;
	}

	public void setPlayerProfile(@Nullable GameProfile profile) {
		ownerProfile = profile;
	}

	public void updatePlayerProfile() {
		if (profileCache == null && FMLCommonHandler.instance().getMinecraftServerInstance() != null)
			setProfileCache(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache());

		if (sessionService == null && FMLCommonHandler.instance().getMinecraftServerInstance() != null)
			setSessionService(FMLCommonHandler.instance().getMinecraftServerInstance().getMinecraftSessionService());

		ownerProfile = updateGameProfile(ownerProfile);
	}

	private GameProfile updateGameProfile(GameProfile input) {
		if (ConfigHandler.retinalScannerFace && input != null && !StringUtils.isNullOrEmpty(input.getName())) {
			if (input.isComplete() && input.getProperties().containsKey("textures"))
				return input;
			else if (profileCache != null && sessionService != null) {
				GameProfile gameprofile = profileCache.getGameProfileForUsername(input.getName());

				if (gameprofile == null)
					return input;
				else {
					Property property = Iterables.getFirst(gameprofile.getProperties().get("textures"), (Property) null);

					if (property == null) {
						try {
							gameprofile = sessionService.fillProfileProperties(gameprofile, true);
						}
						catch (IllegalArgumentException e) { //this seems to only happen on offline servers. log the exception nonetheless, just in case
							SecurityCraft.LOGGER.warn("========= WARNING =========");
							SecurityCraft.LOGGER.warn("The following error is likely caused by using an offline server. If you are not using an offline server (online-mode=true in the server.properties), please reach out to the SecurityCraft devs in their Discord #help channel: https://discord.gg/U8DvBAW");
							SecurityCraft.LOGGER.warn("To mitigate this error, you can set the configuration option \"retinalScannerFace\" to false, in order to disable rendering the owner's face on retinal scanners.");
							SecurityCraft.LOGGER.error("The exception's stacktrace is as follows:", e);
						}
					}
					return gameprofile;
				}
			}
			else
				return input;
		}
		else
			return input;
	}
}
