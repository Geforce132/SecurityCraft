package net.geforcemods.securitycraft.blocks;

import org.apache.logging.log4j.util.TriConsumer;

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
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CageTrapBlock extends DisguisableBlock {
	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");

	public CageTrapBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(DEACTIVATED, false).setValue(WATERLOGGED, false));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext collisionContext) {
		if (level.getBlockEntity(pos) instanceof CageTrapBlockEntity be) {
			if (collisionContext instanceof EntityCollisionContext ctx && ctx.getEntity() != null) {
				Entity entity = ctx.getEntity();

				if (be.isDisabled())
					return getCorrectShape(state, level, pos, ctx, be);
				else if (entity instanceof Player player && ((be.isOwnedBy(player) && be.ignoresOwner()) || be.isAllowed(player)) || entity instanceof OwnableEntity ownableEntity && be.allowsOwnableEntity(ownableEntity))
					return getCorrectShape(state, level, pos, collisionContext, be);
				if (entity instanceof Mob && !state.getValue(DEACTIVATED))
					return be.capturesMobs() ? Shapes.empty() : getCorrectShape(state, level, pos, collisionContext, be);
				else if (entity instanceof ItemEntity)
					return getCorrectShape(state, level, pos, collisionContext, be);
			}

			return state.getValue(DEACTIVATED) ? getCorrectShape(state, level, pos, collisionContext, be) : Shapes.empty();
		}
		else
			return Shapes.empty(); //shouldn't happen
	}

	private VoxelShape getCorrectShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx, DisguisableBlockEntity disguisableTe) {
		if (disguisableTe.isModuleEnabled(ModuleType.DISGUISE)) {
			ItemStack moduleStack = disguisableTe.getModule(ModuleType.DISGUISE);

			if (!moduleStack.isEmpty() && ModuleItem.getBlockAddon(moduleStack) != null)
				return super.getCollisionShape(state, level, pos, ctx);
		}

		return Shapes.block();
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!level.isClientSide) {
			CageTrapBlockEntity cageTrap = (CageTrapBlockEntity) level.getBlockEntity(pos);

			if (cageTrap.isDisabled())
				return;

			boolean isPlayer = entity instanceof Player;

			if (isPlayer || (entity instanceof Mob && cageTrap.capturesMobs())) {
				if (!getShape(state, level, pos, CollisionContext.of(entity)).bounds().move(pos).intersects(entity.getBoundingBox()))
					return;

				if ((isPlayer && cageTrap.isOwnedBy(entity)) && cageTrap.ignoresOwner() || entity instanceof OwnableEntity ownableEntity && cageTrap.allowsOwnableEntity(ownableEntity))
					return;

				if (state.getValue(DEACTIVATED))
					return;

				BlockPos topMiddle = pos.above(4);
				String ownerName = cageTrap.getOwner().getName();

				BlockModifier placer = new BlockModifier(level, new BlockPos.MutableBlockPos().set(pos), cageTrap.getOwner());

				placer.loop((w, p, o) -> {
					if (w.isEmptyBlock(p)) {
						if (p.equals(topMiddle))
							w.setBlockAndUpdate(p, SCContent.HORIZONTAL_REINFORCED_IRON_BARS.get().defaultBlockState());
						else
							w.setBlockAndUpdate(p, ((ReinforcedPaneBlock) SCContent.REINFORCED_IRON_BARS.get()).getStateForPlacement(w, p));
					}
				});
				placer.loop((w, p, o) -> {
					BlockEntity te = w.getBlockEntity(p);

					if (te instanceof IOwnable ownable)
						ownable.setOwner(o.getUUID(), o.getName());

					if (te instanceof ReinforcedIronBarsBlockEntity ironBarsTe)
						ironBarsTe.setCanDrop(false);
				});
				level.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, true));
				level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 3.0F, 1.0F);
				level.gameEvent(null, GameEvent.BLOCK_PLACE, pos);

				if (isPlayer && PlayerUtils.isPlayerOnline(ownerName))
					PlayerUtils.sendMessageToPlayer(ownerName, Utils.localize(SCContent.CAGE_TRAP.get().getDescriptionId()), Utils.localize("messages.securitycraft:cageTrap.captured", ((Player) entity).getName(), Utils.getFormattedCoordinates(pos)), ChatFormatting.BLACK);
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(hand);

		if (stack.getItem() == SCContent.WIRE_CUTTERS.get()) {
			if (!state.getValue(DEACTIVATED)) {
				level.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, true));

				if (!player.isCreative())
					stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

				level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
				return InteractionResult.SUCCESS;
			}
		}
		else if (stack.getItem() == Items.REDSTONE && state.getValue(DEACTIVATED)) {
			level.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, false));

			if (!player.isCreative())
				stack.shrink(1);

			level.playSound(null, pos, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return super.getStateForPlacement(ctx).setValue(DEACTIVATED, false);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(DEACTIVATED, WATERLOGGED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CageTrapBlockEntity(pos, state);
	}

	public static class BlockModifier {
		private Level level;
		private BlockPos.MutableBlockPos pos;
		private BlockPos origin;
		private Owner owner;

		public BlockModifier(Level level, BlockPos.MutableBlockPos origin, Owner owner) {
			this.level = level;
			pos = origin.move(-1, 1, -1);
			this.origin = origin.immutable();
			this.owner = owner;
		}

		public void loop(TriConsumer<Level, BlockPos.MutableBlockPos, Owner> ifTrue) {
			for (int y = 0; y < 4; y++) {
				for (int x = 0; x < 3; x++) {
					for (int z = 0; z < 3; z++) {
						//skip the middle column above the cage trap, but not the place where the horizontal iron bars are
						if (!(x == 1 && z == 1 && y != 3))
							ifTrue.accept(level, pos, owner);

						pos.move(0, 0, 1);
					}

					pos.move(1, 0, -3);
				}

				pos.move(-3, 1, 0);
			}

			pos.set(origin); //reset the mutable block pos for the next usage
		}
	}
}
