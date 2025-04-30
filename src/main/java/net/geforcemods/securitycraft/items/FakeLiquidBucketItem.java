package net.geforcemods.securitycraft.items;

import java.util.function.Supplier;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

public class FakeLiquidBucketItem extends BucketItem {
	public FakeLiquidBucketItem(Supplier<? extends Fluid> supplier, Properties builder) {
		super(supplier, builder);

		DispenserBlock.registerBehavior(this, new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

			@Override
			public ItemStack execute(IBlockSource source, ItemStack stack) {
				BucketItem bucket = (BucketItem) stack.getItem();
				BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				World level = source.getLevel();

				if (bucket.emptyBucket(null, level, blockpos, null)) {
					bucket.checkExtraContent(level, stack, blockpos);
					return new ItemStack(Items.BUCKET);
				}
				else
					return defaultDispenseItemBehavior.dispense(source, stack);
			}
		});
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		return new FluidBucketWrapper(stack);
	}
}
