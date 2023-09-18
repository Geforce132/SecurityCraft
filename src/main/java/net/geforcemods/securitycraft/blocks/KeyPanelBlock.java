package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.KeyPanelBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class KeyPanelBlock extends AbstractPanelBlock {
	public static final VoxelShape FLOOR_NS = Block.box(2.0D, 0.0D, 1.0D, 14.0D, 1.0D, 15.0D);
	public static final VoxelShape FLOOR_EW = Block.box(1.0D, 0.0D, 2.0D, 15.0D, 1.0D, 14.0D);
	public static final VoxelShape CEILING_NS = Block.box(2.0D, 15.0D, 1.0D, 14.0D, 16.0D, 15.0D);
	public static final VoxelShape CEILING_EW = Block.box(1.0D, 15.0D, 2.0D, 15.0D, 16.0D, 14.0D);
	public static final VoxelShape WALL_N = Block.box(2.0D, 1.0D, 15.0D, 14.0D, 15.0D, 16.0D);
	public static final VoxelShape WALL_E = Block.box(0.0D, 1.0D, 2.0D, 1.0D, 15.0D, 14.0D);
	public static final VoxelShape WALL_S = Block.box(2.0D, 1.0D, 0.0D, 14.0D, 15.0D, 1.0D);
	public static final VoxelShape WALL_W = Block.box(15.0D, 1.0D, 2.0D, 16.0D, 15.0D, 14.0D);

	public KeyPanelBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
		switch (state.getValue(FACE)) {
			case FLOOR:
				switch (state.getValue(FACING)) {
					case NORTH:
						return FLOOR_NS;
					case EAST:
						return FLOOR_EW;
					case SOUTH:
						return FLOOR_NS;
					case WEST:
						return FLOOR_EW;
					default:
						return VoxelShapes.empty();
				}
			case CEILING:
				switch (state.getValue(FACING)) {
					case NORTH:
						return CEILING_NS;
					case EAST:
						return CEILING_EW;
					case SOUTH:
						return CEILING_NS;
					case WEST:
						return CEILING_EW;
					default:
						return VoxelShapes.empty();
				}
			case WALL:
				switch (state.getValue(FACING)) {
					case NORTH:
						return WALL_N;
					case EAST:
						return WALL_E;
					case SOUTH:
						return WALL_S;
					case WEST:
						return WALL_W;
					default:
						return VoxelShapes.empty();
				}
		}

		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
		return VoxelShapes.empty();
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (state.getValue(POWERED))
			return ActionResultType.PASS;
		else if (!world.isClientSide) {
			KeyPanelBlockEntity te = (KeyPanelBlockEntity) world.getBlockEntity(pos);

			if (te.isDisabled())
				player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else if (te.verifyPasscodeSet(world, pos, te, player)) {
				if (te.isDenied(player)) {
					if (te.sendsMessages())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
				}
				else if (te.isAllowed(player)) {
					if (te.sendsMessages())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

					activate(state, world, pos, te.getSignalLength());
				}
				else if (player.getItemInHand(hand).getItem() != SCContent.CODEBREAKER.get())
					te.openPasscodeGUI(world, pos, player);
			}
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new KeyPanelBlockEntity();
	}
}
