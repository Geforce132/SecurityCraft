package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.blocks.BlockRetinalScanner;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;


public class TileEntityRetinalScanner extends TileEntityDisguisable {

	private OptionBoolean activatedByEntities = new OptionBoolean("activatedByEntities", false);
	private OptionBoolean sendMessage = new OptionBoolean("sendMessage", true);

	@Override
	public void entityViewed(EntityLivingBase entity){
		if(!world.isRemote && !BlockUtils.getBlockProperty(world, pos, BlockRetinalScanner.POWERED)){
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

}
