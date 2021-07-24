package net.geforcemods.securitycraft.items;

import java.util.function.Consumer;

import net.geforcemods.securitycraft.renderers.ItemKeypadChestRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.IItemRenderProperties;

public class KeypadChestItem extends BlockItem
{
	public KeypadChestItem(Block block, Properties properties)
	{
		super(block, properties);
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer)
	{
		consumer.accept(new IItemRenderProperties() {
			BlockEntityWithoutLevelRenderer renderer = new ItemKeypadChestRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer()
			{
				return renderer;
			}
		});
	}
}
