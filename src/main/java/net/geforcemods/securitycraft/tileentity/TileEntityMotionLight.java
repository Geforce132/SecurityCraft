package net.geforcemods.securitycraft.tileentity;

import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.geforcemods.securitycraft.blocks.BlockMotionActivatedLight;
import net.geforcemods.securitycraft.entity.ai.EntitySentry;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityMotionLight extends CustomizableSCTE implements ITickable {
	private static final int TICKS_BETWEEN_ATTACKS = 5;
	private OptionDouble searchRadiusOption = new OptionDouble(this::getPos, "searchRadius", 5.0D, 5.0D, 20.0D, 1.0D, true);
	private DisabledOption disabled = new DisabledOption(false);
	private int cooldown = TICKS_BETWEEN_ATTACKS;

	@Override
	public void update() {
		if (isDisabled() || cooldown-- > 0)
			return;

		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(searchRadiusOption.get()), e -> !EntityUtils.isInvisible(e) && (!(e instanceof EntityPlayer) || !((EntityPlayer) e).isSpectator()) && !(e instanceof EntitySentry || e instanceof EntityArmorStand));
		IBlockState state = world.getBlockState(pos);
		boolean shouldBeOn = !entities.isEmpty();

		if (state.getValue(BlockMotionActivatedLight.LIT) != shouldBeOn)
			world.setBlockState(pos, state.withProperty(BlockMotionActivatedLight.LIT, shouldBeOn));

		cooldown = TICKS_BETWEEN_ATTACKS;
	}

	@Override
	public void onOptionChanged(Option<?> option) {
		//turn off the light when it's disabled
		if (option.getName().equals("disabled") && ((OptionBoolean) option).get()) {
			IBlockState state = world.getBlockState(pos);

			if (state.getValue(BlockMotionActivatedLight.LIT))
				world.setBlockState(pos, state.withProperty(BlockMotionActivatedLight.LIT, false));
		}
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[] {};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option<?>[] {
				searchRadiusOption, disabled
		};
	}

	public boolean isDisabled() {
		return disabled.get();
	}
}
