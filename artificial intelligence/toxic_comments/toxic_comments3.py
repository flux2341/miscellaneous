"""
calculate the most common toxic and non-toxic terms
"""

import pandas as pd
from nltk.probability import FreqDist

path_comments = r'C:\Users\flux\data\toxic_comments\output-7.csv'

comments = pd.read_csv(path_comments, index_col=0,
                      names=['row_id', 'words', 'n_ratings', 'mean_rating', 'median_rating'],
                      dtype={'row_id': 'int', 'words': 'str',
                             'n_ratings': 'int', 'mean_toxicity': 'float',
                             'median_toxicity': 'float'},
                      skiprows=1, header=None)

fdist_toxic = FreqDist()
fdist_nontoxic = FreqDist()
for rev_id, row in comments.iterrows():
    words = row['words'].split(',')
    mean_toxicity = row['mean_rating']
    if mean_toxicity < 0:
        fdist_toxic += FreqDist(words)
    else:
        fdist_nontoxic += FreqDist(words)


print('toxic ' + '-'*20)
for word, frequency in fdist_toxic.most_common(20):
    print(word, frequency)

print('non-toxic ' + '-' * 20)
for word, frequency in fdist_nontoxic.most_common(20):
    print(word, frequency)

