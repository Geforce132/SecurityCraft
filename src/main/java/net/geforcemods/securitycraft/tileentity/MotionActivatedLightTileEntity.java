package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.blocks.MotionActivatedLightBlock;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class MotionActivatedLightTileEntity extends CustomizableTileEntity {

	private DoubleOption searchRadiusOption = new DoubleOption("searchRadius", ConfigHandler.CONFIG.motionActivatedLightSearchRadius.get(), 5.0D, 20.0D, 5.0D);

	public MotionActivatedLightTileEntity()
	{
		super(SCContent.teTypeMotionLight);
	}

	@Override
	public boolean attackEntity(Entity entity) {
		if(entity instanceof PlayerEntity && PlayerUtils.isPlayerMountedOnCamera((PlayerEntity)entity))
			MotionActivatedLightBlock.toggleLight(world, pos, getOwner(), false);
		else if(entity instanceof LivingEntity && BlockUtils.getBlock(getWorld(), pos) == SCContent.MOTION_ACTIVATED_LIGHT.get())
			MotionActivatedLightBlock.toggleLight(world, pos, getOwner(), !EntityUtils.isInvisible((LivingEntity)entity)); //also automatically switches on/off based on if the entity turns (in-)visible


		return false;
	}

	@Override
	public void attackFailed() {
		if(BlockUtils.getBlock(getWorld(), pos) == SCContent.MOTION_ACTIVATED_LIGHT.get() && BlockUtils.getBlockProperty(getWorld(), getPos(), MotionActivatedLightBlock.LIT))
			MotionActivatedLightBlock.toggleLight(world, pos, getOwner(), false);
	}

	@Override
	public boolean canAttack() {
		return true;
	}

	@Override
	public boolean shouldSyncToClient() {
		return false;
	}

	@Override
	public double getAttackRange() {
		return searchRadiusOption.asDouble();
	}

	@Override
	public CustomModules[] acceptedModules() {
		return new CustomModules[] {};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option<?>[] {searchRadiusOption};
	}

}
