package com.first.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MyCharacter {
    private final AnimPlayer idle, jump, walkRight;
    private boolean isJump, isWalk, dir;
    private final Vector2 pos;
    private final Rectangle rect;

    public MyCharacter() {
        idle = new AnimPlayer("robot/idle.png", 1, 1, 16.0f, Animation.PlayMode.LOOP);
        jump = new AnimPlayer("robot/jump.png", 1, 1, 16.0f, Animation.PlayMode.LOOP);
        walkRight = new AnimPlayer("robot/runRight.png", 6, 1, 16.0f, Animation.PlayMode.LOOP);
        pos = new Vector2((float)Gdx.graphics.getWidth() / 2 - (float)idle.getFrame().getRegionWidth() / 2, (float)Gdx.graphics.getHeight() / 2 - (float)idle.getFrame().getRegionHeight() / 2);
        rect = new Rectangle(pos.x, pos.y, walkRight.getFrame().getRegionWidth(), walkRight.getFrame().getRegionHeight());
    }

    public void setWalk(boolean walk) {
        isWalk = walk;
    }

    public void setDir(boolean dir) {
        this.dir = dir;
    }

    public TextureRegion getFrame() {
        TextureRegion tmpTex = null;
        if (!isJump && !isWalk && !dir) {
            idle.step(Gdx.graphics.getDeltaTime());
            idle.getFrame().flip(false, false);
            if (idle.getFrame().isFlipX()) idle.getFrame().flip(true, false);
            tmpTex = idle.getFrame();
        } else if (!isJump && !isWalk && dir) {
            idle.step(Gdx.graphics.getDeltaTime());
            idle.getFrame().flip(true, false);
            if (!idle.getFrame().isFlipX()) idle.getFrame().flip(true, false);
            tmpTex = idle.getFrame();
        } else if (!isJump && isWalk && !dir) {
            walkRight.step(Gdx.graphics.getDeltaTime());
            walkRight.getFrame().flip(false, false);
            if (walkRight.getFrame().isFlipX()) walkRight.getFrame().flip(true, false);
            tmpTex = walkRight.getFrame();
        } else if (!isJump && isWalk && dir) {
            walkRight.step(Gdx.graphics.getDeltaTime());
            walkRight.getFrame().flip(true, false);
            if (!walkRight.getFrame().isFlipX()) walkRight.getFrame().flip(true, false);
            tmpTex = walkRight.getFrame();
        }
        return tmpTex;
    }

    public Vector2 getPos() {
        return pos;
    }

    public Rectangle getRect(OrthographicCamera camera) {
        float cx = (float)Gdx.graphics.getWidth() / 2 - ((rect.width / 2) / camera.zoom);
        float cy = (float)Gdx.graphics.getHeight() / 2 - ((rect.height / 2) / camera.zoom);
        float cW = rect.getWidth() / camera.zoom;
        float cH = rect.getHeight() / camera.zoom;
        return new Rectangle(cx, cy, cW, cH);
    }

    public Rectangle getRect() {
        return rect;
    }

    public void shapeDraw(ShapeRenderer renderer, OrthographicCamera camera) {
        float cx = (float)Gdx.graphics.getWidth() / 2 - ((rect.width / 2) / camera.zoom);
        float cy = (float)Gdx.graphics.getHeight() / 2 - ((rect.height / 2) / camera.zoom);
        float cW = rect.getWidth() / camera.zoom;
        float cH = rect.getHeight() / camera.zoom;
        renderer.rect(cx, cy, cW, cH);
    }
}