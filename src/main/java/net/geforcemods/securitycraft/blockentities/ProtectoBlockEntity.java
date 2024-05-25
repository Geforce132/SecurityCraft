package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.RespectInvisibilityOption;
import net.geforcemods.securitycraft.blocks.ProtectoBlock;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public class ProtectoBlockEntity extends DisguisableBlockEntity implements ITickable {
	private static final int ATTACK_RANGE = 10;
	private static final int SLOW_SPEED = 200;
	private static final int FAST_SPEED = 100;
	private int cooldown = 0;
	private int ticksBetweenAttacks = isModuleEnabled(ModuleType.SPEED) ? FAST_SPEED : SLOW_SPEED;
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();

	@Override
	public void update() {
		if (isDisabled() || cooldown++ < ticksBetweenAttacks)
			return;

		IBlockState state = world.getBlockState(pos);

		if (world.isRaining() && world.canBlockSeeSky(pos)) {
			List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos).grow(ATTACK_RANGE));

			if (!state.getValue(ProtectoBlock.ACTIVATED))
				world.setBlockState(pos, state.withProperty(ProtectoBlock.ACTIVATED, true));

			if (!entities.isEmpty()) {
				boolean shouldDeactivate = false;

				for (EntityLivingBase entity : entities) {
					if (!(entity instanceof Sentry) && !respectInvisibility.isConsideredInvisible(entity)) {
						if (entity instanceof EntityPlayer) {
							EntityPlayer player = (EntityPlayer) entity;

							if (player.isCreative() || player.isSpectator() || (isOwnedBy(player) && ignoresOwner()) || isAllowed(entity) || allowsOwnableEntity(entity))
								continue;
						}

						world.addWeatherEffect(new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ, false));
						shouldDeactivate = true;
					}
				}

				if (shouldDeactivate)
					world.setBlockState(pos, state.withProperty(ProtectoBlock.ACTIVATED, false));
			}

			cooldown = 0;
		}
		else if (state.getValue(ProtectoBlock.ACTIVATED)) {
			world.setBlockState(pos, state.withProperty(ProtectoBlock.ACTIVATED, false));
		}
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
