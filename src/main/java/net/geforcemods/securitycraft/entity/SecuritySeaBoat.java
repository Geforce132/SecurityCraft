package net.geforcemods.securitycraft.entity;

import java.util.function.Supplier;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class SecuritySeaBoat extends AbstractSecuritySeaBoat {
	public SecuritySeaBoat(EntityType<? extends SecuritySeaBoat> type, Level level, Supplier<Item> dropItem) {
		super(type, level, dropItem);
	}

	@Override
	protected double rideHeight(EntityDimensions dimensions) {
		return dimensions.height() / 3.0F;
	}
}
