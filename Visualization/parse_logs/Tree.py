class Tree(object):
  def __init__(self):
    self.data = {
          'name': 'Node %d' % -1,
          'children': [],
          'fake': True
        }


  def _search_and_append(self, curr_node, to_append, parent):
    if 'name' in curr_node:
      if int(curr_node['name'][5:]) == parent:
        append_node = {
          'name': 'Node %d' % to_append,
          'children': []
        }
        curr_node['children'].append(append_node)
        return True

    for child in curr_node['children']:
      if self._search_and_append(curr_node=child, to_append=to_append, parent=parent):
        return True
    return False
  # end func


  def add_node(self, node, parent=None):
    """
    Append node to parent.

    :param node:
    :type node: int
    :param parent:
    :return:
    """
    node = int(node)
    if parent:
      parent = int(parent)

    if parent is None:
      child = {
        'name': 'Node %d' % node,
        'children': []
      }
      self.data['children'].append(child)
      return True

    # for n in self.data:
    if self._search_and_append(curr_node=self.data, to_append=node, parent=parent):
      return True

    print "parent not found"
    return False  # raise Error - parent not found
  # end func


  def _search_and_prune(self, curr_node, to_prune, parent, new_parent):
    if 'name' in curr_node:
      curr_node_id = int(curr_node['name'][5:])
      if curr_node_id == to_prune:
        parent['children'].remove(curr_node)
        if not new_parent:
          self.data['children'].append(curr_node)
        else:
          self.add_node(node=curr_node_id, parent=new_parent)
        return True

    for child in curr_node['children']:
      if self._search_and_prune(curr_node=child, to_prune=to_prune,
                                parent=curr_node, new_parent=new_parent):
        return True
    return False
  # end func


  def prune(self, node, new_parent=None):
    """
    if new_parent None: remove nodeid from its current parent and
    make it a separate clusterhead (add to self.data list)

    if new_parent is not None: remove nodeid from its current parent and
    make append to new_parent

    :param node:
    :param new_parent: if not None,
    :return:
    """
    node = int(node)
    node_name = 'Node %d' % node
    # for n in self.data:
    if self._search_and_prune(curr_node=self.data, to_prune=node,
                              parent=self.data, new_parent=new_parent):
        return True

    print "node not found"
    return False  # raise Error - node not found
  # end func

