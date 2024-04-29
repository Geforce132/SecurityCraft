package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;

public class SecretStandingSignBlock extends StandingSignBlock {
	public SecretStandingSignBlock(BlockBehaviour.Properties properties, WoodType woodType) {
		super(woodType, properties);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
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
		if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof IModuleInventory inv)
			inv.dropAllModules();

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof SecretSignBlockEntity be && (be.isOwnedBy(player) || be.isAllowed(player)))
			return super.useWithoutItem(state, level, pos, player, hit);

		return InteractionResult.PASS;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SecretSignBlockEntity(pos, state);
	}

	@Override
	public String getDescriptionId() {
		return super.getDescriptionId().replace("_standing", "");
	}
}