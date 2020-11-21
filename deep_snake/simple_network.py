import tensorflow as tf
import tensorflow.keras as keras
import numpy as np
from game import Game

def model():

    model = keras.models.Sequential()
    model.add(tf.keras.layers.Flatten())
    model.add(tf.keras.layers.Dense(16, activation='relu'))
    model.add(tf.keras.layers.Dense(16, activation='relu'))
    model.add(tf.keras.layers.Dense(16, activation='relu'))
    model.add(tf.keras.layers.Dense(4)) # 4 outputs

    return model

def get_loss(logits, actions, rewards):

    neg_logprob = tf.nn.sparse_softmax_cross_entropy_with_logits(logits=logits, labels=actions)
    loss = tf.reduce_mean(neg_logprob * rewards)

    return loss

def step(model, optimizer, states, actions, rewards):

    with tf.GradientTape() as tape:

        logits = model(states)
        loss = get_loss(logits, actions, rewards)
        gradients = tape.gradient(loss, model.trainable_variables)

        optimizer.apply_gradients(zip(gradients, model.trainable_variables))

def epsilon(n, max_n):
    return np.exp(-n/(max_n/5))

def get_action(model, game, epsilon):
    action = np.random.choice(['optimal', 'random'], 1, p=[1-epsilon, epsilon])[0]
    logits = model.predict(game.get_vector().reshape(1, 12, 1))
    prob_weights = tf.nn.softmax(logits).numpy()

    if action == 'optimal':
        action = list(prob_weights[0]).index(max(prob_weights[0]))
    elif action == 'random':
        action = np.random.choice(game.get_valid_directions())

    return action