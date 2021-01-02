package com.mygdx.game;

import java.util.Arrays;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;

public class Boids extends ApplicationAdapter {

	ControlStage stage;
	ShapeRenderer shapeRenderer;
	static Boid[] boids;
	static Boid[] usedBoids;
	static int amountOfBoids = Consts.AMOUNT_OF_BOIDS;
	OrthographicCamera camera;
	
	public static void setBoidAmount(int amount) {
		amount = Math.max(1, amount);
		amount = Math.min(amountOfBoids, amount);
		usedBoids = Arrays.copyOfRange(boids, 0, amount);
	}

	public void updateCamera(OrthographicCamera cam) {
		Vector3 translation = new Vector3();
		if(Gdx.input.isKeyPressed(Keys.UP)) translation.y += 5;
		if(Gdx.input.isKeyPressed(Keys.DOWN)) translation.y -= 5;
		if(Gdx.input.isKeyPressed(Keys.LEFT)) translation.x -= 5;
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) translation.x += 5;
		if(Gdx.input.isKeyPressed(Keys.W)) cam.zoom -= 0.01;
		if(Gdx.input.isKeyPressed(Keys.S)) cam.zoom += 0.01;
		camera.translate(translation);
		camera.update();
	}
	@Override
	public void create () {
		
		shapeRenderer = new ShapeRenderer();
		camera = new OrthographicCamera();
		camera.viewportWidth = Gdx.graphics.getWidth();
		camera.viewportHeight = Gdx.graphics.getHeight();
		camera.position.x = Gdx.graphics.getWidth()/2;
		camera.position.y = Gdx.graphics.getHeight()/2;
		boids = new Boid[amountOfBoids];
		setBoidAmount(50);
		float[] wallConstraints = new float[] {Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Consts.DEPTH, Consts.DEPTH};
		for(int i = 0 ; i < boids.length ; i++) {
			boids[i] = new Boid(wallConstraints);
		}

		stage = new ControlStage(0f, Gdx.graphics.getHeight()*-0.4f);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		updateCamera(camera);
		
		for(int i = 0 ; i < usedBoids.length ; i++) {
			boids[i].update(usedBoids);
		}
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.rect(2, 2, Gdx.graphics.getWidth()-2, Gdx.graphics.getHeight()-2);
		shapeRenderer.end();
		shapeRenderer.begin(ShapeType.Filled);
		for(int i = 0 ; i < usedBoids.length ; i++) {
			boids[i].draw(shapeRenderer);
		}
		shapeRenderer.end();
		stage.draw();
	}
	
	@Override
	public void dispose () {
		shapeRenderer.dispose();
		stage.dispose();
	}
}
