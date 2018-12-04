# from flask import Flask, render_template, jsonify, request
# from threading import Thread
# from time import sleep
# # import pandas as pd

# import sys, os
# from os.path import dirname
# _PARSE_LOGS_PATH = 'TACAN/Visualization/parse_logs'
# project_home = dirname(dirname(sys.path[0]))
# to_ins = os.path.join(project_home, _PARSE_LOGS_PATH)
# # print to_ins
# sys.path.insert(0, to_ins)
# from parse_logs import parse


# app = Flask(__name__)


# @app.route("/")
# def index():
# 	# parse()
# 	t = Thread(target=parse, args=())
# 	t.start()
# 	sleep(1)
# 	return render_template("index.html")


# @app.route("/view")
# def dashboard():
# 	return render_template("dashboard.html")


# @app.route("/view/2")
# def dashboard2():
# 	return render_template("dashboard2.html")


# @app.route("/view/3")
# def dashboard3():
# 	return render_template("dashboard3.html")

# @app.route("/view/4")
# def demo():
# 	return render_template("demo.html")



# tree = ('{'
#           '"name": "Node -1",'
#           '"children": [],'
#           '"fake": true'
#         '}')

# @app.route("/update_data", methods=["POST"])
# def updateTree():
# 	global tree
# 	content = request.data
# 	tree = content
# 	# print ('content', type(content))
# 	# print content
# 	# print "updating"
# 	return 'OK'


# @app.route("/data")
# def data():
# 	# print "I am here"
# 	with open("./static/myflare.json", "w+") as fb:
# 		fb.write(tree)
# 	return jsonify({"result":"success"})


# if __name__ == "__main__":
# 	app.run(debug=True)





from flask import Flask, render_template, jsonify, request
from threading import Thread
from time import sleep
# import pandas as pd

import sys, os
from os.path import dirname
_PARSE_LOGS_PATH = 'AdaptiveCAP/Visualization/parse_logs'
project_home = dirname(dirname(sys.path[0]))
to_ins = os.path.join(project_home, _PARSE_LOGS_PATH)
print to_ins
sys.path.insert(0, to_ins)
from parse_logs import parse


app = Flask(__name__)


@app.route("/view/6")
def index():
	# parse()
	t = Thread(target=parse, args=())
	t.start()
	sleep(1)
	return render_template("index.html")


@app.route("/view")
def dashboard():
	return render_template("dashboard.html")


@app.route("/view/2")
def dashboard2():
	return render_template("dashboard2.html")


@app.route("/view/3")
def dashboard3():
	return render_template("dashboard3.html")

@app.route("/view/4")
def demo():
	return render_template("demo.html")

@app.route("/story")
def story():
	return render_template("story.html")

@app.route("/")
def rest():
    return render_template("restaurants.html")


@app.route("/dashboard_ui")
def dash():
    return render_template("dashboard_ui.html")



tree = ('{'
          '"name": "Node -1",'
          '"children": [],'
          '"fake": true'
        '}')

@app.route("/update_data", methods=["POST"])
def updateTree():
	global tree
	content = request.data
	tree = content
	# print ('content', type(content))
	# print content
	# print "updating"
	return 'OK'


@app.route("/data")
def data():
	# print "I am here"
	with open("./static/myflare.json", "w+") as fb:
		fb.write(tree)
	return jsonify({"result":"success"})


if __name__ == "__main__":
	app.run(debug=True)