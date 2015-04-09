#!/usr/bin/python
import socket
import subprocess
import time
from evdev import UInput, ecodes as e
ui = UInput()

ui.write(e.EV_KEY, e.KEY_N, 1)
ui.write(e.EV_KEY, e.KEY_N, 0)
ui.syn()
sudp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

sudp.bind(('', 10500))
data, addr = sudp.recvfrom(1024)

sudp.close()
ui.write(e.EV_KEY, e.KEY_N, 1)
ui.write(e.EV_KEY, e.KEY_N, 0)
ui.syn()
ui.close()
subprocess.call("./vc.py %s %s %s" % (addr[0], addr[1], 1), shell=True)

