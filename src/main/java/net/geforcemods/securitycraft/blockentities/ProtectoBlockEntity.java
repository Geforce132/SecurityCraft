package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.RespectInvisibilityOption;
import net.geforcemods.securitycraft.blocks.ProtectoBlock;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class ProtectoBlockEntity extends DisguisableBlockEntity implements ITickableTileEntity {
	private static final int ATTACK_RANGE = 10;
	private static final int SLOW_SPEED = 200;
	private static final int FAST_SPEED = 100;
	private int cooldown = 0;
	private int ticksBetweenAttacks = isModuleEnabled(ModuleType.SPEED) ? FAST_SPEED : SLOW_SPEED;
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();

	public ProtectoBlockEntity() {
		super(SCContent.PROTECTO_BLOCK_ENTITY.get());
	}

	@Override
	public void tick() {
		if (level.isRaining() && level.canSeeSkyFromBelowWater(worldPosition)) {
			if (isDisabled() || cooldown++ < ticksBetweenAttacks)
				return;

			List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(worldPosition).inflate(ATTACK_RANGE));

			if (!getBlockState().getValue(ProtectoBlock.ACTIVATED))
				level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ProtectoBlock.ACTIVATED, true));

			if (!entities.isEmpty()) {
				boolean shouldDeactivate = false;

				for (LivingEntity entity : entities) {
					if (!(entity instanceof Sentry || entity instanceof ArmorStandEntity) && !respectInvisibility.isConsideredInvisible(entity)) {
						if (entity instanceof PlayerEntity) {
							PlayerEntity player = (PlayerEntity) entity;

							if (player.isCreative() || player.isSpectator() || (isOwnedBy(player) && ignoresOwner()) || isAllowed(entity) || allowsOwnableEntity(entity))
								continue;
						}

						if (!level.isClientSide)
							LevelUtils.spawnLightning(level, entity.position(), false);

						shouldDeactivate = true;
					}
				}

				if (shouldDeactivate) {
					level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ProtectoBlock.ACTIVATED, false));
					cooldown = 0;
				}
			}
		}
		else if (getBlockState().getValue(ProtectoBlock.ACTIVATED))
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(ProtectoBlock.ACTIVATED, false));
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (module == ModuleType.SPEED)
			ticksBetweenAttacks = FAST_SPEED;
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.SPEED)
			ticksBetweenAttacks = SLOW_SPEED;
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.SPEED, ModuleType.DISGUISE
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				disabled, ignoreOwner, respectInvisibility
		};
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}
}
