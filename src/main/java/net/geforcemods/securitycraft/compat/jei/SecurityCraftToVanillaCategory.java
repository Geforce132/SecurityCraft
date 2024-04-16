package net.geforcemods.securitycraft.compat.jei;

public class SecurityCraftToVanillaCategory extends BaseCategory {
	//	private static final Component TITLE = Utils.localize("jei.securitycraft.category.unreinforcing");
	//
	//	public SecurityCraftToVanillaCategory(IGuiHelper helper) {
	//		super(helper);
	//	}
	//
	//	@Override
	//	public void draw(ReinforcerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
	//		guiGraphics.drawString(Minecraft.getInstance().font, OUTPUT_TEXT, 24, 30, 0x404040, false);
	//	}
	//
	//	@Override
	//	public Component getTitle() {
	//		return TITLE;
	//	}
	//
	//	@Override
	//	public RecipeType<ReinforcerRecipe> getRecipeType() {
	//		return SCJEIPlugin.STV;
	//	}
	//
	//	@Override
	//	public void setRecipe(IRecipeLayoutBuilder builder, ReinforcerRecipe recipe, IFocusGroup focuses) {
	//		builder.addSlot(RecipeIngredientRole.INPUT, 1, 26).addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(recipe.securityCraftBlock()));
	//		builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 26).addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(recipe.vanillaBlock()));
	//	}
}
