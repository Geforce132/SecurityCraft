package net.geforcemods.securitycraft.tileentity;

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
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class RetinalScannerTileEntity extends DisguisableTileEntity {

	private static final Logger LOGGER = LogManager.getLogger();
	private BooleanOption activatedByEntities = new BooleanOption("activatedByEntities", false);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);
	private GameProfile ownerProfile;
	private static PlayerProfileCache profileCache;
	private static MinecraftSessionService sessionService;

	public RetinalScannerTileEntity()
	{
		super(SCContent.teTypeRetinalScanner);
	}

	@Override
	public void entityViewed(LivingEntity entity){
		if(!world.isRemote && !BlockUtils.getBlockProperty(world, pos, RetinalScannerBlock.POWERED) && !EntityUtils.isInvisible(entity)){
			if(!(entity instanceof PlayerEntity) && !activatedByEntities.get())
				return;

			if(entity instanceof PlayerEntity && PlayerUtils.isPlayerMountedOnCamera(entity))
				return;

			if(entity instanceof PlayerEntity && !getOwner().isOwner((PlayerEntity) entity) && !ModuleUtils.checkForModule(world, pos, (PlayerEntity)entity, ModuleType.WHITELIST)) {
				PlayerUtils.sendMessageToPlayer((PlayerEntity) entity, ClientUtils.localize(SCContent.RETINAL_SCANNER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:retinalScanner.notOwner").replace("#", getOwner().getName()), TextFormatting.RED);
				return;
			}

			BlockUtils.setBlockProperty(world, pos, RetinalScannerBlock.POWERED, true);
			world.getPendingBlockTicks().scheduleTick(new BlockPos(pos), SCContent.RETINAL_SCANNER.get(), 60);

			if(entity instanceof PlayerEntity && sendMessage.get())
				PlayerUtils.sendMessageToPlayer((PlayerEntity) entity, ClientUtils.localize(SCContent.RETINAL_SCANNER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:retinalScanner.hello").replace("#", entity.getName().getFormattedText()), TextFormatting.GREEN);
		}
	}

	@Override
	public int getViewCooldown() {
		return 30;
	}

	@Override
	public boolean activatedOnlyByPlayer() {
		return !activatedByEntities.get();
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.WHITELIST, ModuleType.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ activatedByEntities, sendMessage };
	}

	public static void setProfileCache(PlayerProfileCache profileCacheIn) {
		profileCache = profileCacheIn;
	}

	public static void setSessionService(MinecraftSessionService sessionServiceIn) {
		sessionService = sessionServiceIn;
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		super.write(tag);
		if(!StringUtils.isNullOrEmpty(getOwner().getName()) && !(getOwner().getName().equals("owner")))
		{
			if(ownerProfile == null || !getOwner().getName().equals(ownerProfile.getName()))
				setPlayerProfile(new GameProfile((UUID)null, getOwner().getName()));

			updatePlayerProfile();
			CompoundNBT ownerProfileTag = new CompoundNBT();
			NBTUtil.writeGameProfile(ownerProfileTag, ownerProfile);
			tag.put("ownerProfile", ownerProfileTag);
			return tag;
		}
		return tag;
	}

	@Override
	public void read(CompoundNBT tag) {
		super.read(tag);
		if(tag.contains("ownerProfile", Constants.NBT.TAG_COMPOUND))
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
			setProfileCache(ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache());
		if(sessionService == null && ServerLifecycleHooks.getCurrentServer() != null)
			setSessionService(ServerLifecycleHooks.getCurrentServer().getMinecraftSessionService());

		ownerProfile = updateGameProfile(ownerProfile);
	}

	private GameProfile updateGameProfile(GameProfile input) {
		if (ConfigHandler.CONFIG.retinalScannerFace.get() && input != null && !StringUtils.isNullOrEmpty(input.getName())) {
			if (input.isComplete() && input.getProperties().containsKey("textures"))
				return input;
			else if (profileCache != null && sessionService != null) {
				GameProfile gameprofile = profileCache.getGameProfileForUsername(input.getName());
				if (gameprofile == null)
					return input;
				else {
					Property property = Iterables.getFirst(gameprofile.getProperties().get("textures"), (Property)null);
					if (property == null) {
						try {
							gameprofile = sessionService.fillProfileProperties(gameprofile, true);
						}
						catch(IllegalArgumentException e) { //this seems to only happen on offline servers. log the exception nonetheless, just in case
							LOGGER.warn("========= WARNING =========");
							LOGGER.warn("The following error is likely caused by using an offline server. If you are not using an offline server (online-mode=true in the server.properties), please reach out to the SecurityCraft devs in their Discord #help channel: https://discord.gg/U8DvBAW");
							LOGGER.warn("To mitigate this error, you can set the configuration option \"retinalScannerFace\" to false, in order to disable rendering the owner's face on retinal scanners.");
							LOGGER.error("The exception's stacktrace is as follows:", e);
						}
					}
					return gameprofile;
				}
			} else return input;
		} else return input;
	}
}
