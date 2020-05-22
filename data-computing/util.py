# small util


def sanitizer_file(filename, destination_filename):
    with open(filename, 'r') as f:
        for line in f:
            line
            print(line, end="")


def average_time_response(time_column):
    sum = 0
    for time in time_column:
        sum += time
    return sum / len(time_column)


def column_chart_per_peron(read_data_from_csv, plt):
    map_person_by_number = dict.fromkeys(read_data_from_csv.uniqIdentifier, 0)
    for id in read_data_from_csv.uniqIdentifier:
        map_person_by_number[id] += 1
    reduce_size_of_person = lambda person: person[0:5]
    people = list(map(reduce_size_of_person, map_person_by_number.keys()))
    number = map_person_by_number.values()
    plt.bar(people, number)
    plt.show()


def number_of_canceled(data):
    """
    :return tuple,
        first element is number of samples,
        second element is number of cancelations
    """
    number_of_sample_data = len(data.module)
    return number_of_sample_data, len(list(filter(lambda cancel: cancel == "Cancel", data.answers)))
