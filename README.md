# gameoff

Game for game off game jam.

# Concept

Action, Puzzle Platformer either 1 player or ideally 2 player couch co-op.
You control two characters trying to escape (exact theme, characters, and environment TBD). Each requires power to move, but there is only 1 power 'unit' left. You can throw this power unit between each other, and when not being carried, it radiates a short range to allow for this hand off.

One player is shorter and has a low jump. One player is taller, and has a higher jump. Between these two different characteristics and the trading off with the power unit, many options for puzzles and co-operation exists.

Each level will be a single screen level with an exit. Once you exit, no back tracking- so linear progression. However sometimes exit is off the top, off the side, or off the bottom. The goal is to escape. Deaths result in restarting the level, and number of deaths and total time is tracked.

# Dimensions and Tools

Using 16 pixel square tiles, with a screen resolution of 640x360. Allows for 40 tiles wide, and 22.5 tall. Each map is 40x23 with the top row of tiles slightly clipped. This resolution then scales nicely to 720P or 1080P displays for a retro/pixel look.

Used Tiled as level editor.

Two tile layers: background, and platforms.
One object layer: objects

# Controls

In one player mode, a button press switches the player between which character they are controlling.
In two player mode, both are active at all times - but only if near a dropped power unit, or carrying the power unit can you move.

If the Powered Areas object idea (below) is implemented will need to refine 1 player mode controls - might need a way to control both characters at the same time. So in a powered area you could move both of them together quickly.

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
* Powered areas - so objects that radiate power but that you can't throw or pick up- static or moveable could be interesting.

# Level Design

* Introduce new ideas one at a time, lots of quick levels early as learning. Then build to harder levels with those concepts.
* Game difficulty continues to amp up as get closer to the end.
* Thinking because of time constraint, might stick with one tileset and environment to minimize art and level design work but allow for lots of levels.
* Mix of levels - some more puzzle oriented, some more speed/action oriented.
* Each level is discrete, no backtracking. Maybe have concept of special collectibles, a fixed number of them. Most regular levels don't have them. But there are secret levels, that are harder, but allow for getting these collectibles. These are the hardest levels. So you can complete game with perfect score, by getting these 5 or 10 extra collectibles on these hidden, hard levels. If you get to a hard level, and want to return, you can always back out- needs to be easy way back to regular levels.

# .plan

* tune box2d settings and player movement, fix on ground detection for jumping
* add one way collisions (can jump up through, power unit and most things can go up through)
* add switch
* add on contact triggers
* improve player controls and throwing the power unit
* add pass level/exit object and get level switching working
* add death (hit death squares, crushed by platform, etc.)
* add level restart (both after death, and with a button push - button push blows you both up)
* add more objects and start building some levels to experiment and see what works well and is interesting
