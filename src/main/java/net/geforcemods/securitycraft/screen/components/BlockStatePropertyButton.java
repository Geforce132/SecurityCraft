package net.geforcemods.securitycraft.screen.components;

import java.util.Collection;
import java.util.function.Consumer;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockStatePropertyButton<T extends Comparable<T>> extends ToggleComponentButton {
	private final Property<T> property;
	private T value;

	public BlockStatePropertyButton(int xPos, int yPos, int width, int height, int initialValue, Property<T> property, Consumer<IdButton> onClick) {
		super(-1, xPos, yPos, width, height, null, initialValue, property.getPossibleValues().size(), onClick);
		this.property = property;
		onValueChange();
	}

	@Override
	public void onValueChange() {
		if (property != null) {
			Collection<T> values = property.getPossibleValues();
			int i = 0;

			for (T t : values) {
				if (i++ == getCurrentIndex()) {
					value = property.value(t).pValue();
					break;
				}
			}

			setMessage(new TextComponent(property.getName(value)));
		}
	}

	public Property<T> getProperty() {
		return property;
	}

	public T getValue() {
		return value;
	}
}
