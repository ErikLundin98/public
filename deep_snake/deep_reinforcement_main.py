import tensorflow as tf
import simple_network
import numpy as np
from game import Game
import time

class Memory:

    def __init__(self):
        self.clear()
    
    def clear(self):
        self.states = []
        self.actions = []
        self.rewards = []

    def add(self, state, action, reward):
        self.states.append(state)
        self.actions.append(action)
        self.rewards.append(float(reward))

rewards = {
    Game.HIT_APPLE : 100,
    Game.HIT_NOTHING : 0.5,
    Game.HIT_WALL : -50,
    Game.HIT_SELF : -10,
    Game.UNEVENTFUL : -1/2*Game.MAX_UNEVENTFUL_TIME
}

learning_rate = 0.01
episodes = 800

tf.keras.backend.set_floatx('float64')
model = simple_network.model()
optimizer = tf.keras.optimizers.Adam(learning_rate)

game = Game()
memory = Memory()

lookups = {
    Game.HIT_APPLE : 'total-score',
    Game.HIT_WALL : 'wall-death',
    Game.HIT_SELF : 'suicide',
    Game.UNEVENTFUL : 'uneventful',
    Game.HIT_NOTHING : 'ok'
}
stats = {
    'total-score' : 0,
    'wall-death' : 0,
    'suicide' : 0,
    'uneventful' : 0,
    'ok' : 0,
}
high_score = 0

for n in range(episodes):

    game.reset()
    memory.clear()
    if n % 50 == 0:
        print('episode no.', n)
    e = simple_network.epsilon(n, episodes)
    score = 0
    while True:
        state = game.get_vector() # Gets features as a 1*12 vector
        action = simple_network.get_action(model, game, e)
        feedback = game.update(action)
        stats[lookups[feedback]] += 1
        reward = rewards[feedback]
        if feedback == Game.HIT_APPLE:
            score += 1
        memory.add(
            state = state.reshape(12, 1),
            action = action,
            reward = reward
        )
        if feedback in [Game.HIT_SELF, Game.HIT_WALL, Game.UNEVENTFUL]:
            break


    # Save the model every now and then
    if n%99 == 0:
        model.save('saved_models/model' + str(save_episodes.index(n))) #save a model for each generation
        
    simple_network.step(
        model, optimizer, 
        states = np.array(memory.states),
        actions = np.array(memory.actions),
        rewards = memory.rewards)


