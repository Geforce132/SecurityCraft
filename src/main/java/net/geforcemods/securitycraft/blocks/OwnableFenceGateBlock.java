package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class OwnableFenceGateBlock extends FenceGateBlock {
	protected final SoundEvent openSound;
	protected final SoundEvent closeSound;

	public OwnableFenceGateBlock(AbstractBlock.Properties properties, SoundEvent openSound, SoundEvent closeSound) {
		super(properties);
		this.openSound = openSound;
		this.closeSound = closeSound;
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
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos fromPos, boolean flag) {
		if (!level.isClientSide) {
			boolean isPoweredSCBlock = BlockUtils.hasActiveSCBlockNextTo(level, pos);

			if (isPoweredSCBlock || block.defaultBlockState().isSignalSource()) {
				if (isPoweredSCBlock && !state.getValue(OPEN) && !state.getValue(POWERED)) {
					level.setBlock(pos, state.setValue(OPEN, true).setValue(POWERED, true), 2);
					level.playSound(null, pos, openSound, SoundCategory.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
				}
				else if (!isPoweredSCBlock && state.getValue(OPEN) && state.getValue(POWERED)) {
					level.setBlock(pos, state.setValue(OPEN, false).setValue(POWERED, false), 2);
					level.playSound(null, pos, closeSound, SoundCategory.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
				}
				else if (isPoweredSCBlock != state.getValue(POWERED))
					level.setBlock(pos, state.setValue(POWERED, isPoweredSCBlock), 2);
			}
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new OwnableBlockEntity();
	}
}
