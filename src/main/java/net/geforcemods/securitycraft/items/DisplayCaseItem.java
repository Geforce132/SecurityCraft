package net.geforcemods.securitycraft.items;

import java.util.function.Consumer;

import net.geforcemods.securitycraft.renderers.DisplayCaseItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class DisplayCaseItem extends BlockItem {
	private final boolean glowing;

	public DisplayCaseItem(Block block, Properties properties, boolean glowing) {
		super(block, properties);
		this.glowing = glowing;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			BlockEntityWithoutLevelRenderer renderer = new DisplayCaseItemRenderer(() -> glowing); //needs to be a supplier, because initializeClient is called before the field is set

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
		});
	}
}
