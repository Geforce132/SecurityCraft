package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.tileentity.TileEntityMotionLight;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockMotionActivatedLight extends BlockOwnable {
	
	public BlockMotionActivatedLight(Material material) {
		super(material);
		
	}
	
	public static void toggleLight(World world, BlockPos pos, double searchRadius, Owner owner, boolean isLit) {
		if(!world.isRemote)
		{
			if(isLit)
			{
				BlockUtils.setBlock(world, pos, SCContent.motionActivatedLightOn);
				
				if(((IOwnable) world.getTileEntity(pos)) != null)
					((IOwnable) world.getTileEntity(pos)).setOwner(owner.getUUID(), owner.getName());
				
				BlockUtils.updateAndNotify(world, pos, SCContent.motionActivatedLightOn, 1, false);
			}
			else
			{
				BlockUtils.setBlock(world, pos, SCContent.motionActivatedLightOff);
				
				if(((IOwnable) world.getTileEntity(pos)) != null)
					((IOwnable) world.getTileEntity(pos)).setOwner(owner.getUUID(), owner.getName());
				
				BlockUtils.updateAndNotify(world, pos, SCContent.motionActivatedLightOff, 1, false);
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityMotionLight().attacks(EntityPlayer.class, SecurityCraft.config.motionActivatedLightSearchRadius, 1);
	}
	
}
