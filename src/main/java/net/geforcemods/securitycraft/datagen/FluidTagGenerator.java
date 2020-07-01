package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import net.minecraft.tags.FluidTags;

//func_240522_a_ = getBuilder
//func_240534_a_ = add
public class FluidTagGenerator extends FluidTagsProvider
{
	public FluidTagGenerator(DataGenerator generator)
	{
		super(generator);
	}

	@Override
	protected void registerTags()
	{
		func_240522_a_(FluidTags.LAVA).func_240534_a_(SCContent.FAKE_LAVA.get(), SCContent.FLOWING_FAKE_LAVA.get());
		func_240522_a_(FluidTags.WATER).func_240534_a_(SCContent.FAKE_WATER.get(), SCContent.FLOWING_FAKE_WATER.get());
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Fluid Tags";
	}
}
