from gevent import monkey
monkey.patch_all()

import time
from flask import Flask, render_template
from flask.ext.socketio import SocketIO, emit
from threading import Thread
from random import randint

app = Flask(__name__)
app.debug = True;
app.config['SECRET_KEY'] = 'secret!'
app.config['PROPAGATE_EXCEPTIONS'] = True
socketio = SocketIO(app)
thread = None

def background_thread():
    while True:
        time.sleep(1)
        socketio.emit('my response',
                      {'data' : 'Server generated event', 'num' : randint(5,10)},
                      namespace='/test')

@app.route("/")
def index():
    global thread
    if thread is None:
        thread = Thread(target=background_thread)
        thread.start()
    return render_template('index.html')

@socketio.on('connect', namespace='/test')
def test_connect():
    emit('my response', {'data': 'Connected', 'count' : 0})

@socketio.on('disconnect', namespace='/test')
def test_disconnect():
    print('Client disconnected')

if __name__ == "__main__":
    socketio.run(app)
