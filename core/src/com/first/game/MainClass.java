package com.first.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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

public class MainClass extends ApplicationAdapter {
    private SpriteBatch batch;
    private Label label;
    private Texture heart;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private List<Coin> coinList;
    private Texture background;
    private int[] foreGround;
    private MyCharacter robot;
    private int score;

    @Override
    public void create() {
        robot = new MyCharacter();

        map = new TmxMapLoader().load("maps/map1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        background = new Texture("background.png");
        foreGround = new int[1];
        foreGround[0] = map.getLayers().getIndex("Tile Layer 1");

        batch = new SpriteBatch();

        label = new Label(30);
        heart = new Texture("heart.png");

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        RectangleMapObject o = (RectangleMapObject) map.getLayers().get("Object Layer 1").getObjects().get("camera");
        camera.position.x = o.getRectangle().x;
        camera.position.y = o.getRectangle().y;
        camera.zoom = 0.5f;
        camera.update();

        coinList = new ArrayList<>();
        MapLayer ml = map.getLayers().get("Coins");
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

        robot.setWalk(false);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x--;
            robot.setDir(true);
            robot.setWalk(true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x++;
            robot.setDir(false);
            robot.setWalk(true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) camera.position.y++;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.position.y--;

        camera.update();

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.begin();
        batch.draw(robot.getFrame(), Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 4);
        batch.draw(heart, Gdx.graphics.getWidth() - 70, Gdx.graphics.getHeight() - 70, 70, 70);
        label.draw(batch, "Coins collected: " + score, 5, 425);

        for (int i = 0; i < coinList.size(); i++) {
            coinList.get(i).draw(batch, camera);
            if (coinList.get(i).isOverlaps(robot.getRect(), camera)) {
                coinList.remove(i);
                score++;
            }
        }

        batch.end();
        mapRenderer.render(foreGround);
    }

    @Override
    public void dispose() {
        batch.dispose();
        heart.dispose();
        coinList.get(0).dispose();
    }
}
