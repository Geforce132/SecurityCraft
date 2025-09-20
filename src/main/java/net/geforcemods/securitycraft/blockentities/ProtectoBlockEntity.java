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
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class ProtectoBlockEntity extends DisguisableBlockEntity implements ITickingBlockEntity {
	private static final int ATTACK_RANGE = 10;
	private static final int SLOW_SPEED = 200;
	private static final int FAST_SPEED = 100;
	private int cooldown = 0;
	private int ticksBetweenAttacks = isModuleEnabled(ModuleType.SPEED) ? FAST_SPEED : SLOW_SPEED;
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();

	public ProtectoBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.PROTECTO_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (level.isRaining() && level.canSeeSkyFromBelowWater(pos)) {
			if (isDisabled() || cooldown++ < ticksBetweenAttacks)
				return;

			List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(ATTACK_RANGE));

			if (!state.getValue(ProtectoBlock.ACTIVATED))
				level.setBlockAndUpdate(pos, state.setValue(ProtectoBlock.ACTIVATED, true));

			if (!entities.isEmpty()) {
				boolean shouldDeactivate = false;

				for (LivingEntity entity : entities) {
					if (!(entity instanceof Sentry || entity instanceof ArmorStand) && !respectInvisibility.isConsideredInvisible(entity)) {
						if (entity instanceof Player player && (player.isCreative() || !player.canBeSeenByAnyone() || (isOwnedBy(player) && ignoresOwner()) || isAllowed(entity)) || entity instanceof OwnableEntity ownableEntity && allowsOwnableEntity(ownableEntity))
							continue;

						if (!level.isClientSide())
							level.addFreshEntity(LevelUtils.createLightning(level, entity.position(), false));

						shouldDeactivate = true;
					}
				}

				if (shouldDeactivate) {
					level.setBlockAndUpdate(pos, state.setValue(ProtectoBlock.ACTIVATED, false));
					cooldown = 0;
				}
			}
		}
		else if (state.getValue(ProtectoBlock.ACTIVATED))
			level.setBlockAndUpdate(pos, state.setValue(ProtectoBlock.ACTIVATED, false));
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
