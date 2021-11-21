package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blockentities.TrackMineBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;

public class TrackMineBlock extends RailBlock implements IExplosive, EntityBlock {

	public TrackMineBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if(PlayerUtils.isHoldingItem(player, SCContent.REMOTE_ACCESS_MINE, hand))
			return InteractionResult.SUCCESS;

		if(isActive(world, pos) && isDefusable() && player.getItemInHand(hand).getItem() == SCContent.WIRE_CUTTERS.get()) {
			if(defuseMine(world, pos))
			{
				if(!player.isCreative())
					player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

				world.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
				return InteractionResult.SUCCESS;
			}
		}

		if(!isActive(world, pos) && PlayerUtils.isHoldingItem(player, Items.FLINT_AND_STEEL, hand)) {
			if(activateMine(world, pos))
			{
				if(!player.isCreative())
					player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

				world.playSound(null, pos, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos)
	{
		return !ConfigHandler.SERVER.ableToBreakMines.get() ? -1F : super.getDestroyProgress(state, player, world, pos);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof Player player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, player));
	}

	@Override
	public void onMinecartPass(BlockState state, Level world, BlockPos pos, AbstractMinecart cart){
		BlockEntity tile = world.getBlockEntity(pos);

		if(tile instanceof TrackMineBlockEntity te && te.isActive())
		{
			world.destroyBlock(pos, false);
			world.explode(cart, pos.getX(), pos.getY() + 1, pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 4.0F : 8.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
			cart.kill();
		}
	}

	@Override
	public void explode(Level world, BlockPos pos) {
		BlockEntity tile = world.getBlockEntity(pos);

		if(tile instanceof TrackMineBlockEntity te && te.isActive())
		{
			world.destroyBlock(pos, false);
			world.explode((Entity) null, pos.getX(), pos.above().getY(), pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 4.0F : 8.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
		}
	}

	@Override
	public boolean activateMine(Level world, BlockPos pos)
	{
		BlockEntity tile = world.getBlockEntity(pos);

		if(tile instanceof TrackMineBlockEntity te && !te.isActive())
		{
			te.activate();
			return true;
		}
		else return false;
	}

	@Override
	public boolean defuseMine(Level world, BlockPos pos)
	{
		BlockEntity tile = world.getBlockEntity(pos);

		if(tile instanceof TrackMineBlockEntity te && te.isActive())
		{
			te.deactivate();
			return true;
		}
		else return false;
	}

	@Override
	public boolean isActive(Level world, BlockPos pos)
	{
		BlockEntity tile = world.getBlockEntity(pos);

		return tile instanceof TrackMineBlockEntity te && te.isActive();
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
