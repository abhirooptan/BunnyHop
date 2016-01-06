/**
 * @file        WorldController.java
 * @author      Abhiroop Tandon (20061667)
 * @assignment  Bunny Hop game
 * @brief       deals with all the movements and actions in the game
 */

package wit.cgd.bunnyhop.game;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

import wit.cgd.bunnyhop.util.CameraHelper;
import wit.cgd.bunnyhop.game.objects.Rock;
import wit.cgd.bunnyhop.util.Constants;

import com.badlogic.gdx.math.Rectangle;

import wit.cgd.bunnyhop.game.objects.BunnyHead;
import wit.cgd.bunnyhop.game.objects.BunnyHead.JUMP_STATE;
import wit.cgd.bunnyhop.game.objects.Feather;
import wit.cgd.bunnyhop.game.objects.Goal;
import wit.cgd.bunnyhop.game.objects.GoldCoin;
import wit.cgd.bunnyhop.game.objects.Life;

public class WorldController extends InputAdapter{

    private static final String TAG = WorldController.class.getName();

    public CameraHelper         cameraHelper;
    public Level    level;
    public int      lives;
    public int      score;
    public String currentLevel;
    private int		levelNumber;
    
    // Rectangles for collision detection
    private Rectangle   r1  = new Rectangle();
    private Rectangle   r2  = new Rectangle();
    private float   timeLeftGameOverDelay;

    @SuppressWarnings("static-access")
	private void onCollisionBunnyHeadWithRock(Rock rock) {
        BunnyHead bunnyHead = level.bunnyHead;
        float heightDifference = Math.abs(bunnyHead.position.y - (rock.position.y + rock.bounds.height));
        if (heightDifference > 0.25f) {
            boolean hitLeftEdge = bunnyHead.position.x > (rock.position.x + rock.bounds.width / 2.0f);
            if (hitLeftEdge) {
                bunnyHead.position.x = rock.position.x + rock.bounds.width;
            } else {
                bunnyHead.position.x = rock.position.x - bunnyHead.bounds.width;
            }
            return;
        }

        switch (bunnyHead.jumpState) {
        case GROUNDED:
            break;
        case FALLING:
        case JUMP_FALLING:
            bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
            bunnyHead.jumpState = JUMP_STATE.GROUNDED;
            break;
        case JUMP_RISING:
            bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
            break;
        }
    }

    private void onCollisionBunnyWithGoldCoin(GoldCoin goldcoin) {
        goldcoin.collected = true;
        score += goldcoin.getScore();
        //Gdx.app.log(TAG, "Gold coin collected");
    }

    private void onCollisionBunnyWithFeather(Feather feather) {
        feather.collected = true;
        score += feather.getScore();
        level.bunnyHead.setFeatherPowerup(true);
       // Gdx.app.log(TAG, "Feather collected");
    }
    
    private void onCollisionBunnyWithLife(Life life) {
    	life.collected = true;
    	score += life.getScore();
    	lives++;
    	//Gdx.app.log(TAG, "Life collected");
    }
    
    private void onCollisionBunnyWithGoal(Goal goal) {
    	if(score > 3000){ // minimum score to complete the level
    		goal.collected = true;
	       // Gdx.app.log(TAG, "Goal collected");
    	}
    }

    private void testCollisions() {
        r1.set(level.bunnyHead.position.x, level.bunnyHead.position.y, level.bunnyHead.bounds.width,
                level.bunnyHead.bounds.height);

        // Test collision: Bunny Head <-> Rocks
        for (Rock rock : level.rocks) {
            r2.set(rock.position.x, rock.position.y, rock.bounds.width, rock.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionBunnyHeadWithRock(rock);
            // IMPORTANT: must do all collisions for valid
            // edge testing on rocks.
        }

        // Test collision: Bunny Head <-> Gold Coins
        for (GoldCoin goldCoin : level.goldCoins) {
            if (goldCoin.collected) continue;
            r2.set(goldCoin.position.x, goldCoin.position.y, goldCoin.bounds.width, goldCoin.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionBunnyWithGoldCoin(goldCoin);
            break;
        }

        // Test collision: Bunny Head <-> Feathers
        for (Feather feather : level.feathers) {
            if (feather.collected) continue;
            r2.set(feather.position.x, feather.position.y, feather.bounds.width, feather.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionBunnyWithFeather(feather);
            break;
        }
        
     // Test collision: Bunny Head <-> Lifes
        for (Life life : level.lifes) {
            if (life.collected) continue;
            r2.set(life.position.x, life.position.y, life.bounds.width, life.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionBunnyWithLife(life);
            break;
        }
        
        Goal goal = level.goal;
        if (!goal.collected)
        	r2.set(goal.position.x, goal.position.y, goal.bounds.width, goal.bounds.height);
        if (r1.overlaps(r2))
        	onCollisionBunnyWithGoal(goal);
    }

    private void initLevel(int currScore, String currLevel) {
        score = currScore;
        level = new Level(currLevel);
        cameraHelper.setTarget(level.bunnyHead);
        //System.out.println("POsition " + level.bunnyHead.position);
    }


    public WorldController() {
    	score = 0;
    	levelNumber = 1;
    	setCurrentLevel(Constants.LEVEL_01);
        init();
    }
    
    private void init() {
        Gdx.input.setInputProcessor(this);
        cameraHelper = new CameraHelper();
        lives = Constants.LIVES_START;
        timeLeftGameOverDelay = 0;
        levelNumber = 1;
        initLevel(score, getCurrentLevel());
    }

    public void update(float deltaTime) {
        handleDebugInput(deltaTime);
        
        if (isGameOver() || isTimeUp()|| levelNumber > 2) {
            timeLeftGameOverDelay -= deltaTime;
            if (timeLeftGameOverDelay < 0) {
            	setCurrentLevel(Constants.LEVEL_01);
            	init();
            }
        } else {
            handleInputGame(deltaTime);
        }
        level.update(deltaTime);
        testCollisions();
        cameraHelper.update(deltaTime);
        if (!isGameOver() && isPlayerInWater()) {
            lives--;
            //Gdx.input.vibrate(2);
            if (isGameOver()|| levelNumber > 2) timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
            else{
            	initLevel(score, getCurrentLevel());
            	Gdx.app.debug(TAG, getCurrentLevel());
            }
        }
        
        if(isGameWon() && levelNumber < 3){
        	setCurrentLevel(Constants.LEVEL_02);
        	initLevel(score, getCurrentLevel());
        }
    }
    
    private void handleDebugInput (float deltaTime) {

        if (Gdx.app.getType() != ApplicationType.Desktop) return;

        // Camera Controls (move)
        if (!cameraHelper.hasTarget(level.bunnyHead)) { 

            float camMoveSpeed = 5 * deltaTime;
            float camMoveSpeedAccelerationFactor = 5;
            if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camMoveSpeed *= camMoveSpeedAccelerationFactor;
            if (Gdx.input.isKeyPressed(Keys.LEFT)) moveCamera(-camMoveSpeed, 0);
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) moveCamera(camMoveSpeed, 0);
            if (Gdx.input.isKeyPressed(Keys.UP)) moveCamera(0, camMoveSpeed);
            if (Gdx.input.isKeyPressed(Keys.DOWN)) moveCamera(0, -camMoveSpeed);
            if (Gdx.input.isKeyPressed(Keys.BACKSPACE)) cameraHelper.setPosition(0, 0);
        }
        
        float camZoomSpeed = 1 * deltaTime;
        float camZoomSpeedAccelerationFactor = 5;
        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camZoomSpeed *= camZoomSpeedAccelerationFactor;
        if (Gdx.input.isKeyPressed(Keys.COMMA)) cameraHelper.addZoom(camZoomSpeed);
        if (Gdx.input.isKeyPressed(Keys.PERIOD)) cameraHelper.addZoom(-camZoomSpeed);
        if (Gdx.input.isKeyPressed(Keys.SLASH)) cameraHelper.setZoom(1);
    }
    
    private void moveCamera(float x, float y) {
        x += cameraHelper.getPosition().x;
        y += cameraHelper.getPosition().y;
        cameraHelper.setPosition(x, y);
    }
    
    @Override
    public boolean keyUp(int keycode) {

        if (keycode == Keys.R) {                            // Reset game world
            init();
            Gdx.app.debug(TAG, "Game world resetted");
        } else if (keycode == Keys.ENTER) {                 // Toggle camera follow
            cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.bunnyHead);
            Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
        }
        return false;
    }
    
    private void handleInputGame(float deltaTime) {

        if (cameraHelper.hasTarget(level.bunnyHead)) {

            // Player Movement
        	
        	if(Gdx.app.getType() == ApplicationType.Desktop)
        	{
        		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
                    level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
                }
        		else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                    level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
                }
        		
        		// Bunny Jump
                if (Gdx.input.isKeyPressed(Keys.SPACE)) {
                    level.bunnyHead.setJumping(true);
                    //Gdx.app.log(TAG, "jump");
                }
                 else {
                    level.bunnyHead.setJumping(false);
                    //Gdx.app.log(TAG, "fall");
                }
        	}
                         	
        	if(Gdx.app.getType() == ApplicationType.Android)
        	{
        		if (Gdx.input.getAccelerometerY() < -1) {
                    level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
                } 
        		else if (Gdx.input.getAccelerometerY() > 1) {
                    level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
                }
        		
        		// Bunny Jump
        		if (Gdx.input.isTouched(0)) {
                    level.bunnyHead.setJumping(true);
        		}
        		else {
                    level.bunnyHead.setJumping(false);
                    //Gdx.app.log(TAG, "fall");
                }
            }
          
        }

    }
    
    public boolean isGameWon(){
    	if(level.goal.collected){
    		levelNumber++ ;
    		return true;
    	}
    	else
    		return false;
    }
    
    public boolean isTimeUp(){
    	if(level.bunnyHead.timeLeft == 0)
    		return true;
    	else 
    		return false;
    }
    
    public boolean isGameOver() {
        return lives <= 0;
    }

    public boolean isPlayerInWater() {
        return level.bunnyHead.position.y < -5;
    }

	public String getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(String currentLevel) {
		this.currentLevel = currentLevel;
	}
}