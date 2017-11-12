

class Player{
  float x, y;
  float speed, speedBonus;
  float shootingTime, maxShootingTime = 1;
  float score;
  Couple<Character, Boolean>[] keys;
  color colour;
  ArrayList<Ball> myBalls;
  
  Player(char keyUp, char keyDown, char keyLeft, char keyRight, char keyShoot){
    keys = new Couple[]{new Couple(keyUp, false),
                        new Couple(keyDown, false),
                        new Couple(keyLeft, false),
                        new Couple(keyRight, false),
                        new Couple(keyShoot, false)};
    colour = color(random(50, 255), random(50, 255), random(50, 255));
    speed = 5;
    x = width/2.0;
    y = height/2.0;
    players.add(this);
    score = 0;
    shootingTime = 60;
    speedBonus = 2;
    myBalls = new ArrayList();
  }
  
  void draw(){
    act();
    if(score > 0)
      score -= 0.1;
    if(score > OBJECTIVE){
      gameOn = false; back = colour;
      for(Player p : players)  p.score = 0;
    }
    if(speedBonus > 0)    speedBonus -= 0.01;
    if(speedBonus < 0)    speedBonus += 0.01;
    speed = 10-score*8/OBJECTIVE + speedBonus;
    if(speed < 0.1)
      speed = 0.1;
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
    rect(x-2.5, y-2.5, 5, 5);
    //textSize(12);
    //text(score, x, y);
    
    if(score > 0.95 * OBJECTIVE && random(10) < 1){   new Particle(x, y, 1, 5, false); trippyTime += (int)random(1, 10); }
    for(Ball b : myBalls)
      b.goTo(x, y);
  }
  
  void keyPress(char k){
    for(Couple<Character, Boolean> c : keys){
      if(c.un == k){
        c.deux = true;
      }
    }
  }
  
  void keyRelease(char k){
    for(Couple<Character, Boolean> c : keys){
      if(c.un == k){
        c.deux = false;
      }
    }
  }
  
  void act(){
    if(keys[0].deux)  y -= speed;
    if(keys[1].deux)  y += speed;
    if(keys[2].deux)  x -= speed;
    if(keys[3].deux)  x += speed;
    if(keys[4].deux){
      keys[4].deux = false;
      new Particle(x, y, 0.15, 1, colour);
      for(Ball b : balls){
        if(b.contains(x, y)){
          int s = (int) (500-square(b.size/5));
          println("Points gagnés : " + s);
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
  
  void drawCard(int x, int y){
    noStroke();
    int cinquieme = (int) (cardSize/5.0);
    //Couleur
    fill(colour);
    rect(x, y, cardSize-cinquieme, cinquieme);
    //Supprimer
    fill(255, 0, 0);
    rect(x+cardSize-cinquieme, y, cinquieme, cinquieme);
    //Touches
    stroke(255);
    fill(100);
    rect(x+2*cinquieme, y+1.5*cinquieme, cinquieme, cinquieme);
    rect(x+2*cinquieme, y+2.5*cinquieme, cinquieme, cinquieme);
    rect(x+1*cinquieme, y+2.5*cinquieme, cinquieme, cinquieme);
    rect(x+3*cinquieme, y+2.5*cinquieme, cinquieme, cinquieme);
    fill(255);
    text(keys[0].un, x+2.3*cinquieme, y+2.3*cinquieme);
    text(keys[1].un, x+2.3*cinquieme, y+3.3*cinquieme);
    text(keys[2].un, x+1.3*cinquieme, y+3.3*cinquieme);
    text(keys[3].un, x+3.3*cinquieme, y+3.3*cinquieme);
  }
}

class Couple<T, V>{
  T un;
  V deux;
  Couple(T u, V d){
    un = u; deux = d;
  }
}