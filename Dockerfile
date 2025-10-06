FROM public.ecr.aws/amazonlinux/amazonlinux:2023


RUN yum -y update \
    && yum install -y unzip tar gzip bzip2-devel ed gcc gcc-c++ gcc-gfortran \
    less libcurl-devel openssl openssl-devel readline-devel xz-devel \
    zlib-devel glibc-static zlib-static \
    && rm -rf /var/cache/yum \


# Graal VM
ENV JAVA_VERSION java21
ENV GRAAL_VERSION 22.2.0
ENV GRAAL_FOLDERNAME graalvm-ce-java21-22.2.0
ENV GRAAL_FILENAME graalvm-ce-java21-linux-amd64-22.2.0.tar.gz
RUN curl -4 -L https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-21.0.0/graalvm-community-jdk-21.0.0_linux-x64_bin.tar.gz | tar -xvz
#RUN ls -la
RUN mv graalvm-community-openjdk-21+35.1 /usr/lib/graalvm
RUN rm -rf $GRAAL_FOLDERNAME
#RUN curl -4 -L https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-22.2.0/graalvm-ce-java11-linux-amd64-22.2.0.tar.gz | tar -xvz
#RUN mv graalvm-ce-java11-22.2.0 /usr/lib/graalvm
#RUN rm -rf graalvm-ce-java11-22.2.0
#ENV JAVA_HOME /usr/lib/graalvm

#RUN ls /usr/lib/graalvm/bin
#RUN /usr/lib/graalvm/bin/gu install native-image
RUN ln -s /usr/lib/graalvm/bin/native-image /usr/bin/native-image
RUN ln -s /usr/lib/maven/bin/mvn /usr/bin/mvn

# AWS Lambda Builders
RUN dnf update -y && dnf install -y \
    python3.12 \
    python3.12-devel \
    python3.12-pip \
    && dnf clean all \
    && rm -rf /var/cache/dnf

RUN python3 -m ensurepip
RUN python3 -m pip install --upgrade pip
RUN python3 -m pip install aws-lambda-builders

VOLUME /project
WORKDIR /project

ENV JAVA_HOME /usr/lib/graalvm

# Maven
RUN curl -4 -L https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz | tar -xvz
RUN mv apache-maven-3.9.9 /usr/lib/maven
#RUN ln -s /usr/lib/maven/bin/mvn /usr/bin/mvn