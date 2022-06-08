package net.geforcemods.securitycraft.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public record SCManualPage(Item item, PageGroup group, Component title, Component helpInfo, String designedBy, boolean hasRecipeDescription) {}
