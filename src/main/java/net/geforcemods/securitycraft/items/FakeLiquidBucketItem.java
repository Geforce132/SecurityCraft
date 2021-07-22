package net.geforcemods.securitycraft.items;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.Fluid;

public class FakeLiquidBucketItem extends BucketItem
{
	public FakeLiquidBucketItem(Supplier<? extends Fluid> supplier, Properties builder)
	{
		super(supplier, builder);

		DispenserBlock.registerBehavior(this, new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior instance = new DefaultDispenseItemBehavior();

			@Override
			public ItemStack execute(BlockSource source, ItemStack stack)
			{
				BucketItem bucket = (BucketItem)stack.getItem();
				BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				Level world = source.getLevel();

				if(bucket.emptyBucket(null, world, pos, null))
				{
					bucket.checkExtraContent(world, stack, pos);
					return new ItemStack(Items.BUCKET);
				}
				else return instance.dispense(source, stack);
			}
		});
	}
}
