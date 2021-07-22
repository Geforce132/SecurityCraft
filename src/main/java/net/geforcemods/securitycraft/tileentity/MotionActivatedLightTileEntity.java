package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.blocks.MotionActivatedLightBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class MotionActivatedLightTileEntity extends CustomizableTileEntity {

	private DoubleOption searchRadiusOption = new DoubleOption(this::getBlockPos, "searchRadius", 5.0D, 5.0D, 20.0D, 1.0D, true);

	public MotionActivatedLightTileEntity()
	{
		super(SCContent.teTypeMotionLight);
	}

	@Override
	public boolean attackEntity(Entity entity) {
		if(entity instanceof Player && PlayerUtils.isPlayerMountedOnCamera((Player)entity))
			MotionActivatedLightBlock.toggleLight(level, worldPosition, getBlockState(), getOwner(), false);
		else if(entity instanceof LivingEntity && getBlockState().getBlock() == SCContent.MOTION_ACTIVATED_LIGHT.get())
			MotionActivatedLightBlock.toggleLight(level, worldPosition, getBlockState(), getOwner(), !EntityUtils.isInvisible((LivingEntity)entity)); //also automatically switches on/off based on if the entity turns (in-)visible

		return false;
	}

	@Override
	public void attackFailed() {
		if(getBlockState().getBlock() == SCContent.MOTION_ACTIVATED_LIGHT.get() && getBlockState().getValue(MotionActivatedLightBlock.LIT))
			MotionActivatedLightBlock.toggleLight(level, worldPosition, getBlockState(), getOwner(), false);
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
		return searchRadiusOption.get();
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option<?>[] {searchRadiusOption};
	}

}
