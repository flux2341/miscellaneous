import pandas as pd



data_path = r'C:\Users\flux\data\boardgame-users.csv'
test_data_path = r'C:\Users\flux\data\boardgame-users-test.csv'
output_path = r'C:\Users\flux\data\output.csv'

df_main = pd.read_csv(data_path,
                 names=['player_id', 'game_id', 'rating'],
                 dtype={'player_id': 'int', 'game_id': 'int', 'rating': 'float'},
                 skiprows=1, header=None)



df_test = pd.read_csv(test_data_path,
                      names=['player_id', 'game_id'],
                      dtype={'player_id': 'int', 'game_id': 'int'},
                      skiprows=1, header=None)


player_ids = df_main['player_id'].unique()
n_players = len(player_ids)
ratings = {}
for index, player_id in enumerate(player_ids):
    player_set = set()
    df_player = df_main.loc[df_main['player_id'] == player_id]
    for row_id, row in df_player.iterrows():
        game_id = row['game_id']
        rating = row['rating']
        player_set.add((game_id, rating))
    ratings[player_id] = player_set
    if index % 1000 == 0:
        print(str(index/n_players*100)+'%')





