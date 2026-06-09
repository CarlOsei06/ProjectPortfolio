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
Instead of guessing randomly, the program uses a divide‑and‑conquer strategy, halving the search range each turn until it finds the correct number.

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

Based on your response, the computer halves the search space.

This repeats until the number is found.

WeatherAnalyzer — README.md
📌 Overview
WeatherAnalyzer is a Java application that reads daily weather data from a file and provides a variety of analytical operations.
It uses Java Streams to filter, aggregate, and extract insights such as rainfall totals, snowfall days, maximum wind speeds, and weather‑condition‑specific dates.

This project demonstrates clean functional programming, data processing, and object‑oriented design.

🧩 Features
✔️ Print all weather data
Displays each day’s weather entry exactly as it was read from the dataset.

✔️ Filter by weather condition
Print all days matching a condition (e.g., "Clear", "Rain")

Print only the dates for those conditions

✔️ Rainfall analysis
Total rainfall across the entire dataset

Total rainfall after skipping the first 31 days

Total rainfall for the next 28 days after skipping 31 days

Total rainfall for a specific Month

✔️ Snowfall analysis
Print the first N days with snowfall

Print the first 3 snowfall days

✔️ Wind speed analysis
Find the maximum wind speed in the first 31 days

🧠 How It Works
The analyser uses a WeatherDataReader to load a list of WeatherData objects from a file.
Once loaded, the class uses Java Streams to:

Filter entries

Map values

Compute sums

Limit or skip ranges

Extract dates

Compute maximum values

This results in clean, expressive, and efficient data processing

COMP5009 Assignment 2 — Booking & Manifest Processing System
📌 Overview
This project is a Java-based booking and payment processing system developed for COMP5009 Assignment 2.
It reads booking and payment data from CSV files, validates and matches records, applies business rules, and generates a final manifest containing all valid passengers.

The system demonstrates strong use of object‑oriented design, file I/O, data validation, and collection processing.


Features
✔️ CSV Data Processing
Reads bookings.csv and payments.csv

Parses each row into domain objects

Handles malformed or missing data gracefully

✔️ Booking & Payment Validation
Matches bookings to payments

Ensures required fields are present

Filters out invalid or unpaid bookings


Manifest Generation
Produces a final manifest.txt file

Contains only valid, fully paid passengers

Sorted and formatted for readability

✔️ Modular OOP Structure
Separate classes for reading, processing, and output

Clear separation of concerns

Easy to extend or modify


Decision Tree Classifier — COMP Class 4 Worksheet 2
📌 Overview
This project implements a Decision Tree classification algorithm from scratch using the weather dataset provided in Table 1.
The goal is to predict whether Alice will go to the beach tomorrow based on three attributes:

Outlook (sunny, cloudy, rainy)

Temperature (hot, mild, cool)

Windy (true, false)

The implementation includes entropy calculation, information gain, selection of the best attribute, tree construction, and the final prediction for a new instance.

What This Project Demonstrates
This assignment showcases core machine‑learning concepts implemented manually without libraries:
