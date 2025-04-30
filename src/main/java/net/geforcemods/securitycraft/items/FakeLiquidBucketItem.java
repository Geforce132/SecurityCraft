package net.geforcemods.securitycraft.items;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

public class FakeLiquidBucketItem extends BucketItem {
	public FakeLiquidBucketItem(Supplier<? extends Fluid> supplier, Properties builder) {
		super(supplier, builder);

		DispenserBlock.registerBehavior(this, new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

			@Override
			public ItemStack execute(BlockSource source, ItemStack stack) {
				DispensibleContainerItem bucket = (DispensibleContainerItem) stack.getItem();
				BlockPos dispenseAt = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				Level level = source.getLevel();

				if (bucket.emptyContents(null, level, dispenseAt, null)) {
					bucket.checkExtraContent(null, level, stack, dispenseAt);
					return new ItemStack(Items.BUCKET);
				}
				else
					return defaultDispenseItemBehavior.dispense(source, stack);
			}
		});
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		return new FluidBucketWrapper(stack);
	}
}
