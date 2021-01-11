"""

"""

import pandas as pd


path_comments = r'C:\Users\flux\data\toxic_comments\output-7.csv'

comments = pd.read_csv(path_comments, index_col=0,
                      names=['row_id', 'words', 'n_ratings', 'mean_rating', 'median_rating'],
                      dtype={'row_id': 'int', 'words': 'str',
                             'n_ratings': 'int', 'mean_toxicity': 'float',
                             'median_toxicity': 'float'},
                      skiprows=1, header=None)

# figure out the ratings for each word
def calculate_word_ratings(df):
    all_words = set()
    for rev_id, row in df.iterrows():
        words = row['words'].split(',')
        all_words.update(words)

    word_ratings = {word: 0 for word in all_words}
    word_counts = {word: 0 for word in all_words}
    for rev_id, row in df.iterrows():
        words = row['words'].split(',')
        for word in words:
            word_ratings[word] += float(row['mean_rating'])
            word_counts[word] += 1

    for word in word_ratings:
        word_ratings[word] /= word_counts[word]

    return word_ratings



# perform cross-validation

from sklearn.model_selection import train_test_split

n_subsets = 10
test_size = 0.2
true_positive = 0
true_negative = 0
false_positive = 0
false_negative = 0
for i in range(n_subsets):
    train, test = train_test_split(comments, test_size=test_size)
    word_ratings = calculate_word_ratings(train)
    for rev_id, row in test.iterrows():


        # go through the words in the comment, adding up each's toxicity score
        score = 0.0
        words = row['words'].split(',')
        count = 0
        for word in words:
            if word in word_ratings:
                score += word_ratings[word]
                count += 1
        if count == 0:
            score = 0
        else:
            score /= count

        # compare our guessed score and the actual score
        label_score = float(row['mean_rating'])
        if score < 0 and label_score < 0:
            true_positive += 1
        elif score >= 0 and label_score >= 0:
            true_negative += 1
        elif score < 0 and label_score >= 0:
            false_positive += 1
        elif score >= 0 and label_score < 0:
            false_negative += 1
    accuracy = (true_positive + true_negative)/(true_positive + true_negative + false_positive + false_negative)
    print(f'run {i}, accuracy: {accuracy}')
