


- k-means clustering, TSNE, quad tree
- PCA, reduce the game space

- booleans - whether they gave it a rating or not
- low number of ratings




- take all other users into account, weight by similarity?
- cluster users by their collective similarity
- generate more meaningful subsets of the data
    - remove outliers, inconsistent ratings?



1) build a recommendation system
    - given the games and ratings they've entered
    - find the most common and highly rated games among similar people	
2) test recommendation system
    - given a user and a game they haven't played, how would they rate it
    - find similar users, see how they rated the game


- make the algorithm more efficient
- do cross-validation
    - take random subsets of the frequest users list
- take the data in steps - generate a sparse similarity matrix
	- build a sparse similarity matrix
		- combine threshold and count
			- e.g. 0.99 threshold, minimum of 30 others
		- user1, user2, rating_similarity
- what is the best to appropriate additional information?
    - group/cluster games by tags
	- game-game comparison
	    - are two users more similar if they rate similar games similarly
	    - or if they rate different games similarly
		


- boolean classifier, whether they rated it or not
    - better than scoring greater than the median or lower
- only compare games they rated positively
- t-test, compare distributions of each user's ratings


- user A tends to rate around 5, user B tends to rate around B
    - correlation coefficient
- user baseline, game baseline

	
generate markdown for the early work for the machine learning work
other AI stuff





		



	












