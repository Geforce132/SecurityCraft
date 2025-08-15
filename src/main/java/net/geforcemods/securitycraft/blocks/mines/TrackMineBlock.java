package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.blockentities.TrackMineBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TrackMineBlock extends RailBlock implements IExplosive {
	private final float destroyTimeForOwner;

	public TrackMineBlock(AbstractBlock.Properties properties) {
		super(OwnableBlock.withReinforcedDestroyTime(properties));
		destroyTimeForOwner = OwnableBlock.getStoredDestroyTime();
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (player.getItemInHand(hand).getItem() == SCContent.MINE_REMOTE_ACCESS_TOOL.get())
			return ActionResultType.SUCCESS;

		if (isActive(level, pos) && isDefusable() && player.getItemInHand(hand).getItem() == SCContent.WIRE_CUTTERS.get() && defuseMine(level, pos)) {
			if (!player.isCreative())
				player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

			level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
			return ActionResultType.SUCCESS;
		}

		if (!isActive(level, pos) && player.getItemInHand(hand).getItem() == Items.FLINT_AND_STEEL && activateMine(level, pos)) {
			if (!player.isCreative())
				player.getItemInHand(hand).hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

			level.playSound(null, pos, SoundEvents.TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 1.0F, 1.0F);
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, destroyTimeForOwner, state, player, level, pos);
	}

	@Override
	public boolean canHarvestBlock(BlockState state, IBlockReader level, BlockPos pos, PlayerEntity player) {
		return ConfigHandler.SERVER.alwaysDrop.get() || super.canHarvestBlock(state, level, pos, player);
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) placer));
	}

	@Override
	public void onMinecartPass(BlockState state, World level, BlockPos pos, AbstractMinecartEntity cart) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof TrackMineBlockEntity && ((TrackMineBlockEntity) te).isActive()) {
			level.destroyBlock(pos, false);
			level.explode(cart, pos.getX(), pos.getY() + 1, pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 3.0F : 6.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
			cart.remove();
		}
	}

	@Override
	public void explode(World level, BlockPos pos) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof TrackMineBlockEntity && ((TrackMineBlockEntity) te).isActive()) {
			level.destroyBlock(pos, false);
			level.explode((Entity) null, pos.getX(), pos.above().getY(), pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 3.0F : 6.0F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
		}
	}

	@Override
	public boolean activateMine(World level, BlockPos pos) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof TrackMineBlockEntity && !((TrackMineBlockEntity) te).isActive()) {
			((TrackMineBlockEntity) te).activate();
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean defuseMine(World level, BlockPos pos) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof TrackMineBlockEntity && ((TrackMineBlockEntity) te).isActive()) {
			((TrackMineBlockEntity) te).deactivate();
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean isActive(World level, BlockPos pos) {
		TileEntity te = level.getBlockEntity(pos);

		return te instanceof TrackMineBlockEntity && ((TrackMineBlockEntity) te).isActive();
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new TrackMineBlockEntity();
	}
}
