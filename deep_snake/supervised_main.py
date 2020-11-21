import tensorflow as tf
from preview_model import preview
import network
import numpy as np
from game import Game

tf.executing_eagerly()
tf.keras.backend.set_floatx('float64')

features = tf.cast(np.load('dataset/features.npy'), 'float64')
labels = tf.cast(np.load('dataset/labels.npy'), 'float64')

model = network.model()

model.summary()
model.fit(features, labels, epochs=14, batch_size=32)


model.save('saved_models/supervised_learning_model')

          
preview(model, 'supervised')

