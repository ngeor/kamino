FROM ubuntu
MAINTAINER ngeor

RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 3FA7E0328081BFF6A14DA29AA6A19B38D3D831EF
RUN echo "deb http://download.mono-project.com/repo/ubuntu xenial main" | tee /etc/apt/sources.list.d/mono-official.list
RUN apt-get update
RUN DEBIAN_FRONTEND=noninteractive apt-get upgrade -y

# tzdata needed because mono complains about missing /etc/localtime
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y tzdata mono-devel nuget msbuild

VOLUME ["/app"]

ADD build.sh /usr/local/build.sh

CMD /usr/local/build.sh

# docker build docker/build/ -t buzz
# docker run -v ~/Projects/BuzzStats/src:/app buzz
