package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.KeycardLockBlockEntity;
import net.geforcemods.securitycraft.components.KeycardData;
import net.geforcemods.securitycraft.components.OwnerData;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.items.UniversalKeyChangerItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KeycardLockBlock extends AbstractPanelBlock {
	public static final VoxelShape FLOOR_NS = Block.box(4.0D, 0.0D, 3.0D, 12.0D, 3.0D, 13.0D);
	public static final VoxelShape FLOOR_EW = Block.box(3.0D, 0.0D, 4.0D, 13.0D, 3.0D, 12.0D);
	public static final VoxelShape CEILING_NS = Block.box(4.0D, 13.0D, 3.0D, 12.0D, 16.0D, 13.0D);
	public static final VoxelShape CEILING_EW = Block.box(3.0D, 13.0D, 4.0D, 13.0D, 16.0D, 12.0D);
	public static final VoxelShape WALL_N = Block.box(4.0D, 3.0D, 13.0D, 12.0D, 13.0D, 16.0D);
	public static final VoxelShape WALL_E = Block.box(0.0D, 3.0D, 4.0D, 3.0D, 13.0D, 12.0D);
	public static final VoxelShape WALL_S = Block.box(4.0D, 3.0D, 0.0D, 12.0D, 13.0D, 3.0D);
	public static final VoxelShape WALL_W = Block.box(13.0D, 3.0D, 4.0D, 16.0D, 13.0D, 12.0D);

	public KeycardLockBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return KeycardReaderBlock.<KeycardLockBlockEntity>useItemOn(stack, state, level, pos, player, hand, be -> {
			if (!be.isSetUp()) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:keycard_lock.not_set_up"), ChatFormatting.RED);
				return;
			}

			if (stack.getItem() instanceof KeycardItem) {
				KeycardData keycardData = stack.get(SCContent.KEYCARD_DATA);

				if (keycardData == null)
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:keycard_lock.unlinked_keycard"), ChatFormatting.RED);
				else {
					Owner keycardOwner = stack.getOrDefault(SCContent.OWNER_DATA, OwnerData.DEFAULT).toOwner();

					if (!TeamUtils.areOnSameTeam(be.getOwner(), keycardOwner) || !be.getOwner().getUUID().equals(keycardOwner.getUUID()))
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:keycard_lock.different_owner"), ChatFormatting.RED);
				}

				return;
			}

			if (stack.getItem() instanceof UniversalKeyChangerItem) {
				if (be.isOwnedBy(player)) {
					stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
					be.reset();
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:keycard_lock.reset"), ChatFormatting.GREEN);
				}
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", be.getOwner().getName()), ChatFormatting.RED);
			}
		});
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return switch (state.getValue(FACE)) {
			case FLOOR -> switch (state.getValue(FACING)) {
				case NORTH, SOUTH -> FLOOR_NS;
				case EAST, WEST -> FLOOR_EW;
				default -> Shapes.empty();
			};
			case CEILING -> switch (state.getValue(FACING)) {
				case NORTH, SOUTH -> CEILING_NS;
				case EAST, WEST -> CEILING_EW;
				default -> Shapes.empty();
			};
			case WALL -> switch (state.getValue(FACING)) {
				case NORTH -> WALL_N;
				case EAST -> WALL_E;
				case SOUTH -> WALL_S;
				case WEST -> WALL_W;
				default -> Shapes.empty();
			};
		};
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KeycardLockBlockEntity(pos, state);
	}
}
