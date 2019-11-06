package net.geforcemods.securitycraft.datagen;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import net.minecraft.tags.FluidTags;

public class FluidTagGenerator extends FluidTagsProvider
{
	public FluidTagGenerator(DataGenerator generator)
	{
		super(generator);
	}

	@Override
	protected void registerTags()
	{
		getBuilder(FluidTags.LAVA).add(SCContent.fakeLava, SCContent.flowingFakeLava);
		getBuilder(FluidTags.WATER).add(SCContent.fakeWater, SCContent.flowingFakeWater);
	}

	@Override
	public String getName()
	{
		return "SecurityCraft Fluid Tags";
	}
}
