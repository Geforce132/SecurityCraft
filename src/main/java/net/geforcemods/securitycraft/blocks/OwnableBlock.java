package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class OwnableBlock extends Block {
	private static float destroyTimeTempStorage = -1.0F;
	protected final float destroyTimeForOwner;

	public OwnableBlock(AbstractBlock.Properties properties) {
		super(withReinforcedDestroyTime(properties));
		destroyTimeForOwner = getStoredDestroyTime();
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, destroyTimeForOwner, state, player, level, pos);
	}

	public float defaultDestroyProgress(BlockState state, PlayerEntity player, IBlockReader level, BlockPos pos) {
		return super.getDestroyProgress(state, player, level, pos);
	}

	@Override
	public boolean canHarvestBlock(BlockState state, IBlockReader level, BlockPos pos, PlayerEntity player) {
		return ConfigHandler.SERVER.alwaysDrop.get() || super.canHarvestBlock(state, level, pos, player);
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) placer));

		if (stack.hasCustomHoverName()) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof INameSetter)
				((INameSetter) te).setCustomName(stack.getHoverName());
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new OwnableBlockEntity();
	}

	public static AbstractBlock.Properties withReinforcedDestroyTime(AbstractBlock.Properties properties) {
		destroyTimeTempStorage = properties.destroyTime;
		properties.destroyTime = -1.0F;
		return properties;
	}

	public static float getStoredDestroyTime() {
		float storedDestroyTime = destroyTimeTempStorage;

		destroyTimeTempStorage = -1.0F;
		return storedDestroyTime;
	}
}
