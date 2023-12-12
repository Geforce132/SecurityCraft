package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.api.INameSetter;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedLadderBlock extends BlockLadder implements IReinforcedBlock {
	public ReinforcedLadderBlock() {
		setSoundType(SoundType.WOOD);
	}

	@Override
	public float getExplosionResistance(Entity exploder) {
		return Float.MAX_VALUE;
	}

	@Override
	public float getExplosionResistance(World level, BlockPos pos, Entity exploder, Explosion explosion) {
		return Float.MAX_VALUE;
	}

	@Override
	public void onBlockPlacedBy(World level, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(level, pos, state, placer, stack);

		if (placer instanceof EntityPlayer)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (EntityPlayer) placer));

		if (!level.isRemote && stack.hasDisplayName()) {
			TileEntity te = level.getTileEntity(pos);

			if (te instanceof INameSetter)
				((INameSetter) te).setCustomName(stack.getDisplayName());
		}
	}

	@Override
	public List<Block> getVanillaBlocks() {
		return Arrays.asList(Blocks.LADDER);
	}

	@Override
	public int getAmount() {
		return 1;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World level, IBlockState state) {
		return new OwnableBlockEntity();
	}
}
