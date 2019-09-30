package net.geforcemods.securitycraft.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.gui.components.StackHoverChecker;
import net.geforcemods.securitycraft.gui.components.StringHoverChecker;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiSCManual extends GuiScreen {

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
		super();
	}

	@Override
	public void initGui(){
		byte startY = 2;

		if((width - 256) / 2 != startX && startX != -1)
			update = true;

		startX = (width - 256) / 2;
		Keyboard.enableRepeatEvents(true);

		buttonList.add(new GuiSCManual.ChangePageButton(1, startX + 210, startY + 158, true)); //next page
		buttonList.add(new GuiSCManual.ChangePageButton(2, startX + 16, startY + 158, false)); //previous page
		buttonList.add(new GuiSCManual.ChangePageButton(3, startX + 190, startY + 97, true)); //next subpage
		buttonList.add(new GuiSCManual.ChangePageButton(4, startX + 165, startY + 97, false)); //previous subpage
		updateRecipeAndIcons();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		if(update)
		{
			updateRecipeAndIcons();
			update = false;
		}

		if(currentPage == -1)
			mc.getTextureManager().bindTexture(infoBookTitlePage);
		else if(recipe != null || SecurityCraft.instance.manualPages.get(currentPage).isRecipeDisabled())
			mc.getTextureManager().bindTexture(infoBookTexture);
		else
			mc.getTextureManager().bindTexture(infoBookTextureSpecial);

		this.drawTexturedModalRect(startX, 5, 0, 0, 256, 250);

		if(currentPage > -1){
			if(SecurityCraft.instance.manualPages.get(currentPage).getHelpInfo().equals("help.securitycraft:reinforced.info"))
				fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:scManual.reinforced"), startX + 39, 27, 0, false);
			else
				fontRenderer.drawString(ClientUtils.localize(SecurityCraft.instance.manualPages.get(currentPage).getItem().getTranslationKey() + ".name"), startX + 39, 27, 0, false);

			fontRenderer.drawSplitString(subpages.get(currentSubpage), startX + 18, 45, 225, 0);

			String designedBy = SecurityCraft.instance.manualPages.get(currentPage).getDesignedBy();

			if(designedBy != null && !designedBy.isEmpty())
				fontRenderer.drawSplitString(ClientUtils.localize("gui.securitycraft:scManual.designedBy", designedBy), startX + 18, 180, 75, 0);
		}else{
			fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:scManual.intro.1"), startX + 39, 27, 0, false);
			fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:scManual.intro.2"), startX + 60, 159, 0, false);

			if(I18n.hasKey("gui.securitycraft:scManual.author"))
				fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:scManual.author"), startX + 65, 170, 0, false);
		}

		for(int i = 0; i < buttonList.size(); i++)
			buttonList.get(i).drawButton(mc, mouseX, mouseY, 0);

		if(currentPage > -1){
			Item item = SecurityCraft.instance.manualPages.get(currentPage).getItem();
			GuiUtils.drawItemStackToGui(mc, item, startX + 19, 22, !(SecurityCraft.instance.manualPages.get(currentPage).getItem() instanceof ItemBlock));

			mc.getTextureManager().bindTexture(infoBookIcons);

			TileEntity te = ((item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof ITileEntityProvider) ? ((ITileEntityProvider) ((ItemBlock) item).getBlock()).createNewTileEntity(Minecraft.getMinecraft().world, 0) : null);
			Block itemBlock = ((item instanceof ItemBlock) ? ((ItemBlock) item).getBlock() : null);

			if(itemBlock != null){
				if(itemBlock instanceof IExplosive)
					this.drawTexturedModalRect(startX + 107, 117, 54, 1, 18, 18);

				if(te != null){
					if(te instanceof IOwnable)
						this.drawTexturedModalRect(startX + 29, 118, 1, 1, 16, 16);

					if(te instanceof IPasswordProtected)
						this.drawTexturedModalRect(startX + 55, 118, 18, 1, 17, 16);

					if(te instanceof TileEntitySCTE && ((TileEntitySCTE) te).isActivatedByView())
						this.drawTexturedModalRect(startX + 81, 118, 36, 1, 17, 16);

					if(te instanceof CustomizableSCTE)
						this.drawTexturedModalRect(startX + 213, 118, 72, 1, 16, 16);
				}
			}

			if(recipe != null)
			{
				for(int i = 0; i < 3; i++)
					for(int j = 0; j < 3; j++)
					{
						if(((i * 3) + j) >= recipe.size())
							break;

						ItemStack[] matchingStacks = recipe.get((i * 3) + j).getMatchingStacks();

						if(matchingStacks.length == 0 || matchingStacks[0].isEmpty())
							continue;

						GuiUtils.drawItemStackToGui(mc, matchingStacks[0].getItem(), matchingStacks[0].getItemDamage(), (startX + 100) + (j * 20), 144 + (i * 20), !(matchingStacks[0].getItem() instanceof ItemBlock));
					}
			}

			for(HoverChecker chc : hoverCheckers)
			{
				if(chc != null && chc.checkHover(mouseX, mouseY))
				{
					if(chc instanceof StackHoverChecker && !((StackHoverChecker)chc).getStack().isEmpty())
						renderToolTip(((StackHoverChecker)chc).getStack(), mouseX, mouseY);
					else if(chc instanceof StringHoverChecker && ((StringHoverChecker)chc).getName() != null)
						drawHoveringText(((StringHoverChecker)chc).getName(), mouseX, mouseY);
				}
			}
		}
	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		lastPage = currentPage;
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException{
		super.keyTyped(typedChar, keyCode);

		if(keyCode == Keyboard.KEY_LEFT)
			previousSubpage();
		else if(keyCode == Keyboard.KEY_RIGHT)
			nextSubpage();
	}

	@Override
	protected void actionPerformed(GuiButton button){
		if(button.id == 1)
			nextPage();
		else if(button.id == 2)
			previousPage();
		else if(button.id == 3)
			nextSubpage();
		else if(button.id == 4)
			previousSubpage();

		//hide subpage buttons on main page
		buttonList.get(2).visible = currentPage != -1 && subpages.size() > 0;
		buttonList.get(3).visible = currentPage != -1 && subpages.size() > 0;
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		switch((int)Math.signum(Mouse.getEventDWheel()))
		{
			case -1: nextPage(); break;
			case 1: previousPage(); break;
		}

		//hide subpage buttons on main page
		buttonList.get(2).visible = currentPage != -1 && subpages.size() > 1;
		buttonList.get(3).visible = currentPage != -1 && subpages.size() > 1;
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
			return;
		}

		hoverCheckers.clear();

		if(SecurityCraft.instance.manualPages.get(currentPage).hasCustomRecipe())
			recipe = SecurityCraft.instance.manualPages.get(currentPage).getRecipe();
		else
			for(int o = 0; o < CraftingManager.REGISTRY.getKeys().size(); o++)
			{
				IRecipe object = CraftingManager.REGISTRY.getObjectById(o);

				if(object instanceof ShapedRecipes){
					ShapedRecipes recipe = (ShapedRecipes) object;

					if(!recipe.getRecipeOutput().isEmpty() && recipe.getRecipeOutput().getItem() == SecurityCraft.instance.manualPages.get(currentPage).getItem()){
						NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(recipe.recipeItems.size(), Ingredient.EMPTY);

						for(int i = 0; i < recipeItems.size(); i++)
							recipeItems.set(i, recipe.recipeItems.get(i));

						this.recipe = recipeItems;
						break;
					}
				}else if(object instanceof ShapelessRecipes){
					ShapelessRecipes recipe = (ShapelessRecipes) object;

					if(!recipe.getRecipeOutput().isEmpty() && recipe.getRecipeOutput().getItem() == SecurityCraft.instance.manualPages.get(currentPage).getItem()){
						NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(recipe.recipeItems.size(), Ingredient.EMPTY);

						for(int i = 0; i < recipeItems.size(); i++)
							recipeItems.set(i, recipe.recipeItems.get(i));

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
		TileEntity te = ((item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof ITileEntityProvider) ? ((ITileEntityProvider) ((ItemBlock) item).getBlock()).createNewTileEntity(Minecraft.getMinecraft().world, 0) : null);
		Block itemBlock = ((item instanceof ItemBlock) ? ((ItemBlock) item).getBlock() : null);

		if(te != null){
			if(te instanceof IOwnable)
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 29, (startX + 29) + 16, 20, ClientUtils.localize("gui.securitycraft:scManual.ownableBlock")));

			if(te instanceof IPasswordProtected)
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 55, (startX + 55) + 16, 20, ClientUtils.localize("gui.securitycraft:scManual.passwordProtectedBlock")));

			if(te instanceof TileEntitySCTE && ((TileEntitySCTE) te).isActivatedByView())
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 81, (startX + 81) + 16, 20, ClientUtils.localize("gui.securitycraft:scManual.viewActivatedBlock")));

			if(itemBlock instanceof IExplosive)
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 107, (startX + 107) + 16, 20, ClientUtils.localize("gui.securitycraft:scManual.explosiveBlock")));

			if(te instanceof CustomizableSCTE)
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 213, (startX + 213) + 16, 20, ClientUtils.localize("gui.securitycraft:scManual.customizableBlock")));
		}

		//set up subpages
		String helpInfo = ClientUtils.localize(SecurityCraft.instance.manualPages.get(currentPage).getHelpInfo());

		subpages.clear();

		while(fontRenderer.getStringWidth(helpInfo) > subpageLength)
		{
			String trimmed = fontRenderer.trimStringToWidth(helpInfo, 1285);

			trimmed = trimmed.trim().substring(0, trimmed.lastIndexOf(' ')).trim(); //remove last word to remove the possibility to break it up onto multiple pages
			subpages.add(trimmed);
			helpInfo = helpInfo.replace(trimmed, "").trim();
		}

		subpages.add(helpInfo);
	}

	@SideOnly(Side.CLIENT)
	static class ChangePageButton extends GuiButton {
		private final boolean isForward;

		public ChangePageButton(int index, int xPos, int yPos, boolean forward){
			super(index, xPos, yPos, 23, 13, "");
			isForward = forward;
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks){
			if(visible){
				boolean isHovering = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(bookGuiTextures);
				int textureX = 0;
				int textureY = 192;

				if(isHovering)
					textureX += 23;

				if(!isForward)
					textureY += 13;

				this.drawTexturedModalRect(x, y, textureX, textureY, 23, 13);
			}
		}
	}

}
