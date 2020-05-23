import pandas as pd
from matplotlib import pyplot as plt

filename_of_gather_data = "../test2.txt"
delimiter = ", "

data = pd.read_csv(filename_of_gather_data, delimiter)


# print(data)

# print(sample_data.answers) # all "answers"
# print(sample_data.answers.iloc[1]) # one value

# util.sanitizer_file("../test2.txt", "text2.csv")

# print(util.average_time_response(data.time))
# print(data.uniqIdentifier)
# util.column_chart_per_peron(data, plt)
# util.number_of_canceled(data)
# util.bar_questions_yes_no_cancel_options(data, plt)


def basic_to_8_48():
    x = [1, 2, 3]
    y = [1, 4, 9]
    z = [10, 5, 0]

    plt.plot(x, y, 'o')  # circles
    plt.plot(x, z)

    plt.title("test")
    plt.xlabel("x")
    plt.ylabel("y and z")

    plt.legend(["this is y", "this is z"])

    plt.show()
