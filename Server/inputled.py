#!/usr/bin/python
LED1OUT1=18
LED1OUT2=24
LED1IN=26
LED2OUT1=16
LED2OUT2=21
LED2IN=23
import RPi.GPIO as GPIO
import time, sys, subprocess, psutil, signal 
import os
user='pi'
running=True
def is_running(process):
	output = subprocess.Popen('ps aux', shell=True, stdout=subprocess.PIPE);
	processes = output.stdout.read()
	procstr = str(processes)
	if process in procstr:
		return True
	return False

def kill_process(process, sig):
	for proc in psutil.process_iter():
		if process in proc.name():
			print(process)
			os.kill(proc.pid, sig)
# use P1 header pin numbering convention

#subprocess.call("tvservice -p", shell=True)
GPIO.cleanup()
GPIO.setmode(GPIO.BOARD)

GPIO.setup(LED1OUT1, GPIO.OUT)
GPIO.setup(LED1OUT2, GPIO.OUT)
#GPIO.output(LED1OUT2, GPIO.HIGH)
GPIO.setup(LED1IN, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)

GPIO.setup(LED2OUT1, GPIO.OUT)
GPIO.setup(LED2OUT2, GPIO.OUT)

#GPIO.output(LED2OUT2, GPIO.HIGH)
GPIO.setup(LED2IN, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
while True:
	
	if GPIO.input(LED1IN): # Opens a picture with the url for the app

		subprocess.call("tvservice -e 'DMT 4 HDMI'", shell=True)
		GPIO.output(LED1OUT1, GPIO.HIGH)
		GPIO.output(LED1OUT2, GPIO.LOW)
		if not is_running("gst-launch-1.0 filesrc location=apk.png ! pngdec ! imagefreeze  ! eglglessink"):
			subprocess.call("gst-launch-1.0 filesrc location=apk.png ! pngdec ! imagefreeze  ! eglglessink &", shell=True)

	elif not GPIO.input(LED1IN): # Switches the color on the buttons
		GPIO.output(LED1OUT1, GPIO.LOW)
		GPIO.output(LED1OUT2, GPIO.HIGH)
	if GPIO.input(LED2IN): # Starts the process of running the emulator with networked input
		subprocess.call("tvservice -e 'DMT 4 HDMI'", shell=True) # Start the display on mode 4 (640x480 60Hz)
		GPIO.output(LED2OUT2, GPIO.LOW) # Button goes yellow
		subprocess.call("sudo -u %s ./generateid.py &" % user, shell=True) # Forks of next step
		time.sleep(3) # Waits for generateid.py to start

		while not GPIO.input(LED2IN):
			time.sleep(0.3) # Waits on each cycle
			if  running == True or is_running('retroarch'): #Checks if everything is running before setting button to red
				GPIO.output(LED2OUT1, GPIO.HIGH)
				running == True
			if(GPIO.input(LED1IN)):
				subproces.call("./second.py &", shell=True) # Checks if second button is pressed and starts second player input
			if(GPIO.input(LED2IN)): # Kills all the processes related to playing
				kill_process('retroarch', signal.SIGTERM)
				kill_process('generate.py', signal.SIGTERM)
				kill_process('vc.py', signal.SIGTERM)
				kill_process('profile.py', signal.SIGTERM)
				kill_process('fbi', signal.SIGINT)
				kill_process('second.py', signal.SIGTERM)

				subprocess.call("tvservice -o", shell=True) # Shut off display to preserve power and backlight circuitry
				GPIO.output(LED2OUT1, GPIO.LOW)
				GPIO.output(LED2OUT2, GPIO.HIGH)
				break
			#print("Waiting for button push")
			

	elif not GPIO.input(LED2IN) and not is_running('retroarch'):
		GPIO.output(LED2OUT1, GPIO.LOW)
		GPIO.output(LED2OUT2, GPIO.HIGH)


	time.sleep(0.3) # Poll time 0.3 seconds


