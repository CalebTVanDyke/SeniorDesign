import serial
import struct
from random import randint
ser = serial.Serial('COM3', 9600, timeout=1)
print(ser.name)
while True:
    ser.write(struct.pack('B', randint(90, 100)))
ser.close()
