# Erik Lundin's projects

A collection of some of my hobby projects.

## Table of contents

1. [Multidimensional Boids](#Multidimensional-Boids)
2. [Deep Snake](#Deep-Snake)
3. [Genre Guessing in R](#Genre-Guess)
4. [Health Care Chat Bot](#Health-Bot)

<a name="Multidimensional-Boids">

## Multidimensional Boids

A boids implementation written in Java. Boids are a form of simulation of a flock of boids (artificial birds). Each bird follows a few simple rules:

* If a boid sees another boid, it moves towards it
* If it comes too close to another boid, it flies away
* Each boid tries to mimic the movement of nearby boids

![resnake](https://github.com/ErikLundin98/public/blob/main/boids/media/boids.gif)

Head over to https://eriklundin98.github.io/ to see the results and experiment! It is possible to change simulation parameters such as the boids' viewing range, amount of boids, velocity etc. in real time using the sliders and the drop down menu. The boids are four dimensional, and the fourth dimension is visualized by using color. The closer two boids are to each other in the fourth dimension, the more their colors match.

The program is made in such a way that the boids can act in an unlimited amount of dimensions. If you're interested in the source code, it can be found in the [`boids`](https://github.com/ErikLundin98/public/tree/main/boids/core/src/com/mygdx/game) folder. The rendering is done using the [`LibGDX engine`](https://libgdx.badlogicgames.com/)

<a name="Deep-Snake">

## Deep Snake

In this project I used TensorFlow to create and teach deep neural networks to play snake. See the [Jupyter Notebook](https://github.com/ErikLundin98/public/blob/main/deep_snake/deep_snake.ipynb) for demonstrations of the final result and some information about how I did it.

The code can be found in [`deep_snake/`](https://github.com/ErikLundin98/public/tree/main/deep_snake)

![resnake](https://github.com/ErikLundin98/public/blob/main/deep_snake/media/reinforcement_snake.gif)
![slsnake](https://github.com/ErikLundin98/public/blob/main/deep_snake/media/supervised_snake.gif)

<a name="Genre-Guess">
  
## Genre Guessing in R

As a way to learn how to implement machine learning models in R, I created a custom ensemble classifier which predicts song genre based on audio features such as average beats per minute, frequency and other more complicated measurements. See the [R Markdown File](https://github.com/ErikLundin98/public/blob/main/genre_guesser/genreguesser.md) for a more detailed report of the classifier and procedure.

The code can be found in [`genre_guesser/`](https://github.com/ErikLundin98/public/tree/main/genre_guesser)

<a name="Health-Bot">
  
## Health Care Chat Bot

![botdemo](https://github.com/ErikLundin98/public/blob/main/health_care_bot/bot-demo.gif)

I have worked in a simulated company of 25 students to develop an open-source health care bot for the local region. The bot can be used to simplify the process of registering health-related measurements as blood pressure and weight. The bot can also help patients to schedule appointments with health care professional and give useful advice for different scenarios. The idea behind the bot is to make health care from home as simple as possible for patients of all demographics with easy to follow instructions and an intuitive conversational flow. A fork of this repository can be found at https://github.com/ErikLundin98/health-care-bot. Note that the deployment pipeline is configured for GitLab and will not deploy in a GitHub repo. 
