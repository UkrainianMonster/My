package ru.myitschool.sungdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

public class MyGdx extends ApplicationAdapter {
	public static final int SCR_WIDTH = 1280, SCR_HEIGHT = 720;

	SpriteBatch batch;
	OrthographicCamera camera;
	Vector3 touch;
	BitmapFont font;

	Texture[] imgMosq = new Texture[11];
	Texture imgBG;
	Sound[] sndMosq = new Sound[3];

	Mosquito[] mosq = new Mosquito[100];
	Player[] players = new Player[5];
	int frags;
	long timeStart, timeCurrent;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
		touch = new Vector3();
		generateFont();

		imgBG = new Texture("background.jpg");
		for (int i = 0; i < imgMosq.length; i++) {
			imgMosq[i] = new Texture("mosq"+i+".png");
		}
		for (int i = 0; i < sndMosq.length; i++) {
			sndMosq[i] = Gdx.audio.newSound(Gdx.files.internal("mosq"+i+".mp3"));
		}

		for (int i = 0; i < mosq.length; i++) {
			mosq[i] = new Mosquito();
		}

		for (int i = 0; i < players.length; i++) {
			players[i] = new Player("Никто", 0);
		}

		timeStart = TimeUtils.millis();
	}

	@Override
	public void render () {
		// обработка касаний экрана
		if(Gdx.input.justTouched()){
			touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touch);
			for (int i = mosq.length-1; i >= 0; i--) {
				if(mosq[i].isAlive && mosq[i].hit(touch.x, touch.y)) {
					frags++;
					sndMosq[MathUtils.random(0, 2)].play();
					if(frags == mosq.length) gameOver();
					break;
				}
			}
		}

		// события игры
		for (int i = 0; i < mosq.length; i++) {
			mosq[i].move();
		}
		timeCurrent = TimeUtils.millis() - timeStart;
		String timeStr = timeCurrent/1000/60/60+":"+timeCurrent/1000/60%60/10+timeCurrent/1000/60%60%10+":"+timeCurrent/1000%60/10+timeCurrent/1000%60%10;

		// отрисовка всей графики
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(imgBG, 0, 0, SCR_WIDTH, SCR_HEIGHT);
		for (int i = 0; i < mosq.length; i++) {
			batch.draw(imgMosq[mosq[i].faza], mosq[i].getX(), mosq[i].getY(), mosq[i].width, mosq[i].height, 0, 0, 500, 500, mosq[i].isFlip(), false);
		}
		font.draw(batch, "УБИЙСТВА: "+frags, 10, SCR_HEIGHT-10);
		font.draw(batch, timeStr, SCR_WIDTH-200, SCR_HEIGHT-10);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		for (int i = 0; i < imgMosq.length; i++) {
			imgMosq[i].dispose();
		}
	}

	void generateFont(){
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("russianpunk.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.color = new Color(1, 1, 0.4f, 1);
		parameter.size = 40;
		parameter.borderColor = Color.BLACK;
		parameter.borderWidth = 3;
		String str = "";
		for (char i = 0x20; i < 0x7B; i++) str += i;
		for (char i = 0x401; i < 0x452; i++) str += i;
		parameter.characters = str;
		font = generator.generateFont(parameter);
		generator.dispose();
	}

	void gameOver(){

	}
}
