package net.geforcemods.securitycraft.blocks;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class SecureRedstoneInterfaceBlock extends DisguisableBlock {
	public static final BooleanProperty SENDER = BooleanProperty.create("sender");
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public SecureRedstoneInterfaceBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(SENDER, false).setValue(FACING, Direction.UP));
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof SecureRedstoneInterfaceBlockEntity) {
			SecureRedstoneInterfaceBlockEntity be = (SecureRedstoneInterfaceBlockEntity) te;

			if (be.isOwnedBy(player)) {
				if (!level.isClientSide) {
					if (be.isDisabled())
						player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
					else if (be.getOwner().isValidated())
						SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new OpenScreen(DataType.SECURE_REDSTONE_INTERFACE, pos));
					else
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:universalOwnerChanger.ownerInvalidated"), TextFormatting.RED);
				}

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return !state.getValue(SENDER);
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof SecureRedstoneInterfaceBlockEntity) {
			SecureRedstoneInterfaceBlockEntity be = (SecureRedstoneInterfaceBlockEntity) te;

			if (be.isSender() && !be.isDisabled())
				be.refreshPower();
		}
	}

	@Override
	public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction direction) {
		if (state.getValue(FACING) == direction)
			return getSignal(state, level, pos, direction);
		else
			return 0;
	}

	@Override
	public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction direction) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof SecureRedstoneInterfaceBlockEntity)
			return ((SecureRedstoneInterfaceBlockEntity) te).getRedstonePowerOutput();
		else
			return 0;
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (!state.is(newState.getBlock())) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof SecureRedstoneInterfaceBlockEntity) {
				SecureRedstoneInterfaceBlockEntity be = (SecureRedstoneInterfaceBlockEntity) te;

				be.disabled.setValue(true); //make sure receivers that update themselves don't check for this one

				if (be.isSender())
					be.tellSimilarReceiversToRefresh();
				else
					be.updateNeighbors();
			}
		}

		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getClickedFace());
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new SecureRedstoneInterfaceBlockEntity();
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(SENDER, FACING, WATERLOGGED);
	}

	public static class DoorActivator implements IDoorActivator {
		private final List<Block> blocks = Arrays.asList(SCContent.SECURE_REDSTONE_INTERFACE.get());

		@Override
		public boolean isPowering(World level, BlockPos pos, BlockState state, TileEntity be, Direction direction, int distance) {
			return !state.getValue(SENDER) && be instanceof SecureRedstoneInterfaceBlockEntity && ((SecureRedstoneInterfaceBlockEntity) be).isProtectedSignal() && ((SecureRedstoneInterfaceBlockEntity) be).getPower() > 0;
		}

		@Override
		public List<Block> getBlocks() {
			return blocks;
		}
	}
}
