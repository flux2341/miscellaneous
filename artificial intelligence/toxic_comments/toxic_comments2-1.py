"""
organize the dataset into a CSV
"""

import pandas as pd
import matplotlib.pyplot as plt
import string

import nltk
default_stopwords = set(nltk.corpus.stopwords.words('english'))
from nltk.stem import WordNetLemmatizer
wordnet_lemmatizer = WordNetLemmatizer()

import re

path_ratings = r'C:\Users\flux\data\toxic_comments\toxicity_annotations_unanimous.tsv'
path_comments = r'C:\Users\flux\data\toxic_comments\toxicity_annotated_comments_unanimous.tsv'
# path_ratings = r'C:\Users\flux\data\toxic_comments\toxicity_annotations.tsv'
# path_comments = r'C:\Users\flux\data\toxic_comments\toxicity_annotated_comments.tsv'
path_output = r'C:\Users\flux\data\toxic_comments\output-7.csv'

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

output_rows = []
row_num = 0
n_rows = comments.shape[0]
for rev_id, row in comments.iterrows():

    comment_ratings = ratings.loc[[rev_id]]
    n_ratings = comment_ratings.shape[0]
    mean_rating = comment_ratings.mean()['toxicity_score']
    median_rating = comment_ratings.median()['toxicity_score']

    comment = row['comment']

    comment = comment.replace('NEWLINE_TOKEN', ' ')
    comment = comment.replace('TAB_TOKEN', ' ')
    words = nltk.word_tokenize(comment)
    words = [word.lower() for word in words]  # convert to lower case
    words = [word for word in words if word not in default_stopwords]  # remove stopwords
    words = [re.sub(r'[^a-zA-Z]+', '', word) for word in words]  # remove special characters and numbers
    words = [word for word in words if word != '']  # remove blanks
    words = [wordnet_lemmatizer.lemmatize(word) for word in words]  # lemmatize
    words = ','.join(set(words))


    # print(comment)
    # print(words)
    # print()
    # if row_num % 100 == 0:
    #     break

    # skip over blank lines
    if words == '':
        continue

    output_rows.append([words, n_ratings, mean_rating, median_rating])

    row_num += 1
    if row_num % 100 == 0:
        print(str(round(row_num/n_rows*100, 3))+'%')


output_df = pd.DataFrame(output_rows, columns=['words', 'n_ratings', 'mean_toxicity', 'median_toxicity'])
output_df.to_csv(path_output)

