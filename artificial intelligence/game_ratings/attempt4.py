

import pandas as pd

data_path = r'C:\Users\flux\data\boardgame-elite-users.csv'
#data_path = r'C:\Users\flux\data\boardgame-frequent-users.csv'
#data_path = r'C:\Users\flux\data\boardgame-users.csv'
test_data_path = r'C:\Users\flux\data\boardgame-users-test.csv'
output_path = r'C:\Users\flux\data\output-similarity.csv'

print('loading data')



df_main = pd.read_csv(data_path,
                 names=['player_id', 'game_id', 'rating'],
                 dtype={'player_id': 'int', 'game_id': 'int', 'rating': 'float'},
                 skiprows=1, header=None)

print('building dataframes')
player_ids = df_main['player_id'].unique()
ratings = {}
for row_id, player_id in enumerate(player_ids):
    ratings[player_id] = df_main.loc[df_main['player_id'] == player_id]
    if row_id%100 == 0:
        print(str(round(row_id/len(player_ids)*100, 6))+'%')

print('building similarity matrix')

with open(output_path, 'w') as output_csv:
    output_csv.write(',')
    for index, player_id in enumerate(player_ids):
        output_csv.write(str(player_id))
        if index < len(player_ids)-1:
            output_csv.write(',')
    output_csv.write('\n')
    for index1, player_id1 in enumerate(player_ids):
        output_line = str(player_id1)+','
        for index2, player_id2 in enumerate(player_ids):
            inner_join = pd.merge(ratings[player_id1], ratings[player_id2], on='game_id', how='inner')
            diff = inner_join['rating_x'] - inner_join['rating_y']
            similarity = 1 - abs(diff.mean()) / 10
            output_line += str(similarity)
            if index2 < len(player_ids)-1:
                output_line += ','
        output_csv.write(output_line+'\n')

        if index1%10 == 0:
            print(str(round(index1/len(player_ids)*100, 6))+'%')
