# Class 3 worksheet: Part 1
# Implement the Naive Bayes classification algorithm using the data in Table 1
# and then predict whether Alice will most likely go to the beach tomorrow

from operator import itemgetter

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
# use a dictionary to map attribute values to integers
# For example,
# Outlook = ['sunny', 'sunny', 'sunny', 'cloudy', 'cloudy', 'cloudy',
#            'cloudy', 'rainy', 'rainy', 'rainy', 'rainy']
# After mapping, you should have
# mOutlook = [0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2]
# (mOutlook is the correspondng integer list after mapping has been implemented on the Outlook list)

# The example in Part 0 Exercise 3 in the worksheet shows how to do this mapping

# map Outlook values to integers and store in attribute_mapping
attribute_mapping = {}

# mapping dictionaries for each attribute
mapping_outlook = {'sunny': 0, 'cloudy': 1, 'rainy': 2}
mapping_temperature = {'cool': 0, 'mild': 1, 'hot': 2}
mapping_windy = {'true': 0, 'false': 1}
mapping_goto = {'go': 0, 'not go': 1}

mOutlook = [mapping_outlook.get(i) for i in Outlook]
attribute_mapping['Outlook'] = mOutlook
print(mOutlook)

# 2. implement your code here to map EACH attribute list to
#    its corresponding integer list
from operator import itemgetter

mOutlook = [mapping_outlook[v] for v in Outlook]
mTemperature = [mapping_temperature[v] for v in Temperature]
mWindy = [mapping_windy[v] for v in Windy]
mGoToBeach = [mapping_goto[v] for v in GoToBeach]

attribute_mapping['Outlook'] = mOutlook
attribute_mapping['Temperature'] = mTemperature
attribute_mapping['Windy'] = mWindy
attribute_mapping['GoToBeach'] = mGoToBeach


# Function "likelihood(attribute, classIndex)" is to calculate the
# likelihood of all attribute values of the attribute, given a class
# that is P(attribute value | a class)
# implement code to achieve it

def likelihood(attribute, classIndex):
    # attribute: key in attribute_mapping (e.g. 'Outlook', 'Temperature', 'Windy')
    # classIndex: integer class value (0 for 'go', 1 for 'not go')
    vals = attribute_mapping[attribute]
    class_vals = attribute_mapping['GoToBeach']

    # number of distinct encoded values for this attribute
    num_values = max(vals) + 1

    # count occurrences of each attribute value among rows with the given class
    counts = [0] * num_values
    class_count = 0
    for a, c in zip(vals, class_vals):
        if c == classIndex:
            counts[a] += 1
            class_count += 1

    if class_count == 0:
        # no examples for this class, return zeros to avoid division by zero
        return [0.0] * num_values

    # convert counts to probabilities
    return [cnt / class_count for cnt in counts]


# Function "prior_probs(classAttribute)" is to calculate
# the prior probabilities of the TWO classes
# implement code to achieve it

def prior_probs(classAttribute):
    # count examples and number of classes
    total = len(classAttribute)
    if total == 0:
        return []
    num_classes = max(classAttribute) + 1
    counts = [0] * num_classes
    for c in classAttribute:
        counts[c] += 1
    # convert counts to probabilities
    return [cnt / total for cnt in counts]


# Function "posterior_probs(mNew,likelihoodProbs,priorProbs)" is to calculate
# the posterior probability (the probability that Alice will go to the beach tomorrow)
# implement code to achieve it

def posterior_probs(mNew, likelihoodProbs, priorProbs):
    # 5. implement your code here
    attributes = ['Outlook', 'Temperature', 'Windy']
    num_classes = len(priorProbs)
    numerators = [0.0] * num_classes

    for c in range(num_classes):
        p = priorProbs[c]
        for i, attr in enumerate(attributes):
            vals_for_attr = likelihoodProbs.get(attr)
            if vals_for_attr is None:
                p *= 0.0
                break

            # obtain the list of probabilities for this class
            if isinstance(vals_for_attr, dict):
                probs = vals_for_attr.get(c, [])
            else:
                probs = vals_for_attr[c] if c < len(vals_for_attr) else []

            val_index = mNew[i]
            p_attr = probs[val_index] if (0 <= val_index < len(probs)) else 0.0
            p *= p_attr

            if p == 0.0:
                break

        numerators[c] = p

    denom = sum(numerators)
    if denom == 0.0:
        return 0.0

    return numerators[0] / denom


# Invoke one of the functions defined above to obtain
# the likelihood of each attribute for each given class,
# then save them in a dictionary, called likelihoodProbs
# implement code to achieve it

# python
likelihoodProbs = {
    'Outlook': [likelihood('Outlook', 0), likelihood('Outlook', 1)],
    'Temperature': [likelihood('Temperature', 0), likelihood('Temperature', 1)],
    'Windy': [likelihood('Windy', 0), likelihood('Windy', 1)],
}

# Invoke one of the functions defined above to obtain the prior probability
# of each class, and save them to a list called, priorProbs
# implement code to achieve it

# 7. implement your code here
priorProbs = prior_probs(attribute_mapping['GoToBeach'])
print("priorProbs =", priorProbs)

# new instance: tomorrow's weather condition
new_instance = ['cloudy', 'mild', 'true']

# 8. implement your code here to map the new_instance
#    to its corresponding integer list, called mNew

mNew = [
    mapping_outlook.get(new_instance[0]),
    mapping_temperature.get(new_instance[1]),
    mapping_windy.get(new_instance[2])
]
if None in mNew:
    raise ValueError(f"Unknown value in new_instance: {new_instance}")
print("mNew =", mNew)

# Obtain the posterior probability
# (the probability that Alice will go to the beach tomorrow)

posteriorProb = posterior_probs(mNew, likelihoodProbs, priorProbs)
print("posterior probability =", round(posteriorProb, 3))

# 9. implement your code here to print
#    whether Alice will most likely go to the beach tomorrow

if posteriorProb >= 0.5:
    print("Decision: Alice will most likely go to the beach tomorrow.")
else:
    print("Decision: Alice will most likely not go to the beach tomorrow.")

print("P(go) =", round(posteriorProb, 3), "  P(not go) =", round(1 - posteriorProb, 3))

print("P(go) =", round(posteriorProb, 3), "  P(not go) =", round(1 - posteriorProb, 3))

