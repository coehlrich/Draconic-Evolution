package com.brandon3055.draconicevolution.client.gui;

import com.brandon3055.brandonscore.client.gui.effects.GuiEffect;
import com.brandon3055.brandonscore.client.gui.modulargui.MGuiElementBase;
import com.brandon3055.brandonscore.client.gui.modulargui.ModularGuiContainer;
import com.brandon3055.brandonscore.client.gui.modulargui.lib.IMGuiListener;
import com.brandon3055.brandonscore.client.gui.modulargui.modularelements.*;
import com.brandon3055.brandonscore.client.utils.GuiHelper;
import com.brandon3055.brandonscore.network.PacketTileMessage;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.blocks.tileentity.TileCelestialManipulator;
import com.brandon3055.draconicevolution.client.DEParticles;
import com.brandon3055.draconicevolution.inventory.ContainerDummy;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 17/10/2016.
 */
public class GuiCelestialManipulator extends ModularGuiContainer<ContainerDummy> implements IMGuiListener {

    private EntityPlayer player;
    private TileCelestialManipulator tile;
    private MGuiEffectRenderer effectRenderer;
    private MGuiButtonSolid weatherMode;
    private MGuiButtonSolid sunMode;
    private List<MGuiElementBase> weatherControls = new ArrayList<>();
    private List<MGuiElementBase> sunControls = new ArrayList<>();
    private MGuiEnergyBar energyBar;

    public GuiCelestialManipulator(EntityPlayer player, TileCelestialManipulator tile) {
        super(new ContainerDummy(tile, player, 10, 120));

        this.xSize = 180;
        this.ySize = 200;

        this.player = player;
        this.tile = tile;
    }

    @Override
    public void initGui() {
        super.initGui();

        manager.clear();
        weatherControls.clear();
        sunControls.clear();
        manager.add(MGuiBackground.newGenericBackground(this, guiLeft, guiTop + 97, xSize, ySize - 97));
        manager.add(new MGuiLabel(this, guiLeft, guiTop - 12, xSize, 12, DEFeatures.celestialManipulator.getLocalizedName()).setTextColour(InfoHelper.GUI_TITLE));
        manager.add(new MGuiElementBase(this) {
            @Override
            public void renderBackgroundLayer(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
                GlStateManager.color(1, 1, 1);
                GuiHelper.drawPlayerSlots(GuiCelestialManipulator.this, guiLeft + (GuiCelestialManipulator.this.xSize / 2), guiTop + 119, true);
            }
        });
        manager.add(effectRenderer = new MGuiEffectRenderer(this).setParticleTexture(DEParticles.DE_SHEET.toString()));
        manager.add(weatherMode = new MGuiButtonSolid(this, "WEATHER_MODE", guiLeft + 5, guiTop + 1, 50, 12, I18n.format("gui.de.celMod.weather")){
            @Override
            public int getTextColour(boolean hovered, boolean disabled) {
                return disabled ? 0x90600000 : hovered ? 0x9080FFFF : 0x9060FF60;
            }

            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return disabled ? 0x9000FF00 : hovering ? 0x9000FF00 : 0x9000FFFF;
            }

        }.setColours(0x90000000, 0x90111111, 0x90222222));
        manager.add(sunMode = new MGuiButtonSolid(this, "SUN_MODE", guiLeft + xSize - 55, guiTop + 1, 50, 12, I18n.format("gui.de.celMod.time")){
            @Override
            public int getTextColour(boolean hovered, boolean disabled) {
                return disabled ? 0x90600000 : hovered ? 0x9080FFFF : 0x9060FF60;
            }

            @Override
            protected int getBorderColour(boolean hovering, boolean disabled) {
                return disabled ? 0x9000FF00 : hovering ? 0x9000FF00 : 0x9000FFFF;
            }

        }.setColours(0x90000000, 0x90111111, 0x90222222));
        manager.add(energyBar = new MGuiEnergyBar(this, guiLeft + 9, guiTop + 102, xSize - 18, 14).setEnergy(tile.getEnergyStored(EnumFacing.UP), tile.getMaxEnergyStored(EnumFacing.UP)).setHorizontal(true), 4);
        manager.add(new MGuiBorderedRect(this, guiLeft + 2, guiTop - 1, xSize - 4, ySize - 102).setFillColour(0x40000000).setBorderColour(0x90000000));

        int i = 26;
        weatherControls.add(new MGuiButtonSolid(this, "STOP_RAIN", guiLeft + 4, guiTop + i, xSize - 8, 14, I18n.format("gui.de.celMod.stopRain")));
        weatherControls.add(new MGuiButtonSolid(this, "START_RAIN", guiLeft + 4, guiTop + (i += 22), xSize - 8, 14, I18n.format("gui.de.celMod.startRain")));
        weatherControls.add(new MGuiButtonSolid(this, "START_STORM", guiLeft + 4, guiTop + (i += 22), xSize - 8, 14, I18n.format("gui.de.celMod.startStorm")));

        i = 20;
        sunControls.add(new MGuiLabel(this, guiLeft, guiTop + i, xSize, 12, I18n.format("gui.de.celMod.skipTo")));
        i += 12;
        sunControls.add(new MGuiButtonSolid(this, "SUN_RISE", guiLeft + 4, guiTop + i, xSize / 3 - 4, 14, I18n.format("gui.de.celMod.sunRise")));
        sunControls.add(new MGuiButtonSolid(this, "MID_DAY", guiLeft + 4 + xSize / 3 - 2, guiTop + i, xSize / 3 - 4, 14, I18n.format("gui.de.celMod.midDay")));
        sunControls.add(new MGuiButtonSolid(this, "SUN_SET", guiLeft + 4 + (xSize / 3) * 2 - 4, guiTop + i, xSize / 3 - 4, 14, I18n.format("gui.de.celMod.sunSet")));
        i += 20;
        sunControls.add(new MGuiButtonSolid(this, "MOON_RISE", guiLeft + 4, guiTop + i, xSize / 3 - 4, 14, I18n.format("gui.de.celMod.moonRise")));
        sunControls.add(new MGuiButtonSolid(this, "MIDNIGHT", guiLeft + 4 + xSize / 3 - 2, guiTop + i, xSize / 3 - 4, 14, I18n.format("gui.de.celMod.midnight")));
        sunControls.add(new MGuiButtonSolid(this, "MOON_SET", guiLeft + 4 + (xSize / 3) * 2 - 4, guiTop + i, xSize / 3 - 4, 14, I18n.format("gui.de.celMod.moonSet")));
        i += 20;
        sunControls.add(new MGuiButtonSolid(this, "SKIP_24", guiLeft + 4, guiTop + i, xSize / 2 - 5, 14, I18n.format("gui.de.celMod.skip24")));
        sunControls.add(new MGuiButtonSolid(this, "STOP", guiLeft + 1 + xSize / 2, guiTop + i, xSize / 2 - 5, 14, I18n.format("gui.de.celMod.stop")));

        updateControls();
        manager.initElements();
    }

    private void updateControls() {
        if (tile.WEATHER_MODE.value) {
            for (MGuiElementBase elementBase : sunControls) {
                manager.remove(elementBase);
            }
            for (MGuiElementBase elementBase : weatherControls) {
                if (!manager.getElements().contains(elementBase)) {
                    LogHelper.dev(elementBase);
                    manager.add(elementBase);
                }
            }
            sunMode.disabled = false;
            weatherMode.disabled = true;
        }
        else {
            for (MGuiElementBase elementBase : sunControls) {
                if (!manager.getElements().contains(elementBase)) {
                    manager.add(elementBase);
                }
            }
            for (MGuiElementBase elementBase : weatherControls) {
                manager.remove(elementBase);
            }
            sunMode.disabled = true;
            weatherMode.disabled = false;
        }

    }

    @Override
    public void onMGuiEvent(String eventString, MGuiElementBase eventElement) {
        if (eventElement instanceof MGuiButton){
            tile.sendPacketToServer(new PacketTileMessage(tile, (byte)0, ((MGuiButton) eventElement).buttonName, false));
        }
    }

    @Override
    public void updateScreen() {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        for (MGuiElementBase element : manager.getElements()) {
            if (element instanceof MGuiButton && (element.isMouseOver(mouseX, mouseY) || ((element == sunMode || element == weatherMode) && ((MGuiButton) element).disabled))) {
                GuiParticle particle = new GuiParticle(mc.theWorld, element.xPos + 4 + mc.theWorld.rand.nextInt(element.xSize - 8), element.yPos + 6);
                particle.setScale(0.1F);
                particle.setRBGColorF(1, 1, 1);
                effectRenderer.addEffect(particle);
            }
        }
        updateControls();

        effectRenderer.addEffect(new GuiParticle(mc.theWorld, guiLeft + xSize / 2, guiTop + 6));
        super.updateScreen();
    }

    private class GuiParticle extends GuiEffect {

        protected GuiParticle(World world, double posX, double posY) {
            super(world, posX, posY);

            float speed = 5F;
            this.motionX = (-0.5F + rand.nextFloat()) * speed ;
            this.motionY = (-0.5F + rand.nextFloat()) * speed / 4F;
            this.particleMaxAge = 10 + rand.nextInt(10);
            this.particleScale = 0.5F;
            this.particleTextureIndexX = 0;
            this.particleTextureIndexY = 1;
            this.particleRed = 0;
        }

        @Override
        public GuiEffect setScale(float scale) {
            float speed = 5F * scale;
            this.motionX = (-0.5F + rand.nextFloat()) * speed;
            this.motionY = (-0.5F + rand.nextFloat()) * speed;
            return super.setScale(scale);
        }

        @Override
        public void onUpdate() {
            super.onUpdate();
            energyBar.setEnergy(tile.energyStored.value);

            particleTextureIndexX = rand.nextInt(5);
            int ttd = particleMaxAge - particleAge;
            if (ttd < 10){
                particleScale = ttd / 10F;
            }
            if (ttd == 1){
                particleScale = 0.5F;
                setExpired();
            }

        }

        @Override
        public void renderParticle(float partialTicks) {
            if (particleAge == 0) {
                return;
            }
            super.renderParticle(partialTicks);
        }
    }

}