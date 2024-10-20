package net.geforcemods.securitycraft.misc;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public record SCManualPage(Item item, PageGroup group, Component title, Component helpInfo, String designedBy, boolean hasRecipeDescription, Supplier<Optional<List<RecipeDisplay>>> recipe) {

	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, SCManualPage> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT.map(Item::byId, Item::getId), SCManualPage::item,
			ByteBufCodecs.VAR_INT.map(i -> PageGroup.values()[i], PageGroup::ordinal), SCManualPage::group,
			ComponentSerialization.STREAM_CODEC, SCManualPage::title,
			ComponentSerialization.STREAM_CODEC, SCManualPage::helpInfo,
			ByteBufCodecs.STRING_UTF8, SCManualPage::designedBy,
			ByteBufCodecs.BOOL, SCManualPage::hasRecipeDescription,
			ByteBufCodecs.optional(RecipeDisplay.STREAM_CODEC.apply(ByteBufCodecs.list())).map(o -> Suppliers.memoize(() -> o), Supplier::get), SCManualPage::recipe,
			SCManualPage::new);
	//@formatter:on
	public static final StreamCodec<RegistryFriendlyByteBuf, List<SCManualPage>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());
	public Object getInWorldObject() {
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();

			if (block.defaultBlockState().hasBlockEntity())
				return ((EntityBlock) block).newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
		}
		else if (item instanceof BoatItem boatItem)
			return boatItem.getBoat(ClientHandler.getClientLevel(), BlockHitResult.miss(Vec3.ZERO, Direction.NORTH, BlockPos.ZERO), item.getDefaultInstance(), null);

		return null;
	}
}
