# kpiradio
Kotlin Pi Clock Radio Backend

Let your Raspberry Pi stream radio stations using VLC and turn the Pi into a Clock radio by setting an alarm. You can set the alarm, the radio stations and the volume of the Pi in a browser and controlled i.e. with your phone.

This part of the project is written with Spring-boot with Kotlin, Swagger, H2 database, VLC and the Quartz scheduler to provide the backend REST API.

## Installation 
This installation procedure will work on Raspian.

### Pre requisite and libs

``` bash
sudo apt-get update
sudo apt-get install git vlc
```

I've used the jdk 17 in the project so download a jdk from i.e https://adoptium.net/.

Clone the project
``` bash
git clone https://github.com/strmark/kpiradio.git
```

Make the necessary folders
``` bash
mkdir /home/pi/piradio
```

Copy the database to /home/pi/piradio/database
``` bash
cd kpiradio
cp database/piradio.db.* /home/pi/piradio/database/
```

## Run the backend

### Manually with gradle
``` bash
./gradlew bootRun
```

### Automatically at each startup with systemd (Prod)
``` bash
./gradlew jar
=======
cp build/libs/PiRadio-0.0.1-SNAPSHOT.jar /home/pi/piradio/
```

Create and open a Systemd service file for piclodio with sudo privileges in your text editor:
``` bash
sudo nano /etc/systemd/system/piradio.service
```

Place the following content (update the WorkingDirectory path depending on your installation)
``` bash
[Unit]
Description=Pi Radio daemon
After=network.target

[Service]
User=pi
Group=pi
WorkingDirectory=/home/pi/piradio
ExecStart=/usr/bin/java -jar PiRadio-0.0.1-SNAPSHOT.jar

[Install]
WantedBy=multi-user.target

```

We can now start the service we created and enable it so that it starts at boot:
``` bash
sudo systemctl daemon-reload
sudo systemctl start piradio
sudo systemctl enable piradio
```

The backend API should now be accessible on the port http://piradio:8000 of the server. Calling the backend endpoint will open the swagger endpoint.
