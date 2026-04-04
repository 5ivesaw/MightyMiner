package com.jelly.mightyminerv2.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.Color;

/**
 * RenderUtil – ported to 1.21.10 Fabric.
 * Uses MatrixStack + BufferRenderer (immediate draw) instead of GL11 / WorldRenderer.
 */
public class RenderUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void drawPoint(Vec3d vec, Color color) {
        double r = 0.05;
        drawBox(new Box(vec.x - r, vec.y - r, vec.z - r, vec.x + r, vec.y + r, vec.z + r), color);
    }

    public static void drawBlock(BlockPos pos, Color color) {
        drawBox(new Box(pos.getX(), pos.getY(), pos.getZ(),
                        pos.getX()+1, pos.getY()+1, pos.getZ()+1), color);
    }

    public static void outlineBlock(BlockPos pos, Color color) {
        outlineBox(new Box(pos.getX(), pos.getY(), pos.getZ(),
                           pos.getX()+1, pos.getY()+1, pos.getZ()+1), color);
    }

    public static void drawBox(Box box, Color color) {
        Vec3d cam = mc.getEntityRenderDispatcher().camera.getPos();
        MatrixStack ms = new MatrixStack();
        ms.translate(-cam.x, -cam.y, -cam.z);

        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.defaultBlendFunc();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = ms.peek().getPositionMatrix();
        float r = color.getRed()/255f, g = color.getGreen()/255f,
              b = color.getBlue()/255f, a = color.getAlpha()/255f;

        drawFilledBox(buf, matrix, box, r, g, b, a);
        BufferRenderer.drawWithGlobalProgram(buf.end());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void outlineBox(Box box, Color color) {
        Vec3d cam = mc.getEntityRenderDispatcher().camera.getPos();
        MatrixStack ms = new MatrixStack();
        ms.translate(-cam.x, -cam.y, -cam.z);

        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.lineWidth(2f);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        Matrix4f matrix = ms.peek().getPositionMatrix();
        float r = color.getRed()/255f, g = color.getGreen()/255f,
              b = color.getBlue()/255f, a = color.getAlpha()/255f;

        drawOutlineBox(buf, matrix, box, r, g, b, a);
        BufferRenderer.drawWithGlobalProgram(buf.end());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void drawLine(Vec3d start, Vec3d end, Color color) {
        Vec3d cam = mc.getEntityRenderDispatcher().camera.getPos();
        MatrixStack ms = new MatrixStack();
        ms.translate(-cam.x, -cam.y, -cam.z);

        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.lineWidth(1.5f);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        Matrix4f mat = ms.peek().getPositionMatrix();
        float r = color.getRed()/255f, g = color.getGreen()/255f,
              b = color.getBlue()/255f, a = color.getAlpha()/255f;
        buf.vertex(mat, (float)start.x, (float)start.y, (float)start.z).color(r,g,b,a);
        buf.vertex(mat, (float)end.x,   (float)end.y,   (float)end.z).color(r,g,b,a);
        BufferRenderer.drawWithGlobalProgram(buf.end());

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void drawTracer(Vec3d to, Color color) {
        drawLine(mc.player.getCameraPosVec(1f), to, color);
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private static void drawFilledBox(BufferBuilder buf, Matrix4f mat, Box b,
                                       float r, float g, float bv, float a) {
        float x0=(float)b.minX, y0=(float)b.minY, z0=(float)b.minZ;
        float x1=(float)b.maxX, y1=(float)b.maxY, z1=(float)b.maxZ;
        // 6 faces
        buf.vertex(mat,x0,y0,z0).color(r,g,bv,a); buf.vertex(mat,x1,y0,z0).color(r,g,bv,a);
        buf.vertex(mat,x1,y0,z1).color(r,g,bv,a); buf.vertex(mat,x0,y0,z1).color(r,g,bv,a);
        buf.vertex(mat,x0,y1,z0).color(r,g,bv,a); buf.vertex(mat,x0,y1,z1).color(r,g,bv,a);
        buf.vertex(mat,x1,y1,z1).color(r,g,bv,a); buf.vertex(mat,x1,y1,z0).color(r,g,bv,a);
        buf.vertex(mat,x0,y0,z0).color(r,g,bv,a); buf.vertex(mat,x0,y1,z0).color(r,g,bv,a);
        buf.vertex(mat,x1,y1,z0).color(r,g,bv,a); buf.vertex(mat,x1,y0,z0).color(r,g,bv,a);
        buf.vertex(mat,x1,y0,z0).color(r,g,bv,a); buf.vertex(mat,x1,y1,z0).color(r,g,bv,a);
        buf.vertex(mat,x1,y1,z1).color(r,g,bv,a); buf.vertex(mat,x1,y0,z1).color(r,g,bv,a);
        buf.vertex(mat,x1,y0,z1).color(r,g,bv,a); buf.vertex(mat,x1,y1,z1).color(r,g,bv,a);
        buf.vertex(mat,x0,y1,z1).color(r,g,bv,a); buf.vertex(mat,x0,y0,z1).color(r,g,bv,a);
        buf.vertex(mat,x0,y0,z1).color(r,g,bv,a); buf.vertex(mat,x0,y1,z1).color(r,g,bv,a);
        buf.vertex(mat,x0,y1,z0).color(r,g,bv,a); buf.vertex(mat,x0,y0,z0).color(r,g,bv,a);
    }

    private static void drawOutlineBox(BufferBuilder buf, Matrix4f mat, Box b,
                                        float r, float g, float bv, float a) {
        float x0=(float)b.minX, y0=(float)b.minY, z0=(float)b.minZ;
        float x1=(float)b.maxX, y1=(float)b.maxY, z1=(float)b.maxZ;
        // bottom
        buf.vertex(mat,x0,y0,z0).color(r,g,bv,a); buf.vertex(mat,x1,y0,z0).color(r,g,bv,a);
        buf.vertex(mat,x1,y0,z0).color(r,g,bv,a); buf.vertex(mat,x1,y0,z1).color(r,g,bv,a);
        buf.vertex(mat,x1,y0,z1).color(r,g,bv,a); buf.vertex(mat,x0,y0,z1).color(r,g,bv,a);
        buf.vertex(mat,x0,y0,z1).color(r,g,bv,a); buf.vertex(mat,x0,y0,z0).color(r,g,bv,a);
        // top
        buf.vertex(mat,x0,y1,z0).color(r,g,bv,a); buf.vertex(mat,x1,y1,z0).color(r,g,bv,a);
        buf.vertex(mat,x1,y1,z0).color(r,g,bv,a); buf.vertex(mat,x1,y1,z1).color(r,g,bv,a);
        buf.vertex(mat,x1,y1,z1).color(r,g,bv,a); buf.vertex(mat,x0,y1,z1).color(r,g,bv,a);
        buf.vertex(mat,x0,y1,z1).color(r,g,bv,a); buf.vertex(mat,x0,y1,z0).color(r,g,bv,a);
        // verticals
        buf.vertex(mat,x0,y0,z0).color(r,g,bv,a); buf.vertex(mat,x0,y1,z0).color(r,g,bv,a);
        buf.vertex(mat,x1,y0,z0).color(r,g,bv,a); buf.vertex(mat,x1,y1,z0).color(r,g,bv,a);
        buf.vertex(mat,x1,y0,z1).color(r,g,bv,a); buf.vertex(mat,x1,y1,z1).color(r,g,bv,a);
        buf.vertex(mat,x0,y0,z1).color(r,g,bv,a); buf.vertex(mat,x0,y1,z1).color(r,g,bv,a);
    }
}
