package net.geforcemods.securitycraft.blocks;

import java.util.function.BiConsumer;

import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.NeoForge;

public class OwnableFenceGateBlock extends FenceGateBlock implements EntityBlock {
	public OwnableFenceGateBlock(BlockBehaviour.Properties properties, WoodType woodType) {
		super(woodType, properties);
	}

	public OwnableFenceGateBlock(BlockBehaviour.Properties properties, SoundEvent openSound, SoundEvent closeSound) {
		super(properties, openSound, closeSound);
	}

	@Override
	public void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {} //disallow wind charges to open the gate

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));

		if (stack.hasCustomHoverName() && level.getBlockEntity(pos) instanceof INameSetter nameable)
			nameable.setCustomName(stack.getHoverName());
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!level.isClientSide) {
			boolean isPoweredSCBlock = BlockUtils.hasActiveSCBlockNextTo(level, pos);

			if (isPoweredSCBlock || block.defaultBlockState().isSignalSource())
				if (isPoweredSCBlock && !state.getValue(OPEN) && !state.getValue(POWERED)) {
					level.setBlock(pos, state.setValue(OPEN, true).setValue(POWERED, true), 2);
					level.playSound(null, pos, openSound, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
					level.gameEvent(null, GameEvent.BLOCK_OPEN, pos);
				}
				else if (!isPoweredSCBlock && state.getValue(OPEN) && state.getValue(POWERED)) {
					level.setBlock(pos, state.setValue(OPEN, false).setValue(POWERED, false), 2);
					level.playSound(null, pos, closeSound, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
					level.gameEvent(null, GameEvent.BLOCK_CLOSE, pos);
				}
				else if (isPoweredSCBlock != state.getValue(POWERED))
					level.setBlock(pos, state.setValue(POWERED, isPoweredSCBlock), 2);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OwnableBlockEntity(pos, state);
	}
}
