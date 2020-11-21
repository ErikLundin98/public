import numpy as np
import time

class Game:
    DIRECTIONS = np.array([[1, 0], [-1, 0], [0, 1], [0, -1]], int)
    DIRECTION_PAIRS = np.array([[0, 1], [2, 3]], int)
    MIDDLE = 7
    SIZE = 15
    EMPTY = 0
    SNAKE = 1
    SNAKE_HEAD = 2
    APPLE = 3
    WALL = -1
    HIT_WALL = -2
    HIT_SELF = -1
    UNEVENTFUL = -3
    HIT_NOTHING = 0
    HIT_APPLE = 1
    MAX_UNEVENTFUL_TIME = 100

    def __init__(self, max_length=False):
        self.reset()
        self.max_length = max_length
        print(max_length)
    
    def reset(self):
        self.body = np.array([[Game.SIZE/2, Game.SIZE/2]], int)
        self.direction = 0
        self.apple = self.new_apple()
        self.uneventful_tick = 0

    def get_world(self): # Used when the world matrix is needed
        a = np.zeros((Game.SIZE, Game.SIZE), int)
        
        for i in range(len(self.body)-1):
            a[self.body[i][0]][self.body[i][1]] = Game.SNAKE

        a[self.body[-1][0]][self.body[-1][1]] = Game.SNAKE_HEAD
        a[self.apple[0]][self.apple[1]] = Game.APPLE
        
        return np.pad(a, pad_width=1, mode='constant', constant_values=Game.WALL)

    def get_world_features(self):
        body = np.zeros((Game.SIZE, Game.SIZE), int)
        head = np.copy(body)
        apple = np.copy(body)
        for i in range(len(self.body)-1):
            body[self.body[i][0]][self.body[i][1]] = 1

        head[self.body[-1][0]][self.body[-1][1]] = 1
        apple[self.apple[0]][self.apple[1]] = 1

        features = np.array([body, head, apple])

        return features

    def get_vector(self): # Returns a vector of boolean values with world information
        # Format: 
        # up, right, down, left
        vector = np.empty((12), bool)
        vector[0] = self.body[-1][0] == Game.SIZE - 1 # wall below snake
        vector[1] = self.body[-1][0] == 0 # wall above snake
        vector[2] = self.body[-1][1] == Game.SIZE - 1 # wall to right
        vector[3] = self.body[-1][1] == 0 # wall to left
        vector[4] = self.body[-1][0] < self.apple[0] and self.body[-1][1] == self.apple[1] # apple below snake
        vector[5] = self.body[-1][0] > self.apple[0] and self.body[-1][1] == self.apple[1] # apple above snake
        vector[6] = self.body[-1][0] == self.apple[0] and self.body[-1][1] < self.apple[1] # apple to right
        vector[7] = self.body[-1][0] == self.apple[0] and self.body[-1][1] > self.apple[1] # apple to left
        for i in range(4):
            # snake body next to head
            vector[8+i] = any(
                [ True if (self.body[o] == [
                    self.body[-1][0] + Game.DIRECTIONS[i][0], self.body[-1][1] + Game.DIRECTIONS[i][1]]).all() else False for o in range(len(self.body)-1)
                    ]
                )
        
        return vector


    def __str__(self):
        return np.array_str(self.get_world()).replace('0', ' ')
        
    def new_apple(self):
        while True:
            point = np.random.randint(0, Game.SIZE-1, size=2)
            if not any(np.array_equal(part, point) for part in self.body):
                return point

    def get_valid_directions(self):
        valid_directions = np.empty((0), int)
        if self.body[-1][0] != Game.SIZE-1 and self.direction != 1:
            valid_directions = np.append(valid_directions, 0)
        if self.body[-1][0] != 0 and self.direction != 0:
            valid_directions = np.append(valid_directions, 1)
        if self.body[-1][1] != Game.SIZE-1 and self.direction != 3:
            valid_directions = np.append(valid_directions, 2)
        if self.body[-1][1] != 0 and self.direction != 2:
            valid_directions = np.append(valid_directions, 3)
        return valid_directions

    def update(self, direction):
        invalid_direction = False
        for arr in [[0, 1], [2, 3]]:
            if all(dir in arr for dir in [direction, self.direction]):
                invalid_direction = True
                break
        
        self.direction = direction if not invalid_direction and (0 <= direction < 4) else self.direction
        self.body = np.append(self.body, [[self.body[-1][0]+Game.DIRECTIONS[self.direction][0], self.body[-1][1]+Game.DIRECTIONS[self.direction][1]]], 0) # Grow one position

        # Check collisions
        for i in range(len(self.body)-1): # Hit self
            if (self.body[i] == self.body[-1]).all():
                return Game.HIT_SELF
        if any(True if i < 0 or i >= 15 else False for i in self.body[-1]):
            return Game.HIT_WALL # Hit wall

        ate_apple = (self.body[-1] == self.apple).all() # Ate apple
        if ate_apple:
            self.apple = self.new_apple()
            self.uneventful_tick = 0 # Reset uneventful timer
            if self.max_length and self.max_length <= len(self.body):
                self.body = np.delete(self.body, 0, 0)
            return Game.HIT_APPLE
        else:
            self.body = np.delete(self.body, 0, 0) # Delete last cell of body if snake did not eat apple
            self.uneventful_tick += 1
            if self.uneventful_tick >= Game.MAX_UNEVENTFUL_TIME:
                return Game.UNEVENTFUL
            return Game.HIT_NOTHING
        


if __name__ == "__main__":
    game = Game()

    print(game)
    feedback = Game.HIT_NOTHING
    while feedback is not (Game.HIT_SELF or Game.HIT_WALL):
        game.update(int(input("Direction : Down, Up, Right, Left")))
        print(game)
