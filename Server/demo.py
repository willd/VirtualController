#!/usr/bin/python

from evdev import UInput, ecodes as e
import socket
import time
import sys

ui = UInput()

sudp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
args=sys.argv
print(args)

sudp.bind(('', int(args[2]))) 
global dpad 
global key 
global diag
global select
global start
global leftsh
global rightsh

player1=[e.KEY_UP, e.KEY_DOWN, e.KEY_LEFT, e.KEY_RIGHT, e.KEY_Z, e.KEY_S, e.KEY_X, e.KEY_A, e.KEY_Q, e.KEY_W, e.KEY_E, e.KEY_R]
player2=[e.KEY_I, e.KEY_K, e.KEY_J, e.KEY_L, e.KEY_C, e.KEY_F, e.KEY_V, e.KEY_D, e.KEY_T, e.KEY_Y, e.KEY_U, e.KEY_G]
dpad=0
key=0
diag=0
select=0
start=0
leftsh=0
rightsh=0
idata=[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
prev=[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
empty=[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
x=int(args[3])
player=[player1, player2]
def setDirection(data):
	global dpad 
	if(data[4]==191):
		dpad=player[x][0]
	elif(data[4]==63):
		dpad=player[x][1]
	elif(data[0]==191):
		dpad=player[x][2]
	elif(data[0]==63):
		dpad=player[x][3]
def setKey(data):
	global key
	global diag
	if(idata[12]==63):
		key=player[x][4]
	elif(idata[12]==191):
		key=player[x][5]
	elif(idata[8]==63):
		key=player[x][6]
	elif(idata[8]==191):
		key=player[x][7]
def setDiagonal(data):
	global key
	global diag	
	if(data[8]==191 and data[11]==125):
		key=player[x][7]
		diag=player[x][4]
	elif(data==[0, 0, 0, 0, 0, 0, 0, 0, 63, 53, 63, 125, 191, 53, 63, 125, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]):
		diag=player[x][6]
		key=player[x][5]
	elif(data==[0, 0, 0, 0, 0, 0, 0, 0, 191, 53, 63, 125, 191, 53, 63, 125, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]):
		key=player[x][7]
		diag=player[x][5]
def setSelectStart(data):
	global select
	global start
	if(data[27] == 1):
		start = player[x][10]
	if(data[31] == 1):
		select = player[x][11]

		
def setShoulderButton(data):
	global leftsh
	global rightsh
	if(data[23] == 1):
		rightsh = player[x][9]
#[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
	if(data[19] == 1):
		leftsh = player[x][8]

while True: 
	data, addr = sudp.recvfrom(1024)
	
	idata = map(ord,data);
	idata = [idata[3], idata[7], idata[11], idata[15], idata[19], idata[23], idata[27], idata[31]]

	print(idata)

sudp.close()

ui.close()
