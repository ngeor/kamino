FROM mono
MAINTAINER ngeor

ADD ./bin/Debug /usr/local/app

WORKDIR /usr/local/app
CMD mono /usr/local/app/BuzzStats.CrawlerService.exe

EXPOSE 9001
