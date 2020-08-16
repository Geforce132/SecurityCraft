package net.geforcemods.securitycraft.misc;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.text.TranslationTextComponent;

public class SCManualPage {

	private Item item;
	private TranslationTextComponent helpInfo;
	private BooleanSupplier configValue = () -> true;
	private String designedBy = "";
	private static final List<Item> EXPLOSIVES = Arrays.asList(SCContent.BOUNCING_BETTY.get(),
			SCContent.CLAYMORE.get(),
			SCContent.COBBLESTONE_MINE.get(),
			SCContent.DIAMOND_ORE_MINE.get(),
			SCContent.DIRT_MINE.get(),
			SCContent.FURNACE_MINE.get(),
			SCContent.GRAVEL_MINE.get(),
			SCContent.IMS.get(),
			SCContent.SAND_MINE.get(),
			SCContent.STONE_MINE.get(),
			SCContent.TRACK_MINE.get(),
			SCContent.MINE.get(),
			SCContent.EMERALD_ORE_MINE.get(),
			SCContent.QUARTZ_ORE_MINE.get(),
			SCContent.REDSTONE_ORE_MINE.get(),
			SCContent.IRON_ORE_MINE.get(),
			SCContent.COAL_ORE_MINE.get(),
			SCContent.NETHER_GOLD_ORE_MINE.get(),
			SCContent.GILDED_BLACKSTONE_MINE.get(),
			SCContent.ANCIENT_DEBRIS_MINE.get(),
			SCContent.LAPIS_ORE_MINE.get(),
			SCContent.GOLD_ORE_MINE.get()).stream().map(Block::asItem).collect(Collectors.toList());

	public SCManualPage(Item item, TranslationTextComponent helpInfo){
		this.item = item;
		this.helpInfo = helpInfo;

		if(item == SCContent.KEYCARD_LVL_1.get()) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftKeycard1.get();}
		else if(item == SCContent.KEYCARD_LVL_2.get()) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftKeycard2.get();}
		else if(item == SCContent.KEYCARD_LVL_3.get()) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftKeycard3.get();}
		else if(item == SCContent.KEYCARD_LVL_4.get()) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftKeycard4.get();}
		else if(item == SCContent.KEYCARD_LVL_5.get()) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftKeycard5.get();}
		else if(item == SCContent.LIMITED_USE_KEYCARD.get()) {
			configValue = () -> ConfigHandler.CONFIG.ableToCraftLUKeycard.get();}
		else if(EXPLOSIVES.contains(item))
			configValue = () -> ConfigHandler.CONFIG.ableToCraftMines.get();
	}

	public Item getItem() {
		return item;
	}

	public TranslationTextComponent getHelpInfo() {
		return helpInfo;
	}

	public boolean isRecipeDisabled()
	{
		return !configValue.getAsBoolean();
	}

	public void setDesignedBy(String designedBy)
	{
		this.designedBy = designedBy;
	}

	public String getDesignedBy()
	{
		return designedBy;
	}
}
