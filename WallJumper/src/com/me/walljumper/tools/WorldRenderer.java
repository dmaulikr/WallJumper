package com.me.walljumper.tools;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.me.walljumper.Constants;
import com.me.walljumper.WallJumper;
import com.me.walljumper.game_objects.AbstractGameObject;
import com.me.walljumper.game_objects.terrain.Weather;
import com.me.walljumper.gui.Button;
import com.me.walljumper.gui.Image;
import com.me.walljumper.gui.PauseButton;
import com.me.walljumper.gui.SceneObject;
import com.me.walljumper.screens.LevelMenu;
import com.me.walljumper.screens.World;
import com.me.walljumper.screens.WorldScreen;
import com.me.walljumper.screens.screentransitions.ScreenTransitionFade;

public class WorldRenderer implements Disposable{
	private SpriteBatch batch;
	public static WorldRenderer renderer = new WorldRenderer();
	public OrthographicCamera camera;
	public OrthographicCamera background_camera;
	public OrthographicCamera guiCamera;
	public TextureRegion background_image, pauseLayer;
	public PauseButton pauseButton;
	public BitmapFont whiteFont, blackFont;
	public Weather weather;
	public boolean weatherBool;
	private Array<SceneObject> sceneObjects;

	
	private WorldRenderer(){
		
	}
	public void init(){
		
		batch = new SpriteBatch();
		weather = new Weather();
		weatherBool = WallJumper.WorldNum != 1 ? false : true;
		background_image = Assets.instance.nightSky.nightSky;
		pauseLayer = Assets.instance.pause.pauseLayer;
		
		sceneObjects = new Array<SceneObject>();

		
		
		//Initialize main camera
		camera = new OrthographicCamera(Constants.viewportWidth, Constants.viewportHeight);
		camera.position.set(0, 0, 0);
		camera.setToOrtho(false);
		camera.update();
		
		background_camera = new OrthographicCamera(Constants.bgViewportWidth, Constants.bgViewportHeight);
		background_camera.position.set(0,0,0);
		background_camera.setToOrtho(false);
		background_camera.update();
		
		guiCamera = new OrthographicCamera(Constants.bgViewportWidth, Constants.bgViewportHeight);
		guiCamera.position.set(0,0,0);
		guiCamera.setToOrtho(false);
		guiCamera.update();
		
		whiteFont = new BitmapFont(Gdx.files.internal("Font/white.fnt"));
		blackFont = new BitmapFont(Gdx.files.internal("Font/black.fnt"));
		
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		
	}
	public void onScreen(AbstractGameObject obj){
		
	}
	public void writeToWorld(String string, float x, float y){
		
		whiteFont.draw(batch, string, x, y);
		
	}

	public void updateScene(float deltaTime){
		for(SceneObject objs:sceneObjects){
			objs.update(deltaTime);
		}
	}
	public void clearScene(){
		getSceneObjects().clear();
	}
	private void renderWorld(){
		//apply changes to camera, render level
		World.controller.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		World.controller.render(batch);
		weather.render(batch);

		batch.end();
		
	}
	private void renderBackground() {
		// TODO Auto-generated method stub
		batch.setProjectionMatrix(background_camera.combined);
		batch.begin();
		
		batch.draw(background_image.getTexture(), 0, 0, background_camera.viewportWidth,
				background_camera.viewportHeight, background_image.getRegionX(), background_image.getRegionY(),
				background_image.getRegionWidth(), background_image.getRegionHeight(), false, false);
		batch.end();
	}
	private void renderGUI() {
		// TODO Auto-generated method stub
		batch.setProjectionMatrix(guiCamera.combined);
		batch.begin();
		
		renderTapToStart();
		renderTimer();
		pauseButton.render(batch);

		
		renderTransparency();
		otherRenders();

			
		
		batch.end();
	}
	private void otherRenders() {
		//Render menu screen
		for(SceneObject objects: getSceneObjects()){
			objects.render(batch);
		}
	}
	private void renderTimer() {
		if(World.controller.started){
			float curTime = World.controller.getLevelTime();
			
			//if curTime > 1, multiplying by 100 will move the the numbers after the decimal to 
			//front of decimal so they can be placed to the right of the decimal
			float afterDecimal = curTime > 1 ? (curTime % (int)(curTime)) * 100 : curTime * 100;
			String time = "" + (int)(curTime) + "." + (int)(afterDecimal);
			writeToWorld(time, Constants.bgViewportWidth / 2 - 30, Constants.bgViewportHeight - 50);
		}
	}
	private void renderTapToStart() {
		if(!World.controller.started)
			writeToWorld("Tap to start!", Constants.bgViewportWidth / 2 - 50, Constants.bgViewportHeight / 2 + 150);
			
	}
	public void resize(int width, int height){
		
		//Sets our units to be in relation to screen size
		camera.viewportHeight = (float)Constants.viewportHeight;
		camera.viewportWidth = (Constants.viewportHeight / (float)height) * (float)width;
		camera.update();
		
		background_camera.viewportHeight =  Constants.bgViewportHeight;
		background_camera.viewportWidth = (Constants.bgViewportHeight / (float) height) * (float)width;
		background_camera.position.set(background_camera.viewportWidth / 2, background_camera.viewportHeight / 2, 100);
		background_camera.update();
		
		guiCamera.viewportHeight = Constants.bgViewportHeight;
		guiCamera.viewportWidth = (Constants.bgViewportHeight / (float) height) * (float)width;
		guiCamera.position.set(guiCamera.viewportWidth / 2, guiCamera.viewportHeight / 2, 100);
		guiCamera.update();
		
	}
	public void render(){
		renderBackground();
		renderWorld();
		renderGUI();
	}
	private void renderTransparency() {
		if(WallJumper.paused)
			batch.draw(pauseLayer.getTexture(), 0, 0, guiCamera.viewportWidth,  guiCamera.viewportHeight,
					 pauseLayer.getRegionX(), pauseLayer.getRegionY(),
						pauseLayer.getRegionWidth(), pauseLayer.getRegionHeight(), false, false);
			
	}
	public void destroy(){
		guiCamera = null;
		pauseButton = null;
		camera = null;
		pauseLayer = null;
		background_image = null;
		background_camera = null;
		weather.destroy();
		clearScene();
	}
	@Override
	public void dispose() {
		batch.dispose();
	}
	public void levelCompleteMenu() {
		SceneObject.setCamera(guiCamera);
		Image backgroundWindow = new Image(false, Assets.instance.pause.aniScroll, 0, 0, 400, 400){
			@Override
			public void onAnimationComplete() {
				Button levelMenu = new Button(true, Assets.instance.pause.buttonDown, Assets.instance.pause.buttonUp, 0, 0, 150, 100){
					@Override
					public boolean clickRelease() {
						World.controller.backTolevelMenu = true;
						return false;
						
					}
				};
				
				levelMenu.position.set(this.position.x + 30, this.position.y + 30);
				levelMenu.bounds.setPosition(levelMenu.position.x, levelMenu.position.y);
				getSceneObjects().add(levelMenu);
				
				Button nextLevelButton = new Button(true, Assets.instance.pause.buttonUp, Assets.instance.pause.buttonDown, 0, 0, 150, 100){
					@Override
					public boolean clickRelease() {
						World.controller.nextLevel = true;
						return false;
						
					}
				};
				nextLevelButton.position.set(this.position.x + 60 + levelMenu.dimension.x, this.position.y + 30);
				nextLevelButton.bounds.setPosition(nextLevelButton.position.x, nextLevelButton.position.y);
				getSceneObjects().add(nextLevelButton);
			}
		};
		backgroundWindow.setScale(1.8f);
		backgroundWindow.position.set(Constants.bgViewportWidth / 2 - backgroundWindow.dimension.x / 2, Constants.bgViewportHeight / 5);
		getSceneObjects().add(backgroundWindow);
		
		
		
		
		
	}
	public Array<SceneObject> getSceneObjects() {
		return sceneObjects;
	}
	

}
