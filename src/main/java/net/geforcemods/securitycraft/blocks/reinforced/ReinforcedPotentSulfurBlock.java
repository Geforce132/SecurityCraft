package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.ReinforcedPotentSulfurBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PotentSulfurBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.PotentSulfurBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;

public class ReinforcedPotentSulfurBlock extends PotentSulfurBlock implements IReinforcedBlock, EntityBlock {
	private final float destroyTimeForOwner;

	public ReinforcedPotentSulfurBlock(Properties properties) {
		super(properties);
		destroyTimeForOwner = OwnableBlock.getStoredDestroyTime();
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, destroyTimeForOwner, state, player, level, pos);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
		boolean client = level.isClientSide();

		return (BlockEntityTicker<T>) switch (blockState.getValue(STATE)) {
			case DRY -> null;
			case WET -> client ? PotentSulfurBlockEntity.CLIENT_NOXIOUS_GAS_TICKER : PotentSulfurBlockEntity.SERVER_NAUSEA_EFFECT_TICKER;
			case DORMANT -> client ? PotentSulfurBlockEntity.CLIENT_NOXIOUS_GAS_TICKER : PotentSulfurBlockEntity.SERVER_WAITING_COUNTDOWN_TICKER.andThen(PotentSulfurBlockEntity.SERVER_NAUSEA_EFFECT_TICKER);
			case ERUPTING -> client ? PotentSulfurBlockEntity.CLIENT_GEYSER_PLUME_TICKER.apply(SoundEvents.GEYSER_ERUPTION_ACTIVE).andThen(PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER) : PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER.andThen(PotentSulfurBlockEntity.SERVER_WAITING_COUNTDOWN_TICKER);
			case CONTINUOUS -> client ? PotentSulfurBlockEntity.CLIENT_GEYSER_PLUME_TICKER.apply(SoundEvents.GEYSER_CONTINUOUS_ACTIVE).andThen(PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER) : PotentSulfurBlockEntity.LAUNCH_ENTITY_TICKER;
		};
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.POTENT_SULFUR;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ReinforcedPotentSulfurBlockEntity(pos, state);
	}
}
