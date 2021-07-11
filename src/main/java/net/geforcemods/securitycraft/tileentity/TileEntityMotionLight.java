package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.geforcemods.securitycraft.blocks.BlockMotionActivatedLight;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class TileEntityMotionLight extends CustomizableSCTE {

	private OptionDouble searchRadiusOption = new OptionDouble(this::getPos, "searchRadius", 5.0D, 5.0D, 20.0D, 1.0D, true);

	@Override
	public boolean attackEntity(Entity entity) {
		if(entity instanceof EntityPlayer && PlayerUtils.isPlayerMountedOnCamera((EntityPlayer)entity))
			BlockMotionActivatedLight.toggleLight(world, pos, world.getBlockState(pos), getOwner(), false);
		else if(entity instanceof EntityLivingBase)
		{
			IBlockState state = world.getBlockState(pos);

			//also automatically switches on/off based on if the entity turns (in-)visible
			if(state.getBlock() == SCContent.motionActivatedLight && !state.getValue(BlockMotionActivatedLight.LIT))
				BlockMotionActivatedLight.toggleLight(world, pos, state, getOwner(), !EntityUtils.isInvisible((EntityLivingBase)entity));
		}

		return false;
	}

	@Override
	public void attackFailed() {
		IBlockState state = world.getBlockState(pos);

		if(state.getBlock() == SCContent.motionActivatedLight && state.getValue(BlockMotionActivatedLight.LIT))
			BlockMotionActivatedLight.toggleLight(world, pos, state, getOwner(), false);
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
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[] {};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option<?>[] {searchRadiusOption};
	}
}
