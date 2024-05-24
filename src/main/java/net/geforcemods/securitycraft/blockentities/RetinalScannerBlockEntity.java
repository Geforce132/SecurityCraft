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
import net.geforcemods.securitycraft.api.Option.SignalLengthOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class RetinalScannerBlockEntity extends DisguisableBlockEntity implements IViewActivated, ITickableTileEntity, ILockable {
	private static PlayerProfileCache profileCache;
	private static MinecraftSessionService sessionService;
	private BooleanOption activatedByEntities = new BooleanOption("activatedByEntities", false);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private IntOption signalLength = new SignalLengthOption(this::getBlockPos, 60);
	private DoubleOption maximumDistance = new DoubleOption(this::getBlockPos, "maximumDistance", 5.0D, 0.1D, 25.0D, 0.1D) {
		@Override
		public String getKey(String denotation) {
			return "option.generic.viewActivated.maximumDistance";
		}
	};
	private DisabledOption disabled = new DisabledOption(false);
	private GameProfile ownerProfile;
	private int viewCooldown = 0;

	public RetinalScannerBlockEntity() {
		super(SCContent.RETINAL_SCANNER_BLOCK_ENTITY.get());
	}

	@Override
	public void tick() {
		checkView(level, worldPosition);
	}

	@Override
	public boolean onEntityViewed(LivingEntity entity, BlockRayTraceResult hitResult) {
		if (!isLocked() && !isDisabled()) {
			BlockState state = getBlockState();

			if (state.getValue(RetinalScannerBlock.FACING) != hitResult.getDirection())
				return false;

			int signalLength = getSignalLength();

			if ((!state.getValue(RetinalScannerBlock.POWERED) || signalLength == 0) && !Utils.isEntityInvisible(entity)) {
				if (entity instanceof PlayerEntity) {
					PlayerEntity player = (PlayerEntity) entity;
					Owner viewingPlayer = new Owner(player);

					if (ConfigHandler.SERVER.trickScannersWithPlayerHeads.get() && player.getItemBySlot(EquipmentSlotType.HEAD).getItem() == Items.PLAYER_HEAD)
						viewingPlayer = PlayerUtils.getSkullOwner(player);

					if (!isOwnedBy(viewingPlayer) && !isAllowed(viewingPlayer.getName())) {
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.RETINAL_SCANNER.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.notOwner", PlayerUtils.getOwnerComponent(getOwner())), TextFormatting.RED);
						return true;
					}

					if (sendMessage.get())
						PlayerUtils.sendMessageToPlayer((PlayerEntity) entity, Utils.localize(SCContent.RETINAL_SCANNER.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.hello", viewingPlayer.getName()), TextFormatting.GREEN);
				}
				else if (activatedOnlyByPlayer())
					return false;

				level.setBlockAndUpdate(worldPosition, state.cycle(RetinalScannerBlock.POWERED));
				BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.RETINAL_SCANNER.get());

				if (signalLength > 0)
					level.getBlockTicks().scheduleTick(new BlockPos(worldPosition), SCContent.RETINAL_SCANNER.get(), getSignalLength());

				return true;
			}
		}
		else if (entity instanceof PlayerEntity) {
			if (isLocked() && sendMessage.get()) {
				TranslationTextComponent blockName = Utils.localize(SCContent.RETINAL_SCANNER.get().getDescriptionId());

				PlayerUtils.sendMessageToPlayer((PlayerEntity) entity, blockName, Utils.localize("messages.securitycraft:sonic_security_system.locked", blockName), TextFormatting.DARK_RED, false);
				return true;
			}
			else if (isDisabled())
				((PlayerEntity) entity).displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
		}

		return false;
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (option == signalLength) {
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(RetinalScannerBlock.POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, worldPosition, getBlockState().getBlock());
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
				activatedByEntities, sendMessage, signalLength, disabled, maximumDistance
		};
	}

	public static void setProfileCache(PlayerProfileCache profileCacheIn) {
		profileCache = profileCacheIn;
	}

	public static void setSessionService(MinecraftSessionService sessionServiceIn) {
		sessionService = sessionServiceIn;
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);
		if (!StringUtils.isNullOrEmpty(getOwner().getName()) && !(getOwner().getName().equals("owner"))) {
			if (ownerProfile == null || !getOwner().getName().equals(ownerProfile.getName()))
				setPlayerProfile(new GameProfile((UUID) null, getOwner().getName()));

			updatePlayerProfile();
			CompoundNBT ownerProfileTag = new CompoundNBT();
			NBTUtil.writeGameProfile(ownerProfileTag, ownerProfile);
			tag.put("ownerProfile", ownerProfileTag);
			return tag;
		}
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);
		ownerProfile = NBTUtil.readGameProfile(tag.getCompound("ownerProfile"));
	}

	@Nullable
	public GameProfile getPlayerProfile() {
		return ownerProfile;
	}

	public void setPlayerProfile(@Nullable GameProfile profile) {
		ownerProfile = profile;
	}

	public void updatePlayerProfile() {
		if (profileCache == null && ServerLifecycleHooks.getCurrentServer() != null)
			setProfileCache(ServerLifecycleHooks.getCurrentServer().getProfileCache());

		if (sessionService == null && ServerLifecycleHooks.getCurrentServer() != null)
			setSessionService(ServerLifecycleHooks.getCurrentServer().getSessionService());

		ownerProfile = updateGameProfile(ownerProfile);
	}

	private GameProfile updateGameProfile(GameProfile input) {
		if (ConfigHandler.SERVER.retinalScannerFace.get() && input != null && !StringUtils.isNullOrEmpty(input.getName())) {
			if (input.isComplete() && input.getProperties().containsKey("textures"))
				return input;
			else if (profileCache != null && sessionService != null) {
				GameProfile gameprofile = profileCache.get(input.getName());

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
