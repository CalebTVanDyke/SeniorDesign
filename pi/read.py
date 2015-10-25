import serial
ser = serial.Serial('COM4', 9600, timeout=1)
x = ser.read(11).decode("utf-8");
print(x)
ser.close()
