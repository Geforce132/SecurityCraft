package net.geforcemods.securitycraft.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.gui.components.GuiButtonClick;
import net.geforcemods.securitycraft.gui.components.StackHoverChecker;
import net.geforcemods.securitycraft.gui.components.StringHoverChecker;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.HoverChecker;

@OnlyIn(Dist.CLIENT)
public class GuiSCManual extends Screen {

	private ResourceLocation infoBookTexture = new ResourceLocation("securitycraft:textures/gui/info_book_texture.png");
	private ResourceLocation infoBookTextureSpecial = new ResourceLocation("securitycraft:textures/gui/info_book_texture_special.png"); //for items without a recipe
	private ResourceLocation infoBookTitlePage = new ResourceLocation("securitycraft:textures/gui/info_book_title_page.png");
	private ResourceLocation infoBookIcons = new ResourceLocation("securitycraft:textures/gui/info_book_icons.png");
	private static ResourceLocation bookGuiTextures = new ResourceLocation("textures/gui/book.png");

	private List<HoverChecker> hoverCheckers = new ArrayList<HoverChecker>();
	private static int lastPage = -1;
	private int currentPage = lastPage;
	private NonNullList<Ingredient> recipe;
	private int startX = -1;
	private boolean update = false;
	private List<String> subpages = new ArrayList<>();
	private int currentSubpage = 0;
	private final int subpageLength = 1285;

	public GuiSCManual() {
		super(new TranslationTextComponent(SCContent.scManual.getTranslationKey()));
	}

	@Override
	public void init(){
		byte startY = 2;

		if((width - 256) / 2 != startX && startX != -1)
			update = true;

		startX = (width - 256) / 2;
		minecraft.keyboardListener.enableRepeatEvents(true);

		addButton(new GuiSCManual.ChangePageButton(1, startX + 210, startY + 158, true, this::actionPerformed)); //next page
		addButton(new GuiSCManual.ChangePageButton(2, startX + 16, startY + 158, false, this::actionPerformed)); //previous page
		addButton(new GuiSCManual.ChangePageButton(3, startX + 190, startY + 97, true, this::actionPerformed)); //next subpage
		addButton(new GuiSCManual.ChangePageButton(4, startX + 165, startY + 97, false, this::actionPerformed)); //previous subpage
		updateRecipeAndIcons();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		renderBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		if(update)
		{
			updateRecipeAndIcons();
			update = false;
		}

		if(currentPage == -1)
			minecraft.getTextureManager().bindTexture(infoBookTitlePage);
		else if(recipe != null || SecurityCraft.instance.manualPages.get(currentPage).isRecipeDisabled())
			minecraft.getTextureManager().bindTexture(infoBookTexture);
		else
			minecraft.getTextureManager().bindTexture(infoBookTextureSpecial);

		this.blit(startX, 5, 0, 0, 256, 250);

		if(currentPage > -1){
			if(SecurityCraft.instance.manualPages.get(currentPage).getHelpInfo().equals("help.securitycraft:reinforced.info"))
				font.drawString(ClientUtils.localize("gui.securitycraft:scManual.reinforced"), startX + 39, 27, 0);
			else
				font.drawString(ClientUtils.localize(SecurityCraft.instance.manualPages.get(currentPage).getItem().getTranslationKey()), startX + 39, 27, 0);

			font.drawSplitString(subpages.get(currentSubpage), startX + 18, 45, 225, 0);

			String designedBy = SecurityCraft.instance.manualPages.get(currentPage).designedBy();

			if(designedBy != null && !designedBy.isEmpty())
				font.drawSplitString(ClientUtils.localize("gui.securitycraft:scManual.designedBy", designedBy), startX + 18, 180, 75, 0);
		}else{
			font.drawString(ClientUtils.localize("gui.securitycraft:scManual.intro.1"), startX + 39, 27, 0);
			font.drawString(ClientUtils.localize("gui.securitycraft:scManual.intro.2"), startX + 60, 159, 0);

			if(I18n.hasKey("gui.securitycraft:scManual.author"))
				font.drawString(ClientUtils.localize("gui.securitycraft:scManual.author"), startX + 65, 170, 0);
		}

		for(int i = 0; i < buttons.size(); i++)
			buttons.get(i).render(mouseX, mouseY, partialTicks);

		if(currentPage > -1){
			Item item = SecurityCraft.instance.manualPages.get(currentPage).getItem();
			GuiUtils.drawItemStackToGui(minecraft, item, startX + 19, 22, !(SecurityCraft.instance.manualPages.get(currentPage).getItem() instanceof BlockItem));

			minecraft.getTextureManager().bindTexture(infoBookIcons);

			TileEntity te = ((item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof ITileEntityProvider) ? ((ITileEntityProvider) ((BlockItem) item).getBlock()).createNewTileEntity(Minecraft.getInstance().world) : null);
			Block BlockItem = ((item instanceof BlockItem) ? ((BlockItem) item).getBlock() : null);

			if(BlockItem != null){
				if(BlockItem instanceof IExplosive)
					this.blit(startX + 107, 117, 54, 1, 18, 18);

				if(te != null){
					if(te instanceof IOwnable)
						this.blit(startX + 29, 118, 1, 1, 16, 16);

					if(te instanceof IPasswordProtected)
						this.blit(startX + 55, 118, 18, 1, 17, 16);

					if(te instanceof TileEntitySCTE && ((TileEntitySCTE) te).isActivatedByView())
						this.blit(startX + 81, 118, 36, 1, 17, 16);

					if(te instanceof CustomizableSCTE)
						this.blit(startX + 213, 118, 72, 1, 16, 16);
				}
			}

			if(recipe != null)
			{
				for(int i = 0; i < 3; i++)
					for(int j = 0; j < 3; j++){
						if(((i * 3) + j) >= recipe.size())
							break;
						if(recipe.get((i * 3) + j).getMatchingStacks().length == 0 || recipe.get((i * 3) + j).getMatchingStacks()[0].isEmpty())
							continue;

						if(recipe.get((i * 3) + j).getMatchingStacks()[0].getItem() instanceof BlockItem)
							GuiUtils.drawItemStackToGui(minecraft, Block.getBlockFromItem(recipe.get((i * 3) + j).getMatchingStacks()[0].getItem()), (startX + 100) + (j * 20), 144 + (i * 20), !(recipe.get((i * 3) + j).getMatchingStacks()[0].getItem() instanceof BlockItem));
						else
							GuiUtils.drawItemStackToGui(minecraft, recipe.get((i * 3) + j).getMatchingStacks()[0].getItem(), (startX + 100) + (j * 20), 144 + (i * 20), !(recipe.get((i * 3) + j).getMatchingStacks()[0].getItem() instanceof BlockItem));
					}
			}

			for(HoverChecker chc : hoverCheckers)
			{
				if(chc != null && chc.checkHover(mouseX, mouseY))
				{
					if(chc instanceof StackHoverChecker && !((StackHoverChecker)chc).getStack().isEmpty())
						renderTooltip(((StackHoverChecker)chc).getStack(), mouseX, mouseY);
					else if(chc instanceof StringHoverChecker && ((StringHoverChecker)chc).getName() != null)
						renderTooltip(((StringHoverChecker)chc).getName(), mouseX, mouseY);
				}
			}
		}
	}

	@Override
	public void onClose(){
		super.onClose();
		lastPage = currentPage;
		minecraft.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode){
		if(keyCode == GLFW.GLFW_KEY_LEFT)
			previousSubpage();
		else if(keyCode == GLFW.GLFW_KEY_RIGHT)
			nextSubpage();

		return super.charTyped(typedChar, keyCode);
	}

	protected void actionPerformed(GuiButtonClick button){
		if(button.id == 1)
			nextPage();
		else if(button.id == 2)
			previousPage();
		else if(button.id == 3)
			nextSubpage();
		else if(button.id == 4)
			previousSubpage();

		//hide subpage buttons on main page
		buttons.get(2).visible = currentPage != -1 && subpages.size() > 0;
		buttons.get(3).visible = currentPage != -1 && subpages.size() > 0;
	}

	@Override
	public boolean mouseScrolled(double aDouble, double p_mouseScrolled_3_, double p_mouseScrolled_5_)
	{
		super.mouseScrolled(aDouble, p_mouseScrolled_3_, p_mouseScrolled_5_);

		switch((int)Math.signum(p_mouseScrolled_5_))
		{
			case -1: nextPage(); break;
			case 1: previousPage(); break;
		}

		//hide subpage buttons on main page
		buttons.get(2).visible = currentPage != -1 && subpages.size() > 1;
		buttons.get(3).visible = currentPage != -1 && subpages.size() > 1;
		return true;
	}

	private void nextPage()
	{
		currentPage++;

		if(currentPage > SecurityCraft.instance.manualPages.size() - 1)
			currentPage = -1;

		updateRecipeAndIcons();
	}

	private void previousPage()
	{
		currentPage--;

		if(currentPage < -1)
			currentPage = SecurityCraft.instance.manualPages.size() - 1;

		updateRecipeAndIcons();
	}

	private void nextSubpage()
	{
		currentSubpage++;

		if(currentSubpage == subpages.size())
			currentSubpage = 0;
	}

	private void previousSubpage()
	{
		currentSubpage--;

		if(currentSubpage == -1)
			currentSubpage = subpages.size() - 1;
	}

	private void updateRecipeAndIcons(){
		currentSubpage = 0;

		if(currentPage < 0){
			recipe = null;
			hoverCheckers.clear();
			buttons.get(2).visible = false;
			buttons.get(3).visible = false;
			return;
		}

		hoverCheckers.clear();

		if(SecurityCraft.instance.manualPages.get(currentPage).hasCustomRecipe())
			recipe = SecurityCraft.instance.manualPages.get(currentPage).getRecipe();
		else
			for(IRecipe<?> object : Minecraft.getInstance().world.getRecipeManager().getRecipes())
			{
				if(object instanceof ShapedRecipe){
					ShapedRecipe recipe = (ShapedRecipe) object;

					if(!recipe.getRecipeOutput().isEmpty() && recipe.getRecipeOutput().getItem() == SecurityCraft.instance.manualPages.get(currentPage).getItem()){
						NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(recipe.getIngredients().size(), Ingredient.EMPTY);

						for(int i = 0; i < recipeItems.size(); i++)
							recipeItems.set(i, recipe.getIngredients().get(i));

						this.recipe = recipeItems;
						break;
					}
				}else if(object instanceof ShapelessRecipe){
					ShapelessRecipe recipe = (ShapelessRecipe) object;

					if(!recipe.getRecipeOutput().isEmpty() && recipe.getRecipeOutput().getItem() == SecurityCraft.instance.manualPages.get(currentPage).getItem()){
						NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(recipe.getIngredients().size(), Ingredient.EMPTY);

						for(int i = 0; i < recipeItems.size(); i++)
							recipeItems.set(i, recipe.getIngredients().get(i));

						this.recipe = recipeItems;
						break;
					}
				}

				recipe = null;
			}

		boolean reinforcedPage = SecurityCraft.instance.manualPages.get(currentPage).getHelpInfo().equals("help.securitycraft:reinforced.info");

		if(recipe != null && !reinforcedPage)
		{
			outer: for(int i = 0; i < 3; i++)
			{
				for(int j = 0; j < 3; j++)
				{
					if((i * 3) + j == recipe.size())
						break outer;

					if(recipe.get((i * 3) + j).getMatchingStacks().length > 0 && !recipe.get((i * 3) + j).getMatchingStacks()[0].isEmpty())
						hoverCheckers.add(new StackHoverChecker(144 + (i * 20), 144 + (i * 20) + 16, (startX + 100) + (j * 20), (startX + 100) + (j * 20) + 16, 20, recipe.get((i * 3) + j).getMatchingStacks()[0]));
				}
			}
		}
		else if(SecurityCraft.instance.manualPages.get(currentPage).isRecipeDisabled())
			hoverCheckers.add(new StringHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, 20, ClientUtils.localize("gui.securitycraft:scManual.disabled")));
		else if(reinforcedPage)
		{
			recipe = null;
			hoverCheckers.add(new StringHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, 20, ClientUtils.localize("gui.securitycraft:scManual.recipe.reinforced")));
		}
		else
		{
			String name = SecurityCraft.instance.manualPages.get(currentPage).getItem().getRegistryName().getPath();

			hoverCheckers.add(new StringHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, 20, ClientUtils.localize("gui.securitycraft:scManual.recipe." + name)));
		}

		Item item = SecurityCraft.instance.manualPages.get(currentPage).getItem();
		TileEntity te = ((item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof ITileEntityProvider) ? ((ITileEntityProvider) ((BlockItem) item).getBlock()).createNewTileEntity(Minecraft.getInstance().world) : null);
		Block BlockItem = ((item instanceof BlockItem) ? ((BlockItem) item).getBlock() : null);

		if(te != null){
			if(te instanceof IOwnable)
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 29, (startX + 29) + 16, 20, ClientUtils.localize("gui.securitycraft:scManual.ownableBlock")));

			if(te instanceof IPasswordProtected)
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 55, (startX + 55) + 16, 20, ClientUtils.localize("gui.securitycraft:scManual.passwordProtectedBlock")));

			if(te instanceof TileEntitySCTE && ((TileEntitySCTE) te).isActivatedByView())
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 81, (startX + 81) + 16, 20, ClientUtils.localize("gui.securitycraft:scManual.viewActivatedBlock")));

			if(BlockItem instanceof IExplosive)
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 107, (startX + 107) + 16, 20, ClientUtils.localize("gui.securitycraft:scManual.explosiveBlock")));

			if(te instanceof CustomizableSCTE)
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 213, (startX + 213) + 16, 20, ClientUtils.localize("gui.securitycraft:scManual.customizableBlock")));
		}

		//set up subpages
		String helpInfo = ClientUtils.localize(SecurityCraft.instance.manualPages.get(currentPage).getHelpInfo());

		subpages.clear();

		while(font.getStringWidth(helpInfo) > subpageLength)
		{
			String trimmed = font.trimStringToWidth(helpInfo, 1285);

			trimmed = trimmed.trim().substring(0, trimmed.lastIndexOf(' ')).trim(); //remove last word to remove the possibility to break it up onto multiple pages
			subpages.add(trimmed);
			helpInfo = helpInfo.replace(trimmed, "").trim();
		}

		subpages.add(helpInfo);
	}

	static class ChangePageButton extends GuiButtonClick {
		private final boolean isForward;

		public ChangePageButton(int index, int xPos, int yPos, boolean forward, Consumer<GuiButtonClick> onClick){
			super(index, xPos, yPos, 23, 13, "", onClick);
			isForward = forward;
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void render(int mouseX, int mouseY, float partialTicks){
			if(visible){
				boolean isHovering = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				Minecraft.getInstance().getTextureManager().bindTexture(bookGuiTextures);
				int textureX = 0;
				int textureY = 192;

				if(isHovering)
					textureX += 23;

				if(!isForward)
					textureY += 13;

				this.blit(x, y, textureX, textureY, 23, 13);
			}
		}
	}

}
