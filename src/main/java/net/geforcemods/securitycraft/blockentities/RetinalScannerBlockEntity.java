package net.geforcemods.securitycraft.blockentities;

import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IViewActivated;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
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
	private static final Logger LOGGER = LogManager.getLogger();
	private static PlayerProfileCache profileCache;
	private static MinecraftSessionService sessionService;
	private BooleanOption activatedByEntities = new BooleanOption("activatedByEntities", false);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private IntOption signalLength = new IntOption(this::getBlockPos, "signalLength", 60, 5, 400, 5, true); //20 seconds max
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
	public boolean onEntityViewed(LivingEntity entity, BlockRayTraceResult rayTraceResult) {
		if (!isLocked()) {
			BlockState state = level.getBlockState(worldPosition);

			if (state.getValue(RetinalScannerBlock.FACING) != rayTraceResult.getDirection())
				return false;

			if (!state.getValue(RetinalScannerBlock.POWERED) && !EntityUtils.isInvisible(entity)) {
				String name = entity.getName().getString();

				if (entity instanceof PlayerEntity) {
					PlayerEntity player = (PlayerEntity) entity;

					if (ConfigHandler.SERVER.trickScannersWithPlayerHeads.get() && player.getItemBySlot(EquipmentSlotType.HEAD).getItem() == Items.PLAYER_HEAD)
						name = PlayerUtils.getNameOfSkull(player);

					if (!getOwner().isOwner(player) && !ModuleUtils.isAllowed(this, player)) {
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.RETINAL_SCANNER.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.notOwner", PlayerUtils.getOwnerComponent(getOwner().getName())), TextFormatting.RED);
						return true;
					}
				}
				else if (activatedOnlyByPlayer())
					return false;

				level.setBlockAndUpdate(worldPosition, state.setValue(RetinalScannerBlock.POWERED, true));
				BlockUtils.updateIndirectNeighbors(level, worldPosition, SCContent.RETINAL_SCANNER.get());
				level.getBlockTicks().scheduleTick(new BlockPos(worldPosition), SCContent.RETINAL_SCANNER.get(), getSignalLength());

				if (entity instanceof PlayerEntity && sendMessage.get())
					PlayerUtils.sendMessageToPlayer((PlayerEntity) entity, Utils.localize(SCContent.RETINAL_SCANNER.get().getDescriptionId()), Utils.localize("messages.securitycraft:retinalScanner.hello", name), TextFormatting.GREEN);

				return true;
			}
		}
		else if (entity instanceof PlayerEntity && sendMessage.get()) {
			TranslationTextComponent blockName = Utils.localize(SCContent.RETINAL_SCANNER.get().getDescriptionId());

			PlayerUtils.sendMessageToPlayer((PlayerEntity) entity, blockName, Utils.localize("messages.securitycraft:sonic_security_system.locked", blockName), TextFormatting.DARK_RED, false);
			return true;
		}

		return false;
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

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				activatedByEntities, sendMessage, signalLength
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
							LOGGER.warn("========= WARNING =========");
							LOGGER.warn("The following error is likely caused by using an offline server. If you are not using an offline server (online-mode=true in the server.properties), please reach out to the SecurityCraft devs in their Discord #help channel: https://discord.gg/U8DvBAW");
							LOGGER.warn("To mitigate this error, you can set the configuration option \"retinalScannerFace\" to false, in order to disable rendering the owner's face on retinal scanners.");
							LOGGER.error("The exception's stacktrace is as follows:", e);
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
