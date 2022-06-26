package com.first.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
import com.first.game.Coin;
import com.first.game.Label;
import com.first.game.MyCharacter;
import com.first.game.PhysX;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private final SpriteBatch batch;
    private final ShapeRenderer renderer;
    private final Label label;
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final OrthographicCamera camera;
    private final List<Coin> coinList;
    private final Texture background;
    private final Texture heart;
    private final MyCharacter robot;
    private final PhysX physX;
    private final Music music;
    private final int[] foreGround;
    private int score;
    private final boolean start = true;
    final Game game;

    public GameScreen(Game game){
        this.game = game;
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        physX = new PhysX();

        robot = new MyCharacter();
        background = new Texture("images/background.png");
        map = new TmxMapLoader().load("maps/map2.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        if (map.getLayers().get("Land") != null) {
            MapObjects mo = map.getLayers().get("Land").getObjects();
            physX.addObjects(mo);
        }
        MapObject mo1 = map.getLayers().get("Hero Layer").getObjects().get("hero");
        physX.addObject(mo1, robot.getRect(camera));
        System.out.print("" + physX.barrelInit());

        foreGround = new int[1];
        foreGround[0] = map.getLayers().getIndex("Tile Layer 1");

        batch = new SpriteBatch();
        renderer = new ShapeRenderer();

        label = new Label(40);
        heart = new Texture("images/heart.png");

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
        music.play();

        camera.zoom = 0.9f;
    }

    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);


        robot.setWalk(false);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            physX.setHeroForce(new Vector2(-1500, 0));
            robot.setDir(true);
            robot.setWalk(true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            physX.setHeroForce(new Vector2(1500, 0));
            robot.setDir(false);
            robot.setWalk(true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && physX.cl.isOnGround()) {
            physX.setHeroForce(new Vector2(0, 2500));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) camera.position.y--;

        camera.position.x = physX.getHero().getPosition().x;
        camera.position.y = physX.getHero().getPosition().y;
        camera.update();

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.begin();
        batch.draw(robot.getFrame(), robot.getRect(camera).x, robot.getRect(camera).y, robot.getRect(camera).getWidth(), robot.getRect(camera).getHeight());
        batch.draw(heart, Gdx.graphics.getWidth() - 70, Gdx.graphics.getHeight() - 70, 70, 70);
        label.draw(batch, "Coins collected: " + score, 3, 10);

        for (int i = 0; i < coinList.size(); i++) {
            int state;
            state = coinList.get(i).draw(batch, camera);
            if (coinList.get(i).isOverlaps(robot.getRect(camera), camera)) {
                if (state == 0) coinList.get(i).setState();
                if (state == 2) {
                    coinList.remove(i);
                    score++;
                    if (score == 25) {
                        music.stop();
                        game.setScreen(new WinScreen(game));
                    }
                }
            }
        }
        batch.end();

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
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

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

