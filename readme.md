# snake3d_mobile
This projrect is in an alpha stage.
It plans to be a full game engine running on android and a continuation of a previous project, snake3D, which was written in C++.
The project currently features:
* A mesh format containing vertices, indices (to triangles), materials, and submeshes
* A working collision detection routine
* A scene script system for setting up scenes/levels with meshes/objects
* An object system for in-game objects
* A behaviour system for objects
* A movable player and functioning 3rd person camera

Upcoming plans:
* Octree setup for collision (massive performance boost)
* Object-to-object collision for interactions (i.e. collectible objects)
* A median model format to store in assets rather than converting raw model formats at scene creation
