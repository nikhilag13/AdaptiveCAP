_SPANNING_TREE_FILE_DIR = 'parse_logs/'
# _LOG_FILE = '../../phase1/logs/2018-04-16-info.log'
# _LOG_FILE = '../../phase1/logs/2018-04-18-info.log'
# _LOG_FILE = 'phase1/logs/2018-10-15-info.log'
_LOG_FILE = 'infoLogFile.log'
_PARSE_LOGS_DEBUG = True
_DELAY = 2

# TODO: use relative imports
import sys, os
from os.path import dirname
from time import sleep

project_home = dirname(dirname(sys.path[0]))
print project_home
print ('project home--------')
to_ins = os.path.join(project_home, _SPANNING_TREE_FILE_DIR)
print to_ins
sys.path.insert(0, to_ins)
print sys.path

from spanning_tree import SPANNING_INFO
# print spanning_tree.SPANNING_INFO

from Tree import Tree
from Draw import draw
import pprint

pp= pprint.PrettyPrinter()

def parse():
  tree = Tree()
  project_home = dirname(sys.path[0])
  # while project_home.split('/')[-1] != 'AdaptiveCAP':
  #   project_home = dirname(project_home)
  # print ('project_home', project_home)

  log_path = os.path.join(project_home, _LOG_FILE)
  print ('log_path', log_path)

  with open(log_path, 'r') as fp:
    lines = fp.readlines()
    print "heyhehheehheheyeyeyeyeyeyyeyeyyyyyyyyy"
  print "file is accessed"
  interested = False
  hello_sent_from = -1  # new parent
  hello_sent_to = -1  # node which will move

  for line in lines:
    # TODO: start at timestamp/ automatically run afterclustering

    if not line:
      continue

    last_part = line.split('-')[-1].strip()
    if last_part == 'Starting Server':
      node_id = line.split('-')[-2].split(':')[-1].strip()

      if node_id in SPANNING_INFO:
        parent = SPANNING_INFO[node_id]['parentId']
      else:
        parent = None
      if parent:
        parent = int(parent)
      node_id = int(node_id)
      tree.add_node(node=node_id, parent=parent)

      sleep(_DELAY)
      draw(tree.data)

      if _PARSE_LOGS_DEBUG:
        print 'Starting Server'
        print ('node_id', node_id)
        print ('parent', parent)
        pp.pprint(tree.data)
        print "\n"

    elif last_part == 'Got Prune':
      node_id = line.split('-')[-2].split(':')[-1].strip()
      tree.prune(node_id)
      sleep(_DELAY)
      draw(tree.data)
      if _PARSE_LOGS_DEBUG:
        print 'Got Prune'
        pp.pprint(tree.data)
        print "\n"

    elif last_part == 'Got Response: interested: 1':
      interested = True
      hello_sent_from = line.split('-')[-2].split(':')[-1].strip()

    elif interested:
      interested = False
      hello_sent_to = line.split(':')[-1].strip()
      new_parent = int(hello_sent_from)
      to_move = int(hello_sent_to)
      tree.prune(node=to_move, new_parent=new_parent)
      sleep(_DELAY)
      draw(tree.data)
      if _PARSE_LOGS_DEBUG:
        print 'Got Response: interested: 1'
        print "moving Node {to_move} to Node {new_parent}".format(
          to_move=to_move, new_parent=new_parent)
        pp.pprint(tree.data)
        print '\n'
