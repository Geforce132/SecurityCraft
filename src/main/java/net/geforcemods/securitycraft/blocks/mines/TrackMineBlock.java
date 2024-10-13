package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blockentities.TrackMineBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;

public class TrackMineBlock extends RailBlock implements IExplosive, EntityBlock {
	public TrackMineBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (heldItem.is(SCContent.MINE_REMOTE_ACCESS_TOOL.get()))
			return InteractionResult.SUCCESS;

		if (heldItem.getItem() == SCContent.WIRE_CUTTERS.get() && isActive(level, pos) && isDefusable() && defuseMine(level, pos)) {
			if (!player.isCreative())
				player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

			level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
			return InteractionResult.SUCCESS;
		}

		if (heldItem.is(Items.FLINT_AND_STEEL) && !isActive(level, pos) && activateMine(level, pos)) {
			if (!player.isCreative())
				player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

			level.playSound(null, pos, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return !ConfigHandler.SERVER.ableToBreakMines.get() ? -1F : super.getDestroyProgress(state, player, level, pos);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			NeoForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));
	}

	@Override
	public void onMinecartPass(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
		if (level.getBlockEntity(pos) instanceof TrackMineBlockEntity be && be.isActive()) {
			level.destroyBlock(pos, false);
			level.explode(cart, pos.getX(), pos.getY() + 1, pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 3.0F : 6.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionInteraction());

			if (level instanceof ServerLevel serverLevel)
				cart.kill(serverLevel);
		}
	}

	@Override
	public void explode(Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof TrackMineBlockEntity be && be.isActive()) {
			level.destroyBlock(pos, false);
			level.explode(null, pos.getX(), pos.above().getY(), pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 3.0F : 6.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionInteraction());
		}
	}

	@Override
	public boolean activateMine(Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof TrackMineBlockEntity be && !be.isActive()) {
			be.activate();
			level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean defuseMine(Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof TrackMineBlockEntity be && be.isActive()) {
			be.deactivate();
			level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean isActive(Level level, BlockPos pos) {
		return level.getBlockEntity(pos) instanceof TrackMineBlockEntity be && be.isActive();
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TrackMineBlockEntity(pos, state);
	}
}
