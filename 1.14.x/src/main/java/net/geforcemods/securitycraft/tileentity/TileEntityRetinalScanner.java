package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.blocks.BlockRetinalScanner;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;


public class TileEntityRetinalScanner extends CustomizableSCTE {

	private OptionBoolean activatedByEntities = new OptionBoolean("activatedByEntities", false);

	public TileEntityRetinalScanner()
	{
		super(SCContent.teTypeRetinalScanner);
	}

	@Override
	public void entityViewed(LivingEntity entity){
		if(!world.isRemote && !BlockUtils.getBlockPropertyAsBoolean(world, pos, BlockRetinalScanner.POWERED)){
			if(!(entity instanceof PlayerEntity) && !activatedByEntities.asBoolean())
				return;

			if(entity instanceof PlayerEntity && PlayerUtils.isPlayerMountedOnCamera(entity))
				return;

			if(entity instanceof PlayerEntity && !getOwner().isOwner((PlayerEntity) entity) && !ModuleUtils.checkForModule(world, pos, (PlayerEntity)entity, EnumCustomModules.WHITELIST)) {
				PlayerUtils.sendMessageToPlayer((PlayerEntity) entity, ClientUtils.localize(SCContent.retinalScanner.getTranslationKey()), ClientUtils.localize("messages.securitycraft:retinalScanner.notOwner").replace("#", getOwner().getName()), TextFormatting.RED);
				return;
			}

			BlockUtils.setBlockProperty(world, pos, BlockRetinalScanner.POWERED, true);
			world.getPendingBlockTicks().scheduleTick(new BlockPos(pos), SCContent.retinalScanner, 60);

			if(entity instanceof PlayerEntity)
				PlayerUtils.sendMessageToPlayer((PlayerEntity) entity, ClientUtils.localize(SCContent.retinalScanner.getTranslationKey()), ClientUtils.localize("messages.securitycraft:retinalScanner.hello").replace("#", entity.getName().getFormattedText()), TextFormatting.GREEN);
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
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ activatedByEntities };
	}

}
