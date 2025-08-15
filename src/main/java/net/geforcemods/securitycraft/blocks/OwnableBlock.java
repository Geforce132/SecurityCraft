package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;

public class OwnableBlock extends BaseEntityBlock {
	private static float destroyTimeTempStorage = -1.0F;
	protected final float destroyTimeForOwner;

	public OwnableBlock(BlockBehaviour.Properties properties) {
		super(withReinforcedDestroyTime(properties));
		destroyTimeForOwner = getStoredDestroyTime();
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, destroyTimeForOwner, state, player, level, pos);
	}

	public float defaultDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return super.getDestroyProgress(state, player, level, pos);
	}

	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
		return ConfigHandler.SERVER.alwaysDrop.get() || super.canHarvestBlock(state, level, pos, player);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));

		if (stack.hasCustomHoverName() && level.getBlockEntity(pos) instanceof INameSetter nameable)
			nameable.setCustomName(stack.getHoverName());
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new OwnableBlockEntity(pos, state);
	}

	public static BlockBehaviour.Properties withReinforcedDestroyTime(BlockBehaviour.Properties properties) {
		destroyTimeTempStorage = properties.destroyTime;
		return properties.destroyTime(-1.0F);
	}

	public static float getStoredDestroyTime() {
		float storedDestroyTime = destroyTimeTempStorage;

		destroyTimeTempStorage = -1.0F;
		return storedDestroyTime;
	}
}
