package net.geforcemods.securitycraft.misc;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.items.MineRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.SentryRemoteAccessToolItem;
import net.geforcemods.securitycraft.items.SonicSecuritySystemItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LinkingStateItemPropertyHandler {
	public static final float EMPTY_STATE = 0.0F, UNKNOWN_STATE = 0.25F, NOT_LINKED_STATE = 0.5F, LINKED_STATE = 0.75F;
	public static final ResourceLocation LINKING_STATE_PROPERTY = new ResourceLocation(SecurityCraft.MODID, "linking_state");

	private LinkingStateItemPropertyHandler() {}

	public static float cameraMonitor(ItemStack stack, World world, EntityLivingBase entity) {
		if (!(entity instanceof EntityPlayer))
			return EMPTY_STATE;

		EntityPlayer player = (EntityPlayer) entity;
		float linkingState = getLinkingState(world, player, stack, (_world, pos) -> _world.getTileEntity(pos) instanceof SecurityCameraBlockEntity, 30, (tag, i) -> {
			if (!tag.hasKey("Camera" + i))
				return null;

			String camera = tag.getString("Camera" + i);

			return Arrays.stream(camera.substring(0, camera.lastIndexOf(' ')).split(" ")).map(Integer::parseInt).toArray(Integer[]::new);
		});

		if (!CameraMonitorItem.hasCameraAdded(stack.getTagCompound())) {
			if (linkingState == NOT_LINKED_STATE)
				return NOT_LINKED_STATE;
			else
				return EMPTY_STATE;
		}
		else
			return linkingState;
	}

	public static float mineRemoteAccessTool(ItemStack stack, World world, EntityLivingBase entity) {
		if (!(entity instanceof EntityPlayer))
			return EMPTY_STATE;

		EntityPlayer player = (EntityPlayer) entity;
		float linkingState = getLinkingState(world, player, stack, (_world, pos) -> _world.getBlockState(pos).getBlock() instanceof IExplosive, 30, (tag, i) -> {
			if (tag.getIntArray("mine" + i).length > 0)
				return Arrays.stream(tag.getIntArray("mine" + i)).boxed().toArray(Integer[]::new);
			else
				return null;
		});

		if (!MineRemoteAccessToolItem.hasMineAdded(stack.getTagCompound())) {
			if (linkingState == NOT_LINKED_STATE)
				return NOT_LINKED_STATE;
			else
				return EMPTY_STATE;
		}
		else
			return linkingState;
	}

	public static float sentryRemoteAccessTool(ItemStack stack, World world, EntityLivingBase entity) {
		if (!(entity instanceof EntityPlayer))
			return EMPTY_STATE;

		if (Minecraft.getMinecraft().pointedEntity instanceof Sentry) {
			Sentry sentry = (Sentry) Minecraft.getMinecraft().pointedEntity;
			float linkingState;

			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());

			linkingState = loop(12, (tag, i) -> Arrays.stream(tag.getIntArray("sentry" + i)).boxed().toArray(Integer[]::new), stack.getTagCompound(), sentry.getPosition());

			if (!SentryRemoteAccessToolItem.hasSentryAdded(stack.getTagCompound())) {
				if (linkingState == NOT_LINKED_STATE)
					return NOT_LINKED_STATE;
				else
					return EMPTY_STATE;
			}
			else
				return linkingState;
		}
		else
			return (SentryRemoteAccessToolItem.hasSentryAdded(stack.getTagCompound()) ? UNKNOWN_STATE : EMPTY_STATE);
	}

	public static float sonicSecuritySystem(ItemStack stack, World world, EntityLivingBase entity) {
		if (!(entity instanceof EntityPlayer))
			return EMPTY_STATE;

		EntityPlayer player = (EntityPlayer) entity;
		float linkingState = getLinkingState(world, player, stack, (_world, pos) -> {
			TileEntity tile = _world.getTileEntity(pos);

			if (!(tile instanceof ILockable))
				return false;

			//if the block is not ownable/not owned by the player looking at it, don't show the indicator if it's disguised
			if (!(tile instanceof IOwnable) || !((IOwnable) tile).isOwnedBy(player)) {
				if (IDisguisable.getDisguisedBlockStateUnknown(_world, pos) != null)
					return false;
			}

			return true;
		}, 0, null, false, SonicSecuritySystemItem::isAdded);

		if (!SonicSecuritySystemItem.hasLinkedBlock(stack.getTagCompound())) {
			if (linkingState == NOT_LINKED_STATE)
				return NOT_LINKED_STATE;
			else
				return EMPTY_STATE;
		}
		else
			return linkingState;
	}

	public static float getLinkingState(World level, EntityPlayer player, ItemStack stackInHand, BiPredicate<World, BlockPos> isValidHitResult, int tagSize, BiFunction<NBTTagCompound, Integer, Integer[]> getCoords) {
		return getLinkingState(level, player, stackInHand, isValidHitResult, tagSize, getCoords, true, null);
	}

	public static float getLinkingState(World level, EntityPlayer player, ItemStack stackInHand, BiPredicate<World, BlockPos> isValidHitResult, int tagSize, BiFunction<NBTTagCompound, Integer, Integer[]> getCoords, boolean loop, BiPredicate<NBTTagCompound, BlockPos> useCheckmark) {
		if (level == null)
			level = SecurityCraft.proxy.getClientLevel();

		double reachDistance = Minecraft.getMinecraft().playerController.getBlockReachDistance();
		double eyeHeight = player.getEyeHeight();
		Vec3d lookVec = new Vec3d(player.posX + player.getLookVec().x * reachDistance, eyeHeight + player.posY + player.getLookVec().y * reachDistance, player.posZ + player.getLookVec().z * reachDistance);
		RayTraceResult hitResult = level.rayTraceBlocks(new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ), lookVec);

		if (hitResult != null && hitResult.typeOfHit == Type.BLOCK && isValidHitResult.test(level, hitResult.getBlockPos())) {
			if (!stackInHand.hasTagCompound())
				stackInHand.setTagCompound(new NBTTagCompound());

			if (loop)
				return loop(tagSize, getCoords, stackInHand.getTagCompound(), hitResult.getBlockPos());
			else
				return useCheckmark.test(stackInHand.getTagCompound(), hitResult.getBlockPos()) ? LINKED_STATE : NOT_LINKED_STATE;
		}

		return UNKNOWN_STATE;
	}

	private static float loop(int tagSize, BiFunction<NBTTagCompound, Integer, Integer[]> getCoords, NBTTagCompound tag, BlockPos pos) {
		for (int i = 1; i <= tagSize; i++) {
			Integer[] coords = getCoords.apply(tag, i);

			if (coords != null && coords.length == 3 && coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ())
				return LINKED_STATE;
		}

		return NOT_LINKED_STATE;
	}
}
