# There is no go-to "official" Scala docker image so we prefer to use our own
# There is an SBT image provided by Mozilla; however this uses JDK 8
# We prefer JDK 11 in our build; specifically 11.0.6
FROM openjdk:11.0.6

# Enable HTTPS sources in apt-get
RUN apt-get update && apt-get install -y apt-transport-https ca-certificates && apt-get install -y software-properties-common

# Install essential build tools incl. make
RUN apt-get update && apt install build-essential -y --no-install-recommends
RUN apt-get update && apt install -y libreadline-gplv2-dev libncursesw5-dev libssl-dev libsqlite3-dev tk-dev libgdbm-dev libc6-dev libbz2-dev
RUN apt-get update && apt-get upgrade -y && apt-get install wget gcc zlib1g-dev -y
RUN apt-get update && apt-get install -y gcc-arm-linux-gnueabi gcc-arm-linux-gnueabihf libc6-dev-armhf-cross qemu qemu-user-static

# Build Python 3.6
RUN wget --quiet https://www.python.org/ftp/python/3.6.8/Python-3.6.8.tgz
RUN tar zxf Python-3.6.8.tgz
RUN cd Python-3.6.8 && ./configure && make && make install
RUN cd ../ && rm -rf Python-3.6.8

# Install SBT, by downloading the deb and installing it manually
# b/c the keyserver is unreliable as per SBT's own instructions
RUN wget https://bintray.com/artifact/download/sbt/debian/sbt-1.3.7.deb
RUN dpkg -i sbt-1.3.7.deb
RUN apt-get update
RUN apt-get install -y sbt
RUN rm sbt-1.3.7.deb

# Copy source files
WORKDIR /usr/app
COPY . .

# Build source
# This will take a really long time. So when modifying this file,
# make sure all dependencies needed to "make" come before this or
# you may have to wait until SBT is downloaded + everything installed
RUN cd /usr/app
RUN make all
RUN python3 -m pip install -r requirements.txt

ENTRYPOINT [ "python3.6", "/usr/app/test.py" ]