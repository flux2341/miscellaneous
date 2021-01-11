

# semantic analysis with nltk
# - begin with a pre-determined list of words/phrases, count the occurances of words nearby
# - find clusters of words, which are likely to be found near eachother
# - find the most common words, trace their occurance over various levels


import os
import nltk
from nltk.probability import FreqDist
import string


path = 'D:\\data\\writing20170708'

file_count = 0
line_count = 0
word_count = 0
character_count = 0

print('processing '+path)
default_stopwords = set(nltk.corpus.stopwords.words('english'))
master_freq = FreqDist()
for root, dirs, file_names in os.walk(path):
    #path = root.split(os.sep)
    #print(os.path.basename(root))
    for file_name in file_names:
        file_path = root + os.sep + file_name
        if file_name.find('.') == -1 or file_name.endswith('.txt') or file_name.endswith('.md'):
            try:
                # file = open(file_path, 'r', encoding='utf8')
                file = open(file_path, 'r', encoding="ISO-8859-1")
                file_text = file.read()
                file_text = file_text.translate(str.maketrans('', '', string.punctuation))
                words = nltk.word_tokenize(file_text)

                #words = [word for word in words if len(word) > 2]
                words = [word for word in words if not word.isnumeric()]
                words = [word.lower() for word in words]
                words = [word for word in words if word not in default_stopwords]
                fdist = FreqDist(words)
                master_freq += fdist

                file_count += 1
                line_count += file_text.count('\n')
                word_count += len(words)
                character_count += len(file_text)

            except Exception as ex:
                print('error loading '+file_path + ': ' +str(ex))
            finally:
                file.close()

                if file_count%100 == 0:
                    print(f'\tfiles processed: {file_count}')
        else:
            print(f'\tskipping {file_path}')

print()
print(f'files: {file_count}')
print(f'lines: {line_count}')
print(f'words: {word_count}')
print(f'characters: {character_count}')
for word, frequency in master_freq.most_common(50):
    print(f'{word} {frequency}')
