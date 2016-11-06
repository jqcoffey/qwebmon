# A tool to monitor databases -- a WIP
Running queries view:
![Alt text](/running-queries.png?raw=true "An example of the fake-db store")
Inspect a query view:
![Alt text](/inspect-query.png?raw=true "An example of the fake-db store")
# Try it yourself
First install loop:
```
$ npm install --global devloop
```
Then clone the repo and run the loop build process:
```
$ git clone git@github.com:jqcoffey/qwebmon.git
$ cd qwebmon
$ loop
=> Now browse to http://localhost:8080
```
In fact, you really want to browse to: http://localhost:8080/?fake-db

# Have fun with MySQL
First install MySQL:
```
$ brew install mysql
==> Downloading https://homebrew.bintray.com/bottles/mysql-5.7.16.yosemite.bottle.tar.gz
==> Pouring mysql-5.7.16.yosemite.bottle.tar.gz
...
Or, if you don't want/need a background service you can just run:
  mysql.server start
==> Summary
🍺  /usr/local/Cellar/mysql/5.7.16: 13,511 files, 445.5M
$ mysql.server start
Starting MySQL
. SUCCESS!
```
Grab some sample data (https://github.com/datacharmer/test_db):
```
$ wget https://github.com/datacharmer/test_db/archive/master.zip
$ unzip master.zip
$ cd test_db-master/
$ mysql -u root < employees.sql
```
Double check all's well:
```
$ mysql -u root -t < test_employees_md5.sql
... lots of output ...
```
Now run some queries and then pop over to your browser to see what's going on!
```
$ mysqlslap --create-schema=employees --no-drop --delimiter=';' --concurrency=5 --iterations=10 --query=queries/queries.sql
```
# Specifying your own config
Configuration is based on https://github.com/typesafehub/config, and as such you can specify your own config file when launching the app:
```
$ java -jar path/to/qwebmon.jar -Dconfig.file=myconfig.conf
```
