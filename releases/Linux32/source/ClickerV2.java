import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ClickerV2 extends PApplet {




ArrayList<Ball> balls;
ArrayList<Ball> toRemove;
ArrayList<Player> players;
ArrayList<Particle> particles;
ArrayList<Particle> toRemoveParts;
ArrayList<PowerUp> powerups;
ArrayList<PowerUp> toRemovePowers;

int OBJECTIVE = 30000;

boolean gameOn = false;
int cardSize;
Player creating;
int playerID = 0;
int keyID;
int back = color(0);

int trippyTime = 0;



public void setup(){
  //size(1200, 700);
  
  frameRate(60);
  
  surface.setTitle("Bubbles | Cr\u00e9\u00e9 par CLOVIS");
  
  balls = new ArrayList();
  toRemove = new ArrayList();
  players = new ArrayList();
  particles = new ArrayList();
  toRemoveParts = new ArrayList();
  powerups = new ArrayList();
  toRemovePowers = new ArrayList();
  noStroke();
  new Ball();
  
  cardSize = (int) ((width-50)/4.0f);
  
  blendMode(ADD);
}


public void draw(){
  trippyTime--;
  if(trippyTime < 0){
    trippyTime = 0;
    background(red(back), green(back), blue(back), 255);
  }
  if(gameOn){
    back = color(0);
    drawGame();
  }else{
    fill(255);
    textSize(45);
    text("BUBBLES", 10, 50);
    textSize(12);
    text("ENTER : New player | LEFT / RIGHT : Switch player | SHIFT : Change color | BACKSPACE : Remove player", 500, 50);
    textSize(45);
    int y = 70;
    int x = 10;
    for(Player p : players){
      noStroke();
      fill((creating == p) ? 200 : 50);
      rect(x, y, cardSize, cardSize);
      p.drawCard(x, y);
      x += cardSize + 10;
      if(x + cardSize > width){
        x = 10;
        y += cardSize+10;
      }
    }
  }
}


public void drawGame(){
  noStroke();
  fill(255);
  textSize(12);
  float goodness = map(frameRate, 30, 60, 0, 255);
  fill(255-goodness, goodness, 0);
  text("Particules : "+particles.size() + "    FPS : "+(int)frameRate + "    Trippy : "+trippyTime, 5, 15);
  if(random(0, 100) < 1)
    new Ball();
  if(random(0, 250) < 1)
    newPower();
  for(PowerUp p : powerups)
    p.draw();
  for(PowerUp p : toRemovePowers){
    powerups.remove(p);
    new Particle(p.x, p.y, 5, 50, p.colour);
  }
  toRemovePowers.clear();
  for(Ball b : balls)
    b.draw();
  for(Ball b : toRemove){
    balls.remove(b);
    new Particle(b.x, b.y, 5, 50, color(255));
  }
  toRemove.clear();
  int x = 10;
  for(Player p : players){
    p.draw();
    x += 10;
    rect(0, x, (p.score*width)/OBJECTIVE, 10);
  }
  
  //loadPixels();
  for(Particle p : particles){
    p.draw();
  }
  //updatePixels();
  
  for(Particle p : toRemoveParts)
    particles.remove(p);
  toRemoveParts.clear();
}




public void keyPressed(){
  if(key == TAB)  gameOn = !gameOn;
  if(gameOn){
    for(Player p : players)
      p.keyPress(key);
  }else if(key == ENTER || key == RETURN){
    creating = new Player('_', ' ', ' ', ' ', ' ');
    keyID = 0;
    playerID = players.size()-1;
  }else if(keyCode == LEFT && playerID > 0){
    playerID--;
    keyID = 0;
    creating = players.get(playerID);
  }else if(keyCode == RIGHT && playerID < players.size()-1){
    playerID++;
    keyID = 0;
    creating = players.get(playerID);
  }else if(key == BACKSPACE && players.size() > 0){
    players.remove(players.size()-1);
  }else if(keyCode == SHIFT && creating != null){
    creating.colour = color(random(255), random(255), random(255));
  }else{
    if(creating != null){
      if(keyID <= 3){
        creating.keys[keyID].un = key;
        creating.keys[keyID+1].un = '_';
        keyID++;
      }else{
        keyID = 0;
        creating = null;
      }
    }
  }
}

public void keyReleased(){
  if(gameOn)
    for(Player p : players)
      p.keyRelease(key);
}

public void newPower(){
  switch((int) random(9)){
    case 0: new SpeedUpgrade(); break;
    case 1: new SpeedDowngrade(); break;
    case 2: new MultiplePowers(); break;
    case 3: new Teleportation(); break;
    case 4: new Attract(); break;
    case 5: new AttractForever(); break;
    case 6: new Distract(); break;
    case 7: new TrippyVision(); break;
    case 8: new SlowDown(); break;
    default: break;
  }
}

public float square(float n){return n*n;}


class Particle{
  float x, y;
  float speedX, speedY;
  int time;
  int colour;
  int size;
  boolean random = false;
  
  Particle(float X, float Y, float speed, float angle, int colour){
    x = X; y = Y;
    speedX = speed * cos(angle) * random(0.1f, 1.2f);
    speedY = speed * sin(angle) * random(0.1f, 1.2f);
    time = 0;
    this.colour = colour;
    particles.add(this);
    size = (int) (random(0.5f, 2.5f)*speed/10) + 1;
  }
  
  Particle(float X, float Y, float speed, int colour){
    this(X, Y, speed, random(2*PI), colour);
  }
  
  Particle(float X, float Y, float speed, int number, int colour){
    this(X, Y, speed, colour);
    for(int i = 0; i < number-1; i++)
      new Particle(X, Y, speed, colour);
  }
  
  Particle(float X, float Y, float speed, int number, boolean isStatic){
    this(X, Y, speed, color(random(0, 255), random(0, 255), random(0, 255)));
    random = !isStatic;
    for(int i = 0; i < number-1; i++){
      Particle p = new Particle(X, Y, speed, color(random(0, 255), random(0, 255), random(0, 255)));
      p.random = !isStatic; p.size = 4;
    }
  }
  
  public void draw(){
    speedY += 0.02f;
    x += speedX;
    y += speedY;
    time++;
    if(y >= height && !toRemove.contains(this))
      toRemoveParts.add(this);
    
    if(x < 0)      x = width-1;
    if(x > width)  x = 1;
    
    /*for(int X = - size; X < size; X++){
      for(int Y = - size; Y < size; Y++){
        try{
          int loc = int(X+x) + int(Y+y) * width;
          color current = pixels[loc];
          int distance = int(X*X+Y*Y)*2;
          pixels[loc] = color(red(current) + red(colour)/distance, 
                              green(current) + green(colour)/distance, 
                              blue(current) + blue(colour)/distance);
        }catch(ArrayIndexOutOfBoundsException e){}
      }
    }*/
    
    if(size > 3){
      fill(colour, 50);
      ellipse(x, y, size * 4, size * 4);
      fill(colour, 200);
      ellipse(x, y, size * 2, size * 2);
      fill(colour);
      ellipse(x, y, size, size);
    }else{
      fill(colour);
      ellipse(x, y, size*2, size*2);
    }
  }
}


class Player{
  float x, y;
  float speed, speedBonus;
  float shootingTime, maxShootingTime = 1;
  float score;
  Couple<Character, Boolean>[] keys;
  int colour;
  ArrayList<Ball> myBalls;
  
  Player(char keyUp, char keyDown, char keyLeft, char keyRight, char keyShoot){
    keys = new Couple[]{new Couple(keyUp, false),
                        new Couple(keyDown, false),
                        new Couple(keyLeft, false),
                        new Couple(keyRight, false),
                        new Couple(keyShoot, false)};
    colour = color(random(50, 255), random(50, 255), random(50, 255));
    speed = 5;
    x = width/2.0f;
    y = height/2.0f;
    players.add(this);
    score = 0;
    shootingTime = 60;
    speedBonus = 2;
    myBalls = new ArrayList();
  }
  
  public void draw(){
    act();
    if(score > 0)
      score -= 0.1f;
    if(score > OBJECTIVE){
      gameOn = false; back = colour;
      for(Player p : players)  p.score = 0;
    }
    if(speedBonus > 0)    speedBonus -= 0.01f;
    if(speedBonus < 0)    speedBonus += 0.01f;
    speed = 10-score*8/OBJECTIVE + speedBonus;
    if(speed < 0.1f)
      speed = 0.1f;
    shootingTime--;
    if(shootingTime <= 0){
      shootingTime = maxShootingTime;
      keys[4].deux = true;
    }
    if(x < 0)      x = width-1;
    if(x > width)  x = 1;
    if(y < 0)      y = height-1;
    if(y > height) y = 1;
    
    fill(colour);
    rect(x-2.5f, y-2.5f, 5, 5);
    //textSize(12);
    //text(score, x, y);
    
    if(score > 0.95f * OBJECTIVE && random(10) < 1){   new Particle(x, y, 1, 5, false); trippyTime += (int)random(1, 10); }
    for(Ball b : myBalls)
      b.goTo(x, y);
  }
  
  public void keyPress(char k){
    for(Couple<Character, Boolean> c : keys){
      if(c.un == k){
        c.deux = true;
      }
    }
  }
  
  public void keyRelease(char k){
    for(Couple<Character, Boolean> c : keys){
      if(c.un == k){
        c.deux = false;
      }
    }
  }
  
  public void act(){
    if(keys[0].deux)  y -= speed;
    if(keys[1].deux)  y += speed;
    if(keys[2].deux)  x -= speed;
    if(keys[3].deux)  x += speed;
    if(keys[4].deux){
      keys[4].deux = false;
      new Particle(x, y, 0.15f, 1, colour);
      for(Ball b : balls){
        if(b.contains(x, y)){
          int s = (int) (500-square(b.size/5));
          println("Points gagn\u00e9s : " + s);
          score += s > 0 ? s : 0;
          if(maxShootingTime > 1)
            maxShootingTime = 1;
        }
      }
      for(PowerUp p : powerups){
        if(p.contains(x, y)){
          p.run(this);
          if(p.isDangerous())
            break;
        }
      }
    }
  }
  
  public void drawCard(int x, int y){
    noStroke();
    int cinquieme = (int) (cardSize/5.0f);
    //Couleur
    fill(colour);
    rect(x, y, cardSize-cinquieme, cinquieme);
    //Supprimer
    fill(255, 0, 0);
    rect(x+cardSize-cinquieme, y, cinquieme, cinquieme);
    //Touches
    stroke(255);
    fill(100);
    rect(x+2*cinquieme, y+1.5f*cinquieme, cinquieme, cinquieme);
    rect(x+2*cinquieme, y+2.5f*cinquieme, cinquieme, cinquieme);
    rect(x+1*cinquieme, y+2.5f*cinquieme, cinquieme, cinquieme);
    rect(x+3*cinquieme, y+2.5f*cinquieme, cinquieme, cinquieme);
    fill(255);
    text(keys[0].un, x+2.3f*cinquieme, y+2.3f*cinquieme);
    text(keys[1].un, x+2.3f*cinquieme, y+3.3f*cinquieme);
    text(keys[2].un, x+1.3f*cinquieme, y+3.3f*cinquieme);
    text(keys[3].un, x+3.3f*cinquieme, y+3.3f*cinquieme);
  }
}

class Couple<T, V>{
  T un;
  V deux;
  Couple(T u, V d){
    un = u; deux = d;
  }
}


abstract class PowerUp{
  float x, y;
  float size = 40;
  int colour, textColour;
  char symbol;
  int timeout = 1000;
  
  PowerUp(int colour, int textColour, char s){
    x = random(0, width);
    y = random(0, height);
    this.colour = colour;
    this.textColour = textColour;
    symbol = s;
    powerups.add(this);
  }
  
  public void draw(){
    timeout--;
    if(timeout < 0)
      toRemovePowers.add(this);
    fill(colour);
    ellipse(x, y, size, size);
    fill(textColour);
    textSize(20);
    text(symbol, x-6, y+7);
  }
  
  public boolean contains(float nx, float ny){
    if(square(nx - x) + square(ny - y) < square(size/2.0f)){
      if(!toRemovePowers.contains(this))
        toRemovePowers.add(this);
      return true;
    }else{
      return false;
    }
  }
  
  public abstract void run(Player p);
  public boolean isDangerous() {return false;}
}









class SpeedUpgrade extends PowerUp{
  SpeedUpgrade(){super(color(255, 255, 0), color(0), '\u00bb');}
  public void run(Player p){p.speedBonus += 3;}
}


class SpeedDowngrade extends PowerUp{
  SpeedDowngrade(){super(color(150, 150, 0), color(0), '\u00ab');}
  public void run(Player p){p.speedBonus -= 5;}
}



class MultiplePowers extends PowerUp{
  MultiplePowers(){super(color(255), color(0), '+');}
  public void run(Player p){ new Ball(); new Ball(); new Ball(); }
  public boolean isDangerous(){return true;}
}

class Teleportation extends PowerUp{
  Teleportation(){super(color(125), color(0), '?');}
  public void run(Player p){ background(255); new Particle(x, y, 30, 100, true);
    for(Player d : players){
      d.x = random(0, width); d.y = random(0, height);}
    new Particle(p.x, p.y, 30, 100, true);
  }
  public boolean isDangerous(){return true;}
}


class Attract extends PowerUp{
  Attract(){super(color(0, 0, 125), color(255), '!');}
  public void run(Player p){
    for(Ball b : balls){
      b.goTo(x, y); b.goTo(x, y); b.goTo(x, y);}
  }
}

class AttractForever extends PowerUp{
  AttractForever(){super(color(125, 125, 255), color(255), '#');}
  public void run(Player p){
    for(Ball b : balls)
      p.myBalls.add(b);
  }
}


class Distract extends PowerUp{
  Distract(){super(color(0, 0, 125), color(255), '?');}
  public void run(Player p){
    for(Ball b : balls){
      b.goFrom(x, y); b.goFrom(x, y); b.goFrom(x, y);}
  }
}


class TrippyVision extends PowerUp{
  TrippyVision(){super(color(0), color(255), '\u00a4');}
  public void run(Player p){ trippyTime = 200; }
}

class SlowDown extends PowerUp{
  SlowDown(){super(color(255, 0, 0), color(255), '\u00bf');}
  public void run(Player p){ for(Player p2 : players) if(p2 != p) p2.maxShootingTime += 15; }
}


class Ball{
  float x, y;
  float size;
  Couple<Float, Float> speeds;
  
  Ball(){
    x = random(0, width);
    y = random(0, height);
    size = random(5, 100);
    balls.add(this);
    speeds = new Couple(0.0f, 0.0f);
  }
  
  Ball(int x, int y){
    this.x = x;
    this.y = y;
    size = random(5, 100);
    balls.add(this);
  }
  
  public void draw(){
    x += speeds.un;
    y += speeds.deux;
    size -= 0.1f;
    if(size <= 0 && !toRemove.contains(this))
      toRemove.add(this);
    
    if(x < 0)      x = width-1;
    if(x > width)  x = 1;
    if(y < 0)      y = height-1;
    if(y > height) y = 1;
    
    fill(255);
    ellipse(x, y, size, size);
  }
  
  public boolean contains(float nx, float ny){
    if(square(nx - x) + square(ny - y) < square(size/2.0f)){
      if(!toRemove.contains(this))
        toRemove.add(this);
      return true;
    }else{
      return false;
    }
  }
  
  public void goTo(float nx, float ny){
    float l = sqrt(square(nx - x) + square(ny - y)) + size*10;
    speeds.un += 2 * (nx - x) / l;
    speeds.deux += 2 * (ny - y) / l;
  }
  
  public void goFrom(float nx, float ny){
    float l = sqrt(square(nx - x) + square(ny - y)) + size*10;
    speeds.un -= 2 * (nx - x) / l;
    speeds.deux -= 2 * (ny - y) / l;
  }
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ClickerV2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
