package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.TargetingModeOption;
import net.geforcemods.securitycraft.blocks.FloorTrapBlock;
import net.geforcemods.securitycraft.blocks.SometimesVisibleBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.TargetingMode;
import net.geforcemods.securitycraft.particle.FloorTrapCloudParticle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;

public class FloorTrapBlockEntity extends DisguisableBlockEntity implements ITickable {
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private BooleanOption disappearInstantlyInChains = new BooleanOption("disappearInstantlyInChains", true);
	private IntOption disappearDelay = new IntOption(this::getPos, "disappearDelay", 5, 0, 200, 1);
	private IntOption reappearDelay = new IntOption(this::getPos, "reappearDelay", 20, 5, 200, 1);
	private TargetingModeOption targetingMode = new TargetingModeOption(TargetingMode.PLAYERS);
	private boolean shouldDisappear = false, shouldReappear = false;
	private int ticksUntilDisappearing = -1, ticksUntilReappearing = -1;

	@Override
	public void update() {
		if (world.isRemote) {
			IBlockState state = world.getBlockState(pos);

			if (state.getValue(FloorTrapBlock.INVISIBLE))
				SecurityCraft.proxy.addEffect(FloorTrapCloudParticle::createParticle, world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

			return;
		}

		if (!shouldReappear && shouldDisappear) {
			if (ticksUntilDisappearing-- <= 0)
				disappear();

			return;
		}

		TargetingMode mode = targetingMode.get();
		AxisAlignedBB area = new AxisAlignedBB(pos.getX(), pos.getY() + 1.0D, pos.getZ(), pos.getX() + 1.0D, pos.getY() + 1.1666D, pos.getZ() + 1.0D);

		//@formatter:off
		shouldDisappear = world.getEntitiesWithinAABB(EntityLivingBase.class, area, e -> !(e instanceof EntityPlayer && ((EntityPlayer) e).isSpectator()) && !allowsOwnableEntity(e) && !isAllowed(e) && !(isOwnedBy(e) && ignoresOwner()))
			.stream()
			.anyMatch(entity -> mode.allowsPlayers() && entity instanceof EntityPlayer || mode.allowsMobs());
		//@formatter:on

		if (shouldReappear && ticksUntilReappearing-- <= 0 && !shouldDisappear)
			reappear();

		scheduleDisappear(false);
	}

	public void scheduleDisappear(boolean force) {
		scheduleDisappear(disappearDelay.get(), force);
	}

	public void scheduleDisappear(int delay, boolean force) {
		if (force)
			shouldDisappear = true;

		if (shouldDisappear)
			ticksUntilDisappearing = delay;
	}

	public void disappear() {
		IBlockState state = world.getBlockState(pos);

		world.setBlockState(pos, state.withProperty(SometimesVisibleBlock.INVISIBLE, true));
		world.playSound(null, pos, SoundEvents.ENTITY_ENDERDRAGON_FLAP, SoundCategory.BLOCKS, 1.0F, 2.0F);
		shouldDisappear = false;
		scheduleReappear();
	}

	public void scheduleReappear() {
		scheduleReappear(reappearDelay.get());
	}

	public void scheduleReappear(int delay) {
		shouldReappear = true;
		ticksUntilReappearing = delay;
	}

	public void reappear() {
		IBlockState state = world.getBlockState(pos);

		world.setBlockState(pos, state.withProperty(SometimesVisibleBlock.INVISIBLE, false));
		shouldReappear = false;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setBoolean("should_disappear", shouldDisappear);
		tag.setBoolean("should_reappear", shouldReappear);
		tag.setInteger("ticks_until_disappearing", ticksUntilDisappearing);
		tag.setInteger("ticks_until_reappearing", ticksUntilReappearing);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		shouldDisappear = tag.getBoolean("should_disappear");
		shouldReappear = tag.getBoolean("should_reappear");
		ticksUntilDisappearing = tag.getInteger("ticks_until_disappearing");
		ticksUntilReappearing = tag.getInteger("ticks_until_reappearing");
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.DISGUISE, ModuleType.ALLOWLIST, ModuleType.SMART
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				ignoreOwner, targetingMode, disappearInstantlyInChains, disappearDelay, reappearDelay
		};
	}

	@Override
	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	public boolean shouldDisappearInstantlyInChains() {
		return disappearInstantlyInChains.get();
	}
}
