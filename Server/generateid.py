#!/usr/bin/python
import uuid
import subprocess
import fastqrcode as qrcode
import platform
from netifaces import interfaces, ifaddresses, AF_INET
user='willd'
ip=0
for ifaceName in interfaces(): # Checks all available interfaces and matches whether you are on wireless or wired (Should be rewritten to be more dynamic..)
	addresses = [i['addr'] for i in ifaddresses(ifaceName).setdefault(AF_INET, [{'addr':'No IP addr'}] )]
	if(ifaceName == "eth0"):
		ip=addresses	
	if(ifaceName == "wlan0"):
		ip=addresses	

gen = uuid.uuid4() #Generates an unique id for your profile
data = str(gen)+" "+ip[0]
file = qrcode.encode(data) # Encodes IP and UUID in to an QR code
file.save('test.png')
if("armv6l" in platform.uname()): # Starts an imageviewer for arm based devices (Like the Pi)
	subprocess.call("gst-launch-1.0 filesrc location=test.png ! pngdec ! imagefreeze ! videoconvert ! eglglessink &", shell=True) # 
else: # amd64 etc
	subprocess.call("gpicview test.png &", shell=True)
subprocess.call("./profile.py %s" % gen, shell=True)



