

import pandas as pd


# https://stackoverflow.com/questions/31887447/how-do-i-merge-two-lists-of-tuples-based-on-a-key

data_path = r'C:\Users\flux\data\boardgame-users.csv'
test_data_path = r'C:\Users\flux\data\boardgame-users-test.csv'
output_path = r'C:\Users\flux\data\output.csv'

print('loading main data...')

player_ratings = {}

with open(data_path) as csv:
    csv.readline()
    current_player_id = None
    current_player_set = None
    for line in csv:
        data_row = line.split(',')
        player_id = int(data_row[0])
        game_id = int(data_row[1])
        rating = float(data_row[2])

        if current_player_id is None:
            current_player_id = player_id
            current_player_set = set()
        elif current_player_id != player_id:
            player_ratings[current_player_id] = current_player_set
            current_player_id = player_id
            current_player_set = set()

        current_player_set.add((game_id, rating))

test_data = []
with open(test_data_path) as csv:
    csv.readline()
    for line in csv:
        data_row = line.split(',')
        player_id = int(data_row[0])
        game_id = int(data_row[1])
        test_data.append([player_id, game_id, 0.0])

for index, test_data_row in enumerate(test_data):
    player_id1 = test_data_row[0]
    game_id = test_data_row[1]

    for player_id2 in player_ratings:
        intersection = player_ratings[player_id1] & player_ratings[player_id2]


    if index % 100 == 0:
        print(str(index / len(test_data) * 100) + '%')
















