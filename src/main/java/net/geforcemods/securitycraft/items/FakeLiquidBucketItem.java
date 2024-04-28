package net.geforcemods.securitycraft.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.Fluid;

public class FakeLiquidBucketItem extends BucketItem {
	public FakeLiquidBucketItem(Fluid fluid, Properties builder) {
		super(fluid, builder);

		DispenserBlock.registerBehavior(this, new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior instance = new DefaultDispenseItemBehavior();

			@Override
			public ItemStack execute(BlockSource source, ItemStack stack) {
				DispensibleContainerItem bucket = (DispensibleContainerItem) stack.getItem();
				BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
				Level level = source.level();

				if (bucket.emptyContents(null, level, pos, null)) {
					bucket.checkExtraContent(null, level, stack, pos);
					return new ItemStack(Items.BUCKET);
				}
				else
					return instance.dispense(source, stack);
			}
		});
	}
}
