package net.geforcemods.securitycraft.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.gui.components.HoverChecker;
import net.geforcemods.securitycraft.gui.components.PatronScrollList;
import net.geforcemods.securitycraft.gui.components.StackHoverChecker;
import net.geforcemods.securitycraft.gui.components.StringHoverChecker;
import net.geforcemods.securitycraft.items.ItemSCManual;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
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
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiSCManual extends GuiScreen {

	private ResourceLocation infoBookTexture = new ResourceLocation("securitycraft:textures/gui/info_book_texture.png");
	private ResourceLocation infoBookTextureSpecial = new ResourceLocation("securitycraft:textures/gui/info_book_texture_special.png"); //for items without a recipe
	private ResourceLocation infoBookTitlePage = new ResourceLocation("securitycraft:textures/gui/info_book_title_page.png");
	private ResourceLocation infoBookIcons = new ResourceLocation("securitycraft:textures/gui/info_book_icons.png");
	private static ResourceLocation bookGuiTextures = new ResourceLocation("textures/gui/book.png");
	private List<HoverChecker> hoverCheckers = new ArrayList<>();
	private static int lastPage = -1;
	private int currentPage = lastPage;
	private NonNullList<Ingredient> recipe;
	private int startX = -1;
	private List<String> subpages = new ArrayList<>();
	private List<String> author = new ArrayList<>();
	private int currentSubpage = 0;
	private final int subpageLength = 1285;
	private final String intro1 = Utils.localize("gui.securitycraft:scManual.intro.1").setStyle(new Style().setUnderlined(true)).getFormattedText();
	private final String ourPatrons = Utils.localize("gui.securitycraft:scManual.patreon.title").getFormattedText();
	private List<String> intro2;
	private PatronList patronList;
	private GuiButton patreonLinkButton;

	@Override
	public void initGui(){
		byte startY = 2;

		startX = (width - 256) / 2;
		Keyboard.enableRepeatEvents(true);
		buttonList.add(new GuiSCManual.ChangePageButton(1, startX + 210, startY + 188, true)); //next page
		buttonList.add(new GuiSCManual.ChangePageButton(2, startX + 16, startY + 188, false)); //previous page
		buttonList.add(new GuiSCManual.ChangePageButton(3, startX + 180, startY + 97, true)); //next subpage
		buttonList.add(new GuiSCManual.ChangePageButton(4, startX + 155, startY + 97, false)); //previous subpage
		buttonList.add(patreonLinkButton = new HyperlinkButton(5, startX + 225, 143, 16, 16, ""));
		patronList = new PatronList(mc, 115, 90, 50, startX + 125, width, height);
		updateRecipeAndIcons();
		ItemSCManual.PAGES.sort((page1, page2) -> {
			String key1 = Utils.localize(page1.getItem().getTranslationKey() + ".name").getFormattedText();
			String key2 = Utils.localize(page2.getItem().getTranslationKey() + ".name").getFormattedText();

			return key1.compareTo(key2);
		});
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		if(currentPage == -1)
			mc.getTextureManager().bindTexture(infoBookTitlePage);
		else if(recipe != null || ItemSCManual.PAGES.get(currentPage).isRecipeDisabled())
			mc.getTextureManager().bindTexture(infoBookTexture);
		else
			mc.getTextureManager().bindTexture(infoBookTextureSpecial);

		drawTexturedModalRect(startX, 5, 0, 0, 256, 250);

		for(int i = 0; i < buttonList.size(); i++)
		{
			buttonList.get(i).drawButton(mc, mouseX, mouseY, 0);
		}

		if(currentPage > -1)
		{
			Item item = ItemSCManual.PAGES.get(currentPage).getItem();
			boolean isItemBlock = item instanceof ItemBlock;
			String pageNumberText = (currentPage + 2) + "/" + (ItemSCManual.PAGES.size() + 1); //+1 because the "welcome" page is not included
			String designedBy = ItemSCManual.PAGES.get(currentPage).getDesignedBy();

			if(subpages.size() > 1)
				fontRenderer.drawString((currentSubpage + 1) + "/" + subpages.size(), startX + 205, 102, 0x8E8270);

			if(ItemSCManual.PAGES.get(currentPage).getHelpInfo().equals("help.securitycraft:reinforced.info"))
				fontRenderer.drawString(Utils.localize("gui.securitycraft:scManual.reinforced").getFormattedText(), startX + 39, 27, 0, false);
			else
				fontRenderer.drawString(Utils.localize(ItemSCManual.PAGES.get(currentPage).getItem().getTranslationKey() + ".name").getFormattedText(), startX + 39, 27, 0, false);

			if(designedBy != null && !designedBy.isEmpty())
				fontRenderer.drawSplitString(Utils.localize("gui.securitycraft:scManual.designedBy", designedBy).getFormattedText(), startX + 18, 150, 75, 0);

			fontRenderer.drawString(pageNumberText, startX + 240 - fontRenderer.getStringWidth(pageNumberText), 182, 0x8E8270);
			GuiUtils.drawItemStackToGui(new ItemStack(item), startX + 19, 22, !isItemBlock);
			fontRenderer.drawSplitString(subpages.get(currentSubpage), startX + 18, 45, 225, 0);
			mc.getTextureManager().bindTexture(infoBookIcons);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			if(isItemBlock){
				Block block = ((ItemBlock)item).getBlock();

				if(block instanceof IExplosive)
					drawTexturedModalRect(startX + 107, 117, 54, 1, 18, 18);

				if(block.hasTileEntity(block.getDefaultState()))
				{
					TileEntity te = block.createTileEntity(Minecraft.getMinecraft().world, block.getDefaultState());

					if(te instanceof IOwnable)
						drawTexturedModalRect(startX + 29, 118, 1, 1, 16, 16);

					if(te instanceof IPasswordProtected)
						drawTexturedModalRect(startX + 55, 118, 18, 1, 17, 16);

					if(te instanceof TileEntitySCTE && ((TileEntitySCTE) te).isActivatedByView())
						drawTexturedModalRect(startX + 81, 118, 36, 1, 17, 16);

					if(te instanceof ICustomizable)
					{
						ICustomizable scte = (ICustomizable)te;

						drawTexturedModalRect(startX + 213, 118, 72, 1, 16, 16);

						if(scte.customOptions() != null && scte.customOptions().length > 0)
							drawTexturedModalRect(startX + 136, 118, 88, 1, 16, 16);
					}

					if(te instanceof IModuleInventory)
					{
						if(((IModuleInventory)te).acceptedModules() != null && ((IModuleInventory)te).acceptedModules().length > 0)
							drawTexturedModalRect(startX + 163, 118, 105, 1, 16, 16);
					}
				}
			}

			if(recipe != null)
			{
				for(int i = 0; i < 3; i++)
				{
					for(int j = 0; j < 3; j++)
					{
						if(((i * 3) + j) >= recipe.size())
							break;

						ItemStack[] matchingStacks = recipe.get((i * 3) + j).getMatchingStacks();

						if(matchingStacks.length == 0 || matchingStacks[0].isEmpty())
							continue;

						GuiUtils.drawItemStackToGui(matchingStacks[0], (startX + 101) + (j * 19), 144 + (i * 19), !(matchingStacks[0].getItem() instanceof ItemBlock));
					}
				}
			}

			for(HoverChecker chc : hoverCheckers)
			{
				if(chc != null && chc.checkHover(mouseX, mouseY))
				{
					if(chc instanceof StackHoverChecker && !((StackHoverChecker)chc).getStack().isEmpty())
						renderToolTip(((StackHoverChecker)chc).getStack(), mouseX, mouseY);
					else if(chc instanceof StringHoverChecker && ((StringHoverChecker)chc).getName() != null)
						drawHoveringText(((StringHoverChecker)chc).getLines(), mouseX, mouseY);
				}
			}
		}
		else //"welcome" page
		{
			String pageNumberText = "1/" + (ItemSCManual.PAGES.size() + 1); //+1 because the "welcome" page is not included

			fontRenderer.drawString(intro1, width / 2 - fontRenderer.getStringWidth(intro1) / 2, 22, 0, false);

			for(int i = 0; i < intro2.size(); i++)
			{
				String text = intro2.get(i);

				fontRenderer.drawString(text, width / 2 - fontRenderer.getStringWidth(text) / 2, 150 + 10 * i, 0);
			}

			for(int i = 0; i < author.size(); i++)
			{
				String text = author.get(i);

				fontRenderer.drawString(text, width / 2 - fontRenderer.getStringWidth(text) / 2, 180 + 10 * i, 0);
			}

			//the patreon link button may overlap with a name tooltip from the list, so draw the list after the buttons
			if(patronList != null)
				patronList.drawScreen(mouseX, mouseY, partialTicks);

			fontRenderer.drawString(pageNumberText, startX + 240 - fontRenderer.getStringWidth(pageNumberText), 182, 0x8E8270);
			fontRenderer.drawString(ourPatrons, width / 2 - fontRenderer.getStringWidth(ourPatrons) / 2 + 30, 40, 0);
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
		else if(button.id == patreonLinkButton.id)
		{
			handleComponentClick(new TextComponentString("").setStyle(new Style().setClickEvent(new ClickEvent(Action.OPEN_URL, "https://www.patreon.com/Geforce"))));
			return;
		}

		//hide subpage buttons on main page
		buttonList.get(2).visible = currentPage != -1 && subpages.size() > 1;
		buttonList.get(3).visible = currentPage != -1 && subpages.size() > 1;
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		int mouseX = Mouse.getEventX() * width / mc.displayWidth;
		int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

		if(currentPage == -1 && patronList != null && patronList.isHovering)
		{
			patronList.handleMouseInput(mouseX, mouseY);
			return;
		}

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

		if(currentPage > ItemSCManual.PAGES.size() - 1)
			currentPage = -1;

		updateRecipeAndIcons();
	}

	private void previousPage()
	{
		currentPage--;

		if(currentPage < -1)
			currentPage = ItemSCManual.PAGES.size() - 1;

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
		hoverCheckers.clear();
		patreonLinkButton.visible = currentPage == -1;

		if(currentPage < 0){
			buttonList.get(2).visible = false;
			buttonList.get(3).visible = false;
			recipe = null;

			if(I18n.hasKey("gui.securitycraft:scManual.author"))
				author = fontRenderer.listFormattedStringToWidth(Utils.localize("gui.securitycraft:scManual.author").getFormattedText(), 180);
			else
				author.clear();

			intro2 = fontRenderer.listFormattedStringToWidth(Utils.localize("gui.securitycraft:scManual.intro.2").getFormattedText(), 225);
			patronList.fetchPatrons();
			return;
		}

		SCManualPage page = ItemSCManual.PAGES.get(currentPage);

		if(page.hasCustomRecipe())
			recipe = page.getRecipe();
		else
		{
			recipe = null;

			for(int o = 0; o < CraftingManager.REGISTRY.getKeys().size(); o++)
			{
				IRecipe object = CraftingManager.REGISTRY.getObjectById(o);

				if(object instanceof ShapedRecipes){
					ShapedRecipes recipe = (ShapedRecipes) object;

					if(!recipe.getRecipeOutput().isEmpty() && recipe.getRecipeOutput().getItem() == page.getItem()){
						NonNullList<Ingredient> ingredients = recipe.getIngredients();
						NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(9, Ingredient.EMPTY);

						for(int i = 0; i < ingredients.size(); i++)
						{
							recipeItems.set(getCraftMatrixPosition(i, recipe.getWidth(), recipe.getHeight()), ingredients.get(i));
						}

						this.recipe = recipeItems;
						break;
					}
				}else if(object instanceof ShapelessRecipes){
					ShapelessRecipes recipe = (ShapelessRecipes) object;

					if(!recipe.getRecipeOutput().isEmpty() && recipe.getRecipeOutput().getItem() == page.getItem()){
						//don't show keycard reset recipes
						if(recipe.getRegistryName().getPath().endsWith("_reset"))
							continue;

						NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(recipe.recipeItems.size(), Ingredient.EMPTY);

						for(int i = 0; i < recipeItems.size(); i++)
							recipeItems.set(i, recipe.recipeItems.get(i));

						this.recipe = recipeItems;
						break;
					}
				}
			}
		}

		String helpInfo = page.getHelpInfo();
		boolean reinforcedPage = helpInfo.equals("help.securitycraft:reinforced.info") || helpInfo.contains("reinforced_hopper");

		if(recipe != null && !reinforcedPage)
		{
			outer: for(int i = 0; i < 3; i++)
			{
				for(int j = 0; j < 3; j++)
				{
					if((i * 3) + j == recipe.size())
						break outer;

					if(recipe.get((i * 3) + j).getMatchingStacks().length > 0 && !recipe.get((i * 3) + j).getMatchingStacks()[0].isEmpty())
						hoverCheckers.add(new StackHoverChecker(recipe.get((i * 3) + j).getMatchingStacks()[0], 144 + (i * 19), 144 + (i * 19) + 16, (startX + 101) + (j * 19), (startX + 100) + (j * 19) + 16));
				}
			}
		}
		else if(page.isRecipeDisabled())
			hoverCheckers.add(new StringHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.disabled").getFormattedText()));
		else if(reinforcedPage)
		{
			recipe = null;
			hoverCheckers.add(new StringHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.recipe.reinforced").getFormattedText()));
		}
		else
		{
			String name = page.getItem().getRegistryName().getPath();

			hoverCheckers.add(new StringHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.recipe." + name).getFormattedText()));
		}

		Item item = page.getItem();

		if(item instanceof ItemBlock){
			Block block = ((ItemBlock) item).getBlock();

			if(block instanceof IExplosive)
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 107, (startX + 107) + 16, Utils.localize("gui.securitycraft:scManual.explosiveBlock").getFormattedText()));

			if(block.hasTileEntity(block.getDefaultState()))
			{
				TileEntity te = block.createTileEntity(Minecraft.getMinecraft().world, block.getDefaultState());

				if(te instanceof IOwnable)
					hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 29, (startX + 29) + 16, Utils.localize("gui.securitycraft:scManual.ownableBlock").getFormattedText()));

				if(te instanceof IPasswordProtected)
					hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 55, (startX + 55) + 16, Utils.localize("gui.securitycraft:scManual.passwordProtectedBlock").getFormattedText()));

				if(te instanceof TileEntitySCTE && ((TileEntitySCTE) te).isActivatedByView())
					hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 81, (startX + 81) + 16, Utils.localize("gui.securitycraft:scManual.viewActivatedBlock").getFormattedText()));

				if(te instanceof ICustomizable)
				{
					ICustomizable scte = (ICustomizable)te;

					hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 213, (startX + 213) + 16, Utils.localize("gui.securitycraft:scManual.customizableBlock").getFormattedText()));

					if(scte.customOptions() != null && scte.customOptions().length > 0)
					{
						List<String> display = new ArrayList<>();

						display.add(Utils.localize("gui.securitycraft:scManual.options").getFormattedText());
						display.add("---");

						for(Option<?> option : scte.customOptions())
						{
							display.add("- " + Utils.localize("option." + block.getTranslationKey().substring(5) + "." + option.getName() + ".description").getFormattedText());
							display.add("");
						}

						display.remove(display.size() - 1);
						hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 136, (startX + 136) + 16, display));
					}
				}

				if(te instanceof IModuleInventory)
				{
					IModuleInventory moduleInv = (IModuleInventory)te;

					if(moduleInv.acceptedModules() != null && moduleInv.acceptedModules().length > 0)
					{
						List<String> display = new ArrayList<>();

						display.add(Utils.localize("gui.securitycraft:scManual.modules").getFormattedText());
						display.add("---");

						for(EnumModuleType module : moduleInv.acceptedModules())
						{
							display.add("- " + Utils.localize("module." + block.getTranslationKey().substring(5) + "." + module.getItem().getTranslationKey().substring(5).replace("securitycraft:", "") + ".description").getFormattedText());
							display.add("");
						}

						display.remove(display.size() - 1);
						hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 163, (startX + 163) + 16, display));
					}
				}
			}
		}

		//set up subpages
		helpInfo = Utils.localize(page.getHelpInfo()).getFormattedText();
		subpages.clear();

		while(fontRenderer.getStringWidth(helpInfo) > subpageLength)
		{
			String trimmed = fontRenderer.trimStringToWidth(helpInfo, 1285);
			int temp = trimmed.lastIndexOf(' ');
			if(temp > 0)
				trimmed = trimmed.trim().substring(0, temp); //remove last word to remove the possibility to break it up onto multiple pages
			trimmed = trimmed.trim();
			subpages.add(trimmed);
			helpInfo = helpInfo.replace(trimmed, "").trim();
		}

		subpages.add(helpInfo);
	}

	class PatronList extends PatronScrollList
	{
		private final int slotHeight = 12;
		private final ExecutorService executor = Executors.newSingleThreadExecutor();
		private Future<List<String>> patronRequestFuture;
		private List<String> patrons = new ArrayList<>();
		private boolean patronsAvailable = false;
		private boolean error = false;
		private boolean patronsRequested;
		private final int barWidth = 6;
		private final List<String> fetchErrorLines;
		private final List<String> noPatronsLines;
		private final String loadingText = Utils.localize("gui.securitycraft:scManual.patreon.loading").getFormattedText();
		private final int border = 4;

		public PatronList(Minecraft client, int width, int height, int top, int left, int screenWidth, int screenHeight)
		{
			super(client, width, height, top, top + height, left, 12, screenWidth, screenHeight);

			fetchErrorLines = fontRenderer.listFormattedStringToWidth(Utils.localize("gui.securitycraft:scManual.patreon.error").getFormattedText(), listWidth);
			noPatronsLines = fontRenderer.listFormattedStringToWidth(Utils.localize("advancements.empty").getFormattedText(), listWidth - 10);
		}

		@Override
		public int getSize()
		{
			return patrons.size();
		}

		@Override
		public int getContentHeight()
		{
			int height = 50 + (patrons.size() * fontRenderer.FONT_HEIGHT);

			if(height < bottom - top - 8)
				height = bottom - top - 8;

			return height;
		}

		@Override
		public void drawScreen(int mouseX, int mouseY, float partialTicks)
		{
			if(patronsAvailable) //code from ScrollPanel to be able to change colors
			{
				super.drawScreen(mouseX, mouseY, partialTicks);

				//draw tooltip for long patron names
				int mouseListY = (int)(mouseY - top + scrollDistance - border);
				int slotIndex = mouseListY / slotHeight;
				int baseY = top + border - (int)scrollDistance;

				if(mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < patrons.size() && mouseY >= top && mouseY <= bottom)
				{
					String patron = patrons.get(slotIndex);
					int length = fontRenderer.getStringWidth(patron);

					if(length >= listWidth - barWidth)
					{
						drawHoveringText(patron, left - 10, baseY + (slotHeight * slotIndex + slotHeight));
						GlStateManager.disableLighting();
					}
				}

				if (patrons.isEmpty()) {
					for(int i = 0; i < noPatronsLines.size(); i++) {
						String line = noPatronsLines.get(i);

						fontRenderer.drawString(line, left + listWidth / 2 - fontRenderer.getStringWidth(line) / 2, top + 30 + i * 10, 0xFF333333);
					}
				}
			}
			else if(error)
			{
				for(int i = 0; i < fetchErrorLines.size(); i++)
				{
					String line = fetchErrorLines.get(i);

					fontRenderer.drawString(line, left + listWidth / 2 - fontRenderer.getStringWidth(line) / 2, top + 30 + i * 10, 0xFFB00101);
				}
			}
			else if(patronRequestFuture != null && patronRequestFuture.isDone())
			{
				try
				{
					patrons = patronRequestFuture.get();
					executor.shutdown();
					patronsAvailable = true;
				}
				catch(InterruptedException | ExecutionException e)
				{
					error = true;
				}
			}
			else
				fontRenderer.drawString(loadingText, left + listWidth / 2 - fontRenderer.getStringWidth(loadingText) / 2, top + 30, 0);
		}

		@Override
		public void drawPanel(int entryRight, int relativeY, Tessellator tesselator, int mouseX, int mouseY)
		{
			//draw entry strings
			for(int i = 0; i < patrons.size(); i++)
			{
				String patron = patrons.get(i);

				if(patron != null && !patron.isEmpty())
					fontRenderer.drawString(patron, left + 2, relativeY + (slotHeight * i), 0);
			}
		}

		public void fetchPatrons()
		{
			if(!patronsRequested)
			{
				//create thread to fetch patrons. without this, and for example if the player has no internet connection, the game will hang
				patronRequestFuture = executor.submit(() -> {
					try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://gist.githubusercontent.com/bl4ckscor3/bdda6596012b1206816db034350b5717/raw").openStream())))
					{
						return reader.lines().collect(Collectors.toList());
					}
					catch(IOException e)
					{
						error = true;
						return new ArrayList<>();
					}
				});
				patronsRequested = true;
			}
		}
	}

	static class ChangePageButton extends GuiButton {
		private final int textureY;

		public ChangePageButton(int index, int xPos, int yPos, boolean forward){
			super(index, xPos, yPos, 23, 13, "");
			textureY = forward ? 192 : 205;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks){
			if(visible){
				boolean isHovering = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(bookGuiTextures);
				drawTexturedModalRect(x, y, isHovering ? 23 : 0, textureY, 23, 13);
			}
		}
	}

	class HyperlinkButton extends GuiButtonExt
	{
		public HyperlinkButton(int id, int xPos, int yPos, int width, int height, String displayString)
		{
			super(id, xPos, yPos, width, height, displayString);
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
		{
			if(visible)
			{
				mc.getTextureManager().bindTexture(infoBookIcons);
				hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

				if(hovered)
					drawTexturedModalRect(x, y, 138, 1, 16, 16);
				else
					drawTexturedModalRect(x, y, 122, 1, 16, 16);
			}
		}
	}

	//from JEI
	private int getCraftMatrixPosition(int i, int width, int height)
	{
		int index;

		if(width == 1)
		{
			if(height == 3)
				index = (i * 3) + 1;
			else if(height == 2)
				index = (i * 3) + 1;
			else
				index = 4;

		}
		else if(height == 1)
			index = i + 3;
		else if(width == 2)
		{
			index = i;

			if(i > 1)
			{
				index++;

				if(i > 3)
					index++;
			}
		}
		else if(height == 2)
			index = i + 3;
		else
			index = i;

		return index;
	}
}
