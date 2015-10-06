from gevent import monkey
monkey.patch_all()

import time
import serial
import struct
from flask import Flask, flash, redirect, url_for, session, render_template, request, session, jsonify
from flask.ext.socketio import SocketIO, emit
from threading import Thread
from random import randint
from forms import LoginForm, RegisterForm
from db_conn import DbRequest
from date_utils import DateUtils

app = Flask(__name__)
app.debug = True;
app.config['SECRET_KEY'] = 'secret!'
app.config['PROPAGATE_EXCEPTIONS'] = True
socketio = SocketIO(app)
oxygenThread = None 
heartThread = None
db = DbRequest()

LIVE_DATA = "liveData"
LOGIN = "login"
REGISTER = "register"
ABOUT = "about"
PAST_DATA = "pastData"
SETTINGS = "settings"
LOGOUT = "logout"
GET_DAY_DATA = "getDayData"
GET_DATE_RANGE_DATA = "getDateRangeData"


@app.route("/")
def default():
    if 'username' not in session or session['username'] == None:
        return redirect(url_for(LOGIN))
    return redirect(url_for(ABOUT))
 
@app.route("/" + LOGIN, methods=['GET', 'POST'])
def login():
    form = LoginForm(request.form)
    if request.method == 'POST':
        if form.validate(db) == False:
            flash('Username and password did not match.')
        else:
            return redirect(url_for(ABOUT))
    return render_template(LOGIN + '.html', form=form)

@app.route("/" + REGISTER, methods=['GET', 'POST'])
def register():
    form = RegisterForm(request.form)
    if request.method == 'POST':
        if form.validate(db):
            return redirect(url_for(LOGIN))
    print form.errors
    return render_template(REGISTER + '.html', form=form)

@app.route("/" + LOGOUT)
def logout():
    session['username'] = None
    session['user_id'] = None
    return redirect(url_for(LOGIN))

@app.route("/" + ABOUT)
def about():
    if 'username' not in session or session['username'] == None:
        return redirect(url_for(LOGIN))
    return render_template(ABOUT + '.html', about="active", user=session['username'])

@app.route("/" + PAST_DATA)
def pastData():
    if 'username' not in session or session['username'] == None:
        return redirect(url_for(LOGIN))
    return render_template(PAST_DATA + '.html', 
        pastData="active", user=session['username'], 
        dates=DateUtils.GetAvailableDates(db, session['user_id']))

@app.route("/" + SETTINGS)
def settings():
    if 'username' not in session or session['username'] == None:
        return redirect(url_for(LOGIN))
    return render_template(SETTINGS + '.html', settings="active", user=session['username'])

@app.route("/" + GET_DAY_DATA)
def getDayData():
    myDict = DateUtils.GetDataForDay(db, request.args.get('date'), session['user_id'], int(request.args.get('dataGap')))
    return jsonify(**myDict)

@app.route("/" + GET_DATE_RANGE_DATA)
def getDateRangeData():
    myDict = DateUtils.GetDataDateRange(db, request.args.get('startDate'),
        request.args.get('endDate'), session['user_id'],  int(request.args.get('dataGap')))
    return jsonify(**myDict)

@app.route("/" + LIVE_DATA)
def liveData():
    if 'username' not in session or session['username'] == None:
        return redirect(url_for(LOGIN))
    global oxygenThread
    global heartThread
    if oxygenThread is None:
        oxygenThread = Thread(target=oxygen_thread)
        oxygenThread.start()
    if heartThread is None:
        heartThread = Thread(target=heart_thread)
        heartThread.start()
    if request.args.get('chart') == 'oxygen':
        return render_template(LIVE_DATA + '.html', liveData="active", user=session['username'], oxygenActive='active')
    return render_template(LIVE_DATA + '.html', liveData="active", user=session['username'], heartActive='active')

def oxygen_thread():
    # ser = serial.Serial('COM4', 9600, timeout=1)
    while True:
        time.sleep(1)
        # x = ser.read(1)
        # val = struct.unpack('B', x)[0]
        val = randint(97, 100)
        socketio.emit('oxygen',
                      {'data' : 'Server generated event', 'num' : val},
                      namespace='/test')
def heart_thread():
    # ser = serial.Serial('COM4', 9600, timeout=1)
    while True:
        time.sleep(1)
        # x = ser.read(1)
        # val = struct.unpack('B', x)[0]
        val = randint(67, 70)
        socketio.emit('heart',
                      {'data' : 'Server generated event', 'num' : val},
                      namespace='/test')

@socketio.on('connect', namespace='/test')
def test_connect():
    emit('my response', {'data': 'Connected', 'count' : 0})

@socketio.on('disconnect', namespace='/test')
def test_disconnect():
    print('Client disconnected')

if __name__ == "__main__":
    socketio.run(app)
