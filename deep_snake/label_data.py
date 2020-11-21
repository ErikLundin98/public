import numpy as np
import time
import msvcrt
from game import Game

directions = {
    87: 1,
    65: 3,
    83: 0,
    68: 2
}

def get_label():
    while True:
        print('input W, A, S or D')
        input_char = msvcrt.getch().upper()[0]
        if input_char in directions.keys():
            return directions[input_char]
    

features = []
labels = []

game = Game(max_length=False)
feedback = Game.HIT_NOTHING

while True:
    
    while True:
        print(game)
        feature_vector = game.get_world_features()
        label = get_label()
        feedback= game.update(label)

        if feedback in [Game.HIT_SELF, Game.HIT_WALL]:
            break

        features.append(feature_vector)
        labels.append(label)

    action = input('Type QS to quit and save data, QA to quit and append data and Q to quit and discard data')
    
    if action == 'Q':
        break
    if action == 'QA':
        old_features = np.load('dataset/features.npy')
        old_labels = np.load('dataset/labels.npy')
        features = np.append(old_features, np.array(features), axis=0)
        labels = np.append(old_labels, np.array(labels), axis=0)
        action = 'QS'
    if action == 'QS':
        np.save('dataset/features.npy', np.array(features))
        np.save('dataset/labels.npy', np.array(labels))
        break




