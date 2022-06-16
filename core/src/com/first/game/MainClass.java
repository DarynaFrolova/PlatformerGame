package com.first.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private List<Coin> coinList;
    private Texture fon;
    private Texture heart;
    private MyCharacter chip;
    private PhysX physX;

    private int[] foreGround;

    private int score;
//    private boolean start;

    @Override
    public void create() {
        chip = new MyCharacter();
        fon = new Texture("background.png");
        map = new TmxMapLoader().load("maps/map2.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        physX = new PhysX();
        if (map.getLayers().get("Land") != null) {
            MapObjects mo = map.getLayers().get("Land").getObjects();
            physX.addObjects(mo);
            MapObject mo1 = map.getLayers().get("Hero Layer").getObjects().get("hero");
            physX.addObject(mo1);
        }

        foreGround = new int[1];
        foreGround[0] = map.getLayers().getIndex("Tile Layer 1");

        batch = new SpriteBatch();

        label = new Label(40);
        heart = new Texture("heart.png");

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera.position.x = physX.getHero().getPosition().x;
        camera.position.y = physX.getHero().getPosition().y;
        camera.zoom = 0.5f;
        camera.update();

        coinList = new ArrayList<>();
        MapLayer ml = map.getLayers().get("Coins");
        if (ml != null) {
            MapObjects mo = ml.getObjects();
            if (mo.getCount() > 0) {
                for (int i = 0; i < mo.getCount(); i++) {
                    RectangleMapObject tmpMo = (RectangleMapObject) ml.getObjects().get(i);
                    Rectangle rect = tmpMo.getRectangle();
                    coinList.add(new Coin(new Vector2(rect.x, rect.y)));
                }
            }
        }

    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        physX.step();

        chip.setWalk(false);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            physX.setHeroForce(new Vector2(-3000, 0));
            chip.setDir(true);
            chip.setWalk(true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            physX.setHeroForce(new Vector2(3000, 0));
            chip.setDir(false);
            chip.setWalk(true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            physX.setHeroForce(new Vector2(0, 1300));
        }
//		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.position.y--;
//        if (Gdx.input.isKeyPressed(Input.Keys.S)) {start=true;}

        camera.position.x = physX.getHero().getPosition().x;
        camera.position.y = physX.getHero().getPosition().y;
        camera.update();

        batch.begin();
        batch.draw(fon, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.begin();
        batch.draw(chip.getFrame(), Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        batch.draw(heart, Gdx.graphics.getWidth() - 70, Gdx.graphics.getHeight() - 70, 70, 70);
        label.draw(batch, "Coins collected: " + score, 3, 10);

        for (int i = 0; i < coinList.size(); i++) {
            coinList.get(i).draw(batch, camera);
            if (coinList.get(i).isOverlaps(chip.getRect(), camera)) {
                coinList.remove(i);
                score++;
            }
        }
        batch.end();

//        if (start) physX.step();
        physX.debugDraw(camera);
    }

    @Override
    public void dispose() {
        batch.dispose();
        coinList.get(0).dispose();
        heart.dispose();
        physX.dispose();
    }
}