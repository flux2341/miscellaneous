

import pandas as pd
import matplotlib.pyplot as plt
import string

import nltk
from nltk.probability import FreqDist


path_ratings = r'C:\Users\flux\data\toxic_comments\toxicity_annotations_unanimous.tsv'
path_comments = r'C:\Users\flux\data\toxic_comments\toxicity_annotated_comments_unanimous.tsv'

default_stopwords = set(nltk.corpus.stopwords.words('english'))
from nltk.stem import WordNetLemmatizer
wordnet_lemmatizer = WordNetLemmatizer()

freq_dists = {-2:FreqDist(), -1:FreqDist(), 0:FreqDist(), 1:FreqDist(), 2:FreqDist()}


def show_histogram():
    ratings = pd.read_csv(path_ratings, sep='\t', index_col=0)

    # find each worker's average rating
    print(ratings.groupby(['worker_id'])['toxicity_score'].mean())

    # generate a histograms of the ratings
    plt.figure()
    ratings['toxicity_score'].plot.hist(bins=[-2.1, -1.9,
                                              -1.1, -0.9,
                                              -0.1, 0.1,
                                              0.9, 1.1,
                                              1.9, 2.1])
    plt.show()





def count_words():
    ratings = pd.read_csv(path_ratings, sep='\t', index_col=0,
                          names=['rev_id', 'worker_id', 'toxicity', 'toxicity_score'],
                          dtype={'rev_id': 'int', 'worker_id': 'int', 'toxicity':'bool', 'toxicity_score':'int'},
                          skiprows=1, header=None)

    comments = pd.read_csv(path_comments, sep='\t', index_col=0,
                           names=['rev_id', 'comment', 'year', 'logged_in', 'ns', 'sample', 'split'],
                           dtype={'rev_id': 'int',
                                  'comment': 'str',
                                  'year': 'int',
                                  'logged_in': 'bool',
                                  'ns': 'str',
                                  'sample': 'str',
                                  'split': 'str'},
                           skiprows=1, header=None)


    n_rows = ratings.shape[0]
    row_index = 0
    for rev_id, row in ratings.iterrows():
        toxicity_score = row['toxicity_score']
        comment = comments.loc[[rev_id]].iloc[0]['comment']
        comment = comment.replace('NEWLINE_TOKEN', ' ')
        for char in string.punctuation:
            comment = comment.replace(char, ' ')
        words = nltk.word_tokenize(comment)
        words = [wordnet_lemmatizer.lemmatize(word.lower()) for word in words if word not in default_stopwords and not word.isnumeric()]
        words = set(words)


        fdist = FreqDist(words)
        freq_dists[toxicity_score] += fdist

        if row_index%100 == 0:
            print(round(row_index/n_rows*100), '%')
        row_index += 1


    for toxicity_score in freq_dists:
        print(toxicity_score)
        for word, frequency in freq_dists[toxicity_score].most_common(20):
            print(f'\t{word} {frequency}')


count_words()
