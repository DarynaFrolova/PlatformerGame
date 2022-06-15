package com.first.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainClass extends ApplicationAdapter {
    private SpriteBatch batch;
    private AnimPlayer robotAnim;
    //    private Label label;
    private Texture heart;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private List<Coin> coinList;

    private int x;

    @Override
    public void create() {
        map = new TmxMapLoader().load("maps/map1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        batch = new SpriteBatch();
        robotAnim = new AnimPlayer("robot.png", 6, 1, 10.0f);
//        label = new Label(40);
        heart = new Texture("heart.png");

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        RectangleMapObject o = (RectangleMapObject) map.getLayers().get("Object Layer 1").getObjects().get("camera");
        camera.position.x = o.getRectangle().x;
        camera.position.y = o.getRectangle().y;
        camera.zoom = 0.5f;
        camera.update();

        coinList = new ArrayList<>();
        MapLayer ml = map.getLayers().get("coins");
        if (ml != null) {
            MapObjects mo = ml.getObjects();
            if (mo.getCount() > 0) {
                for (MapObject object : mo) {
                    RectangleMapObject tmpMo = (RectangleMapObject) object;
                    Rectangle rect = tmpMo.getRectangle();
                    coinList.add(new Coin(new Vector2(rect.x, rect.y)));
                }
            }
        }

    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.valueOf("1434A4"));

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x--;
            robotAnim.setPlayMode(Animation.PlayMode.LOOP);
            x--;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x++;
            robotAnim.setPlayMode(Animation.PlayMode.LOOP);
            x++;
//        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
//            camera.position.y++;
//            robotAnim.setPlayMode(Animation.PlayMode.LOOP);
//            x++;
//        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
//            camera.position.y--;
//            robotAnim.setPlayMode(Animation.PlayMode.LOOP);
//            x++;
        } else {
            robotAnim.setPlayMode(Animation.PlayMode.NORMAL);
        }

        if (x < 0) {
            x = 0;
        }
        if (x > 565) {
            x = 565;
        }

        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render();

        robotAnim.setTime(Gdx.graphics.getDeltaTime());

        batch.begin();
        batch.draw(heart, Gdx.graphics.getWidth() - 70, Gdx.graphics.getHeight() - 70, 70, 70);
//        label.draw(batch, "Hello World:)", 175, 250);

        for (int i = 0; i < coinList.size(); i++) {
            coinList.get(i).draw(batch, camera);
        }

        batch.draw(robotAnim.getFrame(), x, 0);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        robotAnim.dispose();
        heart.dispose();
        coinList.get(0).dispose();
    }
}
