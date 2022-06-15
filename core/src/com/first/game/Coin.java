package com.first.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Coin {
    private final AnimPlayer animPlayer;
    private final Vector2 position;

    public Coin(Vector2 position) {
        animPlayer = new AnimPlayer("coins.png", 8, 1, 10);
        animPlayer.setPlayMode(Animation.PlayMode.LOOP);
        this.position = new Vector2(position);
    }

    public void draw(SpriteBatch batch, OrthographicCamera camera){
        animPlayer.setTime(Gdx.graphics.getDeltaTime());
        float cx = (position.x - camera.position.x)/camera.zoom + Gdx.graphics.getWidth()/2;
        float cy = (position.y - camera.position.y)/camera.zoom + Gdx.graphics.getHeight()/2;

        batch.draw(animPlayer.getFrame(), cx, cy);
    }

    public void dispose(){
        animPlayer.dispose();
    }
}
