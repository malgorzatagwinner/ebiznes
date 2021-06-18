FROM ubuntu:20.04

WORKDIR /root

VOLUME ["/root/projekt"]
ENV SBT_VERSION 1.4.9
ENV DEBIAN_FRONTEND="noninteractive" TZ="Europe/Warsaw"

# install
RUN apt update && apt install openjdk-8-jdk curl npm -y
RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt sbtVersion

# default for play (scala)
EXPOSE 9000

# default for react (node)
EXPOSE 3000
run curl -sL https://deb.nodesource.com/setup_14.x | bash - && \
apt-get install -y nodejs

# vim: set expandtab ft=dockerfile:
