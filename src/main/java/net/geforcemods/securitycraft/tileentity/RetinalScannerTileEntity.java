package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;


public class RetinalScannerTileEntity extends DisguisableTileEntity {

	private BooleanOption activatedByEntities = new BooleanOption("activatedByEntities", false);
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);

	public RetinalScannerTileEntity()
	{
		super(SCContent.teTypeRetinalScanner);
	}

	@Override
	public void entityViewed(LivingEntity entity){
		if(!world.isRemote && !BlockUtils.getBlockProperty(world, pos, RetinalScannerBlock.POWERED)){
			if(!(entity instanceof PlayerEntity) && !activatedByEntities.asBoolean())
				return;

			if(entity instanceof PlayerEntity && PlayerUtils.isPlayerMountedOnCamera(entity))
				return;

			if(entity instanceof PlayerEntity && !getOwner().isOwner((PlayerEntity) entity) && !ModuleUtils.checkForModule(world, pos, (PlayerEntity)entity, CustomModules.WHITELIST)) {
				PlayerUtils.sendMessageToPlayer((PlayerEntity) entity, ClientUtils.localize(SCContent.retinalScanner.getTranslationKey()), ClientUtils.localize("messages.securitycraft:retinalScanner.notOwner").replace("#", getOwner().getName()), TextFormatting.RED);
				return;
			}

			BlockUtils.setBlockProperty(world, pos, RetinalScannerBlock.POWERED, true);
			world.getPendingBlockTicks().scheduleTick(new BlockPos(pos), SCContent.retinalScanner, 60);

			if(entity instanceof PlayerEntity && sendMessage.asBoolean())
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
	public CustomModules[] acceptedModules() {
		return new CustomModules[]{CustomModules.WHITELIST, CustomModules.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ activatedByEntities, sendMessage };
	}

}
