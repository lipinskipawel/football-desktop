import string

import numpy as np


def average_time_response(time_column):
    sum = 0
    for time in time_column:
        sum += time
    return sum / len(time_column)


def average_time_for_hard_question(data):
    sum = 0
    number = 0
    for index, row in data.iterrows():
        question = row.question
        if question.startswith('czy pomógłbyś') or \
                question.startswith('czy zgłosiłbyś') or \
                question.startswith('czy korzystasz'):
            sum += row.time
            number += 1
    return sum / number


def average_time_for_easy_question(data):
    sum = 0
    number = 0
    for index, row in data.iterrows():
        question = row.question
        if question.startswith('czy lubisz') or \
                question.startswith('czy trzymasz') or \
                question.startswith('czy przeczytałeś/aś') or \
                question.startswith('czy spędzasz'):
            sum += row.time
            number += 1
    return sum / number


def number_of_uniq_id(data):
    return len(set(data.uniqIdentifier))


def bar_number_of_questions(read_data_from_csv, plt):
    map_person_by_number = dict.fromkeys(read_data_from_csv.uniqIdentifier, 0)
    for id in read_data_from_csv.uniqIdentifier:
        map_person_by_number[id] += 1
    reduce_size_of_person = lambda person: person[0:3]
    people = list(map(reduce_size_of_person, map_person_by_number.keys()))
    number = map_person_by_number.values()
    plt.title("Ilość wyświetlonych pytań\ndla danego gracza")
    plt.bar(people, number)
    plt.xlabel("Identyfikator użytkownika")
    plt.ylabel("Ilość pytań")
    plt.show()


def bar_questions_yes_no_cancel_options(data, plt):
    """
    map_questions_to_all_answers: key is the question
        value is the list of answers mapped to numbers. [0, 0, 0] = ["Tak", "Nie", "Cancel"]
    :param data:
    :param plt:
    :return:
    """
    map_questions_to_all_answers = {key: [0, 0, 0] for key in data.question}

    for index, row in data.iterrows():
        answer = row.answers
        if not answer.isdigit():
            val = map_answer_to_index(answer)
            current_question = row.question

            list_of_answers = map_questions_to_all_answers[current_question]
            list_of_answers[val] += 1  # values in the map are mutable and this is a reference
    print(map_questions_to_all_answers)

    # plotting
    questions = map_questions_to_all_answers.keys()
    x_indexes = np.arange(len(questions) - 1)  # for the plot. instead of str we have int
    width_of_bar = 0.25

    number_of_all_questions = list(map_questions_to_all_answers.values())
    number_of_tak_questions = take(number_of_all_questions, 0)
    number_of_nie_questions = take(number_of_all_questions, 1)
    number_of_cancel_questions = take(number_of_all_questions, 2)
    # print(number_of_all_questions)
    # print(number_of_tak_questions)
    # print(number_of_nie_questions)
    # print(number_of_cancel_questions)

    plt.bar(x_indexes - width_of_bar, number_of_tak_questions, width=width_of_bar, label="Tak")  # Tak
    plt.bar(x_indexes, number_of_nie_questions, width=width_of_bar, label="Nie")  # Nie
    plt.bar(x_indexes + width_of_bar, number_of_cancel_questions, width=width_of_bar, label="Odrzuć")  # Cancel

    global_list_of_shorten_questions = []
    for i in range(0, len(questions)):
        global_list_of_shorten_questions.append(string.ascii_lowercase[i])

    plt.xticks(ticks=x_indexes, labels=global_list_of_shorten_questions)
    plt.legend()
    plt.title("Wszystkie odpowiedzi")
    plt.xlabel("Pytania")
    plt.ylabel("Liczba odpowiedzi")
    plt.tight_layout()
    plt.show()


def take(list_of_list_with_three_elements, index_of_element):
    global_list = []
    for small_list in list_of_list_with_three_elements[1:]:
        global_list.append(small_list[index_of_element])
    return global_list


def map_answer_to_index(answer):
    if answer == "Tak":
        return 0
    elif answer == "Nie":
        return 1
    else:
        return 2


def number_of_canceled(data):
    """
    :return tuple,
        first element is number of samples,
        second element is number of cancelations
    """
    number_of_sample_data = len(data.module)
    return number_of_sample_data, len(list(filter(lambda cancel: cancel == "Cancel", data.answers)))


def pie_ai(data, plt):
    great_map = {'0': 0, '1': 0, '2': 0, '3': 0, '4': 0, '5': 0}
    for index, row in data.iterrows():
        if row.question == "Jak oceniasz ai?":
            great_map[row.answers] += 1

    explode = [0.05] * len(great_map.keys())
    plt.pie(great_map.values(), labels=great_map.keys(),
            explode=explode,
            autopct='%1.1f%%',
            wedgeprops={'edgecolor': 'black'})

    plt.title("Jak gracze ocenili AI\nw skali od 0 do 5")
    plt.tight_layout()
    plt.show()
