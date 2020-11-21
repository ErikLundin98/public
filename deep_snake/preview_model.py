import tensorflow as tf
from matplotlib import pyplot as plt
import time
import sys
import network
import simple_network
from game import Game


def preview(model, type='supervised'):

    tf.executing_eagerly()
    tf.keras.backend.set_floatx('float64')

    game = Game()
    for i in range(100):
        game.reset()
        score = 0
        while True:
            action = network.get_action(model, game) if type=='supervised' else simple_network.get_action(model, game, 0)

            feedback = game.update(action)
            if feedback in [Game.HIT_SELF, Game.HIT_WALL, Game.UNEVENTFUL]:
                break
            if feedback == Game.HIT_APPLE:
                score += 1
            
            print('Score:', score)
            print(game)
            plt.imshow(game.get_world())
            plt.draw()
            plt.pause(0.001)
            plt.clf()
    
    
    plt.show()

if __name__ == '__main__':
    args = sys.argv
    
    if args[1] == 'supervised':
        preview(model = tf.keras.models.load_model('saved_models/supervised_learning_model'), type='supervised')
    elif args[1] == 'reinforcement':
        preview(model = tf.keras.models.load_model('saved_models/model3'), type='reinforcement')
    else:
        print("Unspecified model to preview!")