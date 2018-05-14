# Introduction

The Burst Detection System is composed by a Twitter Crawler based on the public Twitter API. It downloads the stream of tweets, it detects the most significant emerging events in the stream, it groups the terms and stores them in a MySQL database.

# System Requirements

The actual system requires the following minimum hardware requirements for the optimal performance:

 - Processor capable to run 4 threads at a time.
 - 8GB Ram.
 - SSD as secondary memory. The space use is approximately 
 - JDK 8.
 - MySQL 5.6 or greater.

## Installation

The source code was implemented using Netbeans with JDK8. The project configuration is included in the repository and can be compiled 

## Dependencies

The following list of external APIs were used in this project and are strictly required for the correct functionality of the system:

 - Twitter4j-4.0.4 (http://twitter4j.org/en/)
 - Sentistrength 2.2  (http://sentistrength.wlv.ac.uk/)
  - Include only the languages for the analysis and their respective dictionaries.
 - MySQL JDBC Driver (mysql-connector-java-5.1.21)
 - Stanford CoreNLP 3.6.0 (https://stanfordnlp.github.io/CoreNLP/download.html)
 - Kuromoji for Japanese NLP data analysis (https://github.com/atilika/kuromoji)

**NOTE:** Some of the APIs listed above require explicit and direct authorization from the authors for their use. I share the URL references as a point of contact.

All *.jar files of the dependencies should be located in the lib folder before the compilation of the code.

### Database configuration

The user specified in the must have DBA privileges to create the tables for each day of information crawled from Twitter.

### Configuration File

The *'_setup.txt'* file must be located in the same folder of the .jar compiled of the project in running time. It contains all the parameters of the system, and the setup of the parameters for the Burst detection algorithm (window size).

Twitter API requires the following parameters gathered from a valid developer account in twitter:

 - AuthConsumerKey
 - AuthConsumerSecret
 - AuthAccessToken
 - AuthAccessTokenSecret

## How to use the system
**User case:**  Earthquake Detection (http://sismos.dcc.uchile.cl/)

When compiled file of the project is:  'Twitter_Crawler_Stream_Chile_Earthquake.jar'

```
WORKINGDIR="$HOME/CrawlerTwitterStream"
java -Xmx8g -XX:+UseThreadPriorities -jar $WORKINGDIR/Twitter_Crawler_Stream_Chile_Earthquake.jar $WORKINGDIR/_setup.txt
```

The '_setup.txt' file  contains all the parameters used in the Earthquake Detection system.

**NOTE:**  For Twitter policy restrictions, we cannot share all the information of the tweets downloaded and gathered in the system. We can share the TweetId for research purposes only.