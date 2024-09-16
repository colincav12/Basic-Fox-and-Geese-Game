# Fox and Geese Game

This project was created out of my particular interest in a mini puzzle game that is in a couple of video games I enjoy. This game takes place on an nxn grid where the user is playing as a number of geese that can only move diagonally. On the grid there is also a fox that is controlled by an AI I will mention below. The goal of the user is to corner the fox by taking turns with the fox, the user moving 1 geese each turn. The geese have to completely corner the fox in the turn limit set by the difficulty selection screen when first run to win.

## Fox AI
This model-based agent, FoxAI, is designed to look ahead from the current location of the fox and analyze how many “Free spots” are available from each potential move it can make. After it receives the information of how many free spots each move will give it, the FoxAI compares each of those values and takes the value representing the most free spots. This moves the fox to that space and the process repeats after the user moves 1 of the geese. The goal of the fox is to survive until the turn limit in order to beat the user. The fox can move omnidirectionally.
