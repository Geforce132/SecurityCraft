package net.geforcemods.securitycraft.items.properties;

import com.mojang.serialization.Codec;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICodebreakable;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs.LateBoundIdMapper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface HitCheck {
	public static final LateBoundIdMapper<ResourceLocation, HitCheck> ID_MAPPER = new LateBoundIdMapper<>();
	public static final Codec<HitCheck> CODEC = ID_MAPPER.codec(ResourceLocation.CODEC);

	public static void bootstrap() {
		ID_MAPPER.put(SecurityCraft.resLoc("security_camera"), (level, hitResult) -> level.getBlockEntity(hitResult.getBlockPos()) instanceof SecurityCameraBlockEntity);
		ID_MAPPER.put(SecurityCraft.resLoc("explosive_block"), (level, hitResult) -> level.getBlockState(hitResult.getBlockPos()).getBlock() instanceof IExplosive);
		ID_MAPPER.put(SecurityCraft.resLoc("lockable"), (level, hitResult) -> level.getBlockEntity(hitResult.getBlockPos()) instanceof ILockable);
		ID_MAPPER.put(SecurityCraft.resLoc("codebreakable"), (level, hitResult) -> level.getBlockEntity(hitResult.getBlockPos()) instanceof ICodebreakable);
	}

	public static BlockHitResult getHitResult(Level level, Player player) {
		double reachDistance = player.blockInteractionRange();
		double eyeHeight = player.getEyeHeight();
		Vec3 lookVec = new Vec3(player.getX() + player.getLookAngle().x * reachDistance, eyeHeight + player.getY() + player.getLookAngle().y * reachDistance, player.getZ() + player.getLookAngle().z * reachDistance);
		BlockHitResult hitResult = level.clip(new ClipContext(new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ()), lookVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

		return hitResult.getType() == HitResult.Type.BLOCK ? hitResult : null;
	}

	boolean isValidHitResult(Level level, BlockHitResult hitResult);
}
