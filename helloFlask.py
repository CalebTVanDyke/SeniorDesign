import serial
from flask import Flask
app = Flask(__name__)

@app.route("/")
def read():
    ser = serial.Serial('COM4', 9600, timeout=1)
    x = ser.read(11).decode("utf-8");
    ser.close()
    return x

if __name__ == "__main__":
    app.run()
