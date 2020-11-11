import string

import numpy as np


class DataManipulation:

    def __init__(self, loaded_data):
        self.data = loaded_data
        self.hard = '[hard]'
        self.easy = '[easy]'

    def get_times_for_questions(self, type_of_question):
        """
        This method creates a list of times for any given type_of_question
        :param type_of_question: type could be 'hard' or 'easy' see __init__
        :return: list of times for given question type
        """
        times = list()
        for index, row in self.data.iterrows():
            question = row.question
            if question.startswith(type_of_question):
                times.append(row.time)
        return times

    def number_of_unique_id(self):
        return len(set(self.data.uniqIdentifier))

    def number_of_canceled(self):
        """
        :return tuple,
            first element is number of samples,
            second element is number of cancellations
        """
        number_of_sample_data = len(self.data.module)
        return number_of_sample_data, len(list(filter(lambda cancel: cancel == "Cancel", self.data.answers)))

    def map_unique_id_by_count(self):
        """
        This method creates a map with keys as a unique identifier of user
        and values as a number of questions that the user saw.
        :return:
        """
        map_person_by_count = dict.fromkeys(self.data.uniqIdentifier, 0)
        for unique_id in self.data.uniqIdentifier:
            map_person_by_count[unique_id] += 1
        people = list(map_person_by_count.keys())
        number = list(map_person_by_count.values())
        return people, number

    def map_questions_to_all_answers(self):
        """
        map_questions_to_all_answers: key is the question
        value is the list of answers mapped to numbers. [0, 0, 0] = ["Yes", "No", "Cancel"]
        :param data:
        :param plt:
        :return:
        """
        map_questions_to_all_answers = {key: [0, 0, 0] for key in self.data.question}

        for index, row in self.data.iterrows():
            answer = row.answers
            if not answer.isdigit():
                val = self.map_answer_to_index(answer)
                current_question = row.question

                list_of_answers = map_questions_to_all_answers[current_question]
                list_of_answers[val] += 1  # values in the map are mutable and this is a reference
        return {x: y for x, y in map_questions_to_all_answers.items() if y != [0, 0, 0]}

    @staticmethod
    def map_answer_to_index(answer):
        if answer == "Yes":
            return 0
        elif answer == "No":
            return 1
        else:
            return 2

    def map_ai_answers(self):
        """
        This method create a map with keys as a possible answers by the user
        and values as a number of seen answer.
        :return: map of seen answers about strength of ai
        """
        great_map = {'0': 0, '1': 0, '2': 0, '3': 0, '4': 0, '5': 0}
        for index, row in self.data.iterrows():
            if row.question == "How do you score ai?":
                great_map[row.answers] += 1
        return great_map


def average_time_response(time_column):
    sum = 0
    for time in time_column:
        sum += time
    return sum / len(time_column)


class DataPlotting:

    def __init__(self, plot, data_manipulation):
        self.plt = plot
        self.data_manipulation = data_manipulation

    def bar_number_of_questions(self):
        (people, number) = self.data_manipulation.map_unique_id_by_count()
        self.plt.title("Ilość wyświetlonych pytań\ndla danego gracza")
        self.plt.bar(people, number)
        self.plt.xlabel("Identyfikator użytkownika")
        self.plt.ylabel("Ilość pytań")
        self.plt.show()

    def bar_questions_yes_no_cancel_options(self):
        """
        map_questions_to_all_answers: key is the question
            value is the list of answers mapped to numbers. [0, 0, 0] = ["Tak", "Nie", "Cancel"]
        :param data:
        :param plt:
        :return:
        """
        map_questions_to_all_answers = self.data_manipulation.map_questions_to_all_answers()
        print(map_questions_to_all_answers)

        # plotting
        questions = map_questions_to_all_answers.keys()
        x_indexes = np.arange(len(questions))  # for the plot. instead of str we have int
        width_of_bar = 0.25

        number_of_all_questions = list(map_questions_to_all_answers.values())
        number_of_tak_questions = self.take(number_of_all_questions, 0)
        number_of_nie_questions = self.take(number_of_all_questions, 1)
        number_of_cancel_questions = self.take(number_of_all_questions, 2)

        self.plt.bar(x_indexes - width_of_bar, number_of_tak_questions, width=width_of_bar, label="Yes")  # Tak
        self.plt.bar(x_indexes, number_of_nie_questions, width=width_of_bar, label="No")  # Nie
        self.plt.bar(x_indexes + width_of_bar, number_of_cancel_questions, width=width_of_bar, label="Cancel")  # Cancel

        global_list_of_shorten_questions = []
        for i in range(0, len(questions)):
            global_list_of_shorten_questions.append(string.ascii_lowercase[i])

        self.plt.xticks(ticks=x_indexes, labels=global_list_of_shorten_questions)
        self.plt.legend()
        self.plt.title("Wszystkie odpowiedzi")
        self.plt.xlabel("Pytania")
        self.plt.ylabel("Liczba odpowiedzi")
        self.plt.tight_layout()
        self.plt.show()

    def take(self, list_of_list_with_three_elements, index_of_element):
        global_list = []
        for small_list in list_of_list_with_three_elements:
            global_list.append(small_list[index_of_element])
        return global_list

    def pie_ai(self):
        great_map = self.data_manipulation.map_ai_answers()

        explode = [0.05] * len(great_map.keys())
        self.plt.pie(great_map.values(), labels=great_map.keys(),
                     explode=explode,
                     autopct='%1.1f%%',
                     wedgeprops={'edgecolor': 'black'})

        self.plt.title("Jak gracze ocenili AI\nw skali od 0 do 5")
        self.plt.tight_layout()
        self.plt.show()
