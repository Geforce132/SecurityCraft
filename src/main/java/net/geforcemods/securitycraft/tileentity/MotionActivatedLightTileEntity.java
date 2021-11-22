package net.geforcemods.securitycraft.tileentity;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.blocks.MotionActivatedLightBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class MotionActivatedLightTileEntity extends CustomizableTileEntity implements ITickableTileEntity {
	private static final int TICKS_BETWEEN_ATTACKS = 5;
	private DoubleOption searchRadiusOption = new DoubleOption(this::getPos, "searchRadius", 5.0D, 5.0D, 20.0D, 1.0D, true);
	private int cooldown = TICKS_BETWEEN_ATTACKS;

	public MotionActivatedLightTileEntity()
	{
		super(SCContent.teTypeMotionLight);
	}

	@Override
	public void tick() {
		if(cooldown-- > 0)
			return;

		List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos).grow(searchRadiusOption.get()), e -> !PlayerUtils.isPlayerMountedOnCamera(e) && !EntityUtils.isInvisible(e));
		boolean shouldBeOn = !entities.isEmpty();

		if(getBlockState().get(MotionActivatedLightBlock.LIT) != shouldBeOn)
			world.setBlockState(pos, getBlockState().with(MotionActivatedLightBlock.LIT, shouldBeOn));

		cooldown = TICKS_BETWEEN_ATTACKS;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option<?>[] {searchRadiusOption};
	}
}
