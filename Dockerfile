FROM centos:6.6

RUN yum install -y yum-plugin-ovl java wget; yum clean all
COPY build/distributions/datasense-*.noarch.rpm /tmp/datasense.rpm
RUN yum clean all;yum install -y unzip /tmp/datasense.rpm && rm -f /tmp/datasense.rpm && yum clean all && \
    wget -O datasense_reports.zip https://github.com/SharedHealth/datasense-reports/archive/master.zip && \
    mkdir -p /var/lib/datasense/ && unzip datasense_reports -d /var/lib/datasense/ && mv /var/lib/datasense/datasense-reports-master /var/lib/datasense/dhis_config/
COPY env/docker_datasense /etc/default/datasense
ENTRYPOINT . /etc/default/datasense && java -Dserver.port=$DATASENSE_PORT -DDATASENSE_LOG_LEVEL=$DATASENSE_LOG_LEVEL -Dfile.encoding=UTF-8 -jar /opt/datasense/lib/datasense.war
