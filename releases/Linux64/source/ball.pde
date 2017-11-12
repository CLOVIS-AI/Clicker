

class Ball{
  float x, y;
  float size;
  Couple<Float, Float> speeds;
  
  Ball(){
    x = random(0, width);
    y = random(0, height);
    size = random(5, 100);
    balls.add(this);
    speeds = new Couple(0.0, 0.0);
  }
  
  Ball(int x, int y){
    this.x = x;
    this.y = y;
    size = random(5, 100);
    balls.add(this);
  }
  
  void draw(){
    x += speeds.un;
    y += speeds.deux;
    size -= 0.1;
    if(size <= 0 && !toRemove.contains(this))
      toRemove.add(this);
    
    if(x < 0)      x = width-1;
    if(x > width)  x = 1;
    if(y < 0)      y = height-1;
    if(y > height) y = 1;
    
    fill(255);
    ellipse(x, y, size, size);
  }
  
  boolean contains(float nx, float ny){
    if(square(nx - x) + square(ny - y) < square(size/2.0)){
      if(!toRemove.contains(this))
        toRemove.add(this);
      return true;
    }else{
      return false;
    }
  }
  
  void goTo(float nx, float ny){
    float l = sqrt(square(nx - x) + square(ny - y)) + size*10;
    speeds.un += 2 * (nx - x) / l;
    speeds.deux += 2 * (ny - y) / l;
  }
  
  void goFrom(float nx, float ny){
    float l = sqrt(square(nx - x) + square(ny - y)) + size*10;
    speeds.un -= 2 * (nx - x) / l;
    speeds.deux -= 2 * (ny - y) / l;
  }
}