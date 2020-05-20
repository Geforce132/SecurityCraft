package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ProjectorContainer extends Container {

	public final int SIZE = 0;

	public ProjectorTileEntity te;
	
	public ProjectorContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory) 
	{
		super(SCContent.cTypeProjector, windowId);
		
		if(world.getTileEntity(pos) instanceof ProjectorTileEntity)
			te = (ProjectorTileEntity) world.getTileEntity(pos);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) 
	{
		return true;
	}

}
