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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
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

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Body heroBody;

    @Override
    public void create() {

        world = new World(new Vector2(0, -9.81f), true);
        debugRenderer = new Box2DDebugRenderer();

        BodyDef def = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape polygonShape = new PolygonShape();

        // land
        def.position.set(new Vector2(2367.95f, 111));
        def.type = BodyDef.BodyType.StaticBody;
        fdef.density = 1;
        fdef.friction = 1f;
        fdef.restitution = 0.0f;
        polygonShape.setAsBox(2367.95f, 32.38f);
        fdef.shape = polygonShape;
        world.createBody(def).createFixture(fdef);

        // boxes
        for (int i = 0; i < 10; i++) {
            def.position.set(new Vector2(MathUtils.random(200, 300), 300f));
            def.type = BodyDef.BodyType.DynamicBody;
            def.gravityScale = MathUtils.random(0.5f, 5f);
            polygonShape.setAsBox(10f, 10f);
            fdef.shape = polygonShape;
            world.createBody(def).createFixture(fdef);
        }

        // hero
        def.position.set(new Vector2(100, 250f));
        def.gravityScale = 1.0f;
        float size = 57;
        polygonShape.setAsBox(size, size);
        fdef.density = 0;
        fdef.shape = polygonShape;
        heroBody = world.createBody(def);
        heroBody.createFixture(fdef);

        polygonShape.dispose();

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
            heroBody.applyForceToCenter(new Vector2(-300.0f, 0.0f), true);
            robot.setDir(true);
            robot.setWalk(true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            heroBody.applyForceToCenter(new Vector2(300.0f, 0.0f), true);
            robot.setDir(false);
            robot.setWalk(true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
//            heroBody.applyForceToCenter(new Vector2(0.0f, 3000.0f), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
//            heroBody.applyForceToCenter(new Vector2(0.0f, -3000.0f), true);
        }

        camera.position.x = heroBody.getPosition().x;
        camera.position.y = heroBody.getPosition().y;
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

        world.step(1 / 60.0f, 3, 3);
        debugRenderer.render(world, camera.combined);
    }

    @Override
    public void dispose() {
        batch.dispose();
        heart.dispose();
        coinList.get(0).dispose();
    }
}
