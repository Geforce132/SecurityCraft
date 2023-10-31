package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.function.BiConsumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.neoforged.neoforge.common.IPlantable;
import net.neoforged.neoforge.common.NeoForge;

public class ReinforcedSnowyDirtBlock extends SnowyDirtBlock implements IReinforcedBlock, BonemealableBlock, EntityBlock {
	private Block vanillaBlock;

	public ReinforcedSnowyDirtBlock(BlockBehaviour.Properties properties, Block vB) {
		super(properties);
		this.vanillaBlock = vB;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if (facing != Direction.UP)
			return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
		else
			return state.setValue(SNOWY, isSnowySetting(facingState));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState state = ctx.getLevel().getBlockState(ctx.getClickedPos().above());
		return defaultBlockState().setValue(SNOWY, isSnowySetting(state));
	}

	private static boolean isSnowySetting(BlockState state) {
		return state.is(BlockTags.SNOW);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
		if (this == SCContent.REINFORCED_MYCELIUM.get()) {
			super.animateTick(state, level, pos, rand);

			if (rand.nextInt(10) == 0)
				level.addParticle(ParticleTypes.MYCELIUM, (double) pos.getX() + (double) rand.nextFloat(), pos.getY() + 1.1D, (double) pos.getZ() + (double) rand.nextFloat(), 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
		return this == SCContent.REINFORCED_GRASS_BLOCK.get() && level.getBlockState(pos.above()).isAir();
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource rand, BlockPos pos, BlockState state) {
		return this == SCContent.REINFORCED_GRASS_BLOCK.get();
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource rand, BlockPos pos, BlockState state) {
		((GrassBlock) Blocks.GRASS_BLOCK).performBonemeal(level, rand, pos, state);
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable) {
		return SCContent.REINFORCED_DIRT.get().canSustainPlant(state, level, pos, facing, plantable);
	}

	@Override
	public boolean onTreeGrow(BlockState state, LevelReader level, BiConsumer<BlockPos, BlockState> placeFunction, RandomSource randomSource, BlockPos pos, TreeConfiguration config) {
		return true; //Do not allow trees to replace reinforced blocks with dirt when growing
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlock;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OwnableBlockEntity(pos, state);
	}
}
