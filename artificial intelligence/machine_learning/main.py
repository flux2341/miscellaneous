import sys
import logicdiscrimination

# data_name = sys.argv[1]
data_names = ["breast_cancer", "climate", "hill_valley",
              "ionosphere", "micromass", "qsar",
              "test", "test2", "test_fscore", "project"]
data_name = data_names[8]
print("using ", data_name)
datafile = "./data/" + data_name + "/" + data_name + ".data"
labelfile = "./data/" + data_name + "/" + data_name + ".labels"
# labelfile = "./data/" + data_name + "/" + data_name + ".trainlabels.4"

# datafile = sys.argv[1]
# labelfile = sys.argv[2]

# Read data

f = open(datafile)
data = []
l = f.readline()
while l != '':
    a = l.split()
    l2 = [0]*len(a)
    for j in range(0, len(a), 1):
        l2[j] = float(a[j])
    data.append(l2)
    l = f.readline()

f.close()

# print("rows: " + str(rows))
# print("cols: " + str(cols))


# Read labels

f = open(labelfile)
labels = {}
n = [0, 0]  # count of how many rows are in each label
l = f.readline()
while l != '':
    a = l.split()
    labels[int(a[1])] = int(a[0])
    l = f.readline()
    n[int(a[0])] += 1

