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
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.blocks.BlockRetinalScanner;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;


public class TileEntityRetinalScanner extends TileEntityDisguisable {

	private static final Logger LOGGER = LogManager.getLogger();
	private OptionBoolean activatedByEntities = new OptionBoolean("activatedByEntities", false);
	private OptionBoolean sendMessage = new OptionBoolean("sendMessage", true);
	private OptionInt signalLength = new OptionInt(this::getPos, "signalLength", 60, 5, 400, 5, true); //20 seconds max
	private GameProfile ownerProfile;
	private static PlayerProfileCache profileCache;
	private static MinecraftSessionService sessionService;

	@Override
	public void entityViewed(EntityLivingBase entity){
		if(!world.isRemote && !BlockUtils.getBlockProperty(world, pos, BlockRetinalScanner.POWERED) && !EntityUtils.isInvisible(entity)){
			if(!(entity instanceof EntityPlayer) && !activatedByEntities.get())
				return;

			if(entity instanceof EntityPlayer && PlayerUtils.isPlayerMountedOnCamera(entity))
				return;

			if(entity instanceof EntityPlayer && !getOwner().isOwner((EntityPlayer) entity) && !ModuleUtils.checkForModule(world, pos, (EntityPlayer)entity, EnumModuleType.WHITELIST)) {
				PlayerUtils.sendMessageToPlayer((EntityPlayer) entity, Utils.localize("tile.securitycraft:retinalScanner.name"), Utils.localize("messages.securitycraft:retinalScanner.notOwner", getOwner().getName()), TextFormatting.RED);
				return;
			}

			BlockUtils.setBlockProperty(world, pos, BlockRetinalScanner.POWERED, true);
			world.scheduleUpdate(new BlockPos(pos), SCContent.retinalScanner, getSignalLength());

			if(entity instanceof EntityPlayer && sendMessage.get())
				PlayerUtils.sendMessageToPlayer((EntityPlayer) entity, Utils.localize("tile.securitycraft:retinalScanner.name"), Utils.localize("messages.securitycraft:retinalScanner.hello", entity.getName()), TextFormatting.GREEN);
		}
	}

	@Override
	public int getViewCooldown() {
		return getSignalLength() + 30;
	}

	@Override
	public boolean activatedOnlyByPlayer() {
		return !activatedByEntities.get();
	}

	public int getSignalLength()
	{
		return signalLength.get();
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[]{EnumModuleType.WHITELIST, EnumModuleType.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ activatedByEntities, sendMessage, signalLength };
	}

	public static void setProfileCache(PlayerProfileCache profileCacheIn) {
		profileCache = profileCacheIn;
	}

	public static void setSessionService(MinecraftSessionService sessionServiceIn) {
		sessionService = sessionServiceIn;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		if(!StringUtils.isNullOrEmpty(getOwner().getName()) && !(getOwner().getName().equals("owner")))
		{
			if(ownerProfile == null || !getOwner().getName().equals(ownerProfile.getName()))
				setPlayerProfile(new GameProfile((UUID)null, getOwner().getName()));

			updatePlayerProfile();
			NBTTagCompound ownerProfileTag = new NBTTagCompound();
			NBTUtil.writeGameProfile(ownerProfileTag, ownerProfile);
			tag.setTag("ownerProfile", ownerProfileTag);
			return tag;
		}
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
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
		if(sessionService == null && FMLCommonHandler.instance().getMinecraftServerInstance() != null)
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
