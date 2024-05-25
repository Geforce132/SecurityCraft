package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.RespectInvisibilityOption;
import net.geforcemods.securitycraft.blocks.MotionActivatedLightBlock;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public class MotionActivatedLightBlockEntity extends CustomizableBlockEntity implements ITickable {
	private static final int TICKS_BETWEEN_ATTACKS = 5;
	private DoubleOption searchRadiusOption = new DoubleOption(this::getPos, "searchRadius", 5.0D, 5.0D, 20.0D, 1.0D);
	private DisabledOption disabled = new DisabledOption(false);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
	private int cooldown = TICKS_BETWEEN_ATTACKS;

	@Override
	public void update() {
		if (world.isRemote || isDisabled() || cooldown-- > 0)
			return;

		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(searchRadiusOption.get()), e -> !respectInvisibility.isConsideredInvisible(e) && (!(e instanceof EntityPlayer) || !((EntityPlayer) e).isSpectator()) && !(e instanceof Sentry || e instanceof EntityArmorStand));
		IBlockState state = world.getBlockState(pos);
		boolean shouldBeOn = !entities.isEmpty();

		if (state.getValue(MotionActivatedLightBlock.LIT) != shouldBeOn)
			world.setBlockState(pos, state.withProperty(MotionActivatedLightBlock.LIT, shouldBeOn));

		cooldown = TICKS_BETWEEN_ATTACKS;
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		//turn off the light when it's disabled
		if (option == disabled && ((BooleanOption) option).get()) {
			IBlockState state = world.getBlockState(pos);

			if (state.getValue(MotionActivatedLightBlock.LIT))
				world.setBlockState(pos, state.withProperty(MotionActivatedLightBlock.LIT, false));
		}
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option<?>[] {
				searchRadiusOption, disabled, respectInvisibility
		};
	}

	public boolean isDisabled() {
		return disabled.get();
	}
}
