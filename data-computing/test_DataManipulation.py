import unittest

import pandas as pd
from pandas import Series

from data_util import DataManipulation


class DataManipulationTest(unittest.TestCase):

    def test_map_id_by_count(self):
        csv_data = {'uniqIdentifier': Series([1, 2, 2, 3])}
        subject = DataManipulation(pd.DataFrame(csv_data))

        map_id_count = subject.map_unique_id_by_count()

        self.assertListEqual(map_id_count[0], [1, 2, 3])
        self.assertListEqual(map_id_count[1], [1, 2, 1])

    def test_map_questions_to_all_answers(self):
        scv_data = {'question': Series(["who", "what", "when", "who", "how"]),
                    'answers': Series(["Yes", "No", "Cancel", "Yes", '1'])}
        subject = DataManipulation(pd.DataFrame(scv_data))

        map_questions_to_all_answers = subject.map_questions_to_all_answers()

        self.assertEqual(len(map_questions_to_all_answers), 3)
        self.assertEqual(map_questions_to_all_answers.get('who'), [2, 0, 0])
        self.assertEqual(map_questions_to_all_answers.get('what'), [0, 1, 0])
        self.assertEqual(map_questions_to_all_answers.get('when'), [0, 0, 1])

    def test_map_ai_answers(self):
        questions = list(map(lambda number: "How do you score ai?", range(0, 7)))
        csv_data = {'question': Series(questions),
                    'answers': Series(['0', '1', '1', '2', '3', '3', '4'])}
        subject = DataManipulation(pd.DataFrame(csv_data))

        ai_answers = subject.map_ai_answers()

        self.assertEqual(ai_answers.get('0'), 1)
        self.assertEqual(ai_answers.get('1'), 2)
        self.assertEqual(ai_answers.get('2'), 1)
        self.assertEqual(ai_answers.get('3'), 2)
        self.assertEqual(ai_answers.get('4'), 1)
        self.assertEqual(ai_answers.get('5'), 0)


if __name__ == '__main__':
    unittest.main()
