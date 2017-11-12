


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
color back = color(0);

int trippyTime = 0;



void setup(){
  //size(1200, 700);
  fullScreen();
  frameRate(60);
  
  surface.setTitle("Bubbles | Créé par CLOVIS");
  
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


void draw(){
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


void drawGame(){
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




void keyPressed(){
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

void keyReleased(){
  if(gameOn)
    for(Player p : players)
      p.keyRelease(key);
}

void newPower(){
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

float square(float n){return n*n;}