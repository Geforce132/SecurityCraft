package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FakeWaterBlock extends FlowingFluidBlock
{
	public FakeWaterBlock()
	{
		super(SCContent.fakeWater, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(-1.0F, 6000000.0F));
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		if(!world.isRemote)
		{
			if(!(entity instanceof PlayerEntity) || !((PlayerEntity) entity).isCreative())
				entity.attackEntityFrom(CustomDamageSources.FAKE_WATER, 5F);
		}
	}
}
