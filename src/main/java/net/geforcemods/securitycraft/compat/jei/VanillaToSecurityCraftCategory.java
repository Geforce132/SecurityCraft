//package net.geforcemods.securitycraft.compat.jei;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//
//import mezz.jei.api.constants.VanillaTypes;
//import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
//import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
//import mezz.jei.api.helpers.IGuiHelper;
//import mezz.jei.api.recipe.IFocusGroup;
//import mezz.jei.api.recipe.RecipeIngredientRole;
//import mezz.jei.api.recipe.RecipeType;
//import net.geforcemods.securitycraft.util.Utils;
//import net.minecraft.client.Minecraft;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.item.ItemStack;
//
//public class VanillaToSecurityCraftCategory extends BaseCategory {
//	private static final Component TITLE = Utils.localize("jei.securitycraft.category.reinforcing");
//
//	public VanillaToSecurityCraftCategory(IGuiHelper helper) {
//		super(helper);
//	}
//
//	@Override
//	public void draw(ReinforcerRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack pose, double mouseX, double mouseY) {
//		Minecraft.getInstance().font.draw(pose, OUTPUT_TEXT, 24, 5, 0x404040);
//	}
//
//	@Override
//	public Component getTitle() {
//		return TITLE;
//	}
//
//	@Override
//	public RecipeType<ReinforcerRecipe> getRecipeType() {
//		return SCJEIPlugin.VTS;
//	}
//
//	@Override
//	public void setRecipe(IRecipeLayoutBuilder builder, ReinforcerRecipe recipe, IFocusGroup focuses) {
//		builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(recipe.vanillaBlock()));
//		builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 1).addIngredient(VanillaTypes.ITEM_STACK, new ItemStack(recipe.securityCraftBlock()));
//	}
//}
