// Module aliases for Matter.js
const Engine = Matter.Engine,
      Render = Matter.Render,
      Runner = Matter.Runner,
      Bodies = Matter.Bodies,
      World = Matter.World;

// Create an engine
const engine = Engine.create();

// Create a renderer to display the physics simulation
const render = Render.create({
  element: document.body,    // Attach to the body element
  engine: engine,            // Connect to the physics engine
  options: {
    width: window.innerWidth,  // Full-width canvas
    height: window.innerHeight, // Full-height canvas
    wireframes: false,          // Render filled shapes instead of wireframes
  }
});

// STACK TO FIRE AT - TEMPORARY
let stack = Matter.Composites.stack(1075,200,6,6,0,0,function(x,y){
  return Matter.Bodies.polygon(x,y,20,20);
});

// PLATFORM TO REST STACK ON - TEMPORARY
const platform = Bodies.rectangle(1200, 500, 300, 20, {
  isStatic: true,
  render: {
    fillStyle: 'black'
  }
});

// BALL ATTACKED TO SLING FOR FIRE
let ball = Matter.Bodies.circle(300, 400, 20,{
  render: {
    fillStyle: 'orange'
  }
});
let sling = Matter.Constraint.create({
  pointA: {x:300, y:400},
  bodyB: ball,
  stiffness: 0.05
});

// MOUSE + MOUSE CONSTRAINT
let mouse = Matter.Mouse.create(render.canvas);
let mouseConstraint = Matter.MouseConstraint.create(engine, {
  mouse: mouse,
  stiffness: 0.2,  // This controls how stiff the constraint is
    angularStiffness: 0.2, // Controls the angular stiffness
  constraint: {
    render: {visible: false}
  }
});
render.mouse = mouse;

let firing = false;
Matter.Events.on(mouseConstraint, 'enddrag', function(e){
  if(e.body === ball) firing = true;
});
Matter.Events.on(engine, 'afterUpdate', function(){
  if(firing && Math.abs(ball.position.x - 300)<20 && Math.abs(ball.position.y-400)<20){
    ball = Matter.Bodies.circle(300,400,20,{
      render: {
        fillStyle: 'orange'
      }
    });
    Matter.World.add(engine.world, ball);
    sling.bodyB = ball;
    firing = false;
  }
});

Matter.Events.on(engine, 'collisionStart', function(event) {
  const pairs = event.pairs;
  
  pairs.forEach(pair => {
    // Check if the ball is one of the bodies involved in the collision
    if (pair.bodyA === ball || pair.bodyB === ball) {
      console.log('Collision detected with ball!');
    }
  });
});

// Add the box and ground to the world
World.add(engine.world, [platform, ball, stack, sling, mouseConstraint]);

// Run the engine
Matter.Runner.run(engine);

// Run the renderer
Render.run(render);