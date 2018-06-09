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
import net.minecraft.world.World;

public class BlockMotionActivatedLight extends BlockOwnable {
	
	public BlockMotionActivatedLight(Material material, boolean lightState) {
		super(material);
		
	}
	
	public static void toggleLight(World world, int x, int y, int z, double searchRadius, Owner owner, boolean isLit) {
		if(!world.isRemote)
		{
			if(isLit)
			{
				world.setBlock(x, y, z, SCContent.motionActivatedLightOn);
				
				if(((IOwnable) world.getTileEntity(x, y, z)) != null)
					((IOwnable) world.getTileEntity(x, y, z)).setOwner(owner.getUUID(), owner.getName());
				
				BlockUtils.updateAndNotify(world, x, y, z, SCContent.motionActivatedLightOn, 1, false);
			}
			else
			{
				world.setBlock(x, y, z, SCContent.motionActivatedLightOff);
				
				if(((IOwnable) world.getTileEntity(x, y, z)) != null)
					((IOwnable) world.getTileEntity(x, y, z)).setOwner(owner.getUUID(), owner.getName());
				
				BlockUtils.updateAndNotify(world, x, y, z, SCContent.motionActivatedLightOff, 1, false);
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityMotionLight().attacks(EntityPlayer.class, SecurityCraft.config.motionActivatedLightSearchRadius, 1);
	}
	
}
