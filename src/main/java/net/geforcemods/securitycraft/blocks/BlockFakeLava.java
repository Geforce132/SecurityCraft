package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFakeLava extends BlockFlowingFluid
{
	public BlockFakeLava()
	{
		super(SCContent.fakeLava, Block.Properties.create(Material.LAVA).doesNotBlockMovement().tickRandomly().hardnessAndResistance(-1.0F, 6000000.0F).lightValue(15));
	}

	@Override
	public void onEntityCollision(IBlockState state, World world, BlockPos pos, Entity entity)
	{
		if(!world.isRemote)
			if(entity instanceof EntityPlayer){
				((EntityPlayer) entity).heal(4);
				((EntityPlayer) entity).extinguish();
			}
	}
}
