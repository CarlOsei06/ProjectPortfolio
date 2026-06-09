NaiveBayes — README.md
📌 Overview
This project implements a Naive Bayes classifier that predicts whether Alice will go to the beach based on categorical weather conditions.
It includes full preprocessing, probability calculations, and a final prediction for a new weather instance.

🚀 Features
Encodes categorical weather data

Calculates prior and likelihood probabilities

Computes posterior probabilities

Makes a final prediction

Fully commented and easy to follow
How It Works
The dataset is encoded into numerical values

Priors for each class are computed

Likelihoods for each feature given each class are calculated

Posterior probability is computed using Bayes’ theorem

The classifier predicts the class with the highest posterior

🛠️ Technologies
Python

NumPy / math

Basic probability theory


running the Script
Code
python NaiveBayes.py
📄 Example Output
Prior probabilities

Likelihood tables

Posterior probability

Final prediction: Go / Don’t Go


GuessTheNumber — README.md
📌 Overview
This project is a number-guessing game where the computer guesses the player’s secret number using a Binary Search algorithm.
Instead of guessing randomly, the program uses a divide‑and‑conquer strategy, cutting the search range in half each turn until it finds the correct number.

This makes the game extremely efficient — it can guess any number in just log₂(n) steps.

🎯 Features
The computer guesses your number

Uses Binary Search for optimal efficiency

Divide‑and‑conquer strategy

Clear console interaction

Fast and accurate guessing

Great demonstration of algorithmic thinking



How It Works
You choose a secret number within a range (e.g., 1–100).

The computer starts by guessing the middle of the range.

You tell it whether your number is higher, lower, or correct.

Based on your response, the computer cuts the search space in half.

This repeats until the number is found.

