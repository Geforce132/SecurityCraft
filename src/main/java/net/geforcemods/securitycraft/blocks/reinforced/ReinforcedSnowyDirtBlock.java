package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SnowyDirtBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FlowersFeature;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedSnowyDirtBlock extends SnowyDirtBlock implements IReinforcedBlock, IGrowable {
	private Block vanillaBlock;

	public ReinforcedSnowyDirtBlock(AbstractBlock.Properties properties, Block vB) {
		super(properties);
		this.vanillaBlock = vB;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos) {
		if (facing != Direction.UP)
			return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
		else {
			Block block = facingState.getBlock();
			return state.setValue(SNOWY, block == Blocks.SNOW_BLOCK || block == Blocks.SNOW || block == SCContent.REINFORCED_SNOW_BLOCK.get());
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		Block block = ctx.getLevel().getBlockState(ctx.getClickedPos().above()).getBlock();
		return defaultBlockState().setValue(SNOWY, block == Blocks.SNOW_BLOCK || block == Blocks.SNOW || block == SCContent.REINFORCED_SNOW_BLOCK.get());
	}

	@Override
	public void animateTick(BlockState state, World level, BlockPos pos, Random rand) {
		if (this == SCContent.REINFORCED_MYCELIUM.get()) {
			super.animateTick(state, level, pos, rand);

			if (rand.nextInt(10) == 0)
				level.addParticle(ParticleTypes.MYCELIUM, (double) pos.getX() + (double) rand.nextFloat(), pos.getY() + 1.1D, (double) pos.getZ() + (double) rand.nextFloat(), 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public boolean canSustainPlant(BlockState state, IBlockReader level, BlockPos pos, Direction facing, IPlantable plantable) {
		return SCContent.REINFORCED_DIRT.get().canSustainPlant(state, level, pos, facing, plantable);
	}

	@Override
	public boolean isValidBonemealTarget(IBlockReader level, BlockPos pos, BlockState state, boolean isClient) {
		return this == SCContent.REINFORCED_GRASS_BLOCK.get() && level.getBlockState(pos.above()).isAir(level, pos.above());
	}

	@Override
	public boolean isBonemealSuccess(World level, Random rand, BlockPos pos, BlockState state) {
		return this == SCContent.REINFORCED_GRASS_BLOCK.get();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void performBonemeal(ServerWorld level, Random rand, BlockPos pos, BlockState state) {
		BlockPos posAbove = pos.above();
		BlockState grass = Blocks.GRASS.defaultBlockState();

		for (int i = 0; i < 128; ++i) {
			BlockPos tempPos = posAbove;
			int j = 0;

			while (true) {
				if (j >= i / 16) {
					BlockState tempState = level.getBlockState(tempPos);

					if (tempState.getBlock() == grass.getBlock() && rand.nextInt(10) == 0)
						((IGrowable) grass.getBlock()).performBonemeal(level, rand, tempPos, tempState);

					if (!tempState.isAir(level, tempPos))
						break;

					BlockState placeState;

					if (rand.nextInt(8) == 0) {
						List<ConfiguredFeature<?, ?>> flowers = level.getBiome(tempPos).getGenerationSettings().getFlowerFeatures();

						if (flowers.isEmpty())
							break;

						ConfiguredFeature<?, ?> configuredFeature = flowers.get(0);

						placeState = ((FlowersFeature) configuredFeature.feature).getRandomFlower(rand, tempPos, configuredFeature.config());
					}
					else
						placeState = grass;

					if (placeState.canSurvive(level, tempPos))
						level.setBlock(tempPos, placeState, 3);

					break;
				}

				tempPos = tempPos.offset(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

				if (level.getBlockState(tempPos.below()).getBlock() != this || level.getBlockState(tempPos).isCollisionShapeFullBlock(level, tempPos))
					break;

				++j;
			}
		}
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlock;
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) placer));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new OwnableBlockEntity();
	}
}
