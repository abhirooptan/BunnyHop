/**
 * @file        BunnyHopMain.java
 * @author      Abhiroop Tandon (20061667)
 * @assignment  Bunny Hop game
 * @brief       the main file of the game
 */

package wit.cgd.bunnyhop;

import wit.cgd.bunnyhop.game.WorldController;
import wit.cgd.bunnyhop.game.WorldRenderer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.assets.AssetManager;

import wit.cgd.bunnyhop.game.Assets;

import com.badlogic.gdx.ApplicationListener;

public class BunnyHopMain implements ApplicationListener {

    @SuppressWarnings("unused")
	private static final String TAG = BunnyHopMain.class.getName();

    private WorldController worldController;
    private WorldRenderer worldRenderer;
    
    private boolean paused;

    @Override
    public void create() {

        Gdx.app.setLogLevel(Application.LOG_DEBUG);             // Set Libgdx log level to DEBUG

        Assets.instance.init(new AssetManager());               // Load assets

        worldController = new WorldController();                // Initialize controller and renderer
        worldRenderer = new WorldRenderer(worldController);

        paused = false;                                         // Game world is active on start
    }

    @Override
    public void resume() {
        Assets.instance.init(new AssetManager());
        paused = false;
    }

    @Override
    public void dispose() {
        worldRenderer.dispose();
        Assets.instance.dispose();
    }
    @Override
    public void render() {

    	if (!paused) {           // Do not update game world when paused.
            // Update game world by the time that has passed since last rendered frame.
            worldController.update(Gdx.graphics.getDeltaTime());
        }

        // Sets the clear screen color to: Cornflower Blue
        Gdx.gl.glClearColor(0x64/255.0f, 0x95/255.0f, 0xed/255.0f, 0xff/255.0f);

        // Clears the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render game world to screen
        worldRenderer.render();
    }
    @Override
    public void resize (int width, int height) {
        worldRenderer.resize(width, height);
    }
    @Override
    public void pause () {
      paused = true;
    }
}