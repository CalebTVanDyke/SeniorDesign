from gevent import monkey
monkey.patch_all()

import time
import serial
import struct
from flask import Flask, render_template
from flask.ext.socketio import SocketIO, emit
from threading import Thread
from random import randint

app = Flask(__name__)
app.debug = True;
app.config['SECRET_KEY'] = 'secret!'
app.config['PROPAGATE_EXCEPTIONS'] = True
socketio = SocketIO(app)
oxygenThread = None
heartThread = None


def oxygen_thread():
    # ser = serial.Serial('COM4', 9600, timeout=1)
    while True:
        time.sleep(1)
        # x = ser.read(1)
        # val = struct.unpack('B', x)[0]
        val = randint(90, 100)
        socketio.emit('oxygen',
                      {'data' : 'Server generated event', 'num' : val},
                      namespace='/test')
def heart_thread():
    # ser = serial.Serial('COM4', 9600, timeout=1)
    while True:
        time.sleep(1)
        # x = ser.read(1)
        # val = struct.unpack('B', x)[0]
        val = randint(60, 120)
        socketio.emit('heart',
                      {'data' : 'Server generated event', 'num' : val},
                      namespace='/test')

@app.route("/")
def index():
    global oxygenThread
    global heartThread
    if oxygenThread is None:
        oxygenThread = Thread(target=oxygen_thread)
        oxygenThread.start()
    if heartThread is None:
        heartThread = Thread(target=heart_thread)
        heartThread.start()
    return render_template('index.html')

@socketio.on('connect', namespace='/test')
def test_connect():
    emit('my response', {'data': 'Connected', 'count' : 0})

@socketio.on('disconnect', namespace='/test')
def test_disconnect():
    print('Client disconnected')

if __name__ == "__main__":
    socketio.run(app)
