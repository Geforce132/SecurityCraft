package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.geforcemods.securitycraft.blocks.BlockMotionActivatedLight;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class TileEntityMotionLight extends CustomizableSCTE {
	
	private OptionDouble searchRadiusOption = new OptionDouble("searchRadius", SecurityCraft.config.motionActivatedLightSearchRadius, 5.0D, 20.0D, 5.0D);

	@Override
	public boolean attackEntity(Entity entity) {
		if(entity instanceof EntityPlayer)
		{
			if(BlockUtils.getBlock(getWorld(), pos) == SCContent.motionActivatedLightOff)
				BlockMotionActivatedLight.toggleLight(worldObj, pos, searchRadiusOption.asDouble(), getOwner(), true);
			
			return false;
		}
		
		return false;
	}
	
	@Override
	public void attackFailed() {
		if(BlockUtils.getBlock(getWorld(), pos) == SCContent.motionActivatedLightOn)
			BlockMotionActivatedLight.toggleLight(worldObj, pos, searchRadiusOption.asDouble(), getOwner(), false);
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
	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[] {};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option<?>[] {searchRadiusOption};
	}

}
