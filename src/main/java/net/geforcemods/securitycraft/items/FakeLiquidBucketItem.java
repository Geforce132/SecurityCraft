package net.geforcemods.securitycraft.items;

import java.util.function.Supplier;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FakeLiquidBucketItem extends BucketItem
{
	public FakeLiquidBucketItem(Supplier<? extends Fluid> supplier, Properties builder)
	{
		super(supplier, builder);

		DispenserBlock.registerDispenseBehavior(this, new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior instance = new DefaultDispenseItemBehavior();

			@Override
			public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
			{
				BucketItem bucket = (BucketItem)stack.getItem();
				BlockPos pos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
				World world = source.getWorld();

				if(bucket.tryPlaceContainedLiquid(null, world, pos, null))
				{
					bucket.onLiquidPlaced(world, stack, pos);
					return new ItemStack(Items.BUCKET);
				}
				else return instance.dispense(source, stack);
			}
		});
	}
}
