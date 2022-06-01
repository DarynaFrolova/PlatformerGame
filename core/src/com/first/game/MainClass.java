package com.first.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainClass extends ApplicationAdapter {
    SpriteBatch batch;
    AnimPlayer robotAnim;
    Label label;
    Texture heart;

    private int x;

    @Override
    public void create() {
        batch = new SpriteBatch();
        robotAnim = new AnimPlayer("robot.png", 6, 1, 10.0f);
        label = new Label(40);
        heart = new Texture("heart.png");
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.valueOf("1434A4"));

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            robotAnim.setPlayMode(Animation.PlayMode.LOOP);
            x--;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            robotAnim.setPlayMode(Animation.PlayMode.LOOP);
            x++;
        } else {
            robotAnim.setPlayMode(Animation.PlayMode.NORMAL);
        }

        if (x < 0)
            x = 0;
        if (x > 565)
            x = 565;

        robotAnim.setTime(Gdx.graphics.getDeltaTime());

        batch.begin();
        batch.draw(robotAnim.getFrame(), x, 0);
        batch.draw(heart, Gdx.graphics.getWidth() - 70, Gdx.graphics.getHeight() - 70, 70, 70);
        label.draw(batch, "Hello World:)", 175, 250);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        robotAnim.dispose();
        heart.dispose();
    }
}
