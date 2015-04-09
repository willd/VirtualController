#!/usr/bin/python
import os
import socket
import subprocess
import sys
import platform
debug=False
game='smw.smc'
sudp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
uuid=str(sys.argv) # Fetches arguments supplied from flags
sudp.bind(('', 10500)) # Binds to port 10500 to get the apps uuid
data, addr = sudp.recvfrom(1024)

#print(addr)
path="saves/"+str(data)+"/"
sudp.close()
if( not debug):

	if(path in os.listdir(".")): # Checks if path is already created and starts retroarch with save state
		subprocess.call("retroarch %s -S %s -s %s &" % (game, path, path), shell=True)
	elif(data==uuid): # Checks if the servers generated id matches the one sent from client and creates profile
		subprocess.call("mkdir %s" % path, shell=True)		
		subprocess.call("retroarch %s -S %s -s %s &" % (game, path, path), shell=True)
	elif(data!=uuid and data!=None): # If data is inconsistent but not null, try to create dir and proceed like first case
		subprocess.call("mkdir %s" % path, shell=True)		
		subprocess.call("retroarch %s -S %s -s %s &" % (game, path, path), shell=True)

	else:	#On any error, start new temporary instance.
		subprocess.call("retroarch %s &" % (game) , shell=True)
	
subprocess.call("rm test.png", shell=True) # Remove temporary file
if("armv6l" in platform.uname()): # Kill picture viewer
	subprocess.call("killall gst-launch-1.0", shell=True)
else:
	subprocess.call("killall gpicview", shell=True)

if(debug): # Debug purposes
	subprocess.call("./demo.py %s %s %s" % (addr[0], addr[1], 0), shell=True)
else:	# Sends address and port to input controller
	subprocess.call("./vc.py %s %s %s" % (addr[0], addr[1], 0), shell=True)
