package net.geforcemods.securitycraft.tileentity;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.blocks.BlockRetinalScanner;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;


public class TileEntityRetinalScanner extends TileEntityDisguisable {

	private OptionBoolean activatedByEntities = new OptionBoolean("activatedByEntities", false);
	private OptionBoolean sendMessage = new OptionBoolean("sendMessage", true);
	private GameProfile ownerProfile;
	private static PlayerProfileCache profileCache;
	private static MinecraftSessionService sessionService;

	@Override
	public void entityViewed(EntityLivingBase entity){
		if(!world.isRemote && !BlockUtils.getBlockProperty(world, pos, BlockRetinalScanner.POWERED) && !EntityUtils.isInvisible(entity)){
			if(!(entity instanceof EntityPlayer) && !activatedByEntities.asBoolean())
				return;

			if(entity instanceof EntityPlayer && PlayerUtils.isPlayerMountedOnCamera(entity))
				return;

			if(entity instanceof EntityPlayer && !getOwner().isOwner((EntityPlayer) entity) && !ModuleUtils.checkForModule(world, pos, (EntityPlayer)entity, EnumCustomModules.WHITELIST)) {
				PlayerUtils.sendMessageToPlayer((EntityPlayer) entity, ClientUtils.localize("tile.securitycraft:retinalScanner.name"), ClientUtils.localize("messages.securitycraft:retinalScanner.notOwner").replace("#", getOwner().getName()), TextFormatting.RED);
				return;
			}

			BlockUtils.setBlockProperty(world, pos, BlockRetinalScanner.POWERED, true);
			world.scheduleUpdate(new BlockPos(pos), SCContent.retinalScanner, 60);

			if(entity instanceof EntityPlayer && sendMessage.asBoolean())
				PlayerUtils.sendMessageToPlayer((EntityPlayer) entity, ClientUtils.localize("tile.securitycraft:retinalScanner.name"), ClientUtils.localize("messages.securitycraft:retinalScanner.hello").replace("#", entity.getName()), TextFormatting.GREEN);
		}
	}

	@Override
	public int getViewCooldown() {
		return 30;
	}

	@Override
	public boolean activatedOnlyByPlayer() {
		return !activatedByEntities.asBoolean();
	}

	@Override
	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST, EnumCustomModules.DISGUISE};
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
		if(tag.hasKey("ownerProfile", Constants.NBT.TAG_COMPOUND))
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
		ownerProfile = updateGameProfile(ownerProfile);
	}

	public static GameProfile updateGameProfile(GameProfile input) {
		if (profileCache == null && FMLCommonHandler.instance().getMinecraftServerInstance() != null)
			setProfileCache(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache());
		if(sessionService == null && FMLCommonHandler.instance().getMinecraftServerInstance() != null)
			setSessionService(FMLCommonHandler.instance().getMinecraftServerInstance().getMinecraftSessionService());

		if (input != null && !StringUtils.isNullOrEmpty(input.getName())) {
			if (profileCache != null && sessionService != null) {
				GameProfile gameprofile = profileCache.getGameProfileForUsername(input.getName());
				if (gameprofile == null)
					return input;
				else {
					Property property = Iterables.getFirst(gameprofile.getProperties().get("textures"), (Property)null);
					if (property == null) {
						gameprofile = sessionService.fillProfileProperties(gameprofile, true);
					}
					return gameprofile;
				}
			} else return input;
		} else return input;
	}
}
