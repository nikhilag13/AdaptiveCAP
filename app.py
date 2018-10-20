from flask import Flask, render_template, jsonify, request

import sys, os
from os.path import dirname

app = Flask(__name__)

@app.route("/")
def dashboardIndex():
	return render_template("bootdemo.html")

@app.route("/view/sparse")
def dashboard1():
	return render_template("demoSparse.html")

@app.route("/view/medium")
def dashboard2():
	return render_template("demoMedium.html")

@app.route("/view/dense")
def dashboard3():
	return render_template("demoDense.html")

@app.route("/view/tree")
def dashboard4():
	return render_template("demo.html")

if __name__ == "__main__":
	app.run(debug=True)