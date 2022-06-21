package com.first.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Coin {
    private final AnimPlayer animPlayer;
    private final Vector2 position;
    private Rectangle rectangle;
    private Sound sound;
    private int state;
    private float time;


    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setState() {
        sound.play(0.25f, 1, 0);
        time = 0.5f;
        state = 1;
    }

    public Coin(Vector2 position) {
        animPlayer = new AnimPlayer("coins.png", 8, 1, 10, Animation.PlayMode.LOOP);
        this.position = new Vector2(position);
        rectangle = new Rectangle(position.x, position.y, animPlayer.getFrame().getRegionWidth(), animPlayer.getFrame().getRegionWidth());
        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/sound_coin.mp3"));
    }

    public int draw(SpriteBatch batch, OrthographicCamera camera) {
        animPlayer.step(Gdx.graphics.getDeltaTime());
        float cx = (position.x - camera.position.x) / camera.zoom + Gdx.graphics.getWidth() / 2;
        float cy = (position.y - camera.position.y) / camera.zoom + Gdx.graphics.getHeight() / 2;

        batch.draw(animPlayer.getFrame(), cx, cy);
        if (state == 1) time -= Gdx.graphics.getDeltaTime();
        if (time < 0) state = 2;
        return state;
    }

    public boolean isOverlaps(Rectangle heroRect, OrthographicCamera camera) {
        float cx = (rectangle.x - camera.position.x) / camera.zoom + Gdx.graphics.getWidth() / 2;
        float cy = (rectangle.y - camera.position.y) / camera.zoom + Gdx.graphics.getHeight() / 2;
        Rectangle rect = new Rectangle(cx, cy, rectangle.width, rectangle.height);
        return rect.overlaps(heroRect);
    }

    public void dispose() {
        animPlayer.dispose();
        sound.dispose();
    }
}
