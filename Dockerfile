FROM ubuntu:18.04

WORKDIR /root

VOLUME ["/root/projekt"]
ENV SBT_VERSION 1.4.9
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

# vim: set expandtab ft=dockerfile:
