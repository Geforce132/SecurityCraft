package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import org.joml.Vector3f;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class KeycardReaderBlock extends DisguisableBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public KeycardReaderBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide) {
			KeycardReaderBlockEntity be = (KeycardReaderBlockEntity) level.getBlockEntity(pos);

			if (be.isDisabled())
				player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else if (be.isDenied(player)) {
				if (be.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Component.translatable(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);
			}
			else {
				ItemStack stack = player.getItemInHand(hand);
				Item item = stack.getItem();
				boolean isCodebreaker = item == SCContent.CODEBREAKER.get();

				//either no keycard, or an unlinked keycard, or an admin tool
				if ((!(item instanceof KeycardItem) || !stack.hasTag() || !stack.getTag().getBoolean("linked")) && !isCodebreaker) {
					//only allow the owner and players on the allowlist to open the gui
					if (be.isOwnedBy(player) || be.isAllowed(player))
						NetworkHooks.openScreen((ServerPlayer) player, be, pos);
				}
				else if (item != SCContent.LIMITED_USE_KEYCARD.get()) { //limited use keycards are only crafting components now
					if (isCodebreaker) {
						double chance = ConfigHandler.SERVER.codebreakerChance.get();

						if (chance < 0.0D)
							PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.KEYCARD_READER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreakerDisabled"), ChatFormatting.RED);
						else {
							if (!player.isCreative())
								stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

							if (player.isCreative() || new Random().nextDouble() < chance)
								activate(level, pos, be.getSignalLength());
							else
								PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CODEBREAKER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreaker.failed"), ChatFormatting.RED);
						}
					}
					else {
						MutableComponent feedback = insertCard(level, pos, be, stack, player);

						if (feedback != null)
							PlayerUtils.sendMessageToPlayer(player, Component.translatable(getDescriptionId()), feedback, ChatFormatting.RED);
					}
				}
			}
		}

		return InteractionResult.SUCCESS;
	}

	public MutableComponent insertCard(Level level, BlockPos pos, KeycardReaderBlockEntity te, ItemStack stack, Player player) {
		CompoundTag tag = stack.getTag();
		Owner keycardOwner = new Owner(tag.getString("ownerName"), tag.getString("ownerUUID"));

		//owner of this keycard reader and the keycard reader the keycard got linked to do not match
		if ((ConfigHandler.SERVER.enableTeamOwnership.get() && !PlayerUtils.areOnSameTeam(te.getOwner(), keycardOwner)) || !te.getOwner().getUUID().equals(keycardOwner.getUUID()))
			return Component.translatable("messages.securitycraft:keycardReader.differentOwner");

		//the keycard's signature does not match this keycard reader's
		if (te.getSignature() != tag.getInt("signature"))
			return Component.translatable("messages.securitycraft:keycardReader.wrongSignature");

		int keycardLevel = ((KeycardItem) stack.getItem()).getLevel();

		//the keycard's level
		if (!te.getAcceptedLevels()[keycardLevel]) //both are 0 indexed, so it's ok
			return Component.translatable("messages.securitycraft:keycardReader.wrongLevel", keycardLevel + 1); //level is 0-indexed, so it has to be increased by one to match with the item name

		boolean powered = level.getBlockState(pos).getValue(POWERED);

		if (tag.getBoolean("limited")) {
			int uses = tag.getInt("uses");

			if (uses <= 0)
				return Component.translatable("messages.securitycraft:keycardReader.noUses");

			if (!player.isCreative() && !powered)
				tag.putInt("uses", --uses);
		}

		if (!powered)
			activate(level, pos, te.getSignalLength());

		return null;
	}

	public void activate(Level level, BlockPos pos, int signalLength) {
		level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(POWERED, true));
		BlockUtils.updateIndirectNeighbors(level, pos, SCContent.KEYCARD_READER.get());
		level.scheduleTick(pos, SCContent.KEYCARD_READER.get(), signalLength);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		level.setBlockAndUpdate(pos, state.setValue(POWERED, false));
		BlockUtils.updateIndirectNeighbors(level, pos, SCContent.KEYCARD_READER.get());
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
		if ((state.getValue(POWERED))) {
			double x = pos.getX() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.7F + (rand.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (rand.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;
			float r = 0.6F + 0.4F;
			float g = Math.max(0.0F, 0.7F - 0.5F);
			float b = Math.max(0.0F, 0.6F - 0.7F);
			Vector3f vec = new Vector3f(r, g, b);

			level.addParticle(new DustParticleOptions(vec, 1), false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			level.addParticle(new DustParticleOptions(vec, 1), false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			level.addParticle(new DustParticleOptions(vec, 1), false, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			level.addParticle(new DustParticleOptions(vec, 1), false, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			level.addParticle(new DustParticleOptions(vec, 1), false, x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getPlayer().getDirection().getOpposite()).setValue(POWERED, false);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, WATERLOGGED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KeycardReaderBlockEntity(pos, state);
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
