

# matrix of user-user comparisons
# user - what game they should try next
# user + game - figure out rating

# graph count of each rating for a particular game

# collaborative-filtering user-user
# content filtering - what is it about the games the user likes

# with open(data_path) as csvfile:
#     csvfile.readline()
#     total = 0
#     count = 0
#     data = []
#     for row in csvfile:
#         row = row.split(',')
#         player_id = int(row[0])
#         game_id = int(row[1])
#         rating = float(row[2])
#         total += rating
#         count += 1
#         data.append((player_id, game_id, rating))
#     print(data)



import pandas as pd

import matplotlib.pyplot as plt

#data_path = r'C:\Users\flux\data\boardgame-elite-users.csv'
#data_path = r'C:\Users\flux\data\boardgame-frequent-users.csv'
data_path = r'C:\Users\flux\data\boardgame-users.csv'

test_data_path = r'C:\Users\flux\data\boardgame-users-test.csv'
output_path = r'C:\Users\flux\data\output.csv'

print('loading data')
df_test = pd.read_csv(test_data_path,
                      names=['player_id', 'game_id'],
                      dtype={'player_id': 'int', 'game_id': 'int'},
                      skiprows=1, header=None)

df = pd.read_csv(data_path,
                 names=['player_id', 'game_id', 'rating'],
                 dtype={'player_id': 'int', 'game_id': 'int', 'rating': 'float'},
                 skiprows=1, header=None)

# print(df.groupby(['game_id'])['rating'].mean())

# plt.figure()
# one_game = df['rating']
# #one_game = df.loc[df['game_id'] == 3]['rating']
# print(one_game)
# one_game.plot.hist()
# plt.show()

#dfp = df.pivot(index='player_id', columns='game_id', values='rating')

print('building dataframes')
player_ids = df['player_id'].unique()
ratings = {}
for row_id, player_id in enumerate(player_ids):
    ratings[player_id] = df.loc[df['player_id'] == player_id]
    if row_id%100 == 0:
        print(str(round(row_id/len(player_ids)*100, 6))+'%')


# for player_id1 in player_ids:
#     for player_id2 in player_ids:
#         if player_id1 != player_id2:
#             inner_join = pd.merge(ratings[player_id1], ratings[player_id2], on='game_id', how='inner')
#             diff = inner_join['rating_x'] - inner_join['rating_y']
#             similarity = 1-abs(sum(diff)/diff.shape[0])/10
#             if similarity > 0.99:
#                 print(player_id1, player_id2, similarity)
#
#             # outer_join = pd.merge(ratings[player_id1], ratings[player_id2], on='game_id', how='outer')
#             # n_left = ratings[player_id1].shape[0]
#             # n_right = ratings[player_id2].shape[0]
#             # n_matches = inner_join.shape[0]
#             # v = average(n_matches/n_left, n_matches/n_right)
#
#             # print(ratings[player_id1].shape[0])
#             # print(ratings[player_id2].shape[0])
#             # print(inner_join.shape[0])
#             # print(outer_join.shape[0]-inner_join.shape[0])
#             #print(player_id1, player_id2, v)


print('processing test data')

df_test['rating'] = pd.Series([0.0]*df_test.shape[0])
for row_id, row in df_test.iterrows():

    player_id1 = row['player_id']
    game_id = row['game_id']

    game_rating_sum = 0
    game_rating_count = 0

    if player_id1 not in ratings:
        print(player_id1, ': skipping player')
        df_test.at[row_id, 'rating'] = 7.5
        continue

    for player_id2 in player_ids:
        inner_join = pd.merge(ratings[player_id1], ratings[player_id2], on='game_id', how='inner')
        diff = inner_join['rating_x'] - inner_join['rating_y']
        similarity = 1 - abs(diff.mean())/10
        if similarity > 0.9:
            game_row = ratings[player_id2].loc[ratings[player_id2]['game_id'] == game_id]
            if not game_row.empty:
                game_rating_sum += game_row['rating'].iloc[0]
                game_rating_count += 1

    if game_rating_count > 0:
        print(player_id1, ': ', game_rating_sum/game_rating_count)
        df_test.at[row_id, 'rating'] = game_rating_sum / game_rating_count
    else:
        print(player_id1, ': no matches found')

    if row_id%100 == 0:
        print(str(round(row_id/df_test.shape[0]*100, 6))+'%')



df_test.to_csv(output_path)


# player_ratings = {}
# for player_id in player_ids:
#     ratings = df.loc[df['player_id'] == player_id]
#     player_ratings[player_id] = []
#     for id, rating in ratings.iterrows():
#         player_ratings[player_id].append((rating['game_id'], rating['rating']))


# for player_id1 in player_ids:
#     ratings1 = df.loc[df['player_id'] == player_id1]
#     for player_id2 in player_ids:
#
#         ratings1 = df.loc[df['player_id'] == player_id1]
#
#         n_matching = 0
#         n_different = 0
#         for rating1 in player_ratings[player_id1]:
#             matched = False
#             for rating2 in player_ratings[player_id2]:
#                 if rating1[0] == rating2[0]:
#                     n_matching += 1
#                     matched = True
#
#


