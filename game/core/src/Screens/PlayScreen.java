package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGDXGame;

public class PlayScreen implements Screen {
    private MyGDXGame game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private TiledMap map;
    private TmxMapLoader mapLoader;
    private OrthogonalTiledMapRenderer renderer;

    public PlayScreen(MyGDXGame game) {
        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new StretchViewport(MyGDXGame.V_WIDTH, MyGDXGame.V_HEIGHT, gameCam);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("test_map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            gameCam.position.y += 100 * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            gameCam.position.x += 100 * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            gameCam.position.y -= 100 * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            gameCam.position.x -= 100 * dt;
        }
    }

    public void update(float dt) {
        handleInput(dt);

        gameCam.update();
        renderer.setView(gameCam);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();


    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
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

    }
}
