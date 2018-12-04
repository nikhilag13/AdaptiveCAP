import sys, os

# _app_path = 'Visualization/'
# from os.path import dirname
# project_home = dirname(dirname(sys.path[0]))
# to_append = os.path.join(project_home, _app_path)
# # print to_append
# sys.path.insert(0, to_append)
# print (sys.path)
# import app

import requests, json
# from flask import jsonify


def draw(list_of_nodes):
  """
  Will be invoked every time there is a change in the topology.

  list_of_nodes samples:
  1)
  [{'children': [{'children': [{'children': [{'children': [],
                                            'name': 'Node 9'}],
                              'name': 'Node 4'}],
                'name': 'Node 2'}],
  'name': 'Node 0'},
 {'children': [{'children': [], 'name': 'Node 10'}], 'name': 'Node 8'},
 {'children': [{'children': [], 'name': 'Node 6'}], 'name': 'Node 7'},
 {'children': [{'children': [], 'name': 'Node 3'},
               {'children': [{'children': [], 'name': 'Node 11'}],
                'name': 'Node 5'}],
  'name': 'Node 1'}]

  2)
  [{'children': [{'children': [], 'name': 'Node 1'}], 'name': 'Node 0'}]

  :param list_of_nodes: samples provided above
  :type list_of_nodes: [dict]
  :return: None
  Visualization/parse_logs/Draw.py
  """
  pass
  # raise NotImplementedError()
  # print list_of_nodes
  # app.updateTree(list_of_nodes)
  url = 'http://127.0.0.1:5000/update_data'
  requests.post(url=url, data=json.dumps(list_of_nodes))
