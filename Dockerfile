# There is no go-to "official" Scala docker image so we prefer to use our own
# There is an SBT image provided by Mozilla; however this uses JDK 8
# We prefer JDK 11 in our build; specifically 11.0.6
FROM openjdk:11.0.6

# Enable HTTPS sources in apt-get
RUN apt-get update
RUN apt-get install -y apt-transport-https ca-certificates
RUN apt-get install -y software-properties-common
RUN add-apt-repository ppa:deadsnakes/ppa

# Install essential build tools incl. make
RUN apt install build-essential -y --no-install-recommends

# Install Python 3 for test script
RUN apt-get update
RUN apt-get install -y python3.6 python3.6-dev python3-pip python3.6-venv
RUN apt-get install -y python3-pip python3-dev
RUN python3.6 -m pip install pip --upgrade
RUN python3.6 -m pip install wheel

# Install SBT, from the official SBT installation instructions
RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add
RUN apt-get update
RUN apt-get install -y sbt

# Copy source files
WORKDIR /usr/app
COPY . .

# Build source
# This will take a really long time. So when modifying this file,
# make sure all dependencies needed to "make" come before this or
# you may have to wait until SBT is downloaded + everything installed
RUN cd /usr/app
RUN make all

ENTRYPOINT [ "python3.6", "/usr/app/test.py" ]