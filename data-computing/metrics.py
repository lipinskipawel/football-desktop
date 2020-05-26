import pandas as pd
from matplotlib import pyplot as plt

import util

filename_of_gather_data = "../test2.txt"
delimiter = ", "

data = pd.read_csv(filename_of_gather_data, delimiter, engine='python')
plt.style.use("fivethirtyeight")

print("Ilosc badanych osob ", util.number_of_uniq_id(data))
print("Ilosc odpowiedzi udzialonych do odrzuconych", util.number_of_canceled(data))
print("Sredni czas odpowiedzi:", util.average_time_response(data.time), "sekundy")
print("Sredni czas odpowiedzi na pytania trudne", util.average_time_for_hard_question(data))
print("Sredni czas odpowiedzi na pytania łatwe", util.average_time_for_easy_question(data))
util.bar_number_of_questions(data, plt)
util.bar_questions_yes_no_cancel_options(data, plt)
util.pie_ai(data, plt)
