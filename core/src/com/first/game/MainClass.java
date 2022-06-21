package com.first.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
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
    private ShapeRenderer renderer;

    private int[] foreGround;

    private int score;
    //    private boolean start;
    private Music music;

    @Override
    public void create() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        physX = new PhysX();

        chip = new MyCharacter();
        fon = new Texture("background.png");
        map = new TmxMapLoader().load("maps/map2.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        if (map.getLayers().get("Land") != null) {
            MapObjects mo = map.getLayers().get("Land").getObjects();
            physX.addObjects(mo);
        }
        MapObject mo1 = map.getLayers().get("Hero Layer").getObjects().get("hero");
        physX.addObject(mo1, chip.getRect(camera));
        System.out.print("" + physX.barrelInit());

        foreGround = new int[1];
        foreGround[0] = map.getLayers().getIndex("Tile Layer 1");

        batch = new SpriteBatch();
        renderer = new ShapeRenderer();

        label = new Label(40);
        heart = new Texture("heart.png");

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
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/game-music.mp3"));
        music.setLooping(true);
        music.setVolume(0.005f);
//        music.play();

        camera.zoom = 0.9f;

    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);


        chip.setWalk(false);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            physX.setHeroForce(new Vector2(-1500, 0));
            chip.setDir(true);
            chip.setWalk(true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            physX.setHeroForce(new Vector2(1500, 0));
            chip.setDir(false);
            chip.setWalk(true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && physX.cl.isOnGround()) {
            physX.setHeroForce(new Vector2(0, 2500));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.position.y--;
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
        batch.draw(chip.getFrame(), chip.getRect(camera).x, chip.getRect(camera).y, chip.getRect(camera).getWidth(), chip.getRect(camera).getHeight());
        batch.draw(heart, Gdx.graphics.getWidth() - 70, Gdx.graphics.getHeight() - 70, 70, 70);
        label.draw(batch, "Coins collected: " + score, 3, 10);

        for (int i = 0; i < coinList.size(); i++) {
            int state;
            state = coinList.get(i).draw(batch, camera);
            if (coinList.get(i).isOverlaps(chip.getRect(camera), camera)) {
                if (state == 0) coinList.get(i).setState();
                if (state == 2) {
                    coinList.remove(i);
                    score++;
                }
            }
        }
        batch.end();
        physX.debugDraw(camera);

//        if (start) physX.step();
        physX.step();

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.CORAL);
        for (Fixture fixture : physX.barrelBodys) {
            float cx = (fixture.getBody().getPosition().x - camera.position.x) / camera.zoom + Gdx.graphics.getWidth() / 2;
            float cy = (fixture.getBody().getPosition().y - camera.position.y) / camera.zoom + Gdx.graphics.getHeight() / 2;
            float cR = fixture.getShape().getRadius() / camera.zoom;
            renderer.circle(cx, cy, cR);
        }
        renderer.end();

    }

    @Override
    public void dispose() {
        batch.dispose();
        coinList.get(0).dispose();
        heart.dispose();
        physX.dispose();
        music.stop();
        music.dispose();
    }
}