import tensorflow as tf
import numpy as np
from game import Game

# Input will be 15*15=225 neurons
def model():
    model = tf.keras.models.Sequential()
    
    model.add(tf.keras.layers.SeparableConv2D(3, (3, 3), input_shape=(3, Game.SIZE, Game.SIZE), padding='SAME', data_format='channels_first'))
    #model.add(tf.keras.layers.Conv2D(2, (3, 3), padding='SAME'))
    #model.add(tf.keras.layers.Conv2D(2, (3, 3), padding='SAME'))
    model.add(tf.keras.layers.Flatten())
    model.add(tf.keras.layers.Dense(225, activation='relu'))
    model.add(tf.keras.layers.Dense(225, activation='relu'))
    model.add(tf.keras.layers.Dense(225, activation='relu'))
    model.add(tf.keras.layers.Dense(225, activation='relu'))
    model.add(tf.keras.layers.Dense(10, activation='relu'))
    model.add(tf.keras.layers.Dense(4)) # 4 outputs

    loss_function = tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True)

    model.compile(optimizer='adam', loss=loss_function, metrics=['accuracy'])
    return model



def get_action(model, game):
    
    logits = model.predict(game.get_world_features().reshape(1, 3, Game.SIZE, Game.SIZE))
    prob_weights = tf.nn.softmax(logits).numpy()
    action = list(prob_weights[0]).index(max(prob_weights[0]))
    
    return action