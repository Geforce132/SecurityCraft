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

import com.mojang.blaze3d.matrix.MatrixStack;
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
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
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
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ScrollPanel;
import net.minecraftforge.fml.client.gui.GuiUtils;

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
	private List<ITextProperties> subpages = new ArrayList<>();
	private List<IReorderingProcessor> author = new ArrayList<>();
	private int currentSubpage = 0;
	private final int subpageLength = 1285;
	private final IFormattableTextComponent intro1 = Utils.localize("gui.securitycraft:scManual.intro.1").setStyle(Style.EMPTY.setUnderlined(true));
	private final TranslationTextComponent ourPatrons = Utils.localize("gui.securitycraft:scManual.patreon.title");
	private List<IReorderingProcessor> intro2;
	private PatronList patronList;
	private Button patreonLinkButton;

	public SCManualScreen() {
		super(new TranslationTextComponent(SCContent.SC_MANUAL.get().getTranslationKey()));
	}

	@Override
	public void init(){
		byte startY = 2;

		startX = (width - 256) / 2;
		minecraft.keyboardListener.enableRepeatEvents(true);
		addButton(new SCManualScreen.ChangePageButton(1, startX + 210, startY + 188, true, this::actionPerformed)); //next page
		addButton(new SCManualScreen.ChangePageButton(2, startX + 16, startY + 188, false, this::actionPerformed)); //previous page
		addButton(new SCManualScreen.ChangePageButton(3, startX + 180, startY + 97, true, this::actionPerformed)); //next subpage
		addButton(new SCManualScreen.ChangePageButton(4, startX + 155, startY + 97, false, this::actionPerformed)); //previous subpage
		addButton(patreonLinkButton = new HyperlinkButton(startX + 225, 143, 16, 16, StringTextComponent.EMPTY, b -> handleComponentClicked(Style.EMPTY.setClickEvent(new ClickEvent(Action.OPEN_URL, "https://www.patreon.com/Geforce")))));
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
			String key1 = Utils.localize(page1.getItem().getTranslationKey()).getString();
			String key2 = Utils.localize(page2.getItem().getTranslationKey()).getString();

			return key1.compareTo(key2);
		});
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		if(currentPage == -1)
			minecraft.getTextureManager().bindTexture(infoBookTitlePage);
		else if(recipe != null && recipe.size() > 0)
			minecraft.getTextureManager().bindTexture(infoBookTexture);
		else
			minecraft.getTextureManager().bindTexture(infoBookTextureSpecial);

		blit(matrix, startX, 5, 0, 0, 256, 250);

		for(int i = 0; i < buttons.size(); i++)
		{
			buttons.get(i).render(matrix, mouseX, mouseY, partialTicks);
		}

		if(currentPage > -1)
		{
			Item item = SCManualItem.PAGES.get(currentPage).getItem();
			String pageNumberText = (currentPage + 2) + "/" + (SCManualItem.PAGES.size() + 1); //+1 because the "welcome" page is not included
			String designedBy = SCManualItem.PAGES.get(currentPage).getDesignedBy();

			if(subpages.size() > 1)
				font.drawString(matrix, (currentSubpage + 1) + "/" + subpages.size(), startX + 205, 102, 0x8E8270);

			if(designedBy != null && !designedBy.isEmpty())
				font.func_238418_a_(Utils.localize("gui.securitycraft:scManual.designedBy", designedBy), startX + 18, 150, 75, 0);

			if(SCManualItem.PAGES.get(currentPage).getHelpInfo().getKey().equals("help.securitycraft:reinforced.info"))
				font.drawText(matrix, Utils.localize("gui.securitycraft:scManual.reinforced"), startX + 39, 27, 0);
			else
				font.drawText(matrix, Utils.localize(SCManualItem.PAGES.get(currentPage).getItem().getTranslationKey()), startX + 39, 27, 0);

			font.func_238418_a_(subpages.get(currentSubpage), startX + 18, 45, 225, 0);
			font.drawString(matrix, pageNumberText, startX + 240 - font.getStringWidth(pageNumberText), 182, 0x8E8270);
			minecraft.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(item), startX + 19, 22);
			minecraft.getTextureManager().bindTexture(infoBookIcons);

			if(item instanceof BlockItem){
				Block block = ((BlockItem) item).getBlock();

				if(block instanceof IExplosive)
					blit(matrix, startX + 107, 117, 54, 1, 18, 18);

				if(block.hasTileEntity(block.getDefaultState()))
				{
					TileEntity te = block.createTileEntity(block.getDefaultState(), Minecraft.getInstance().world);

					if(te instanceof IOwnable)
						blit(matrix, startX + 29, 118, 1, 1, 16, 16);

					if(te instanceof IPasswordProtected)
						blit(matrix, startX + 55, 118, 18, 1, 17, 16);

					if(te instanceof SecurityCraftTileEntity && ((SecurityCraftTileEntity) te).isActivatedByView())
						blit(matrix, startX + 81, 118, 36, 1, 17, 16);

					if(te instanceof ICustomizable)
					{
						ICustomizable scte = (ICustomizable)te;

						blit(matrix, startX + 213, 118, 72, 1, 16, 16);

						if(scte.customOptions() != null && scte.customOptions().length > 0)
							blit(matrix, startX + 136, 118, 88, 1, 16, 16);
					}

					if(te instanceof IModuleInventory)
					{
						if(((IModuleInventory)te).acceptedModules() != null && ((IModuleInventory)te).acceptedModules().length > 0)
							blit(matrix, startX + 163, 118, 105, 1, 16, 16);
					}
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
					if(chc instanceof TextHoverChecker && ((TextHoverChecker)chc).getName() != null)
						GuiUtils.drawHoveringText(matrix, ((TextHoverChecker)chc).getLines(), mouseX, mouseY, width, height, -1, font);
					else if(i < displays.length && !displays[i].getCurrentStack().isEmpty())
						renderTooltip(matrix, displays[i].getCurrentStack(), mouseX, mouseY);
				}
			}
		}
		else //"welcome" page
		{
			String pageNumberText = "1/" + (SCManualItem.PAGES.size() + 1); //+1 because the "welcome" page is not included

			font.drawText(matrix, intro1, width / 2 - font.getStringPropertyWidth(intro1) / 2, 22, 0);

			for(int i = 0; i < intro2.size(); i++)
			{
				IReorderingProcessor text = intro2.get(i);

				font.func_238422_b_(matrix, text, width / 2 - font.func_243245_a(text) / 2, 150 + 10 * i, 0);
			}

			for(int i = 0; i < author.size(); i++)
			{
				IReorderingProcessor text = author.get(i);

				font.func_238422_b_(matrix, text, width / 2 - font.func_243245_a(text) / 2, 180 + 10 * i, 0);
			}

			//the patreon link button may overlap with a name tooltip from the list, so draw the list after the buttons
			if(patronList != null)
				patronList.render(matrix, mouseX, mouseY, partialTicks);

			font.drawString(matrix, pageNumberText, startX + 240 - font.getStringWidth(pageNumberText), 182, 0x8E8270);
			font.drawText(matrix, ourPatrons, width / 2 - font.getStringPropertyWidth(ourPatrons) / 2 + 30, 40, 0);
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

		if(currentPage == -1 && patronList != null && patronList.isMouseOver(mouseX, mouseY) && !patronList.patrons.isEmpty())
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
				author = font.trimStringToWidth(Utils.localize("gui.securitycraft:scManual.author"), 180);
			else
				author.clear();

			intro2 = font.trimStringToWidth(Utils.localize("gui.securitycraft:scManual.intro.2"), 225);

			patronList.fetchPatrons();
			return;
		}

		SCManualPage page = SCManualItem.PAGES.get(currentPage);

		recipe = null;

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
		}

		TranslationTextComponent helpInfo = page.getHelpInfo();
		boolean reinforcedPage = helpInfo.getKey().equals("help.securitycraft:reinforced.info") || helpInfo.getKey().contains("reinforced_hopper");

		if(page.hasRecipeDescription())
		{
			String name = page.getItem().getRegistryName().getPath();

			hoverCheckers.add(new TextHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.recipe." + name)));
		}
		else if(reinforcedPage)
		{
			recipe = null;
			hoverCheckers.add(new TextHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.recipe.reinforced")));
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
			hoverCheckers.add(new TextHoverChecker(144, 144 + (2 * 20) + 16, startX + 100, (startX + 100) + (2 * 20) + 16, Utils.localize("gui.securitycraft:scManual.disabled")));

		Item item = page.getItem();

		if(item instanceof BlockItem){
			Block block = ((BlockItem) item).getBlock();

			if(block instanceof IExplosive)
				hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 107, (startX + 107) + 16, Utils.localize("gui.securitycraft:scManual.explosiveBlock")));

			if(block.hasTileEntity(block.getDefaultState()))
			{
				TileEntity te = block.createTileEntity(block.getDefaultState(), Minecraft.getInstance().world);

				if(te instanceof IOwnable)
					hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 29, (startX + 29) + 16, Utils.localize("gui.securitycraft:scManual.ownableBlock")));

				if(te instanceof IPasswordProtected)
					hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 55, (startX + 55) + 16, Utils.localize("gui.securitycraft:scManual.passwordProtectedBlock")));

				if(te instanceof SecurityCraftTileEntity && ((SecurityCraftTileEntity) te).isActivatedByView())
					hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 81, (startX + 81) + 16, Utils.localize("gui.securitycraft:scManual.viewActivatedBlock")));

				if(te instanceof ICustomizable)
				{
					ICustomizable scte = (ICustomizable)te;

					hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 213, (startX + 213) + 16, Utils.localize("gui.securitycraft:scManual.customizableBlock")));

					if(scte.customOptions() != null && scte.customOptions().length > 0)
					{
						List<ITextComponent> display = new ArrayList<>();

						display.add(Utils.localize("gui.securitycraft:scManual.options"));
						display.add(new StringTextComponent("---"));

						for(Option<?> option : scte.customOptions())
						{
							display.add(new StringTextComponent("- ").appendSibling(Utils.localize("option" + block.getTranslationKey().substring(5) + "." + option.getName() + ".description")));
							display.add(StringTextComponent.EMPTY);
						}

						display.remove(display.size() - 1);
						hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 136, (startX + 136) + 16, display));
					}
				}

				if(te instanceof IModuleInventory)
				{
					IModuleInventory moduleInv = (IModuleInventory)te;

					if(moduleInv.acceptedModules() != null && moduleInv.acceptedModules().length > 0)
					{
						List<ITextComponent> display = new ArrayList<>();

						display.add(Utils.localize("gui.securitycraft:scManual.modules"));
						display.add(new StringTextComponent("---"));

						for(ModuleType module : moduleInv.acceptedModules())
						{
							display.add(new StringTextComponent("- ").appendSibling(Utils.localize("module" + block.getTranslationKey().substring(5) + "." + module.getItem().getTranslationKey().substring(5).replace("securitycraft.", "") + ".description")));
							display.add(StringTextComponent.EMPTY);
						}

						display.remove(display.size() - 1);
						hoverCheckers.add(new TextHoverChecker(118, 118 + 16, startX + 163, (startX + 163) + 16, display));
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
		subpages = font.getCharacterManager().func_238362_b_(helpInfo, subpageLength, Style.EMPTY);
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
		private final List<IReorderingProcessor> fetchErrorLines;
		private final List<IReorderingProcessor> noPatronsLines;
		private final ITextComponent loadingText = Utils.localize("gui.securitycraft:scManual.patreon.loading");

		public PatronList(Minecraft client, int width, int height, int top, int left)
		{
			super(client, width, height, top, left);

			barLeft = left + width - barWidth;
			fetchErrorLines = font.trimStringToWidth(Utils.localize("gui.securitycraft:scManual.patreon.error"), width);
			noPatronsLines = font.trimStringToWidth(Utils.localize("advancements.empty"), width - 10);
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
		public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
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
				drawGradientRect(matrix, left, top, right, bottom, 0xC0BFBBB2, 0xD0BFBBB2); //list background
				drawPanel(matrix, right, baseY, tess, mouseX, mouseY);
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
						renderTooltip(matrix, new StringTextComponent(patron), left - 10, baseY + (slotHeight * slotIndex + slotHeight));
				}

				if (patrons.isEmpty()) {
					for(int i = 0; i < noPatronsLines.size(); i++) {
						IReorderingProcessor line = noPatronsLines.get(i);

						font.func_238422_b_(matrix, line, left + width / 2 - font.func_243245_a(line) / 2, top + 30 + i * 10, 0xFF333333);
					}
				}
			}
			else if(error)
			{
				for(int i = 0; i < fetchErrorLines.size(); i++)
				{
					IReorderingProcessor line = fetchErrorLines.get(i);

					font.func_238422_b_(matrix, line, left + width / 2 - font.func_243245_a(line) / 2, top + 30 + i * 10, 0xFFB00101);
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
				font.drawText(matrix, loadingText, left + width / 2 - font.getStringPropertyWidth(loadingText) / 2, top + 30, 0);
		}

		@Override
		protected void drawPanel(MatrixStack matrix, int entryRight, int relativeY, Tessellator tesselator, int mouseX, int mouseY)
		{
			//draw entry strings
			for(int i = 0; i < patrons.size(); i++)
			{
				String patron = patrons.get(i);

				if(patron != null && !patron.isEmpty())
					font.drawString(matrix, patron, left + 2, relativeY + (slotHeight * i), 0);
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
		public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
			if(visible){
				boolean isHovering = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				Minecraft.getInstance().getTextureManager().bindTexture(bookGuiTextures);
				blit(matrix, x, y, isHovering ? 23 : 0, textureY, 23, 13);
			}
		}
	}

	class HyperlinkButton extends Button
	{
		public HyperlinkButton(int xPos, int yPos, int width, int height, ITextComponent displayString, IPressable handler)
		{
			super(xPos, yPos, width, height, displayString, handler);
		}

		@Override
		public void renderWidget(MatrixStack matrix, int mouseX, int mouseY, float partial)
		{
			minecraft.getTextureManager().bindTexture(infoBookIcons);
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

			if(isHovered)
				blit(matrix, x, y, 138, 1, 16, 16);
			else
				blit(matrix, x, y, 122, 1, 16, 16);
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
