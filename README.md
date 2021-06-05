# kpiradio
Kotlin Pi Clock Radio Backend

This part of the project is written with Spring-boot with Kotlin, H2 database, VLC and the Quartz scheduler to provide the backend REST API.

## Installation 
This installation procedure will works on Raspian.

### Pre requisite and libs

``` bash
sudo apt-get update
sudo apt-get install git vlc
```

Download jdk a from i.e https://adoptopenjdk.net/ and follow the installation instruction within binary tar or zip.

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
cd back
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

The backend API should now be accessible on the port 8000 of the server.
