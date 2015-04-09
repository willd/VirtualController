#!/usr/bin/python

from evdev import UInput, ecodes as e
import socket
import time
import sys

ui = UInput()

sudp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
args=sys.argv
print(args)

sudp.bind(('', int(args[2]))) # Rebinds to previous port
global dpad 
global key 
global diag
global select
global start
global leftsh
global rightsh

# Array containing keys matching retroarch.cfg for the emulator
player1=[e.KEY_UP, e.KEY_DOWN, e.KEY_LEFT, e.KEY_RIGHT, e.KEY_Z, e.KEY_S, e.KEY_X, e.KEY_A, e.KEY_Q, e.KEY_W, e.KEY_E, e.KEY_R]
player2=[e.KEY_I, e.KEY_K, e.KEY_J, e.KEY_L, e.KEY_C, e.KEY_F, e.KEY_V, e.KEY_D, e.KEY_T, e.KEY_Y, e.KEY_U, e.KEY_G]

dpad=0
key=0
diag=0
select=0
start=0
leftsh=0
rightsh=0

idata=[0, 0, 0, 0, 0, 0, 0, 0]
prev= [0, 0, 0, 0, 0, 0, 0, 0]
empty=[0, 0, 0, 0, 0, 0, 0, 0]
x=int(args[3]) # Which player do we apply for?
player=[player1, player2]
def setDirection(data):	# Sets the direction of the player
	global dpad 
	if(data[1]==255):
		dpad=player[x][0]
	elif(data[1]==1):
		dpad=player[x][1]
	elif(data[0]==255):
		dpad=player[x][2]
	elif(data[0]==1):
		dpad=player[x][3]
def setKey(data): # Sets a single key
	global key
	if(idata[3]==1):
		key=player[x][4]
	elif(idata[3]==255):
		key=player[x][5]
	elif(idata[2]==1):
		key=player[x][6]
	elif(idata[2]==255):
		key=player[x][7]
def setDiagonal(data): # Sets multiple keys from diagonal input. Some cases are unmatched due to no effect in game
	global key
	global diag	
	if(data[2]==3 and data[3]==2):
		key=player[x][7]
		diag=player[x][4]
	elif(data[2]==2 and data[3]==3):
		diag=player[x][6]
		key=player[x][5]
	elif(data[2]==2 and data[3]==2):
		key=player[x][7]
		diag=player[x][5]
def setSelectStart(data):
	global select
	global start
	if(data[6] == 1):
		start = player[x][10]
	if(data[7] == 1):
		select = player[x][11]

		
def setShoulderButton(data):
	global leftsh
	global rightsh
	if(data[5] == 1):
		rightsh = player[x][9]
	if(data[4] == 1):
		leftsh = player[x][8]
	
while True: 
	data, addr = sudp.recvfrom(1024)

	idata = map(ord,data) # Maps data to array
	idata = [idata[3], idata[7], idata[11], idata[15], idata[19], idata[23], idata[27], idata[31]] # Discard everything but every fourth value. These holds every relevant data
	#print(idata)
	if(idata==prev and idata!=empty): # If the array is not empty and the same as before, skip rest of loop. Makes you not need to push the button everytime to get results.
		prev=idata
		continue


	if(idata == empty): # If no key is pressed, stop writing keys and sync
		ui.write(e.EV_KEY, diag, 0)
		ui.write(e.EV_KEY, key, 0) 
		ui.write(e.EV_KEY, dpad, 0)
		ui.write(e.EV_KEY, leftsh, 0)
		ui.write(e.EV_KEY, rightsh, 0)
		ui.write(e.EV_KEY, select, 0)
		ui.write(e.EV_KEY, start, 0)

		ui.syn()
		diag=0
		key=0
		dpad=0	
		leftsh=0
		rightsh=0
		select=0
		start=0
		continue


	if(idata!=prev): # On change set keys to zero, sync and then set new key
		ui.write(e.EV_KEY, diag, 0)
		ui.write(e.EV_KEY, key, 0) 
		ui.write(e.EV_KEY, dpad, 0)
		ui.write(e.EV_KEY, leftsh, 0)
		ui.write(e.EV_KEY, rightsh, 0)
		ui.write(e.EV_KEY, select, 0)
		ui.write(e.EV_KEY, start, 0)

		ui.syn()
                diag=0
                key=0
                dpad=0

		setDirection(idata)
		setShoulderButton(idata)
		setSelectStart(idata)
		if not (int(idata[2]) == 3 or int(idata[3]) == 3): # Checks if diagonal keys are pressed
			setKey(idata)
			ui.write(e.EV_KEY, dpad, 0)
		else:
			setDiagonal(idata)
		ui.write(e.EV_KEY, diag, 1)
		ui.write(e.EV_KEY, dpad, 1)
		ui.write(e.EV_KEY, key, 1)  
		ui.write(e.EV_KEY, leftsh, 1)
		ui.write(e.EV_KEY, rightsh, 1)
		ui.write(e.EV_KEY, select, 1)
		ui.write(e.EV_KEY, start, 1)

		ui.syn()	

sudp.close()

ui.close()
