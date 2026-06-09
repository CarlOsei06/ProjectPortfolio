# Class 4 worksheet: Part 1
# Implement the Decision Tree classification algorithm using the data in Table 1
# and then predict whether Alice will go to the beach tomorrow

import math


# Data in Table 1, one list for each attribute
Outlook = ['sunny', 'sunny', 'sunny', 'cloudy', 'cloudy', 'cloudy',
           'cloudy', 'rainy', 'rainy', 'rainy', 'rainy']

Temperature = ['cool', 'hot', 'hot', 'cool', 'hot', 'mild',
               'hot', 'mild', 'hot', 'cool', 'mild']

Windy = ['true', 'true', 'false', 'true', 'true', 'false',
         'false', 'true', 'false', 'false', 'false']

GoToBeach = ['go', 'go', 'go', 'not go', 'not go', 'go',
             'go', 'not go', 'not go', 'not go', 'not go']

# Pre-processing data
# use a dictionary to map attribute values to their corresponding integers
# For example,
# Outlook = ['sunny', 'sunny', 'sunny', 'cloudy', 'cloudy', 'cloudy',
#            'cloudy', 'rainy', 'rainy', 'rainy', 'rainy']
# After mapping, we get
# [0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2]

attributeValue_mapping = {
    'sunny': 0,
    'cloudy': 1,
    'rainy': 2,
    'hot': 0,
    'mild': 1,
    'cool': 2,
    'true': 0,
    'false': 1,
    'go': 0,
    'not go': 1
}

# Map EACH attribute list to its corresponding integer list
# Then, the three predictor attributes (Outlook, Temperature, Windy)
# are stored in the "predictorAttributes" list,
# and the class attribute (GoToBeach) is stored in the "classAttribute" list

predictorAttributes = []
predictorAttributes.append([attributeValue_mapping.get(i) for i in Outlook])
predictorAttributes.append([attributeValue_mapping.get(i) for i in Temperature])
predictorAttributes.append([attributeValue_mapping.get(i) for i in Windy])
classAttribute = [attributeValue_mapping.get(i) for i in GoToBeach]

# Attribute index mapping dictionary
attributeIndex = {
    0: 'Outlook',
    1: 'Temperature',
    2: 'Windy'
}

from collections import Counter
# Function "cal_entropy(y)" is to calculate the entropy of y,
# where y is a list containing the various number of classAttribute values.
# Implement code to achieve it.

def cal_entropy(y):
    import math

    if not y:
        return 0.0
    counts = Counter(y)
    total = len(y)
    entropy = 0.0
    for cnt in counts.values():
        p = cnt / total
        entropy -= p * math.log2(p)
    return entropy
     



# Function "information_gain(predictorAttribute, classAttribute)" is to calculate
# the information gain when a "predictorAttribute" is used to partition "classAttribute".
# When entropy calculation is needed, it invokes Function "cal_entropy(y)" defined above.
# Implement code to achieve it.

def information_gain(predictorAttribute, classAttribute):
    from collections import defaultdict

    if not classAttribute:
        return 0.0

    parent_entropy = cal_entropy(classAttribute)
    total = len(classAttribute)

    value_to_classes = defaultdict(list)
    for p_val, c_val in zip(predictorAttribute, classAttribute):
        value_to_classes[p_val].append(c_val)

    weighted_entropy = 0.0
    for classes in value_to_classes.values():
        weighted_entropy += (len(classes) / total) * cal_entropy(classes)

    return parent_entropy - weighted_entropy




# Function "best_split(availablePredictorAttributes, classAttribute)" is to find
# the predictor attribute (among all available predictor attributes) that has the
# largest information gain when partitioning the classAttribute.
# When the calculation of information gain is needed,
# it invokes Function "information_gain(predictorAttribute, classAttribute)" defined above.
# Implement code to achieve it.

def best_split(availablePredictorAttributes, classAttribute):
    if not classAttribute:
        return None, {}

    best_ig = None
    best_attr = None
    best_split_map = {}

    total = len(classAttribute)

    for idx, predictor in enumerate(availablePredictorAttributes):
        if predictor is None:
            continue
        # Compute information gain for this predictor
        ig = information_gain(predictor, classAttribute)

        # Build split mapping: predictor value -> list of indices in the dataset
        split_map = {}
        for i, p_val in enumerate(predictor):
            split_map.setdefault(p_val, []).append(i)

        # Select predictor with highest information gain
        if best_ig is None or ig > best_ig:
            best_ig = ig
            best_attr = idx
            best_split_map = split_map

    return best_attr, best_split_map




# Function "record_bestSplit(bestSplit)" is to record
# the child nodes of the bestSplitAttribute (the predictor attribute that
# has the largest information gain when partitioning the classAttribute)
# Implement code to achieve it.

def record_bestSplit(bestSplit):

        if not bestSplit:
            return {}
        recorded = {}
        for val, indices in bestSplit.items():
            recorded[val] = [classAttribute[i] for i in indices]
        return recorded

# decisionTree is a list to record EACH selected predictor attribute and its child nodes
decisionTree = []

bestSplitAttribute, bestSplit = best_split(predictorAttributes, classAttribute)

# Record the first selected predictor attribute and its child nodes, in the decisionTree list
attri = attributeIndex.get(bestSplitAttribute)
record_nodes = record_bestSplit(bestSplit)
decisionTree.append([attri, record_nodes])

# Check each child node of the first selected predictor attribute.
# If a child node is not a pure leaf node (its entropy != 0), then further
# partition the class attribute values at this child node (by selecting
# the best one from the remaining predictor attributes).
# Record the second selected predictor attribute and its child nodes, in the decisionTree list
# Implement code to achieve it.

for child_value, child_class_list in record_nodes.items():
    # if child is not pure, try to split it further
    if cal_entropy(child_class_list) != 0.0:
        # original/global indices that belong to this child
        indices_subset = bestSplit.get(child_value, [])
        if not indices_subset:
            continue

        # build class sublist for these indices
        class_sublist = [classAttribute[i] for i in indices_subset]

        # exclude the already-used predictor
        available_predictors = [p for p in predictorAttributes]
        available_predictors[bestSplitAttribute] = None

        # find best remaining predictor for this subset
        best_ig2 = None
        best_attr2 = None
        best_split_map2 = {}

        for idx, predictor in enumerate(available_predictors):
            if predictor is None:
                continue
            # predictor values restricted to the subset (keeps original/global indices)
            pred_vals = [predictor[i] for i in indices_subset]

            # information gain on the subset
            ig = information_gain(pred_vals, class_sublist)

            # build split mapping: predictor value -> list of global indices
            split_map = {}
            for gv, orig_idx in zip(pred_vals, indices_subset):
                split_map.setdefault(gv, []).append(orig_idx)

            if best_ig2 is None or ig > best_ig2:
                best_ig2 = ig
                best_attr2 = idx
                best_split_map2 = split_map

        # if a best second attribute was found, record it and its child nodes
        if best_attr2 is not None:
            second_attr_name = attributeIndex.get(best_attr2)
            second_record_nodes = record_bestSplit(best_split_map2)
            # append entry: [parent_name, parent_value, second_name, second_record_nodes, second_split_map]
            decisionTree.append([attributeIndex.get(bestSplitAttribute), child_value,
                                 second_attr_name, second_record_nodes, best_split_map2])


# Print the final Decision Tree list
print("Decision Tree list:")
print(decisionTree)

# New instance (tomorrow's weather condition)
new_instance = ['cloudy', 'mild', 'true']

# Use the decisionTree you have built above to predict
# whether Alice will go to the beach tomorrow.
# Implement code to achieve it.
# Map new instance to integer values
instance_mapped = [attributeValue_mapping.get(v) for v in new_instance]

# reverse map for class labels
class_reverse = {v: k for k, v in attributeValue_mapping.items() if k in ('go', 'not go')}

def majority_class(labels):
    return Counter(labels).most_common(1)[0][0] if labels else None

# empty tree -> global majority
if not decisionTree:
    pred_int = majority_class(classAttribute)
    print("Prediction:", class_reverse.get(pred_int))
else:
    # root entry can be [name, nodes] or [name, nodes, split_map]
    root = decisionTree[0]
    root_name = root[0]
    root_nodes = root[1]

    # find root attribute index
    root_attr_idx = next(k for k, v in attributeIndex.items() if v == root_name)
    root_value = instance_mapped[root_attr_idx]

    # unseen root value -> global majority
    if root_value not in root_nodes:
        pred_int = majority_class(classAttribute)
        print("Prediction:", class_reverse.get(pred_int))
    else:
        child_classes = root_nodes[root_value]
        # pure leaf
        if cal_entropy(child_classes) == 0.0:
            print("Prediction:", class_reverse.get(child_classes[0]))
        else:
            # search for second-level entry matching this parent and value
            second_entry = None
            for entry in decisionTree[1:]:
                # expected shape: [parent_name, parent_value, second_name, second_record_nodes, ...]
                if len(entry) >= 2 and entry[0] == root_name and entry[1] == root_value:
                    second_entry = entry
                    break

            if not second_entry:
                pred_int = majority_class(child_classes)
                print("Prediction:", class_reverse.get(pred_int))
            else:
                second_name = second_entry[2]
                second_nodes = second_entry[3]
                second_attr_idx = next(k for k, v in attributeIndex.items() if v == second_name)
                second_value = instance_mapped[second_attr_idx]

                if second_value not in second_nodes:
                    pred_int = majority_class(child_classes)
                    print("Prediction:", class_reverse.get(pred_int))
                else:
                    second_child_classes = second_nodes[second_value]
                    if cal_entropy(second_child_classes) == 0.0:
                        print("Prediction:", class_reverse.get(second_child_classes[0]))
                    else:
                        pred_int = majority_class(second_child_classes)
                        print("Prediction:", class_reverse.get(pred_int))

