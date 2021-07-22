package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ReinforcedCryingObsidianBlock extends BaseReinforcedBlock
{
	public ReinforcedCryingObsidianBlock(Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, Random rand)
	{
		if(rand.nextInt(5) == 0)
		{
			Direction direction = Direction.getRandom(rand);

			if(direction != Direction.UP)
			{
				BlockPos offsetPos = pos.relative(direction);
				BlockState offsetState = world.getBlockState(offsetPos);

				if(!state.canOcclude() || !offsetState.isFaceSturdy(world, offsetPos, direction.getOpposite()))
				{
					double xOffset = direction.getStepX() == 0 ? rand.nextDouble() : 0.5D + direction.getStepX() * 0.6D;
					double yOffset = direction.getStepY() == 0 ? rand.nextDouble() : 0.5D + direction.getStepY() * 0.6D;
					double zOffset = direction.getStepZ() == 0 ? rand.nextDouble() : 0.5D + direction.getStepZ() * 0.6D;

					world.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}
}
