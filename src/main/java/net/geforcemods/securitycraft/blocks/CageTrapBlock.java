package net.geforcemods.securitycraft.blocks;

import java.util.function.Consumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.CageTrapBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedIronBarsBlockEntity;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPaneBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class CageTrapBlock extends DisguisableBlock {
	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");

	public CageTrapBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(DEACTIVATED, false).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof CageTrapBlockEntity) {
			CageTrapBlockEntity be = (CageTrapBlockEntity) te;

			if (ctx instanceof EntitySelectionContext) {
				EntitySelectionContext esc = (EntitySelectionContext) ctx;
				Entity entity = esc.getEntity();

				if (be.isDisabled() || entity instanceof PlayerEntity && ((be.isOwnedBy(entity) && be.ignoresOwner()) || be.isAllowed(entity)) || be.allowsOwnableEntity(entity))
					return getCorrectShape(state, level, pos, ctx, be);
				if (entity instanceof MobEntity && !state.getValue(DEACTIVATED))
					return be.capturesMobs() ? VoxelShapes.empty() : getCorrectShape(state, level, pos, ctx, be);
				else if (entity instanceof ItemEntity)
					return getCorrectShape(state, level, pos, ctx, be);
			}

			return state.getValue(DEACTIVATED) ? getCorrectShape(state, level, pos, ctx, be) : VoxelShapes.empty();
		}
		else
			return VoxelShapes.empty(); //shouldn't happen
	}

	private VoxelShape getCorrectShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx, DisguisableBlockEntity disguisableTe) {
		if (disguisableTe.isModuleEnabled(ModuleType.DISGUISE)) {
			ItemStack moduleStack = disguisableTe.getModule(ModuleType.DISGUISE);

			if (!moduleStack.isEmpty() && ModuleItem.getBlockAddon(moduleStack) != null)
				return super.getCollisionShape(state, level, pos, ctx);
		}

		return VoxelShapes.block();
	}

	@Override
	public void entityInside(BlockState state, World level, BlockPos pos, Entity entity) {
		if (!level.isClientSide) {
			CageTrapBlockEntity cageTrap = (CageTrapBlockEntity) level.getBlockEntity(pos);

			if (cageTrap.isDisabled())
				return;

			boolean isPlayer = entity instanceof PlayerEntity;

			if (isPlayer || (entity instanceof MobEntity && cageTrap.capturesMobs())) {
				if (!getShape(state, level, pos, ISelectionContext.of(entity)).bounds().move(pos).intersects(entity.getBoundingBox()))
					return;

				if ((isPlayer && cageTrap.isOwnedBy(entity)) && cageTrap.ignoresOwner() || cageTrap.allowsOwnableEntity(entity))
					return;

				if (state.getValue(DEACTIVATED))
					return;

				BlockPos topMiddle = pos.above(4);
				Owner owner = cageTrap.getOwner();
				String ownerUUID = owner.getUUID();
				String ownerName = owner.getName();

				loopIronBarPositions(pos.mutable(), barPos -> {
					if (level.isEmptyBlock(barPos)) {
						if (barPos.equals(topMiddle))
							level.setBlockAndUpdate(barPos, SCContent.HORIZONTAL_REINFORCED_IRON_BARS.get().defaultBlockState());
						else
							level.setBlockAndUpdate(barPos, ((ReinforcedPaneBlock) SCContent.REINFORCED_IRON_BARS.get()).getStateForPlacement(level, barPos));
					}

					TileEntity barBe = level.getBlockEntity(barPos);

					if (barBe instanceof IOwnable)
						((IOwnable) barBe).setOwner(ownerUUID, ownerName);

					if (barBe instanceof ReinforcedIronBarsBlockEntity)
						((ReinforcedIronBarsBlockEntity) barBe).setCanDrop(false);
				});
				level.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, true));
				level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundCategory.BLOCKS, 3.0F, 1.0F);

				if (isPlayer && PlayerUtils.isPlayerOnline(ownerName))
					PlayerUtils.sendMessageToPlayer(ownerName, Utils.localize(SCContent.CAGE_TRAP.get().getDescriptionId()), Utils.localize("messages.securitycraft:cageTrap.captured", ((PlayerEntity) entity).getName(), Utils.getFormattedCoordinates(pos)), TextFormatting.BLACK);
			}
		}
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		ItemStack stack = player.getItemInHand(hand);

		if (stack.getItem() == SCContent.WIRE_CUTTERS.get()) {
			if (!state.getValue(DEACTIVATED)) {
				level.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, true));

				if (!player.isCreative())
					stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

				level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
				return ActionResultType.SUCCESS;
			}
		}
		else if (stack.getItem() == Items.REDSTONE && state.getValue(DEACTIVATED)) {
			level.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, false));

			if (!player.isCreative())
				stack.shrink(1);

			level.playSound(null, pos, SoundEvents.TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 1.0F);
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return super.getStateForPlacement(ctx).setValue(DEACTIVATED, false);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(DEACTIVATED, WATERLOGGED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new CageTrapBlockEntity();
	}

	public static void loopIronBarPositions(BlockPos.Mutable pos, Consumer<BlockPos.Mutable> positionAction) {
		pos.move(-1, 1, -1);

		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 3; x++) {
				for (int z = 0; z < 3; z++) {
					//skip the middle column above the cage trap, but not the place where the horizontal iron bars are
					if (!(x == 1 && z == 1 && y != 3))
						positionAction.accept(pos);

					pos.move(0, 0, 1);
				}

				pos.move(1, 0, -3);
			}

			pos.move(-3, 1, 0);
		}
	}
}
