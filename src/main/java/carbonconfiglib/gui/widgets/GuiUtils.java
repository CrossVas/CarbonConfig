package carbonconfiglib.gui.widgets;

import java.util.ArrayDeque;
import java.util.Deque;

import org.lwjgl.opengl.GL11;

import carbonconfiglib.gui.config.ConfigElement.GuiAlign;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

/**
 * Copyright 2023 Speiger, Meduris
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class GuiUtils
{
	private static final float U_SCALE = 1F / 0x100;
	private static final float V_SCALE = 1F / 0x100;
	private static final ScissorsStack STACK = new ScissorsStack();
	
	public static float calculateScrollOffset(float width, FontRenderer font, GuiAlign align, String text, int seed) {
		int textWidth = font.getStringWidth(text);
		if(textWidth > width) {
			float diff = textWidth - width + 2F;
			double timer = (milliTime() + seed) / 1000D;
			double minDiff = Math.max(diff * 0.5D, 3.0D);
			double offset = Math.sin((Math.PI / 2D) * Math.cos(((Math.PI * 2D) * timer) / minDiff)) / 2D + 0.01F + align.alignCenter();
			return (float)lerp(offset, 0D, diff);
		}
		return 0;
	}
	
	public static void drawScrollingString(FontRenderer font, String text, float x, float y, float width, float height, GuiAlign align, int color, int seed) {
		int textWidth = font.getStringWidth(text);
		if(textWidth > width) {
			float diff = textWidth - width + 2F;
			double timer = (milliTime() + seed) / 1000D;
			double minDiff = Math.max(diff * 0.5D, 3.0D);
			double offset = Math.sin((Math.PI / 2D) * Math.cos(((Math.PI * 2D) * timer) / minDiff)) / 2D + 0.01F + align.alignCenter();
			pushScissors((int)x, (int)y, (int)width, (int)height);
			font.drawString(text, (int)(x - align.align(width) + align.align(textWidth) + (float)lerp(offset, 0D, diff)), (int)(y + (height / 2) - (font.FONT_HEIGHT / 3)), color, false);
			popScissors();
			return;
		}
		float offset = align.align(textWidth);
		font.drawString(text, (int)(x - align.align(width) + offset), (int)(y + (height / 2) - (font.FONT_HEIGHT / 3)), color, false);
	}
	
	public static void drawScrollingShadowString(FontRenderer font, String text, float x, float y, float width, float height, GuiAlign align, int color, int seed) {
		int textWidth = font.getStringWidth(text);
		if(textWidth > width) {
			float diff = textWidth - width + 2F;
			double timer = (milliTime() + seed) / 1000D;
			double minDiff = Math.max(diff * 0.5D, 3.0D);
			double offset = Math.sin((Math.PI / 2D) * Math.cos(((Math.PI * 2D) * timer) / minDiff)) / 2D + 0.01F + align.alignCenter();
			pushScissors((int)x, (int)y, (int)width, (int)height);
			font.drawStringWithShadow(text, (int)(x - align.align(width) + align.align(textWidth) + (float)lerp(offset, 0D, diff)), (int)(y + (height / 2) - (font.FONT_HEIGHT / 3)), color);
			popScissors();
			return;
		}
		float offset = align.align(textWidth);
		font.drawStringWithShadow(text, (int)(x - align.align(width) + offset), (int)(y + (height / 2) - (font.FONT_HEIGHT / 3)), color);
	}
	
	private static long milliTime() {
		return System.nanoTime() / 1000000L;
	}
	
	private static double lerp(double value, double min, double max) {
		return min + value * (max - min);
	}
	
	public static void pushScissors(int x, int y, int width, int height) {
		pushScissors(new Rect(x, y, width, height));;
	}
	
	public static void pushScissors(Rect rect) {
		STACK.push(rect);
		applyScissors(rect);
	}
	
	public static void popScissors() {
		applyScissors(STACK.pop());
	}
	
	private static void applyScissors(Rect rect) {
		if(rect == null) {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		int bottom = rect.maxY;
		double scaledHeight = (double)mc.displayHeight / (double)res.getScaledHeight_double();
		double scaledWidth = (double)mc.displayWidth / (double)res.getScaledWidth_double();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor((int)(rect.getX() * scaledWidth), (int)(mc.displayHeight - bottom * scaledHeight), (int)(rect.getWidth() * scaledWidth), (int)(rect.getHeigth() * scaledHeight));
	}
	
	public static void drawTextureWithBorder(ResourceLocation res, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(res);
		drawTexture(x, y, u, v, width, height, textureWidth, textureHeight, topBorder, bottomBorder, leftBorder, rightBorder, zLevel);
	}
	
	private static void drawTexture(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, int topBorder, int bottomBorder, int leftBorder, int rightBorder, float zLevel) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 0, 1);
		
		int fillerWidth = textureWidth - leftBorder - rightBorder;
		int fillerHeight = textureHeight - topBorder - bottomBorder;
		int canvasWidth = width - leftBorder - rightBorder;
		int canvasHeight = height - topBorder - bottomBorder;
		int xPasses = canvasWidth / fillerWidth;
		int remainderWidth = canvasWidth % fillerWidth;
		int yPasses = canvasHeight / fillerHeight;
		int remainderHeight = canvasHeight % fillerHeight;

		drawTextured(x, y, u, v, leftBorder, topBorder, zLevel, tessellator);
		drawTextured(x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder, zLevel, tessellator);
		drawTextured(x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder, zLevel, tessellator);
		drawTextured(x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder, zLevel, tessellator);

		for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++) {
			drawTextured(x + leftBorder + (i * fillerWidth), y, u + leftBorder, v, (i == xPasses ? remainderWidth : fillerWidth), topBorder, zLevel, tessellator);
			drawTextured(x + leftBorder + (i * fillerWidth), y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, (i == xPasses ? remainderWidth : fillerWidth), bottomBorder, zLevel, tessellator);
			for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
				drawTextured(x + leftBorder + (i * fillerWidth), y + topBorder + (j * fillerHeight), u + leftBorder, v + topBorder, (i == xPasses ? remainderWidth : fillerWidth), (j == yPasses ? remainderHeight : fillerHeight), zLevel, tessellator);
		}

		for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++) {
			drawTextured(x, y + topBorder + (j * fillerHeight), u, v + topBorder, leftBorder, (j == yPasses ? remainderHeight : fillerHeight), zLevel, tessellator);
			drawTextured(x + leftBorder + canvasWidth, y + topBorder + (j * fillerHeight), u + leftBorder + fillerWidth, v + topBorder, rightBorder, (j == yPasses ? remainderHeight : fillerHeight), zLevel, tessellator);
		}
		tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
	}
	
	private static void drawTextured(int x, int y, int u, int v, int width, int height, float zLevel, Tessellator builder) {
		builder.addVertexWithUV(x, y + height, zLevel, u * U_SCALE, (v + height) * V_SCALE);
		builder.addVertexWithUV(x + width, y + height, zLevel, (u + width) * U_SCALE, (v + height) * V_SCALE);
		builder.addVertexWithUV(x + width, y, zLevel, (u + width) * U_SCALE, v * V_SCALE);
		builder.addVertexWithUV(x, y, zLevel, u * U_SCALE, v * V_SCALE);
	}
	
	public static void drawTextureRegion(float x, float y, float width, float height, Icon icon, float texWidth, float texHeight) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(icon.getTexture());
		drawTextureRegion(x, y, icon.getX(), icon.getY(), width, height, texWidth, texHeight, icon.getSheetWidth(), icon.getSheetHeight());
	}
	
	public static void drawTextureRegion(float x, float y, int xOff, int yOff, float width, float height, Icon icon, float texWidth, float texHeight) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(icon.getTexture());
		drawTextureRegion(x, y, icon.getX() + xOff, icon.getY() + yOff, width, height, texWidth, texHeight, icon.getSheetWidth(), icon.getSheetHeight());
	}
    
	public static void drawTextureRegion(float x, float y, float texX, float texY, float width, float height, float texWidth, float texHeight, float textureWidth, float textureHeight) {
		float maxX = x + width;
		float maxY = y + height;
		float t_minX = texX / textureWidth;
		float t_minY = texY / textureHeight;
		float t_maxX = (texX + texWidth) / textureWidth;
		float t_maxY = (texY + texHeight) / textureHeight;
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, maxY, 0, t_minX, t_maxY);
		tessellator.addVertexWithUV(maxX, maxY, 0, t_maxX, t_maxY);
		tessellator.addVertexWithUV(maxX, y, 0, t_maxX, t_minY);
		tessellator.addVertexWithUV(x, y, 0, t_minX, t_minY);
		
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 0, 1);
		tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static class Rect {
		int minX;
		int minY;
		int maxX;
		int maxY;
		
		public Rect(int x, int y, int width, int heigth) {
			this.minX = x;
			this.minY = y;
			this.maxX = x + width;
			this.maxY = y + heigth;
		}
		
		public void limit(Rect rect) {
			minX = Math.max(rect.minX, minX);
			minY = Math.max(rect.minY, minY);
			maxX = Math.min(rect.maxX, maxX);
			maxY = Math.min(rect.maxY, maxY);
		}
		
		public int getX() { return minX; }
		public int getY() { return minY; }
		public int getWidth() { return maxX - minX; }
		public int getHeigth() { return maxY - minY; }
	}
	
	public static class ScissorsStack {
		Deque<Rect> stack = new ArrayDeque<>();
		
		public void push(Rect owner) {
			if(stack.isEmpty()) {
				stack.push(owner);
				return;
			}
			owner.limit(stack.peek());
		}
		
		public Rect pop() {
			stack.pop();
			return stack.peek();
		}
	}
}
