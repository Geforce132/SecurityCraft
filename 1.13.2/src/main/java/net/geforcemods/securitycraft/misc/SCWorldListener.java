package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldEventListener;

public class SCWorldListener implements IWorldEventListener
{
	@Override
	public void notifyBlockUpdate(IBlockReader world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
	{
		//chunky code because of readability
		if(oldState.getBlock() == Blocks.DIRT && newState.getBlock() == Blocks.GRASS_BLOCK && (world.getBlockState(pos.up()).getBlock() == SCContent.bogusWaterFlowing || world.getBlockState(pos.up()).getBlock() == SCContent.bogusWater))
			world.setBlockState(pos, oldState);
		else if(oldState == SCContent.bogusLava && newState.getBlock() == Blocks.LAVA)
			world.setBlockState(pos, oldState);
		else if(oldState == SCContent.bogusLavaFlowing && newState.getBlock() == Blocks.LAVA)
			world.setBlockState(pos, oldState);
		else if(oldState == SCContent.bogusWater && newState.getBlock() == Blocks.WATER)
			world.setBlockState(pos, oldState);
		else if(oldState == SCContent.bogusWaterFlowing && newState.getBlock() == Blocks.WATER)
			world.setBlockState(pos, oldState);
	}

	@Override
	public void notifyLightSet(BlockPos pos) {}

	@Override
	public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {}

	@Override
	public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent sound, SoundCategory category, double x, double y, double z, float volume, float pitch) {}

	@Override
	public void playRecord(SoundEvent sound, BlockPos pos) {}

	@Override
	public void addParticle(IParticleData data, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed) {}

	@Override
	public void addParticle(IParticleData data, boolean ignoreRange, boolean minimiseParticleLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {}

	@Override
	public void onEntityAdded(Entity entity) {}

	@Override
	public void onEntityRemoved(Entity entity) {}

	@Override
	public void broadcastSound(int soundID, BlockPos pos, int data) {}

	@Override
	public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {}

	@Override
	public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {}
}