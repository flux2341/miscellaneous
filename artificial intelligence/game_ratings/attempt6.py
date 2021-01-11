

# for a given test user and game
# compare this user with all other users


import pandas as pd

#data_path = r'C:\Users\flux\data\boardgame-elite-users.csv'
data_path = r'C:\Users\flux\data\boardgame-frequent-users.csv'
#data_path = r'C:\Users\flux\data\boardgame-users.csv'
test_data_path = r'C:\Users\flux\data\boardgame-users-test.csv'
output_path = r'C:\Users\flux\data\output-frequent-weighted.csv'

print('loading data')

df_test = pd.read_csv(test_data_path,
                      names=['player_id', 'game_id'],
                      dtype={'player_id': 'int', 'game_id': 'int'},
                      skiprows=1, header=None)

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


print('processing test data')

similarity_threshold = 0.9

df_test['rating'] = pd.Series([float('nan')]*df_test.shape[0])
for row_id, row in df_test.iterrows():

    print(str(round(row_id / df_test.shape[0] * 100, 4)) + '%', end=' ')

    player_id1 = row['player_id']
    game_id = row['game_id']

    game_rating_sum = 0
    game_rating_count = 0

    if player_id1 not in ratings:
        print(player_id1, ': skipping player')
        continue

    weight_sum = 0
    for player_id2 in player_ids:
        inner_join = pd.merge(ratings[player_id1], ratings[player_id2], on='game_id', how='inner')
        diff = inner_join['rating_x'] - inner_join['rating_y']
        similarity = 1 - abs(diff.mean())/10
        if similarity > similarity_threshold:
            game_row = ratings[player_id2].loc[ratings[player_id2]['game_id'] == game_id]
            if not game_row.empty:
                weight = (similarity-similarity_threshold)/(1.0-similarity_threshold)
                game_rating_sum += (game_row['rating'].iloc[0])*weight
                game_rating_count += 1
                weight_sum += weight

    if game_rating_count > 0:
        v = game_rating_sum/weight_sum
        print(int(player_id1), v, game_rating_count)
        df_test.at[row_id, 'rating'] = v
    else:
        print(player_id1, ': no matches found')


df_test = df_test.dropna()
df_test.to_csv(output_path)



















