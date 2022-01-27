package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.blocks.ProtectoBlock;
import net.geforcemods.securitycraft.entity.Sentry;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class ProtectoBlockEntity extends DisguisableBlockEntity implements ITickableTileEntity {
	private static final int ATTACK_RANGE = 10;
	private static final int SLOW_SPEED = 200;
	private static final int FAST_SPEED = 100;
	private int cooldown = 0;
	private int ticksBetweenAttacks = hasModule(ModuleType.SPEED) ? FAST_SPEED : SLOW_SPEED;

	public ProtectoBlockEntity() {
		super(SCContent.beTypeProtecto);
	}

	@Override
	public void tick() {
		if (cooldown++ < ticksBetweenAttacks)
			return;

		if (level.isRaining() && level.canSeeSkyFromBelowWater(worldPosition)) {
			List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(worldPosition).inflate(ATTACK_RANGE));

			if (!getBlockState().getValue(ProtectoBlock.ACTIVATED)) {
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ProtectoBlock.ACTIVATED, true));
			}

			if (entities.size() != 0) {
				boolean shouldDeactivate = false;

				for (LivingEntity entity : entities) {
					if (!(entity instanceof Sentry) && !EntityUtils.isInvisible(entity)) {
						if (entity instanceof PlayerEntity) {
							PlayerEntity player = (PlayerEntity) entity;

							if (player.isCreative() || player.isSpectator() || getOwner().isOwner(player) || ModuleUtils.isAllowed(this, entity)) {
								continue;
							}
						}

						if (!level.isClientSide) {
							LevelUtils.spawnLightning(level, entity.position(), false);
						}

						shouldDeactivate = true;
					}
				}

				if (shouldDeactivate) {
					level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ProtectoBlock.ACTIVATED, false));
				}
			}

			cooldown = 0;
		}
		else if (getBlockState().getValue(ProtectoBlock.ACTIVATED)) {
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ProtectoBlock.ACTIVATED, false));
		}
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module) {
		super.onModuleInserted(stack, module);

		if (module == ModuleType.SPEED) {
			ticksBetweenAttacks = FAST_SPEED;
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module) {
		super.onModuleRemoved(stack, module);

		if (module == ModuleType.SPEED) {
			ticksBetweenAttacks = SLOW_SPEED;
		}
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.SPEED, ModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return null;
	}
}
