package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.blocks.MotionActivatedLightBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class MotionActivatedLightBlockEntity extends CustomizableBlockEntity {

	private DoubleOption searchRadiusOption = new DoubleOption(this::getBlockPos, "searchRadius", 5.0D, 5.0D, 20.0D, 1.0D, true);

	public MotionActivatedLightBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.beTypeMotionLight, pos, state);
	}

	@Override
	public boolean attackEntity(Entity entity) {
		if(entity instanceof Player player && PlayerUtils.isPlayerMountedOnCamera(player))
			MotionActivatedLightBlock.toggleLight(level, worldPosition, getBlockState(), getOwner(), false);
		else if(entity instanceof LivingEntity lEntity && getBlockState().getBlock() == SCContent.MOTION_ACTIVATED_LIGHT.get())
			MotionActivatedLightBlock.toggleLight(level, worldPosition, getBlockState(), getOwner(), !EntityUtils.isInvisible(lEntity)); //also automatically switches on/off based on if the entity turns (in-)visible

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
