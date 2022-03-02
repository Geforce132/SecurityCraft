package net.geforcemods.securitycraft.misc;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;

public record SCManualPage(Item item, PageGroup group, TranslatableComponent title, TranslatableComponent helpInfo, String designedBy, boolean hasRecipeDescription) {}
