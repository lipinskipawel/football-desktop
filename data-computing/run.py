import pandas as pd
from matplotlib import pyplot as plt

import data_util
from data_util import DataManipulation

filename_of_gather_data = "data/example-data.csv"
delimiter = ", "

data = pd.read_csv(filename_of_gather_data, delimiter, engine='python')
plt.style.use("fivethirtyeight")

data_manipulation = DataManipulation(loaded_data=data)

print("Ilosc badanych osob ", data_manipulation.number_of_unique_id())
print("Ilosc odpowiedzi udzialonych do odrzuconych", data_manipulation.number_of_canceled())
print("Sredni czas odpowiedzi:", data_util.average_time_response(data.time), "sekundy")

hard = "Sredni czas odpowiedzi na pytania trudne"
easy = "Sredni czas odpowiedzi na pytania łatwe"
print(hard, data_util.average_time_response(data_manipulation.get_times_for_questions(data_manipulation.hard)))
print(easy, data_util.average_time_response(data_manipulation.get_times_for_questions(data_manipulation.easy)))

plotting = data_util.DataPlotting(plt, data_manipulation)
plotting.bar_number_of_questions()
plotting.bar_questions_yes_no_cancel_options()
plotting.pie_ai()
