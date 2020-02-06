# There is no go-to "official" Scala docker image so we prefer to use our own
# There is an SBT image provided by Mozilla; however this uses JDK 8
# We prefer JDK 11 in our build; specifically 11.0.6
FROM openjdk:11.0.6

# Enable HTTPS sources in apt-get
RUN apt-get update
RUN apt-get install -y apt-transport-https ca-certificates
RUN apt-get install -y software-properties-common

# Install essential build tools incl. make
RUN apt install build-essential checkinstall -y --no-install-recommends
RUN apt install -y libreadline-gplv2-dev libncursesw5-dev libssl-dev libsqlite3-dev tk-dev libgdbm-dev libc6-dev libbz2-dev
RUN apt-get update
RUN apt-get upgrade -y
RUN apt-get install wget gcc zlib1g-dev -y

# Build Python 3.6
RUN wget --quiet https://www.python.org/ftp/python/3.6.8/Python-3.6.8.tgz
RUN tar zxf Python-3.6.8.tgz
RUN cd Python-3.6.8 && ./configure && make && make install

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