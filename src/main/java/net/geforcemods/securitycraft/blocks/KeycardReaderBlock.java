package net.geforcemods.securitycraft.blocks;

import java.util.Random;
import java.util.function.BiConsumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class KeycardReaderBlock extends DisguisableBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public KeycardReaderBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return use(state, level, pos, player, hand, (stack, be) -> {
			//only allow the owner and players on the allowlist to open the gui
			if (be.isOwnedBy(player) || be.isAllowed(player))
				NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) be, pos);
		});
	}

	public static <BE extends KeycardReaderBlockEntity> ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BiConsumer<ItemStack, BE> noKeycardRightclick) {
		if (!level.isClientSide) {
			BE be = (BE) level.getBlockEntity(pos);

			if (be.isDisabled())
				player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else if (be.isDenied(player)) {
				if (be.sendsDenylistMessage())
					PlayerUtils.sendMessageToPlayer(player, new TranslationTextComponent(state.getBlock().getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
			}
			else {
				ItemStack stack = player.getItemInHand(hand);
				Item item = stack.getItem();
				boolean isCodebreaker = item == SCContent.CODEBREAKER.get();
				boolean isKeycardHolder = item == SCContent.KEYCARD_HOLDER.get();

				//either no keycard, or an unlinked keycard, or an admin tool
				if (!isKeycardHolder && (!(item instanceof KeycardItem) || !stack.hasTag() || !stack.getTag().getBoolean("linked")) && !isCodebreaker)
					noKeycardRightclick.accept(stack, be);
				else if (item != SCContent.LIMITED_USE_KEYCARD.get()) //limited use keycards are only crafting components now
					return be.onRightClickWithActionItem(stack, hand, player, isCodebreaker, isKeycardHolder);
			}
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public void tick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
		if (state.getValue(POWERED)) {
			level.setBlockAndUpdate(pos, state.setValue(POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, pos, SCContent.KEYCARD_READER.get());
		}
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock()) && state.getValue(POWERED)) {
			level.updateNeighborsAt(pos, this);
			BlockUtils.updateIndirectNeighbors(level, pos, this);
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World level, BlockPos pos, Random rand) {
		if ((state.getValue(POWERED))) {
			double x = pos.getX() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.7F + (rand.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;
			float r = 0.6F + 0.4F;
			float g = Math.max(0.0F, 0.7F - 0.5F);
			float b = Math.max(0.0F, 0.6F - 0.7F);

			level.addParticle(new RedstoneParticleData(r, g, b, 1), false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			level.addParticle(new RedstoneParticleData(r, g, b, 1), false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			level.addParticle(new RedstoneParticleData(r, g, b, 1), false, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			level.addParticle(new RedstoneParticleData(r, g, b, 1), false, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			level.addParticle(new RedstoneParticleData(r, g, b, 1), false, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		if ((state.getValue(POWERED)))
			return 15;
		else
			return 0;
	}

	@Override
	public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(POWERED, false);
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, WATERLOGGED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new KeycardReaderBlockEntity();
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
