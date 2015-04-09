#!/usr/bin/python3
import RPi.GPIO as GPIO   
import time, sys

LED1OUT1=24
LED1OUT2=21
LED1IN=26
LED2OUT1=18
LED2OUT2=16
LED2IN=15

# use P1 header pin numbering convention
GPIO.setmode(GPIO.BOARD)
while True:
        GPIO.setup(LED1OUT1, GPIO.OUT)
        GPIO.setup(LED1OUT2, GPIO.OUT)
        GPIO.setup(LED1IN, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
        GPIO.setup(LED2OUT1, GPIO.OUT)
        GPIO.setup(LED2OUT2, GPIO.OUT)
        GPIO.setup(LED2IN, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)

        GPIO.output(LED1OUT2, GPIO.LOW)
        GPIO.output(LED1OUT1, GPIO.HIGH)
        time.sleep(0.1)
        GPIO.output(LED1OUT1, GPIO.LOW)
        GPIO.output(LED1OUT2, GPIO.HIGH)

        GPIO.output(LED2OUT2, GPIO.LOW)
        GPIO.output(LED2OUT1, GPIO.HIGH)
        time.sleep(0.1)
        GPIO.output(LED2OUT1, GPIO.LOW)
        GPIO.output(LED2OUT2, GPIO.HIGH)


        if GPIO.input(LED1IN):
                GPIO.cleanup()
                exit()


