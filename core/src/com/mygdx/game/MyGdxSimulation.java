package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

import static utils.Constants.PPM;

public class MyGdxSimulation extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture playerTexture;
	private Sprite playerSprite;

	private OrthographicCamera camera;

	private Box2DDebugRenderer b2dr;
	private World world; // Box2D physics world
	private Body player, platform;

	@Override
	public void create() {
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, w/2, h/2);

		world = new World(new Vector2(0, -9.8f), false);
		b2dr = new Box2DDebugRenderer();

		player = createBox(8, 10, 32, 32, false);
		platform = createBox(0, 0, 64, 32, true);

		batch = new SpriteBatch();

		playerTexture = new Texture("player.jpg");
		playerSprite = new Sprite(playerTexture);
		playerSprite.setPosition(w/2 -playerSprite.getWidth()/2, h/2 - playerSprite.getHeight()/2);
	}

	@Override
	public void render() {
		// Make our own update method for game logic. Do this before rendering
		update(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		b2dr.render(world, camera.combined.scl(PPM));

		batch.begin();
		batch.draw(playerTexture, 0, 0);
		batch.end();
	}

	public void resize(int width, int height) {
		camera.setToOrtho(false, width/2, height/2);
	}

	/**
	 * Dispose things whenever you can
	 */
	@Override
	public void dispose() {
		batch.dispose();
		playerTexture.dispose();
		world.dispose();
		b2dr.dispose();
	}

	/**
	 * Make our own update method for game logic. Do this before the rendering.
	 * @param delta Makes sure time is fixed. Delta time.
	 */
	public void update(float delta) {
		world.step(1/60f, 6, 2);

		

		inputUpdate(delta);
		cameraUpdate(delta);
	}

	public void inputUpdate(float delta) {
		int horiztonalForce = 0;

		if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
			horiztonalForce += -1;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
			horiztonalForce += 1;
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
				Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			player.applyForceToCenter(0, 300, false); // apply 300N upwards
		}

		player.setLinearVelocity(horiztonalForce * 5, player.getLinearVelocity().y);
	}

	private void cameraUpdate(float delta) {
		Vector3 position = camera.position;
		position.x = player.getPosition().x * PPM; // scalling position to match view
		position.y = player.getPosition().y * PPM;
		camera.position.set(position);

		camera.update();
	}

	public Body createBox(int x, int y, int width, int height, boolean isStatic) {
		Body pBody;

		//set up physical properties of body
		BodyDef def = new BodyDef();

		if (isStatic)
			def.type = BodyDef.BodyType.StaticBody;
		else
			def.type = BodyDef.BodyType.DynamicBody;

		def.type = BodyDef.BodyType.DynamicBody;
		def.position.set(x/PPM, y/PPM);
		def.fixedRotation = true;
		pBody = world.createBody(def);

		//set up the shape
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width/2 / PPM, height/2 / PPM); // PPM turns regular units into box2d units

		pBody.createFixture(shape, 1.0f);
		shape.dispose(); // Things you are done using just get rid of

		return pBody;
	}
}
