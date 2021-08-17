package net.geforcemods.securitycraft.blocks;

import java.util.stream.Stream;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.inventory.GenericTEMenu;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class TrophySystemBlock extends OwnableBlock {

	private static final VoxelShape SHAPE = Stream.of(
			Block.box(6.5, 0, 12, 9.5, 1.5, 15),
			Block.box(5.5, 7, 5.5, 10.5, 11, 10.5),
			Block.box(7, 12, 7, 9, 13, 9),
			Block.box(6.5, 12.5, 6.5, 9.5, 15, 9.5),
			Block.box(7, 14.5, 7, 9, 15.5, 9),
			Block.box(7.25, 9, 7.25, 8.75, 12, 8.75),
			Block.box(1, 0, 6.5, 4, 1.5, 9.5),
			Block.box(12, 0, 6.5, 15, 1.5, 9.5),
			Block.box(6.5, 0, 1, 9.5, 1.5, 4)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.block());

	public TrophySystemBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos){
		return BlockUtils.isSideSolid(world, pos.below(), Direction.UP);
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if(!canSurvive(state, world, pos)) {
			world.destroyBlock(pos, true);
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (((IOwnable) world.getBlockEntity(pos)).getOwner().isOwner(player)) {
			if (!world.isClientSide)
				NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
						return new GenericTEMenu(SCContent.mTypeTrophySystem, windowId, world, pos);
					}

					@Override
					public Component getDisplayName() {
						return new TranslatableComponent(getDescriptionId());
					}
				}, pos);

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter source, BlockPos pos, CollisionContext context)
	{
		return SHAPE;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TrophySystemBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.beTypeTrophySystem, WorldUtils::blockEntityTicker);
	}
}
