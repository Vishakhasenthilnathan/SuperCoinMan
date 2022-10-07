package com.vish.supercoinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class SuperCoinMan extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture[] man;
    int manState = 0;
    int pause = 0;
    int manInPositionOfHeight = 0;
    float gravity = 0.2f;
    float velocity = 0f;
    Random random;
    Rectangle manShape;

    ArrayList<Integer> coinXs = new ArrayList<>();
    ArrayList<Integer> coinYs = new ArrayList<>();
    ArrayList<Rectangle> coinRectangles = new ArrayList<>();
    Texture coin;
    int coinCount;

    ArrayList<Integer> bombXs = new ArrayList<>();
    ArrayList<Integer> bombYs = new ArrayList<>();
    ArrayList<Rectangle> bombRectangles = new ArrayList<>();
    Texture bomb;
    int bombCount;

    int score;
    BitmapFont scoreBoard;
    int gameState;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        man = new Texture[4];
        for (int i = 0; i < 4; i++) {
            int number = i + 1;
            man[i] = new Texture("frame-" + number + ".png");
        }
        manInPositionOfHeight = Gdx.graphics.getHeight() / 2;
        coin = new Texture("coin.png");
        bomb = new Texture("bomb.png");
        random = new Random();
        scoreBoard = new BitmapFont();
        scoreBoard.setColor(Color.BLUE);
        scoreBoard.getData().setScale(10);
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {
            //live
            coinsAndBombs();
        } else if (gameState == 0) {
            //waiting to start
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 2) {
            //game over ,  restart
            if (Gdx.input.justTouched()) {
                restartGame();
            }
        }

        if (gameState == 2) {
            //show dizzy man pic
            Texture gameover = new Texture("dizzy-1.png");
            batch.draw(gameover, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, Gdx.graphics.getHeight() / 2 - man[manState].getHeight() / 2);
        } else {
            onTouch();
        }
        bombAndCoinCollision();

        scoreBoard.draw(batch, String.valueOf(score), 100, 200);
        batch.end();
    }

    private void restartGame() {
        gameState = 1;
        manInPositionOfHeight = Gdx.graphics.getHeight() / 2;
        score = 0;
        velocity = 0;
        gravity = 0;
        coinXs.clear();
        coinYs.clear();
        coinCount = 0;
        coinRectangles.clear();
        bombXs.clear();
        bombYs.clear();
        bombCount = 0;
        bombRectangles.clear();
    }

    private void onTouch() {
        //if screen is touched then the velocity is reduced, then height in each second will increase
        if (Gdx.input.justTouched()) {
            velocity = -10;
        }
        if (pause < 8) {
            pause++;
        } else {
            pause = 0;
            if (manState < 3) {
                manState++;
            } else {
                manState = 0;
            }
        }
        //each second the position of man in height will be reduced by the velocity + gravity hence will keep going down
        velocity += gravity;
        manInPositionOfHeight -= velocity;
        //goes below the screen then set it to the bottom
        if (manInPositionOfHeight <= 0) {
            manInPositionOfHeight = 0;
        }
        if (manInPositionOfHeight > Gdx.graphics.getHeight()) {
            manInPositionOfHeight = 0;
        }
        batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[0].getWidth() / 2, manInPositionOfHeight);

        manShape = new Rectangle(Gdx.graphics.getWidth() / 2 - man[0].getWidth() / 2, manInPositionOfHeight, man[manState].getWidth(), man[manState].getHeight());
    }

    private void bombAndCoinCollision() {
        for (int i = 0; i < coinRectangles.size(); i++) {
            if (Intersector.overlaps(manShape, coinRectangles.get(i))) {
                Gdx.app.log("Coin", "Collision!");
                score++;
                coinRectangles.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);
                break;
            }
        }
        for (int i = 0; i < bombRectangles.size(); i++) {
            if (Intersector.overlaps(manShape, bombRectangles.get(i))) {
                Gdx.app.log("Bomb", "Bombed!");
                gameState = 2;
            }
        }
    }

    private void coinsAndBombs() {
        //for every 50render times coin will be generated
        if (coinCount < 50) {
            coinCount++;
        } else {
            coinCount = 0;
            makeCoin();
        }
        //This is used to identify coin Collision with man
        coinRectangles.clear();

        for (int i = 0; i < coinXs.size(); i++) {
            batch.draw(coin, coinXs.get(i), coinYs.get(i));
            coinXs.set(i, coinXs.get(i) - 4);
            coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
        }

        bombRectangles.clear();
        //for every 100render secs coin will be generated
        if (bombCount < 100) {
            bombCount++;
        } else {
            bombCount = 0;
            makeBomb();
        }
        for (int i = 0; i < bombXs.size(); i++) {
            batch.draw(bomb, bombXs.get(i), bombYs.get(i));
            //We are slowing moving the bombs/coins by some value towards the screen
            bombXs.set(i, bombXs.get(i) - 8);
            bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
        }
    }

    @Override
    public void dispose() {
        batch.dispose();

    }

    public void makeCoin() {
        //coin at random height generated based on height of app
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        coinYs.add((int) height);
        coinXs.add(Gdx.graphics.getWidth());
    }

    public void makeBomb() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        bombYs.add((int) height);
        bombXs.add(Gdx.graphics.getWidth());
    }
}
