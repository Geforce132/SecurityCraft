package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.blockentities.IronFenceBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ElectrifiedIronFenceGateBlock extends OwnableFenceGateBlock implements ITileEntityProvider {
	public ElectrifiedIronFenceGateBlock() {
		super(BlockPlanks.EnumType.OAK, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_DOOR_CLOSE);
		ObfuscationReflectionHelper.setPrivateValue(Block.class, this, Material.IRON, 18);
		setSoundType(SoundType.METAL);
		setHarvestLevel("pickaxe", 1);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (state.getValue(OPEN))
			return;

		ElectrifiedIronFenceBlock.hurtOrConvertEntity(world, pos, state, entity);
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
		TileEntity te = world.getTileEntity(pos);

		return te != null && te.receiveClientEvent(id, param);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new IronFenceBlockEntity();
	}
}
