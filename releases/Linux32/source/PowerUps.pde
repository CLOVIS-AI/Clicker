

abstract class PowerUp{
  float x, y;
  float size = 40;
  color colour, textColour;
  char symbol;
  int timeout = 1000;
  
  PowerUp(color colour, color textColour, char s){
    x = random(0, width);
    y = random(0, height);
    this.colour = colour;
    this.textColour = textColour;
    symbol = s;
    powerups.add(this);
  }
  
  void draw(){
    timeout--;
    if(timeout < 0)
      toRemovePowers.add(this);
    fill(colour);
    ellipse(x, y, size, size);
    fill(textColour);
    textSize(20);
    text(symbol, x-6, y+7);
  }
  
  boolean contains(float nx, float ny){
    if(square(nx - x) + square(ny - y) < square(size/2.0)){
      if(!toRemovePowers.contains(this))
        toRemovePowers.add(this);
      return true;
    }else{
      return false;
    }
  }
  
  abstract void run(Player p);
  boolean isDangerous() {return false;}
}









class SpeedUpgrade extends PowerUp{
  SpeedUpgrade(){super(color(255, 255, 0), color(0), '»');}
  void run(Player p){p.speedBonus += 3;}
}


class SpeedDowngrade extends PowerUp{
  SpeedDowngrade(){super(color(150, 150, 0), color(0), '«');}
  void run(Player p){p.speedBonus -= 5;}
}



class MultiplePowers extends PowerUp{
  MultiplePowers(){super(color(255), color(0), '+');}
  void run(Player p){ new Ball(); new Ball(); new Ball(); }
  boolean isDangerous(){return true;}
}

class Teleportation extends PowerUp{
  Teleportation(){super(color(125), color(0), '?');}
  void run(Player p){ background(255); new Particle(x, y, 30, 100, true);
    for(Player d : players){
      d.x = random(0, width); d.y = random(0, height);}
    new Particle(p.x, p.y, 30, 100, true);
  }
  boolean isDangerous(){return true;}
}


class Attract extends PowerUp{
  Attract(){super(color(0, 0, 125), color(255), '!');}
  void run(Player p){
    for(Ball b : balls){
      b.goTo(x, y); b.goTo(x, y); b.goTo(x, y);}
  }
}

class AttractForever extends PowerUp{
  AttractForever(){super(color(125, 125, 255), color(255), '#');}
  void run(Player p){
    for(Ball b : balls)
      p.myBalls.add(b);
  }
}


class Distract extends PowerUp{
  Distract(){super(color(0, 0, 125), color(255), '?');}
  void run(Player p){
    for(Ball b : balls){
      b.goFrom(x, y); b.goFrom(x, y); b.goFrom(x, y);}
  }
}


class TrippyVision extends PowerUp{
  TrippyVision(){super(color(0), color(255), '¤');}
  void run(Player p){ trippyTime = 200; }
}

class SlowDown extends PowerUp{
  SlowDown(){super(color(255, 0, 0), color(255), '¿');}
  void run(Player p){ for(Player p2 : players) if(p2 != p) p2.maxShootingTime += 15; }
}