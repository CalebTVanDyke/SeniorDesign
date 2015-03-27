import serial
import random

ser = serial.Serial('COM3', 9600, timeout=1)
print(ser.name)
while True:
    ser.write("hello world".encode())
ser.close()
