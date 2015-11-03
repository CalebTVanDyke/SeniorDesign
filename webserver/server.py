from gevent import monkey
monkey.patch_all()

import time
import serial
import struct
from flask import Flask, flash, redirect, url_for, session, render_template, request, session, jsonify
from flask.ext.socketio import SocketIO, emit
from threading import Thread
from random import randint
from forms import LoginForm, RegisterForm, ChangePasswordForm
from db_conn import DbRequest
from date_utils import DateUtils 
from user_utils import UserUtils
import datetime

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
API_LOGIN = "apiLogin"
API_REGISTER = "apiRegister"
DATETIME_RANGE = "getDateTimeRange"
LOAD_DATA = "loadData"
CHANGE_PASSWORD = "changePassword"


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
    user_id = request.args.get('user_id')
    if user_id == None:
        user_id = session['user_id']
    myDict = DateUtils.GetDataForDay(db, request.args.get('date'), user_id, int(request.args.get('dataGap')))
    return jsonify(**myDict)

@app.route("/" + GET_DATE_RANGE_DATA)
def getDateRangeData():
    user_id = request.args.get('user_id')
    if user_id == None:
        user_id = session['user_id']
    myDict = DateUtils.GetDataDateRange(db, request.args.get('startDate'),
        request.args.get('endDate'), user_id,  int(request.args.get('dataGap')))
    return jsonify(**myDict)

@app.route("/" + API_LOGIN, methods=['GET', 'POST'])
def apiLogin():
    username = request.form['username']
    password = request.form['password']
    if UserUtils.login(db, username, password):
        return jsonify(**{"error" : False, "user_id" : session["user_id"]})
    return jsonify(**{"error" : True})

@app.route("/" + API_REGISTER, methods=['GET', 'POST'])
def apiRegister():
    username = request.form['username']
    password = request.form['password']
    email = request.form['email']
    phone = request.form['phone']
    if UserUtils.register(db, username, password, email, phone):
        return jsonify(**{"error" : False})
    return jsonify(**{"error" : True})

@app.route("/" + DATETIME_RANGE)
def getDateTimeRange():
    user_id = request.args.get('user_id')
    if user_id == None:
        user_id = session['user_id']
    myDict = DateUtils.GetDataDateTimeRange(db, request.args.get('startDateTime'),
        request.args.get('endDateTime'), user_id,  int(request.args.get('dataGap')))
    return jsonify(**myDict)

@app.route("/" + LOAD_DATA)
def loadData():
    heartRate = request.args.get("heartRate")
    blood = request.args.get("bloodOxygen")
    temp = request.args.get("temp")
    user_id = request.args.get("user_id")
    time = datetime.datetime.now()
    if heartRate == None or blood == None or temp == None:
        return jsonify(**{"error" : True})
    query = "INSERT INTO `readings` (`user_id`, `time`,`blood_oxygen`, `heart_rate`, `temp`) VALUES ('{0}', '{1}', '{2}', '{3}', '{4}');" \
        .format(user_id, time, blood, heartRate, temp)
    result = db.query(query)
    return jsonify(**{"error" : False})

@app.route("/" + CHANGE_PASSWORD, methods=['GET', 'POST'])
def changePassword():
    form = ChangePasswordForm(request.form)
    if request.method == 'POST':
        if form.validate(db):
            return render_template(CHANGE_PASSWORD + '.html', 
                form=form, 
                passActive="active", 
                message='Password changed successfully',
                user=session['username'])
    return render_template(CHANGE_PASSWORD + '.html', 
        form=form, 
        passActive="active", 
        hidden='hidden', 
        settings="active",
        user=session['username'])


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
    socketio.run(app, host='0.0.0.0', port=80)
