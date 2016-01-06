/**
 * @file        Goal.java
 * @author      Abhiroop Tandon (20061667)
 * @assignment  Bunny Hop game
 * @brief       deals with the wining goal in the game
 */

package wit.cgd.bunnyhop.game.objects;

import wit.cgd.bunnyhop.game.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Goal extends AbstractGameObject
{
	private TextureRegion   regGoal;
    public boolean          collected;
    public int				number;

    public Goal() {
        init();
    }

    private void init() {
        dimension.set(1.0f, 1.0f);
        regGoal = Assets.instance.goal.goal;
        // Set bounding box for collision detection
        bounds.set(0, 0, dimension.x, dimension.y);
        collected = false;
        number = 0;
    }

	@Override
	public void render(SpriteBatch batch) {
        if (collected) return;
        TextureRegion reg = null;
        reg = regGoal;
        batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x,
                scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
                false, false);
    }
}
