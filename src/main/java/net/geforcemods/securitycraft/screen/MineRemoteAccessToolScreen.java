package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.network.server.SetExplosiveState;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.PictureButton;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MineRemoteAccessToolScreen extends Screen{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/mrat.png");
	private static final ResourceLocation INFO_BOOK_ICONS = new ResourceLocation("securitycraft:textures/gui/info_book_icons.png"); //for the explosion icon
	private ItemStack mrat;
	private ClickButton[][] guiButtons = new ClickButton[6][4]; //6 mines, 4 actions (defuse, prime, detonate, unbind)
	private static final int DEFUSE = 0, ACTIVATE = 1, DETONATE = 2, UNBIND = 3;
	private int xSize = 256, ySize = 184;
	private List<TextHoverChecker> hoverCheckers = new ArrayList<>();

	public MineRemoteAccessToolScreen(ItemStack item) {
		super(new TranslationTextComponent(item.getTranslationKey()));

		mrat = item;
	}

	@Override
	public void func_231160_c_(){
		super.func_231160_c_();

		int padding = 25;
		int y = padding;
		int[] coords = null;
		int id = 0;
		hoverCheckers.clear();

		for(int i = 0; i < 6; i++)
		{
			y += 30;
			coords = getMineCoordinates(i);
			int startX = (field_230708_k_ - xSize) / 2;
			int startY = (field_230709_l_ - ySize) / 2;

			// initialize buttons
			for(int j = 0; j < 4; j++)
			{
				int btnX = startX + j * padding + 154;
				int btnY = startY + y - 48;

				switch(j)
				{
					case DEFUSE:
						guiButtons[i][j] = new PictureButton(id++, btnX, btnY, 20, 20, field_230707_j_, new ItemStack(SCContent.WIRE_CUTTERS.get()), this::actionPerformed);
						guiButtons[i][j].field_230693_o_ = false;
						break;
					case ACTIVATE:
						guiButtons[i][j] = new PictureButton(id++, btnX, btnY, 20, 20, field_230707_j_, new ItemStack(Items.FLINT_AND_STEEL), this::actionPerformed);
						guiButtons[i][j].field_230693_o_ = false;
						break;
					case DETONATE:
						guiButtons[i][j] = new PictureButton(id++, btnX, btnY, 20, 20, INFO_BOOK_ICONS, 54, 1, 18, 18, this::actionPerformed);
						guiButtons[i][j].field_230693_o_ = false;
						break;
					case UNBIND:
						guiButtons[i][j] = new ClickButton(id++, btnX, btnY, 20, 20, "X", this::actionPerformed);
						guiButtons[i][j].field_230693_o_ = false;
						break;
				}

				func_230480_a_(guiButtons[i][j]);
			}

			BlockPos minePos = new BlockPos(coords[0], coords[1], coords[2]);
			if (!(coords[0] == 0 && coords[1] == 0 && coords[2] == 0)) {
				guiButtons[i][UNBIND].field_230693_o_ = true;
				if (Minecraft.getInstance().player.world.isBlockPresent(minePos)) {
					Block block = field_230706_i_.world.getBlockState(minePos).getBlock();
					if (block instanceof IExplosive) {
						boolean field_230693_o_ = ((IExplosive) block).isActive(field_230706_i_.world, minePos);
						boolean defusable = ((IExplosive) block).isDefusable();

						guiButtons[i][DEFUSE].field_230693_o_ = field_230693_o_ && defusable;
						guiButtons[i][ACTIVATE].field_230693_o_ = !field_230693_o_ && defusable;
						guiButtons[i][DETONATE].field_230693_o_ = field_230693_o_;
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][DEFUSE], ClientUtils.localize("gui.securitycraft:mrat.defuse")));
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][ACTIVATE], ClientUtils.localize("gui.securitycraft:mrat.activate")));
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][DETONATE], ClientUtils.localize("gui.securitycraft:mrat.detonate")));
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][UNBIND], ClientUtils.localize("gui.securitycraft:mrat.unbind")));
					}
					else {
						removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2]);
						for (int j = 0; j < 4; j++) {
							guiButtons[i][j].field_230693_o_ = false;
						}
					}
				}
				else {
					for (int j = 0; j < 3; j++) {
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][j], ClientUtils.localize("gui.securitycraft:mrat.outOfRange")));
					}
					hoverCheckers.add(new TextHoverChecker(guiButtons[i][UNBIND], ClientUtils.localize("gui.securitycraft:mrat.unbind")));
				}
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		func_230446_a_();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		field_230706_i_.getTextureManager().bindTexture(TEXTURE);
		int startX = (field_230708_k_ - xSize) / 2;
		int startY = (field_230709_l_ - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
		super.render(mouseX, mouseY, partialTicks);
		String mratName = ClientUtils.localize(SCContent.REMOTE_ACCESS_MINE.get().getTranslationKey());
		field_230712_o_.drawString(mratName, startX + xSize / 2 - field_230712_o_.getStringWidth(mratName), startY + -25 + 13, 0xFF0000);

		for(int i = 0; i < 6; i++)
		{
			int[] coords = getMineCoordinates(i);
			String line;

			if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0)
				line = ClientUtils.localize("gui.securitycraft:mrat.notBound");
			else
				line = ClientUtils.localize("gui.securitycraft:mrat.mineLocations").replace("#location", Utils.getFormattedCoordinates(new BlockPos(coords[0], coords[1], coords[2])));

			field_230712_o_.drawString(line, startX + xSize / 2 - field_230712_o_.getStringWidth(line) + 25, startY + i * 30 + 13, 4210752);
		}

		for(TextHoverChecker chc : hoverCheckers)
		{
			if(chc != null && chc.checkHover(mouseX, mouseY) && chc.getName() != null)
				renderTooltip(chc.getLines(), mouseX, mouseY);
		}
	}

	protected void actionPerformed(ClickButton button){
		int mine = button.id / 4;
		int action = button.id % 4;

		int[] coords = getMineCoordinates(mine);

		switch(action)
		{
			case DEFUSE:
				((IExplosive)Minecraft.getInstance().player.world.getBlockState(new BlockPos(coords[0], coords[1], coords[2])).getBlock()).defuseMine(Minecraft.getInstance().player.world, new BlockPos(coords[0], coords[1], coords[2]));
				SecurityCraft.channel.sendToServer(new SetExplosiveState(coords[0], coords[1], coords[2], "defuse"));
				guiButtons[mine][DEFUSE].field_230693_o_ = false;
				guiButtons[mine][ACTIVATE].field_230693_o_ = true;
				guiButtons[mine][DETONATE].field_230693_o_ = false;
				break;
			case ACTIVATE:
				((IExplosive)Minecraft.getInstance().player.world.getBlockState(new BlockPos(coords[0], coords[1], coords[2])).getBlock()).activateMine(Minecraft.getInstance().player.world, new BlockPos(coords[0], coords[1], coords[2]));
				SecurityCraft.channel.sendToServer(new SetExplosiveState(coords[0], coords[1], coords[2], "activate"));
				guiButtons[mine][DEFUSE].field_230693_o_ = true;
				guiButtons[mine][ACTIVATE].field_230693_o_ = false;
				guiButtons[mine][DETONATE].field_230693_o_ = true;
				break;
			case DETONATE:
				SecurityCraft.channel.sendToServer(new SetExplosiveState(coords[0], coords[1], coords[2], "detonate"));
				removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2]);

				for(int i = 0; i < 4; i++)
				{
					guiButtons[mine][i].field_230693_o_ = false;
				}

				break;
			case UNBIND:
				removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2]);

				for(int i = 0; i < 4; i++)
				{
					guiButtons[mine][i].field_230693_o_ = false;
				}
		}
	}

	/**
	 * @param mine 0 based
	 */
	private int[] getMineCoordinates(int mine)
	{
		mine++; //mines are stored starting by mine1 up to mine6

		if(mrat.getItem() != null && mrat.getItem() == SCContent.REMOTE_ACCESS_MINE.get() && mrat.getTag() != null &&  mrat.getTag().getIntArray("mine" + mine) != null && mrat.getTag().getIntArray("mine" + mine).length > 0)
			return mrat.getTag().getIntArray("mine" + mine);
		else
			return new int[] {0,0,0};
	}

	private void removeTagFromToolAndUpdate(ItemStack stack, int x, int y, int z)
	{
		if(stack.getTag() == null)
			return;

		for(int i = 1; i <= 6; i++)
		{
			if(stack.getTag().getIntArray("mine" + i).length > 0)
			{
				int[] coords = stack.getTag().getIntArray("mine" + i);

				if(coords[0] == x && coords[1] == y && coords[2] == z && !(coords[0] == 0 && coords[1] == 0 && coords[2] == 0))
				{
					stack.getTag().putIntArray("mine" + i, new int[]{0, 0, 0});
					SecurityCraft.channel.sendToServer(new UpdateNBTTagOnServer(stack));
					return;
				}
			}
		}
	}

	@Override
	public boolean func_231177_au__()
	{
		return false;
	}

	@Override
	public boolean func_231046_a_(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (field_230706_i_.gameSettings.keyBindInventory.isActiveAndMatches(InputMappings.getInputByCode(p_keyPressed_1_, p_keyPressed_2_))) {
			this.func_231175_as__();
			return true;
		}
		return super.func_231046_a_(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}
}
