package com.vish.supercoinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Random;

public class SuperCoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manState=0;
	int pause=0;
	int manInPositionOfHeight = 0;
	float gravity =0.2f;
	float velocity =0f;
	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	Texture coin;
	int coinCount;
	Random random;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		for (int i = 0; i <4; i++) {
			int number = i+1;
			man[i] = new Texture("frame-"+number+".png");
		}
		manInPositionOfHeight = Gdx.graphics.getHeight()/2;
		coin = new Texture("coin.png");
		random = new Random();
	}

	public void makeCoin(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(coinCount<100){
			coinCount++;
		}else{
			coinCount=0;
			makeCoin();
		}

		for (int i = 0; i < coinXs.size(); i++) {
			batch.draw(coin,coinXs.get(i),coinYs.get(i));
			coinXs.set(i,coinXs.get(i)-5);
		}

		//if screen is touched then the velocity is reduced, then height in each second will increase
		if(Gdx.input.justTouched()){
			velocity=-10;
		}
		if(pause<8){
			pause++;
		}else{
			pause=0;
			if(manState<3){
				manState++;
			}else {
				manState=0;
			}
		}
		//each second the position of man in height will be reduced by the velocity + gravity hence will keep going down
		velocity += gravity;
		manInPositionOfHeight -= velocity;
		//goes below the screen then set it to the bottom
		if(manInPositionOfHeight <=0){
			manInPositionOfHeight =0;
		}
		batch.draw(man[manState],Gdx.graphics.getWidth()/2 - man[0].getWidth()/2, manInPositionOfHeight);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
