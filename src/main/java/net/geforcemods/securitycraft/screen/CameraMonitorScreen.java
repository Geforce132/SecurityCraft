package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.SecurityCraftTileEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.misc.CameraView;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CameraMonitorScreen extends Screen {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private PlayerInventory playerInventory;
	private CameraMonitorItem cameraMonitor;
	private CompoundNBT nbtTag;
	private Button prevPageButton;
	private Button nextPageButton;
	private ClickButton[] cameraButtons = new ClickButton[10];
	private ClickButton[] unbindButtons = new ClickButton[10];
	private HoverChecker[] hoverCheckers = new HoverChecker[10];
	private SecurityCraftTileEntity[] cameraTEs = new SecurityCraftTileEntity[10];
	private int[] cameraViewDim = new int[10];
	private int xSize = 176, ySize = 166;
	private int page = 1;

	public CameraMonitorScreen(PlayerInventory inventory, CameraMonitorItem item, CompoundNBT itemNBTTag) {
		super(new TranslationTextComponent(SCContent.CAMERA_MONITOR.get().getTranslationKey()));
		playerInventory = inventory;
		cameraMonitor = item;
		nbtTag = itemNBTTag;
	}

	public CameraMonitorScreen(PlayerInventory inventory, CameraMonitorItem item, CompoundNBT itemNBTTag, int page) {
		this(inventory, item, itemNBTTag);
		this.page = page;
	}

	@Override
	public void init(){
		super.init();

		prevPageButton = new ClickButton(-1, width / 2 - 68, height / 2 + 40, 20, 20, "<", this::actionPerformed);
		nextPageButton = new ClickButton(0, width / 2 + 52, height / 2 + 40, 20, 20, ">", this::actionPerformed);
		addButton(prevPageButton);
		addButton(nextPageButton);

		cameraButtons[0] = new ClickButton(1, width / 2 - 38, height / 2 - 60 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[1] = new ClickButton(2, width / 2 - 8, height / 2 - 60 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[2] = new ClickButton(3, width / 2 + 22, height / 2 - 60 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[3] = new ClickButton(4, width / 2 - 38, height / 2 - 30 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[4] = new ClickButton(5, width / 2 - 8, height / 2 - 30 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[5] = new ClickButton(6, width / 2 + 22, height / 2 - 30 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[6] = new ClickButton(7, width / 2 - 38, height / 2 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[7] = new ClickButton(8, width / 2 - 8, height / 2 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[8] = new ClickButton(9, width / 2 + 22, height / 2 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[9] = new ClickButton(10, width / 2 - 38, height / 2 + 40, 80, 20, "", this::actionPerformed);

		unbindButtons[0] = new ClickButton(11, width / 2 - 19, height / 2 - 68 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[1] = new ClickButton(12, width / 2 + 11, height / 2 - 68 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[2] = new ClickButton(13, width / 2 + 41, height / 2 - 68 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[3] = new ClickButton(14, width / 2 - 19, height / 2 - 38 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[4] = new ClickButton(15, width / 2 + 11, height / 2 - 38 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[5] = new ClickButton(16, width / 2 + 41, height / 2 - 38 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[6] = new ClickButton(17, width / 2 - 19, height / 2 + 2, 8, 8, "x", this::actionPerformed);
		unbindButtons[7] = new ClickButton(18, width / 2 + 11, height / 2 + 2, 8, 8, "x", this::actionPerformed);
		unbindButtons[8] = new ClickButton(19, width / 2 + 41, height / 2 + 2, 8, 8, "x", this::actionPerformed);
		unbindButtons[9] = new ClickButton(20, width / 2 + 41, height / 2 + 32, 8, 8, "x", this::actionPerformed);

		for(int i = 0; i < 10; i++) {
			ClickButton button = cameraButtons[i];
			int camID = (button.id + ((page - 1) * 10));
			ArrayList<CameraView> views = cameraMonitor.getCameraPositions(nbtTag);
			CameraView view;

			button.setMessage(button.getMessage() + camID);
			addButton(button);

			if((view = views.get(camID - 1)) != null) {
				if(view.dimension != Minecraft.getInstance().player.dimension.getId()) {
					hoverCheckers[button.id - 1] = new HoverChecker(button);
					cameraViewDim[button.id - 1] = view.dimension;
				}

				TileEntity te = Minecraft.getInstance().world.getTileEntity(view.getLocation());

				if(BlockUtils.getBlock(Minecraft.getInstance().world, view.getLocation()) != SCContent.SECURITY_CAMERA.get() || (te instanceof SecurityCameraTileEntity && !((SecurityCameraTileEntity)te).getOwner().isOwner(Minecraft.getInstance().player) && !((SecurityCameraTileEntity)te).hasModule(ModuleType.SMART)))
				{
					button.active = false;
					cameraTEs[button.id - 1] = null;
					continue;
				}

				cameraTEs[button.id - 1] = (SecurityCraftTileEntity) Minecraft.getInstance().world.getTileEntity(view.getLocation());
				hoverCheckers[button.id - 1] = new HoverChecker(button);
			}
			else
			{
				button.active = false;
				unbindButtons[button.id - 1].active = false;
				cameraTEs[button.id - 1] = null;
				continue;
			}
		}

		for(int i = 0; i < 10; i++)
			addButton(unbindButtons[i]);

		if(page == 1)
			prevPageButton.active = false;

		if(page == 3 || cameraMonitor.getCameraPositions(nbtTag).size() < (page * 10) + 1)
			nextPageButton.active = false;

		for(int i = cameraMonitor.getCameraPositions(nbtTag).size() + 1; i <= (page * 10); i++)
			cameraButtons[(i - 1) - ((page - 1) * 10)].active = false;

	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);

		super.render(matrix, mouseX, mouseY, partialTicks);

		font.drawString(ClientUtils.localize("gui.securitycraft:monitor.selectCameras"), startX + xSize / 2 - font.getStringWidth(ClientUtils.localize("gui.securitycraft:monitor.selectCameras")) / 2, startY + 6, 4210752);

		for(int i = 0; i < hoverCheckers.length; i++)
			if(hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY)){
				if(cameraTEs[i] == null)
					this.renderTooltip(font.listFormattedStringToWidth(ClientUtils.localize("gui.securitycraft:monitor.cameraInDifferentDim").replace("#", cameraViewDim[i] + ""), 150), mouseX, mouseY, font);

				if(cameraTEs[i] != null && cameraTEs[i].hasCustomSCName())
					this.renderTooltip(font.listFormattedStringToWidth(ClientUtils.localize("gui.securitycraft:monitor.cameraName").replace("#", cameraTEs[i].getCustomSCName().getString()), 150), mouseX, mouseY, font);
			}
	}

	protected void actionPerformed(ClickButton button) {
		if(button.id == -1)
			minecraft.displayGuiScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page - 1));
		else if(button.id == 0)
			minecraft.displayGuiScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page + 1));
		else if (button.id < 11){
			int camID = button.id + ((page - 1) * 10);

			CameraView view = (cameraMonitor.getCameraPositions(nbtTag).get(camID - 1));

			if(BlockUtils.getBlock(Minecraft.getInstance().world, view.getLocation()) == SCContent.SECURITY_CAMERA.get()) {
				((SecurityCameraBlock) BlockUtils.getBlock(Minecraft.getInstance().world, view.getLocation())).mountCamera(Minecraft.getInstance().world, view.x, view.y, view.z, camID, Minecraft.getInstance().player);
				SecurityCraft.channel.sendToServer(new MountCamera(view.x, view.y, view.z, camID));
				Minecraft.getInstance().player.closeScreen();
			}
			else
				button.active = false;
		}
		else
		{
			int camID = (button.id - 10) + ((page - 1) * 10);

			SecurityCraft.channel.sendToServer(new RemoveCameraTag(playerInventory.getCurrentItem(), camID));
			nbtTag.remove(CameraMonitorItem.getTagNameFromPosition(nbtTag, cameraMonitor.getCameraPositions(nbtTag).get(camID - 1)));
			button.active = false;
			cameraButtons[(camID - 1) % 10].active = false;
		}
	}

	@Override
	public boolean isPauseScreen(){
		return false;
	}

}