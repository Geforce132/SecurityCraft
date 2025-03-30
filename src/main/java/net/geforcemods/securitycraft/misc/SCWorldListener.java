package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.CameraNightVisionEffectInstance;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

public class SCWorldListener implements IWorldEventListener {
	@Override
	public void notifyBlockUpdate(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
		//chunky code because of readability
		if (oldState.getBlock() == Blocks.DIRT && newState.getBlock() == Blocks.GRASS && (world.getBlockState(pos.up()).getBlock() == SCContent.bogusWaterFlowing || world.getBlockState(pos.up()).getBlock() == SCContent.fakeWater))
			world.setBlockState(pos, oldState);
		else if (oldState.getBlock() == SCContent.fakeLava && newState.getBlock() == Blocks.LAVA)
			world.setBlockState(pos, oldState);
		else if (oldState.getBlock() == SCContent.bogusLavaFlowing && newState.getBlock() == Blocks.FLOWING_LAVA)
			world.setBlockState(pos, oldState);
		else if (oldState.getBlock() == SCContent.fakeWater && newState.getBlock() == Blocks.WATER)
			world.setBlockState(pos, oldState);
		else if (oldState.getBlock() == SCContent.bogusWaterFlowing && newState.getBlock() == Blocks.FLOWING_WATER)
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
	public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {}

	@Override
	public void spawnParticle(int id, boolean ignoreRange, boolean minimiseParticleLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {}

	@Override
	public void onEntityAdded(Entity entity) {}

	@Override
	public void onEntityRemoved(Entity entity) {
		if (entity instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) entity;
			World level = player.world;

			if (player.getSpectatingEntity() instanceof SecurityCamera) {
				SecurityCamera cam = (SecurityCamera) player.getSpectatingEntity();
				TileEntity tile = level.getTileEntity(new BlockPos(cam.posX, cam.posY, cam.posZ));

				if (player.getActivePotionEffect(MobEffects.NIGHT_VISION) instanceof CameraNightVisionEffectInstance)
					player.removePotionEffect(MobEffects.NIGHT_VISION);

				if (tile instanceof SecurityCameraBlockEntity)
					((SecurityCameraBlockEntity) tile).stopViewing();

				cam.setDead();
			}

			for (SecurityCameraBlockEntity viewedCamera : BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getTileEntitiesWithCondition(level, be -> be.getCameraFeedChunks(player) != null || be.hasPlayerFrameLink(player))) {
				viewedCamera.unlinkFrameForPlayer(player.getUniqueID(), null);
				viewedCamera.clearCameraFeedChunks(player);
			}
		}
	}

	@Override
	public void broadcastSound(int soundID, BlockPos pos, int data) {}

	@Override
	public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {}

	@Override
	public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {}
}