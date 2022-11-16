package net.geforcemods.securitycraft.items;

import java.util.function.Consumer;

import net.geforcemods.securitycraft.renderers.DisplayCaseItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class DisplayCaseItem extends BlockItem {
	public DisplayCaseItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			BlockEntityWithoutLevelRenderer renderer = new DisplayCaseItemRenderer();

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}
}
