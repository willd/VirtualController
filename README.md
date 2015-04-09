VCserver
========

Server for the VirtualController app

This server is built in python, employing Retroarch, the emulator for playing games.

Python2.7 packages:
fastqrcode - To display the QR code containing your UUID and the servers IP address
netifaces - Fetches the IP address for your server
evdev - Translates network input to virtual keyboard presses

Package dependancies for Python packages:
python-pip
python-dev
libqrencode-dev 
libevdev-dev

Debian apt-get line:
sudo apt-get install python-pip python-dev libqrencode-dev libevdev-dev
sudo pip install fastqrcode netifaces evdev

Retroarch is probably not in your repository and should thus be built following this link (You can fetch the separate components from git yourself if you want, this fetches *everything* for all platform)sgo
https://github.com/Themaister/RetroArch/wiki

When compiling don't forget to configure --enable-udev on retroarch, or it might not work!


The server is using uinput, which requires writing permissions on /dev/uinput as well 
as the module loaded:

modprobe uinput

chmod 666 /dev/uinput

You start the server by running generate.py, which in turn start profile.py, that then 
starts vc.py

inputled.py - Button based input, that starts the profile based system

generateid.py - Generates an UUID and displays it and the servers IP through an QR code containing both.

profile.py - Contains an UDP server on port 10500, which the first part of the app uses to confirm the UUID, 
and to start the relevant profile

vc.py - Translates input from the app, on a port the app and server agrees on.

second.py - Opens up a second instance of vc.py for a second player


