package com.simu;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.security.Key;

public class MyGdxGame extends ApplicationAdapter {
    public final float TIMESTEP = 0.0f;

	SpriteBatch batch;
	Texture img;
    RigidBody craft;
    OrthographicCamera cam;
    Vector3 v1;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("D:\\Programmation\\Simu\\core\\assets\\badlogic.jpg");
        craft = new RigidBody();
	}


	@Override
	public void render () {

        UpdateSimulation();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
		//batch.draw(img, 0, 0);
		batch.end();

        System.out.println("x = " + craft.getPosition().x + " y = " + craft.getPosition().y);
        System.out.println("orientation = " + craft.getOrientation());

        v1 = new Vector3(craft.getPosition().x + 20.0f, craft.getPosition().y, 0);
        v1 = craft.rotate2D(craft.getOrientation(), v1);
        // Affichage de l'overcraft sous forme de rectangle
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(craft.getPosition().x, craft.getPosition().y, craft.getWidth(), craft.getHeight());
        shapeRenderer.line(craft.getPosition().x, craft.getPosition().y, v1.x, v1.y);
        shapeRenderer.end();
	}

    public void UpdateSimulation() {
        double dt = Gdx.graphics.getDeltaTime();
        craft.setThrusters(false, false);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            craft.modulateThrust(true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            craft.modulateThrust(false);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            craft.setThrusters(true, false);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            craft.setThrusters(false, true);
        }

        // update the simulation
        craft.updateBodyEuler(dt);
        if(craft.getPosition().x > Gdx.graphics.getWidth()) {
            craft.setPosition(0, craft.getPosition().y, craft.getPosition().z);
        }
        if(craft.getPosition().x < 0) {
            craft.setPosition(Gdx.graphics.getWidth(), craft.getPosition().y, craft.getPosition().z);
        }
        if(craft.getPosition().y > Gdx.graphics.getHeight()) {
            craft.setPosition(craft.getPosition().x, Gdx.graphics.getHeight(), craft.getPosition().z);
        }
        if(craft.getPosition().y < 0) {
            craft.setPosition(craft.getPosition().x, 0, craft.getPosition().z);
        }
    }
}
