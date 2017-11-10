# gameoff

Game for game off game jam.

Trello: https://trello.com/b/zovNNphw

# Concept

Action, Puzzle Platformer either 1 player or ideally 2 player couch co-op.
You control a cyborg (brain) that can move between two mechanical bodies. You can throw/drop the  brain and move it between the two bodies to change which your are controlling.

One player is shorter and has a low jump. One player is taller, and has a higher jump. Between these two different characteristics and the trading off with the power unit, many options for puzzles and co-operation exists.

Each level will be a single screen level with an exit. Once you exit, no back tracking- so linear progression. However sometimes exit is off the top, off the side, or off the bottom. The goal is to escape. Deaths result in restarting the level, and number of deaths and total time is tracked.

# Dimensions and Tools

** Note will be switching to 32 pixel tiles - double resolution - artist would like a higher def look - should have some initial art next week **

Using 16 pixel square tiles, with a screen resolution of 640x360. Allows for 40 tiles wide, and 22.5 tall. Each map is 40x23 with the top row of tiles slightly clipped. This resolution then scales nicely to 720P or 1080P displays for a retro/pixel look.

Used Tiled as level editor.

Two tile layers: background, and platforms.
One object layer: objects

# Controls

* Desktop Controls: 
    (Singleplayer)
    - Arrow keys to move left or right
    - Enter to Jump
    - Space to drop brain.
    - z to switch players

In one player mode, whichever unit has the brain is under your control. When the brain is dropped, arrow keys move it- it cannot jump.
In two player mode, each controls one player - whoever drops the brain controls it.



# Brainstorming

Objects to use in puzzle elements:

* Moving platforms - these can be on timer, triggered by contact, or triggered by a switch. Different sizes and can move horizontally or vertically. Could be interesting puzzles putting one character on platform, then throwing power unit to other who works the switch, etc. As well as more standard uses.
* Switches- hidden and visible switches - some are one way - on contact triggered and stays triggered, some can be toggled and are visible. Allow for triggering events within the level, such as moving platforms, and anything that can be on/off, etc.
* Moveable Blocks - players can move to change shape of level, allow for new areas to be reached by jumping on block etc.
* Breakable Blocks - could break on contact, or switches - could trap you, or allow to pass certain areas.
* Standard "death" blocks/spikes - so static tiles that are deadly and to be avoided
* Moving enemies - some jumpable, some not, various movement patterns and triggers
* Moving enemies/things that force quick choices and harder platforming - so requires some action sequences.
* Lasers/Beams on timers, triggers, switches
* Lasers/Beams that can be reflected - so could pair with reflector objects that could be moved. Beam could also destroy power unit, and/or blocks to add other puzzle elements.
* General idea is with platforms and other elements could use these to transport power unit back to other player strategically. So ride a platform down, but can't throw it up, but exit platform and throw or drop power unit on platform so it rides back up to other player, etc.
* Water/liquid - changes physics when it
* Fans - push characters, objects in some direction
* Trampolines - the usual, could be static or moveable, or moving
* Explosives - can be moved, and triggered - perhaps by jumping on them, maybe by a switch, destroys blocks and/or enemies etc. in a range
* Gravity - could play with tiles/areas where gravity changes
* Light - could have light/dark - so stage goes dark or lit up based on switches, timer, etc.
* Teleports - linked to other teleport locations in level - power unit moves through it as well which could add interesting element - possibly lasers go through it as well, and maybe moveable blocks and enemies? Could also have one way teleports which allow for interesting puzzles.
* Slippery Tiles - tiles with little (or no) friction
* Sticky Tiles - could have tiles that once you're on you're stuck - maybe a switch can change their state or another event.
* Bigger Enemies/Bosses - maybe a larger, more involved enemy where to kill it you must use some of the level objects like lasers, etc. Also might be interesting to have enemies that maybe you can jump on (like a platform) but also shoot at you, but their lasers/weapons get stopped by tiles etc. So you could picture positioning one character in a safe spot, the other jumping on the enemy, etc.

# Level Design

* Introduce new ideas one at a time, lots of quick levels early as learning. Then build to harder levels with those concepts.
* Game difficulty continues to amp up as get closer to the end.
* Thinking because of time constraint, might stick with one tileset and environment to minimize art and level design work but allow for lots of levels.
* Mix of levels - some more puzzle oriented, some more speed/action oriented.
* Each level is discrete, no backtracking. Maybe have concept of special collectibles, a fixed number of them. Most regular levels don't have them. But there are secret levels, that are harder, but allow for getting these collectibles. These are the hardest levels. So you can complete game with perfect score, by getting these 5 or 10 extra collectibles on these hidden, hard levels. If you get to a hard level, and want to return, you can always back out- needs to be easy way back to regular levels.

