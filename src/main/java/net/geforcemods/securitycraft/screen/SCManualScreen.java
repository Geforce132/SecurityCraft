package net.geforcemods.securitycraft.screen;

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
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.items.SCManualItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.screen.components.IngredientDisplay;
import net.geforcemods.securitycraft.screen.components.StringHoverChecker;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ScrollPanel;

@OnlyIn(Dist.CLIENT)
public class SCManualScreen extends Screen {

	private ResourceLocation infoBookTexture = new ResourceLocation("securitycraft:textures/gui/info_book_texture.png");
	private ResourceLocation infoBookTextureSpecial = new ResourceLocation("securitycraft:textures/gui/info_book_texture_special.png"); //for items without a recipe
	private ResourceLocation infoBookTitlePage = new ResourceLocation("securitycraft:textures/gui/info_book_title_page.png");
	private ResourceLocation infoBookIcons = new ResourceLocation("securitycraft:textures/gui/info_book_icons.png");
	private static ResourceLocation bookGuiTextures = new ResourceLocation("textures/gui/book.png");
	private List<HoverChecker> hoverCheckers = new ArrayList<>();
	private static int lastPage = -1;
	private int currentPage = lastPage;
	private NonNullList<Ingredient> recipe;
	private IngredientDisplay[] displays = new IngredientDisplay[9];
	private int startX = -1;
	private boolean update = false;
	private List<String> subpages = new ArrayList<>();
	private List<String> author = new ArrayList<>();
	private int currentSubpage = 0;
	private final int subpageLength = 1285;
	private final String intro1 = Utils.localize("gui.securitycraft:scManual.intro.1").setStyle(new Style().setUnderlined(true)).getFormattedText();
	private final String ourPatrons = Utils.localize("gui.securitycraft:scManual.patreon.title").getFormattedText();
	private List<String> intro2;
	private PatronList patronList;
	private Button patreonLinkButton;

	public SCManualScreen() {
		super(new TranslationTextComponent(SCContent.SC_MANUAL.get().getTranslationKey()));
	}

	@Override
	public void init(){
		byte startY = 2;

		if((width - 256) / 2 != startX && startX != -1)
			update = true;

		startX = (width - 256) / 2;
		minecraft.keyboardListener.enableRepeatEvents(true);
		addButton(new SCManualScreen.ChangePageButton(1, startX + 210, startY + 188, true, this::actionPerformed)); //next page
		addButton(new SCManualScreen.ChangePageButton(2, startX + 16, startY + 188, false, this::actionPerformed)); //previous page
		addButton(new SCManualScreen.ChangePageButton(3, startX + 180, startY + 97, true, this::actionPerformed)); //next subpage
		addButton(new SCManualScreen.ChangePageButton(4, startX + 155, startY + 97, false, this::actionPerformed)); //previous subpage
		addButton(patreonLinkButton = new HyperlinkButton(startX + 225, 143, 16, 16, "", b -> handleComponentClicked(new StringTextComponent("").setStyle(new Style().setClickEvent(new ClickEvent(Action.OPEN_URL, "https://www.patreon.com/Geforce"))))));
		children.add(patronList = new PatronList(minecraft, 115, 90, 50, startX + 125));

		for(int i = 0; i < 3; i++)
		{
			for(int j = 0; j < 3; j++)
			{
				displays[(i * 3) + j] = new IngredientDisplay((startX + 101) + (j * 19), 144 + (i * 19));
			}
		}

		updateRecipeAndIcons();
		SCManualItem.PAGES.sort((page1, page2) -> {
			String key1 = Utils.localize(page1.getItem().getTranslationKey()).getFormattedText();
			String key2 = Utils.localize(page2.getItem().getTranslationKey()).getFormattedText();

			return key1.compareTo(key2);
		});
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		if(update)
		{
			updateRecipeAndIcons();
			update = false;
		}

		if(currentPage == -1)
			minecraft.getTextureManager().bindTexture(infoBookTitlePage);
		else if(recipe != null && recipe.size() > 0)
			minecraft.getTextureManager().bindTexture(infoBookTexture);
		else
			minecraft.getTextureManager().bindTexture(infoBookTextureSpecial);

		blit(startX, 5, 0, 0, 256, 250);

		if(currentPage > -1){
			if(SCManualItem.PAGES.get(currentPage).getHelpInfo().equals("help.securitycraft:reinforced.info"))
				font.drawString(Utils.localize("gui.securitycraft:scManual.reinforced").getFormattedText(), startX + 39, 27, 0);
			else
				font.drawString(Utils.localize(SCManualItem.PAGES.get(currentPage).getItem().getTranslationKey()).getFormattedText(), startX + 39, 27, 0);

			font.drawSplitString(subpages.get(currentSubpage), startX + 18, 45, 225, 0);

			String designedBy = SCManualItem.PAGES.get(currentPage).getDesignedBy();

			if(designedBy != null && !designedBy.isEmpty())
				font.drawSplitString(Utils.localize("gui.securitycraft:scManual.designedBy", designedBy).getFormattedText(), startX + 18, 150, 75, 0);
		}else{
			font.drawString(intro1, width / 2 - font.getStringWidth(intro1) / 2, 22, 0);

			for(int i = 0; i < intro2.size(); i++)
			{
				String text = intro2.get(i);

				font.drawString(text, width / 2 - font.getStringWidth(text) / 2, 150 + 10 * i, 0);
			}

			for(int i = 0; i < author.size(); i++)
			{
				String text = author.get(i);

				font.drawString(text, width / 2 - font.getStringWidth(text) / 2, 180 + 10 * i, 0);
			}

			font.drawString(ourPatrons, width / 2 - font.getStringWidth(ourPatrons) / 2 + 30, 40, 0);
		}

		for(int i = 0; i < buttons.size(); i++)
			buttons.get(i).render(mouseX, mouseY, partialTicks);

		if(currentPage > -1)
		{
			Item item = SCManualItem.PAGES.get(currentPage).getItem();

			//draw page numbers
			if(subpages.size() > 1)
				font.drawString((currentSubpage + 1) + "/" + subpages.size(), startX + 205, 102, 0x8E8270);

			String pageNumberText = (currentPage + 2) + "/" + (SCManualItem.PAGES.size() + 1); //+1 because the "welcome" page is not included

			font.drawString(pageNumberText, startX + 240 - font.getStringWidth(pageNumberText), 182, 0x8E8270);

			minecraft.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(item), startX + 19, 22);
			minecraft.getTextureManager().bindTexture(infoBookIcons);

			if(item instanceof BlockItem){
				Block block = ((BlockItem) item).getBlock();
				TileEntity te = block.hasTileEntity(block.getDefaultState()) ? block.createTileEntity(block.getDefaultState(), Minecraft.getInstance().world) : null;

				if(block instanceof IExplosive)
					blit(startX + 107, 117, 54, 1, 18, 18);

				if(te instanceof IOwnable)
					blit(startX + 29, 118, 1, 1, 16, 16);

				if(te instanceof IPasswordProtected)
					blit(startX + 55, 118, 18, 1, 17, 16);

				if(te instanceof SecurityCraftTileEntity && ((SecurityCraftTileEntity) te).isActivatedByView())
					blit(startX + 81, 118, 36, 1, 17, 16);

				if(te instanceof ICustomizable)
				{
					ICustomizable scte = (ICustomizable)te;

					blit(startX + 213, 118, 72, 1, 16, 16);

					if(scte.customOptions() != null && scte.customOptions().length > 0)
						blit(startX + 136, 118, 88, 1, 16, 16);
				}

				if(te instanceof IModuleInventory)
				{
					if(((IModuleInventory)te).acceptedModules() != null && ((IModuleInventory)te).acceptedModules().length > 0)
						blit(startX + 163, 118, 105, 1, 16, 16);
				}
			}

			for(IngredientDisplay display : displays)
			{
				display.render(minecraft, partialTicks);
			}

			for(int i = 0; i < hoverCheckers.size(); i++)
			{
				HoverChecker chc = hoverCheckers.get(i);

				if(chc != null && chc.checkHover(mouseX, mouseY))
				{
					if(chc instanceof StringHoverChecker && ((StringHoverChecker)chc).getName() != null)
						renderTooltip(((StringHoverChecker)chc).getLines(), mouseX, mouseY);
					else if(i < displays.length && !displays[i].getCurrentStack().isEmpty())
						renderTooltip(displays[i].getCurrentStack(), mouseX, mouseY);
				}
			}
		}
		else //"welcome" page
		{
			String pageNumberText = "1/" + (SCManualItem.PAGES.size() + 1); //+1 because the "welcome" page is not included

			//the patreon link button may overlap with a name tooltip from the list, so draw the list after the buttons
			if(patronList != null)
				patronList.render(mouseX, mouseY, partialTicks);

			font.drawString(pageNumberText, startX + 240 - font.getStringWidth(pageNumberText), 182, 0x8E8270);
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

	protected void actionPerformed(IdButton button){
		if(button.id == 1)
			nextPage();
		else if(button.id == 2)
			previousPage();
		else if(button.id == 3)
			nextSubpage();
		else if(button.id == 4)
			previousSubpage();

		//hide subpage buttons on main page
		buttons.get(2).visible = currentPage != -1 && subpages.size() > 1;
		buttons.get(3).visible = currentPage != -1 && subpages.size() > 1;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scroll)
	{
		super.mouseScrolled(mouseX, mouseY, scroll);

		if(currentPage == -1 && patronList != null && patronList.isMouseOver(mouseX, mouseY))
		{
			patronList.mouseScrolled(mouseX, mouseY, scroll);
			return true;
		}

		switch((int)Math.signum(scroll))
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

		if(currentPage > SCManualItem.PAGES.size() - 1)
			currentPage = -1;

		updateRecipeAndIcons();
	}

	private void previousPage()
	{
		currentPage--;

		if(currentPage < -1)
			currentPage = SCManualItem.PAGES.size() - 1;

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
			recipe = null;
			buttons.get(2).visible = false;
			buttons.get(3).visible = false;

			if(I18n.hasKey("gui.securitycraft:scManual.author"))
				author = font.listFormattedStringToWidth(Utils.localize("gui.securitycraft:scManual.author").getFormattedText(), 180);
			else
				author.clear();

			intro2 = font.listFormattedStringToWidth(Utils.localize("gui.securitycraft:scManual.intro.2").getFormattedText(), 225);
			patronList.fetchPatrons();
			return;
		}

		SCManualPage page = SCManualItem.PAGES.get(currentPage);

		for(IRecipe<?> object : Minecraft.getInstance().world.getRecipeManager().getRecipes())
		{
			if(object instanceof ShapedRecipe){
				ShapedRecipe recipe = (ShapedRecipe) object;

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
			}else if(object instanceof ShapelessRecipe){
				ShapelessRecipe recipe = (ShapelessRecipe) object;

				if(!recipe.getRecipeOutput().isEmpty() && recipe.getRecipeOutput().getItem() == page.getItem()){
					//don't show keycard reset recipes
					if(recipe.getId().getPath().endsWith("_reset"))
						continue;

					NonNullList<Ingredient> recipeItems = NonNullList.<Ingredient>withSize(recipe.getIngredients().size(), Ingredient.EMPTY);

					for(int i = 0; i < recipeItems.size(); i++)
						recipeItems.set(i, recipe.getIngredients().get(i));

					this.recipe = recipeItems;
					break;
				}
			}

			recipe = null;
		}

		String helpInfo = page.getHelpInfo();
		boolean reinforcedPage = helpInfo.equals("help.securitycraft:reinforced.info") || helpInfo.contains("reinforced_hopper");

		if(page.hasRecipeDescription())
		{
			String name = page.getItem().getRegistryName().getPath();

			hoverCheckers.add(new StringHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.recipe." + name).getFormattedText()));
		}
		else if(reinforcedPage)
		{
			recipe = null;
			hoverCheckers.add(new StringHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.recipe.reinforced").getFormattedText()));
		}
		else if(recipe != null)
		{
			for(int i = 0; i < 3; i++)
			{
				for(int j = 0; j < 3; j++)
				{
					hoverCheckers.add(new HoverChecker(144 + (i * 19), 144 + (i * 19) + 16, (startX + 101) + (j * 19), (startX + 101) + (j * 19) + 16));
				}
			}
		}
		else
			hoverCheckers.add(new StringHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.disabled").getFormattedText()));

		Item item = page.getItem();

		if(item instanceof BlockItem){
			Block block = ((BlockItem) item).getBlock();
			TileEntity te = block.hasTileEntity(block.getDefaultState()) ? block.createTileEntity(block.getDefaultState(), Minecraft.getInstance().world) : null;

			if(block instanceof IExplosive)
				hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 107, (startX + 107) + 16, Utils.localize("gui.securitycraft:scManual.explosiveBlock").getFormattedText()));

			if(te != null){
				if(te instanceof IOwnable)
					hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 29, (startX + 29) + 16, Utils.localize("gui.securitycraft:scManual.ownableBlock").getFormattedText()));

				if(te instanceof IPasswordProtected)
					hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 55, (startX + 55) + 16, Utils.localize("gui.securitycraft:scManual.passwordProtectedBlock").getFormattedText()));

				if(te instanceof SecurityCraftTileEntity && ((SecurityCraftTileEntity) te).isActivatedByView())
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
							display.add("- " + Utils.localize("option" + block.getTranslationKey().substring(5) + "." + option.getName() + ".description").getFormattedText());
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

						for(ModuleType module : moduleInv.acceptedModules())
						{
							display.add("- " + Utils.localize("module" + block.getTranslationKey().substring(5) + "." + module.getItem().getTranslationKey().substring(5).replace("securitycraft.", "") + ".description").getFormattedText());
							display.add("");
						}

						display.remove(display.size() - 1);
						hoverCheckers.add(new StringHoverChecker(118, 118 + 16, startX + 163, (startX + 163) + 16, display));
					}
				}
			}
		}

		if(recipe != null && recipe.size() > 0)
		{
			for(int i = 0; i < 3; i++)
			{
				for(int j = 0; j < 3; j++)
				{
					int index = (i * 3) + j;

					if(index >= recipe.size())
						displays[index].setIngredient(Ingredient.EMPTY);
					else
						displays[index].setIngredient(recipe.get(index));
				}
			}
		}
		else
		{
			for(IngredientDisplay display : displays)
			{
				display.setIngredient(Ingredient.EMPTY);
			}
		}

		//set up subpages
		helpInfo = Utils.localize(page.getHelpInfo()).getFormattedText();
		subpages.clear();

		while(font.getStringWidth(helpInfo) > subpageLength)
		{
			String trimmed = font.trimStringToWidth(helpInfo, 1285);
			int lastIndex = trimmed.lastIndexOf(' ');

			if(lastIndex > 0)
				trimmed = trimmed.trim().substring(0, lastIndex); //remove last word to remove the possibility to break it up onto multiple pages

			trimmed = trimmed.trim();
			subpages.add(trimmed);
			helpInfo = helpInfo.replace(trimmed, "").trim();
		}

		subpages.add(helpInfo);
		buttons.get(2).visible = currentPage != -1 && subpages.size() > 1;
		buttons.get(3).visible = currentPage != -1 && subpages.size() > 1;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		if(patronList != null)
			patronList.mouseClicked(mouseX, mouseY, button);

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		if(patronList != null)
			patronList.mouseReleased(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		if(patronList != null)
			patronList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	class PatronList extends ScrollPanel
	{
		private final int slotHeight = 12;
		private final ExecutorService executor = Executors.newSingleThreadExecutor();
		private Future<List<String>> patronRequestFuture;
		private List<String> patrons = new ArrayList<>();
		private boolean patronsAvailable = false;
		private boolean error = false;
		private boolean patronsRequested;
		private final int barWidth = 6;
		private final int barLeft;
		private final List<String> fetchErrorLines;
		private final String loadingText = Utils.localize("gui.securitycraft:scManual.patreon.loading").getFormattedText();

		public PatronList(Minecraft client, int width, int height, int top, int left)
		{
			super(client, width, height, top, left);

			barLeft = left + width - barWidth;
			fetchErrorLines = font.listFormattedStringToWidth(Utils.localize("gui.securitycraft:scManual.patreon.error").getFormattedText(), width);
		}

		@Override
		protected int getContentHeight()
		{
			int height = 50 + (patrons.size() * font.FONT_HEIGHT);

			if(height < bottom - top - 8)
				height = bottom - top - 8;

			return height;
		}

		@Override
		public void render(int mouseX, int mouseY, float partialTicks)
		{
			if(patronsAvailable) //code from ScrollPanel to be able to change colors
			{
				Tessellator tess = Tessellator.getInstance();
				BufferBuilder buffer = tess.getBuffer();
				Minecraft client = Minecraft.getInstance();
				double scale = client.getMainWindow().getGuiScaleFactor();
				int baseY = top + border - (int)scrollDistance;
				int extraHeight = getContentHeight() + border - height;

				GL11.glEnable(GL11.GL_SCISSOR_TEST);
				GL11.glScissor((int)(left  * scale), (int)(client.getMainWindow().getFramebufferHeight() - (bottom * scale)),
						(int)(width * scale), (int)(height * scale));
				drawGradientRect(left, top, right, bottom, 0xC0BFBBB2, 0xD0BFBBB2); //list background
				drawPanel(right, baseY, tess, mouseX, mouseY);
				RenderSystem.disableDepthTest();

				if(extraHeight > 0)
				{
					int barHeight = getBarHeight();
					int barTop = (int)scrollDistance * (height - barHeight) / extraHeight + top;

					if(barTop < top)
						barTop = top;

					//scrollbar background
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
					buffer.pos(barLeft,            bottom, 0.0D).color(0x8E, 0x82, 0x70, 0xFF).endVertex();
					buffer.pos(barLeft + barWidth, bottom, 0.0D).color(0x8E, 0x82, 0x70, 0xFF).endVertex();
					buffer.pos(barLeft + barWidth, top,    0.0D).color(0x8E, 0x82, 0x70, 0xFF).endVertex();
					buffer.pos(barLeft,            top,    0.0D).color(0x8E, 0x82, 0x70, 0xFF).endVertex();
					tess.draw();
					//scrollbar border
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
					buffer.pos(barLeft,            barTop + barHeight, 0.0D).color(0x80, 0x70, 0x55, 0xFF).endVertex();
					buffer.pos(barLeft + barWidth, barTop + barHeight, 0.0D).color(0x80, 0x70, 0x55, 0xFF).endVertex();
					buffer.pos(barLeft + barWidth, barTop,             0.0D).color(0x80, 0x70, 0x55, 0xFF).endVertex();
					buffer.pos(barLeft,            barTop,             0.0D).color(0x80, 0x70, 0x55, 0xFF).endVertex();
					tess.draw();
					//scrollbar
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
					buffer.pos(barLeft,                barTop + barHeight - 1, 0.0D).color(0xD1, 0xBF, 0xA1, 0xFF).endVertex();
					buffer.pos(barLeft + barWidth - 1, barTop + barHeight - 1, 0.0D).color(0xD1, 0xBF, 0xA1, 0xFF).endVertex();
					buffer.pos(barLeft + barWidth - 1, barTop,                 0.0D).color(0xD1, 0xBF, 0xA1, 0xFF).endVertex();
					buffer.pos(barLeft,                barTop,                 0.0D).color(0xD1, 0xBF, 0xA1, 0xFF).endVertex();
					tess.draw();
				}

				RenderSystem.enableTexture();
				RenderSystem.shadeModel(GL11.GL_FLAT);
				RenderSystem.enableAlphaTest();
				RenderSystem.disableBlend();
				GL11.glDisable(GL11.GL_SCISSOR_TEST);

				//draw tooltip for long patron names
				int mouseListY = (int)(mouseY - top + scrollDistance - border);
				int slotIndex = mouseListY / slotHeight;

				if(mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < patrons.size() && mouseY >= top && mouseY <= bottom)
				{
					String patron = patrons.get(slotIndex);
					int length = font.getStringWidth(patron);

					if(length >= width - barWidth)
						renderTooltip(patron, left - 10, baseY + (slotHeight * slotIndex + slotHeight));
				}
			}
			else if(error)
			{
				for(int i = 0; i < fetchErrorLines.size(); i++)
				{
					String line = fetchErrorLines.get(i);

					font.drawString(line, left + width / 2 - font.getStringWidth(line) / 2, top + 30 + i * 10, 0xFFB00101);
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
				font.drawString(loadingText, left + width / 2 - font.getStringWidth(loadingText) / 2, top + 30, 0);
		}

		@Override
		protected void drawPanel(int entryRight, int relativeY, Tessellator tesselator, int mouseX, int mouseY)
		{
			//draw entry strings
			for(int i = 0; i < patrons.size(); i++)
			{
				String patron = patrons.get(i);

				if(patron != null && !patron.isEmpty())
					font.drawString(patron, left + 2, relativeY + (slotHeight * i), 0);
			}
		}

		public void fetchPatrons()
		{
			if(!patronsRequested)
			{
				//create thread to fetch patrons. without this, and for example if the player has no internet connection, the game will hang
				patronRequestFuture = executor.submit(() -> {
					try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://gist.githubusercontent.com/bl4ckscor3/3196e6740774e386871a74a9606eaa61/raw").openStream())))
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

		public int getBarHeight()
		{
			int barHeight = (height * height) / getContentHeight();

			if(barHeight < 32)
				barHeight = 32;

			if(barHeight > height - border * 2)
				barHeight = height - border * 2;

			return barHeight;
		}
	}

	static class ChangePageButton extends IdButton {
		private final int textureY;

		public ChangePageButton(int index, int xPos, int yPos, boolean forward, Consumer<IdButton> onClick){
			super(index, xPos, yPos, 23, 13, "", onClick);
			textureY = forward ? 192 : 205;
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void render(int mouseX, int mouseY, float partialTicks){
			if(visible){
				boolean isHovering = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				Minecraft.getInstance().getTextureManager().bindTexture(bookGuiTextures);
				blit(x, y, isHovering ? 23 : 0, textureY, 23, 13);
			}
		}
	}

	class HyperlinkButton extends Button
	{
		public HyperlinkButton(int xPos, int yPos, int width, int height, String displayString, IPressable handler)
		{
			super(xPos, yPos, width, height, displayString, handler);
		}

		@Override
		public void renderButton(int mouseX, int mouseY, float partial)
		{
			minecraft.getTextureManager().bindTexture(infoBookIcons);
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

			if(isHovered)
				blit(x, y, 138, 1, 16, 16);
			else
				blit(x, y, 122, 1, 16, 16);
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
