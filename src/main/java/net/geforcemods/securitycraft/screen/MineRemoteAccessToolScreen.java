package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.network.server.RemoteControlMine;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.screen.components.PictureButton;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MineRemoteAccessToolScreen extends Screen{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/mrat.png");
	private static final ResourceLocation INFO_BOOK_ICONS = new ResourceLocation("securitycraft:textures/gui/info_book_icons.png"); //for the explosion icon
	private final TranslatableComponent mratName = Utils.localize(SCContent.REMOTE_ACCESS_MINE.get().getDescriptionId());
	private ItemStack mrat;
	private IdButton[][] guiButtons = new IdButton[6][4]; //6 mines, 4 actions (defuse, prime, detonate, unbind)
	private static final int DEFUSE = 0, ACTIVATE = 1, DETONATE = 2, UNBIND = 3;
	private int xSize = 256, ySize = 184;
	private List<TextHoverChecker> hoverCheckers = new ArrayList<>();

	public MineRemoteAccessToolScreen(ItemStack item) {
		super(new TranslatableComponent(item.getDescriptionId()));

		mrat = item;
	}

	@Override
	public void init(){
		super.init();

		int padding = 25;
		int y = padding;
		int[] coords = null;
		int id = 0;

		hoverCheckers.clear();

		for(int i = 0; i < 6; i++)
		{
			y += 30;
			coords = getMineCoordinates(i);
			int startX = (width - xSize) / 2;
			int startY = (height - ySize) / 2;

			// initialize buttons
			for(int j = 0; j < 4; j++)
			{
				int btnX = startX + j * padding + 154;
				int btnY = startY + y - 48;

				switch(j)
				{
					case DEFUSE:
						guiButtons[i][j] = new PictureButton(id++, btnX, btnY, 20, 20, itemRenderer, new ItemStack(SCContent.WIRE_CUTTERS.get()), this::actionPerformed);
						guiButtons[i][j].active = false;
						break;
					case ACTIVATE:
						guiButtons[i][j] = new PictureButton(id++, btnX, btnY, 20, 20, itemRenderer, new ItemStack(Items.FLINT_AND_STEEL), this::actionPerformed);
						guiButtons[i][j].active = false;
						break;
					case DETONATE:
						guiButtons[i][j] = new PictureButton(id++, btnX, btnY, 20, 20, INFO_BOOK_ICONS, 54, 1, 0, 1, 18, 18, 256, 256, this::actionPerformed);
						guiButtons[i][j].active = false;
						break;
					case UNBIND:
						guiButtons[i][j] = new IdButton(id++, btnX, btnY, 20, 20, "X", this::actionPerformed);
						guiButtons[i][j].active = false;
						break;
				}

				addButton(guiButtons[i][j]);
			}

			BlockPos minePos = new BlockPos(coords[0], coords[1], coords[2]);

			if (!(coords[0] == 0 && coords[1] == 0 && coords[2] == 0)) {
				guiButtons[i][UNBIND].active = true;

				if (Minecraft.getInstance().player.level.isLoaded(minePos)) {
					Block block = minecraft.level.getBlockState(minePos).getBlock();

					if (block instanceof IExplosive) {
						boolean active = ((IExplosive) block).isActive(minecraft.level, minePos);
						boolean defusable = ((IExplosive) block).isDefusable();

						guiButtons[i][DEFUSE].active = active && defusable;
						guiButtons[i][ACTIVATE].active = !active && defusable;
						guiButtons[i][DETONATE].active = active;
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][DEFUSE], Utils.localize("gui.securitycraft:mrat.defuse")));
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][ACTIVATE], Utils.localize("gui.securitycraft:mrat.activate")));
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][DETONATE], Utils.localize("gui.securitycraft:mrat.detonate")));
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][UNBIND], Utils.localize("gui.securitycraft:mrat.unbind")));
					}
					else {
						removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2]);

						for (int j = 0; j < 4; j++) {
							guiButtons[i][j].active = false;
						}
					}
				}
				else {
					for (int j = 0; j < 3; j++) {
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][j], Utils.localize("gui.securitycraft:mrat.outOfRange")));
					}

					hoverCheckers.add(new TextHoverChecker(guiButtons[i][UNBIND], Utils.localize("gui.securitycraft:mrat.unbind")));
				}
			}
		}
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(matrix, startX, startY, 0, 0, xSize, ySize);
		super.render(matrix, mouseX, mouseY, partialTicks);
		font.draw(matrix, mratName, startX + xSize / 2 - font.width(mratName), startY + -25 + 13, 0xFF0000);

		for(int i = 0; i < 6; i++)
		{
			int[] coords = getMineCoordinates(i);
			TranslatableComponent line;

			if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0)
				line = Utils.localize("gui.securitycraft:mrat.notBound");
			else
				line = Utils.localize("gui.securitycraft:mrat.mineLocations", new BlockPos(coords[0], coords[1], coords[2]));

			font.draw(matrix, line, startX + xSize / 2 - font.width(line) + 25, startY + i * 30 + 13, 4210752);
		}

		for(TextHoverChecker chc : hoverCheckers)
		{
			if(chc != null && chc.checkHover(mouseX, mouseY) && chc.getName() != null)
				renderComponentTooltip(matrix, chc.getLines(), mouseX, mouseY);
		}
	}

	protected void actionPerformed(IdButton button){
		int mine = button.id / 4;
		int action = button.id % 4;

		int[] coords = getMineCoordinates(mine);

		switch(action)
		{
			case DEFUSE:
				((IExplosive)Minecraft.getInstance().player.level.getBlockState(new BlockPos(coords[0], coords[1], coords[2])).getBlock()).defuseMine(Minecraft.getInstance().player.level, new BlockPos(coords[0], coords[1], coords[2]));
				SecurityCraft.channel.sendToServer(new RemoteControlMine(coords[0], coords[1], coords[2], "defuse"));
				guiButtons[mine][DEFUSE].active = false;
				guiButtons[mine][ACTIVATE].active = true;
				guiButtons[mine][DETONATE].active = false;
				break;
			case ACTIVATE:
				((IExplosive)Minecraft.getInstance().player.level.getBlockState(new BlockPos(coords[0], coords[1], coords[2])).getBlock()).activateMine(Minecraft.getInstance().player.level, new BlockPos(coords[0], coords[1], coords[2]));
				SecurityCraft.channel.sendToServer(new RemoteControlMine(coords[0], coords[1], coords[2], "activate"));
				guiButtons[mine][DEFUSE].active = true;
				guiButtons[mine][ACTIVATE].active = false;
				guiButtons[mine][DETONATE].active = true;
				break;
			case DETONATE:
				SecurityCraft.channel.sendToServer(new RemoteControlMine(coords[0], coords[1], coords[2], "detonate"));
				removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2]);

				for(int i = 0; i < 4; i++)
				{
					guiButtons[mine][i].active = false;
				}

				break;
			case UNBIND:
				removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2]);

				for(int i = 0; i < 4; i++)
				{
					guiButtons[mine][i].active = false;
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
	public boolean isPauseScreen()
	{
		return false;
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(p_keyPressed_1_, p_keyPressed_2_))) {
			this.removed();
			return true;
		}
		return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
	}
}
