package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockFakeLava extends FlowingFluidBlock
{
	public BlockFakeLava()
	{
		super(SCContent.fakeLava, Block.Properties.create(Material.LAVA).doesNotBlockMovement().tickRandomly().hardnessAndResistance(-1.0F, 6000000.0F).lightValue(15));
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		if(!world.isRemote)
			if(entity instanceof PlayerEntity){
				((PlayerEntity) entity).heal(4);
				((PlayerEntity) entity).extinguish();
			}
	}
}
