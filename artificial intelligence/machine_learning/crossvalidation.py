import random
import nearestmeans
import bayesian
import perceptron
import logicdiscrimination
import randomclassifier
import knearestneighbor


def run(data, labels, n):

    accuracies = [0]*5
    for i in range(10):

        cv_labels, cv_validation = splitlabels(labels)

        # knearestneighbor.run(data, cv_labels, n)
        # accuracies[0] += accuracy(cv_labels, cv_validation)
        # resetlabels(cv_labels, cv_validation)

        nearestmeans.run(data, cv_labels, n)
        accuracies[1] += accuracy(cv_labels, cv_validation)
        resetlabels(cv_labels, cv_validation)

        bayesian.run(data, cv_labels, n)
        accuracies[2] += accuracy(cv_labels, cv_validation)
        resetlabels(cv_labels, cv_validation)

        perceptron.run(data, cv_labels, n)
        accuracies[3] += accuracy(cv_labels, cv_validation)
        resetlabels(cv_labels, cv_validation)

        randomclassifier.run(data, cv_labels, n)
        accuracies[4] += accuracy(cv_labels, cv_validation)
        resetlabels(cv_labels, cv_validation)

    for i in range(len(accuracies)):
        accuracies[i] /= 10
        accuracies[i] *= 100

    print("k nearest neighbors: ", "%.2f" % accuracies[0], "%")
    print("nearest means:       ", "%.2f" % accuracies[1], "%")
    print("bayesian:            ", "%.2f" % accuracies[2], "%")
    print("perceptron:          ", "%.2f" % accuracies[3], "%")
    print("random:              ", "%.2f" % accuracies[4], "%")



def splitlabels(labels):

    cv_labels = {}
    cv_validation = {}

    for index in labels:
        if random.randint(1,10) == 1:
            cv_validation[index] = labels[index]
        else:
            cv_labels[index] = labels[index]

    return cv_labels, cv_validation


def resetlabels(cv_labels, cv_validation):
    for index in cv_validation:
        del cv_labels[index]


"""
n_validation = len(labels) / 10
validation = {}
while len(validation) < n_validation:
    index = random.randint(0, len(labels) - 1)
    if labels.get(index) is not None:
        validation[index] = labels[index]
        del labels[index]
"""





def accuracy(cv_labels, cv_validation):
    correct = 0
    incorrect = 0
    for index in cv_validation:
        if cv_validation[index] == cv_labels[index]:
            correct += 1
        else:
            incorrect += 1
    return correct / (correct + incorrect)


def balancederror(cv_labels, cv_validation):
    a = 0
    b = 0
    c = 0
    d = 0
    for index in cv_validation:
        if cv_validation[index] == 0:
            if cv_labels[index] == 0:
                a += 1
            else:
                b += 1
        else:
            if cv_labels[index] == 0:
                c += 1
            else:
                d += 1
    return 0.5 * (b / (a + b) + c / (c + d))

