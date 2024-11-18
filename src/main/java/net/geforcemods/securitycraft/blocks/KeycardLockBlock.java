package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.KeycardLockBlockEntity;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.items.UniversalKeyChangerItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
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

public class KeycardLockBlock extends AbstractPanelBlock {
	public static final VoxelShape FLOOR_NS = Block.box(4.0D, 0.0D, 3.0D, 12.0D, 3.0D, 13.0D);
	public static final VoxelShape FLOOR_EW = Block.box(3.0D, 0.0D, 4.0D, 13.0D, 3.0D, 12.0D);
	public static final VoxelShape CEILING_NS = Block.box(4.0D, 13.0D, 3.0D, 12.0D, 16.0D, 13.0D);
	public static final VoxelShape CEILING_EW = Block.box(3.0D, 13.0D, 4.0D, 13.0D, 16.0D, 12.0D);
	public static final VoxelShape WALL_N = Block.box(4.0D, 3.0D, 13.0D, 12.0D, 13.0D, 16.0D);
	public static final VoxelShape WALL_E = Block.box(0.0D, 3.0D, 4.0D, 3.0D, 13.0D, 12.0D);
	public static final VoxelShape WALL_S = Block.box(4.0D, 3.0D, 0.0D, 12.0D, 13.0D, 3.0D);
	public static final VoxelShape WALL_W = Block.box(13.0D, 3.0D, 4.0D, 16.0D, 13.0D, 12.0D);

	public KeycardLockBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return KeycardReaderBlock.<KeycardLockBlockEntity>use(state, level, pos, player, hand, (stack, be) -> {
			if (!be.isSetUp()) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:keycard_lock.not_set_up"), TextFormatting.RED);
				return;
			}

			if (stack.getItem() instanceof KeycardItem) {
				boolean hasTag = stack.hasTag();

				if (!hasTag || !stack.getTag().getBoolean("linked"))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:keycard_lock.unlinked_keycard"), TextFormatting.RED);
				else if (hasTag) {
					CompoundNBT tag = stack.getTag();
					Owner keycardOwner = new Owner(tag.getString("ownerName"), tag.getString("ownerUUID"));

					if (!TeamUtils.areOnSameTeam(be.getOwner(), keycardOwner) || !be.getOwner().getUUID().equals(keycardOwner.getUUID()))
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:keycard_lock.different_owner"), TextFormatting.RED);
				}

				return;
			}

			if (stack.getItem() instanceof UniversalKeyChangerItem) {
				if (be.isOwnedBy(player)) {
					stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
					be.reset();
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:keycard_lock.reset"), TextFormatting.GREEN);
				}
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", be.getOwner().getName()), TextFormatting.RED);
			}
		});
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
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
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new KeycardLockBlockEntity();
	}
}
