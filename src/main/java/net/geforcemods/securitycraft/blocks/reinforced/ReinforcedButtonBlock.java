package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.function.BiConsumer;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.AllowlistOnlyBlockEntity;
import net.geforcemods.securitycraft.misc.CommonDoorActivator;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;

public class ReinforcedButtonBlock extends ButtonBlock implements IReinforcedBlock, EntityBlock {
	private final Block vanillaBlock;

	public ReinforcedButtonBlock(BlockBehaviour.Properties properties, Block vb, BlockSetType blockSetType, int ticksToStayPressed) {
		super(blockSetType, ticksToStayPressed, properties);
		this.vanillaBlock = vb;
		CommonDoorActivator.addActivator(this);
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, state, player, level, pos);
	}

	@Override
	protected void onExplosionHit(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer) {
		if (!explosion.canTriggerBlocks())
			super.onExplosionHit(state, level, pos, explosion, dropConsumer);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult rayTrace) {
		if (isAllowedToPress((AllowlistOnlyBlockEntity) level.getBlockEntity(pos), player))
			return super.useWithoutItem(state, level, pos, player, rayTrace);

		return InteractionResult.FAIL;
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative() && level.getBlockEntity(pos) instanceof IModuleInventory inv)
			inv.getInventory().clear();

		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (!ConfigHandler.SERVER.vanillaToolBlockBreaking.get() && level.getBlockEntity(pos) instanceof IModuleInventory inv)
				inv.dropAllModules();

			if (!isMoving && state.getValue(POWERED)) {
				level.updateNeighborsAt(pos, this);
				level.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), this);
			}
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	public boolean isAllowedToPress(AllowlistOnlyBlockEntity be, Player entity) {
		return be.isOwnedBy(entity) || be.isAllowed(entity);
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
		return new AllowlistOnlyBlockEntity(pos, state);
	}
}
