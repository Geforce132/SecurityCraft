package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;

public class SecretWallSignBlock extends WallSignBlock {
	public SecretWallSignBlock(Block.Properties properties, WoodType woodType) {
		super(properties, woodType);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (Player) placer));
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative() && level.getBlockEntity(pos) instanceof IModuleInventory inv)
			inv.getInventory().clear();

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof IModuleInventory inv)
				inv.dropAllModules();

			if (!newState.hasBlockEntity())
				level.removeBlockEntity(pos);
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide && player.getItemInHand(hand).getItem() == SCContent.ADMIN_TOOL.get())
			return SCContent.ADMIN_TOOL.get().useOn(new UseOnContext(player, hand, hit));

		SecretSignBlockEntity be = (SecretSignBlockEntity) level.getBlockEntity(pos);

		if (be != null && be.isPlayerAllowedToSeeText(player))
			return super.use(state, level, pos, player, hand, hit);

		return InteractionResult.FAIL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SecretSignBlockEntity(pos, state);
	}

	@Override
	public String getDescriptionId() {
		return Util.makeDescriptionId("block", getRegistryName()).replace("_wall", "");
	}
}