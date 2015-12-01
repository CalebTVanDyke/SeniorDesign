from gevent import monkey
monkey.patch_all()

import time
import serial
import struct
from flask import Flask, flash, redirect, url_for, session, render_template, request, session, jsonify
from flask.ext.socketio import SocketIO, emit
from threading import Thread
from random import randint
from forms import LoginForm, RegisterForm, ChangePasswordForm, GeneralSettingForm, AlertSettingsForm
from db_conn import DbRequest
from date_utils import DateUtils 
from user_utils import UserUtils, User, Receiver
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
CHANGE_PASSWORD_API = "changePasswordApi"
GENERAL_SETTINGS = "generalSettings"
ALERT_SETTINGS = "alertSettings"
DELETE_RECEIVER = "deleteReceiver"


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
    heartRate = float(request.args.get("heartRate"))
    blood = float(request.args.get("bloodOxygen"))
    temp = float(request.args.get("temp"))
    user_id = request.args.get("user_id")
    time = datetime.datetime.now()
    if heartRate == None or blood == None or temp == None:
        return jsonify(**{"error" : True})
    query = "INSERT INTO `readings` (`user_id`, `time`,`blood_oxygen`, `heart_rate`, `temp`) VALUES ('{0}', '{1}', '{2}', '{3}', '{4}');" \
        .format(user_id, time, blood, heartRate, temp)
    db.query(query)

    result = db.query("SELECT sensitivity, avg_temp, avg_heart_rate, avg_blood_ox FROM `users` where id=" + user_id)[0]
    sensitivity = result['sensitivity']
    avg_blood_ox = float(result['avg_blood_ox'])
    avg_heart_rate = float(result['avg_heart_rate'])
    avg_temp = float(result['avg_temp'])
    alert = False
    if sensitivity == 'h':
        if abs(avg_blood_ox - blood) >= 3:
            alert = True
        if abs(avg_temp - temp) >= 2:
            alert = True
        if abs(avg_heart_rate - heartRate) >= 10:
            alert = True
    elif sensitivity == 'm':
        if abs(avg_blood_ox - blood) >= 5:
            alert = True
        if abs(avg_temp - temp) >= 3:
            alert = True
        if abs(avg_heart_rate - heartRate) >= 20:
            alert = True
    else:
        if abs(avg_blood_ox - blood) >= 7:
            alert = True
        if abs(avg_temp - temp) >= 4:
            alert = True
        if abs(avg_heart_rate - heartRate) >= 30:
            alert = True

    # update averages
    avg = db.query("SELECT AVG(temp) as 'avg'  FROM `readings` WHERE user_id=" + user_id)[0]['avg']
    db.query("UPDATE `users` SET avg_temp=" + avg + " WHERE id=" + user_id)
    avg = db.query("SELECT AVG(heart_rate) as 'avg'  FROM `readings` WHERE user_id=" + user_id)[0]['avg']
    db.query("UPDATE `users` SET avg_heart_rate=" + avg + " WHERE id=" + user_id)
    avg = db.query("SELECT AVG(blood_oxygen) as 'avg'  FROM `readings` WHERE user_id=" + user_id)[0]['avg']
    db.query("UPDATE `users` SET avg_blood_ox=" + avg + " WHERE id=" + user_id)
    return jsonify(**{"alert" : alert})

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

@app.route("/" + GENERAL_SETTINGS, methods=['GET', 'POST'])
def generalSettings():
    form = GeneralSettingForm(request.form)
    if request.method == 'POST':
        if form.validate(db):
            UserUtils.save_user_info(db, session['user_id'], form.primary_phone.data, form.primary_email.data)
            return render_template(GENERAL_SETTINGS + '.html',
                form=form,
                generalActive='active',
                message="Settings saved successfully.",
                settings='active',
                user=session['username'])
        else:
            return render_template(GENERAL_SETTINGS + '.html',
                form=form,
                generalActive='active',
                hidden='hidden',
                settings='active',
                user=session['username'])
    info = UserUtils.get_user_info(db, session["user_id"])
    form.primary_email.data = info.primary_email
    form.primary_phone.data = info.primary_phone
    return render_template(GENERAL_SETTINGS + '.html',
        form=form,
        generalActive='active',
        hidden='hidden',
        settings='active',
        user=session['username'])

@app.route("/" + ALERT_SETTINGS, methods=['GET', 'POST'])
def alertSettings():
    form = AlertSettingsForm(request.form)
    info = UserUtils.get_user_info(db, session["user_id"])
    if request.method == 'POST':
        if form.validate():
            if form.email.data != "":
                UserUtils.add_receiver(db, form.email.data, session["user_id"])
                info = UserUtils.get_user_info(db, session["user_id"])
                return render_template(ALERT_SETTINGS + '.html',
                    form=form,
                    alertActive='active',
                    settings='active',
                    user=session['username'],
                    message='Receiver added successfully',
                    receivers=info.receivers)
            else:
                UserUtils.add_receiver(db, form.phone.data + "@" + form.carrier.data, session["user_id"])
                info = UserUtils.get_user_info(db, session["user_id"])
                return render_template(ALERT_SETTINGS + '.html',
                    form=form,
                    alertActive='active',
                    settings='active',
                    user=session['username'],
                    message='Receiver added successfully',
                    receivers=info.receivers)
    return render_template(ALERT_SETTINGS + '.html',
        form=form,
        alertActive='active',
        settings='active',
        user=session['username'],
        hidden='hidden',
        receivers=info.receivers)

    

@app.route("/" + CHANGE_PASSWORD_API, methods=['GET', 'POST'])
def changePasswordApi():
    user_id = request.form['user_id']
    old_password = request.form['old_password']
    new_password = request.form['new_password']
    if UserUtils.change_password(db, user_id, old_password, new_password):
        return jsonify(**{"error" : False})
    return jsonify(**{"error" : True})

@app.route("/" + DELETE_RECEIVER, methods=['GET', 'POST'])
def deleteReceiver():
    UserUtils.delete_receiver(db, request.args.get('toDelete'), session['user_id'])
    return jsonify(**{"error" : False})


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
