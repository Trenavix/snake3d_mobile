# snake3d_mobile
This project is in an alpha stage.
It plans to be a full game engine running on android and a continuation of a previous project, snake3D, which was written in C++.
The project currently features:
* A Mesh class format containing vertices, indices (to triangles), materials, and submeshes (for bone rigging)
* A custom binary format ("mesh32") containing various data including vertices, polygon indices, materials, and bones/armature, aimed to load into the engine quickly
* An efficient collision detection routine
* Octree setup for optimised collision on large meshes ie levels (reduces iteration count significantly)
* A scene script system for setting up scenes/levels with meshes/objects
* An object system for in-game objects which is processed multithreaded in a parallel loop, then drawn in order on one thread
* A behaviour system for objects
* A movable player and functioning 3rd person camera
* Object-to-object collision for interactions (i.e. collectible objects)
* Übershaders that support a wide range of functions like texture scrolling, bone animation, billboarding, alpha-testing, amongst others

Upcoming plans:
* Dynamic alpha sorting objects
* Alpha level meshes to be split by nodes and merged with objects for alpha sorting
* Use JNI to run some methods under C++ for better data management (mesh32 to Mesh could get a speedup on scene creation)
* Implement multi-texturing with multiple UV sets for various effects such as moving cloud shadows

Screenshots:

![PtLightsSM](https://media.discordapp.net/attachments/356315343926329345/988827143087747143/20220621_182558.jpg)
![RedPtLightHF](https://media.discordapp.net/attachments/356315343926329345/988825171009871882/Screenshot_20220621-181717_Video_Player.jpg)

Other info: Vertices currently support position, UV, RGBA, normal, and weight (for bone rigging / skinning)