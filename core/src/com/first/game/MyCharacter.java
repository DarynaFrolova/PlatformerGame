package com.first.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class MyCharacter {
    private final AnimPlayer animIdle, animJump, animRight;
    private boolean dir, run, jump;
    private float dTime;
    private final Rectangle rect;

    public MyCharacter() {
        animIdle = new AnimPlayer("robot/idle.png", 1, 1, 16.0f, Animation.PlayMode.LOOP);
        animJump = new AnimPlayer("robot/jump.png", 1, 1, 16.0f, Animation.PlayMode.LOOP);
        animRight = new AnimPlayer("robot/runRight.png", 6, 1, 16.0f, Animation.PlayMode.LOOP);
        rect = new Rectangle(Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight()/4, animIdle.getFrame().getRegionWidth(), animIdle.getFrame().getRegionHeight());
    }

    public void setTime(float time) {
        dTime = time;
    }

    public TextureRegion getFrame(){
        dTime = Gdx.graphics.getDeltaTime();
        TextureRegion tmp;
        if (dir & run & !jump) {
            animRight.step(dTime);
            if(!animRight.getFrame().isFlipX())
                animRight.getFrame().flip(true, false);
            tmp = animRight.getFrame();
        } else if (!dir & run & !jump) {
            animRight.step(dTime);
            if(animRight.getFrame().isFlipX())
                animRight.getFrame().flip(true, false);
            tmp = animRight.getFrame();
        } else if(dir & !run & !jump) {
            animIdle.step(dTime);
            if(!animIdle.getFrame().isFlipX())
                animIdle.getFrame().flip(true, false);
            tmp = animIdle.getFrame();
        } else if (!dir & !run & !jump) {
            animIdle.step(dTime);
            if (animIdle.getFrame().isFlipX())
                animIdle.getFrame().flip(true, false);
            tmp = animIdle.getFrame();
        } else if (dir & !run & jump) {
            animJump.step(dTime);
            if (!animJump.getFrame().isFlipX())
                animJump.getFrame().flip(true, false);
            tmp = animJump.getFrame();
        } else {
            animJump.step(dTime);
            if (!animJump.getFrame().isFlipX())
                animJump.getFrame().flip(true, false);
            tmp = animJump.getFrame();
        }
        return tmp;
    }

    public void setWalk(boolean b) {
        run = b;
    }
    public void setDir(boolean b) {
        dir = b;
    }
    public Rectangle getRect() {return rect;}
}