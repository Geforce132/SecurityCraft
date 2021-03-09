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
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CameraMonitorScreen extends Screen {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslationTextComponent selectCameras = ClientUtils.localize("gui.securitycraft:monitor.selectCameras");
	private PlayerInventory playerInventory;
	private CameraMonitorItem cameraMonitor;
	private CompoundNBT nbtTag;
	private IdButton prevPageButton;
	private IdButton nextPageButton;
	private IdButton[] cameraButtons = new IdButton[10];
	private IdButton[] unbindButtons = new IdButton[10];
	private HoverChecker[] hoverCheckers = new HoverChecker[10];
	private SecurityCraftTileEntity[] cameraTEs = new SecurityCraftTileEntity[10];
	private ResourceLocation[] cameraViewDim = new ResourceLocation[10];
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

		addButton(prevPageButton = new IdButton(-1, width / 2 - 68, height / 2 + 40, 20, 20, "<", this::actionPerformed));
		addButton(nextPageButton = new IdButton(0, width / 2 + 52, height / 2 + 40, 20, 20, ">", this::actionPerformed));

		cameraButtons[0] = new IdButton(1, width / 2 - 38, height / 2 - 60 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[1] = new IdButton(2, width / 2 - 8, height / 2 - 60 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[2] = new IdButton(3, width / 2 + 22, height / 2 - 60 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[3] = new IdButton(4, width / 2 - 38, height / 2 - 30 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[4] = new IdButton(5, width / 2 - 8, height / 2 - 30 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[5] = new IdButton(6, width / 2 + 22, height / 2 - 30 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[6] = new IdButton(7, width / 2 - 38, height / 2 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[7] = new IdButton(8, width / 2 - 8, height / 2 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[8] = new IdButton(9, width / 2 + 22, height / 2 + 10, 20, 20, "", this::actionPerformed);
		cameraButtons[9] = new IdButton(10, width / 2 - 38, height / 2 + 40, 80, 20, "", this::actionPerformed);

		unbindButtons[0] = new IdButton(11, width / 2 - 19, height / 2 - 68 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[1] = new IdButton(12, width / 2 + 11, height / 2 - 68 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[2] = new IdButton(13, width / 2 + 41, height / 2 - 68 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[3] = new IdButton(14, width / 2 - 19, height / 2 - 38 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[4] = new IdButton(15, width / 2 + 11, height / 2 - 38 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[5] = new IdButton(16, width / 2 + 41, height / 2 - 38 + 10, 8, 8, "x", this::actionPerformed);
		unbindButtons[6] = new IdButton(17, width / 2 - 19, height / 2 + 2, 8, 8, "x", this::actionPerformed);
		unbindButtons[7] = new IdButton(18, width / 2 + 11, height / 2 + 2, 8, 8, "x", this::actionPerformed);
		unbindButtons[8] = new IdButton(19, width / 2 + 41, height / 2 + 2, 8, 8, "x", this::actionPerformed);
		unbindButtons[9] = new IdButton(20, width / 2 + 41, height / 2 + 32, 8, 8, "x", this::actionPerformed);

		for(int i = 0; i < 10; i++) {
			IdButton button = cameraButtons[i];
			int camID = (button.id + ((page - 1) * 10));
			ArrayList<CameraView> views = cameraMonitor.getCameraPositions(nbtTag);
			CameraView view;

			button.setMessage(button.getMessage().copyRaw().appendSibling(new StringTextComponent("" + camID)));
			addButton(button);

			if((view = views.get(camID - 1)) != null) {
				if(!view.dimension.equals(Minecraft.getInstance().player.world.getDimensionKey().getLocation())) {
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
		this.blit(matrix, startX, startY, 0, 0, xSize, ySize);

		super.render(matrix, mouseX, mouseY, partialTicks);

		font.drawText(matrix, selectCameras, startX + xSize / 2 - font.getStringPropertyWidth(selectCameras) / 2, startY + 6, 4210752);

		for(int i = 0; i < hoverCheckers.length; i++)
			if(hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY)){
				if(cameraTEs[i] == null)
					renderTooltip(matrix, font.trimStringToWidth(ClientUtils.localize("gui.securitycraft:monitor.cameraInDifferentDim", cameraViewDim[i]), 150), mouseX, mouseY);

				if(cameraTEs[i] != null && cameraTEs[i].hasCustomSCName())
					renderTooltip(matrix, font.trimStringToWidth(ClientUtils.localize("gui.securitycraft:monitor.cameraName", cameraTEs[i].getCustomSCName()), 150), mouseX, mouseY);
			}
	}

	protected void actionPerformed(IdButton button) {
		if(button.id == prevPageButton.id)
			minecraft.displayGuiScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page - 1));
		else if(button.id == nextPageButton.id)
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

			SecurityCraft.channel.sendToServer(new RemoveCameraTag(PlayerUtils.getSelectedItemStack(playerInventory, SCContent.CAMERA_MONITOR.get()), camID));
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