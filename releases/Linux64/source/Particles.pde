

class Particle{
  float x, y;
  float speedX, speedY;
  int time;
  color colour;
  int size;
  boolean random = false;
  
  Particle(float X, float Y, float speed, float angle, color colour){
    x = X; y = Y;
    speedX = speed * cos(angle) * random(0.1, 1.2);
    speedY = speed * sin(angle) * random(0.1, 1.2);
    time = 0;
    this.colour = colour;
    particles.add(this);
    size = (int) (random(0.5, 2.5)*speed/10) + 1;
  }
  
  Particle(float X, float Y, float speed, color colour){
    this(X, Y, speed, random(2*PI), colour);
  }
  
  Particle(float X, float Y, float speed, int number, color colour){
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
  
  void draw(){
    speedY += 0.02;
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