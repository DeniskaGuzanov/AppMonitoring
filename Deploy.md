## Развертывание приложения на сервере
### Подготовка приложения для работы в DOCKER контейнере
Подготовку к развертыванию приложения начнем с создания файлов Dockerfile и docker-compose.yml.  
Dockerfile содержит необходимые команды для создания образа docker на основе openjdk:17-jdk-alpine из репозитория hub.docker.com:
```Dockerfile
FROM openjdk:17-jdk-alpine
WORKDIR /var/app
COPY target/*.jar /var/app/app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/var/app/app.jar"]
```
```FROM openjdk:17-jdk-alpine``` – эта строка устанавливает базовый образ, который содержит OpenJDK 17 и Alpine Linux(небольшой и легкий дистрибутив Linux, который хорошо подходит для использования в Docker-образах).

```WORKDIR /var/app``` - эта строка устанавливает рабочий каталог в /var/app. Это место, где будут размещаться все файлы, которые копируется в образ.

```COPY target/*.jar /var/app/app.jar``` - эта строка копирует jar-файл приложения из локального рабочего каталога в /var/app/app.jar в Docker-образе.

```ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/var/app/app.jar"]``` - Эта строка определяет команду, которая будет выполняться при запуске Docker-образа. В данном случае это команда запуска Java-приложения из jar-файла. -Djava.security.egd=file:/dev/./urandom используется для обхода проблемы с генерацией случайных чисел в Docker.

Таким образом, этот Dockerfile создает образ Docker, который содержит OpenJDK 17 и запускает ваше Java-приложение из jar-файла на порту 8080

Для работы контейнера с web-приложением совместно с контейнером базы данных создадим файл docker-compose.yml
```YAML
version: '3'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    restart: always
  postgres:
    image: postgres
    ports:
      - "5432:5432"
    environment:
      - POSTGRES_PASSWORD=postgres
      - POSTGRES_USER=postgres
      - POSTGRES_DB=mgpDB
    volumes:
      - ./database:/var/lib/postgresql/data
      - ./backup:/backup
    restart: always
```
Этот Docker-файл описывает две службы: app и postgres.

Служба app строится из локального Dockerfile, который, содержит Java-приложение. Порт 8080 приложения прослушивается на порту 8080 хост-машины. Служба app зависит от службы postgres, что означает, что app не будет запущена, пока postgres не будет запущена и доступна. Служба app всегда перезапускается, если она падает, в том числе, когда сервер будет перезагружен или выключен-включен.

Служба postgres использует образ postgres, который предоставляет базу данных PostgreSQL.  
Порт 5432 базы данных прослушивается на порту 5432 хост-машины.  
В среде postgres устанавливаются следующие переменные окружения: ```POSTGRES_PASSWORD, POSTGRES_USER и POSTGRES_DB```.  
Также создается точка монтирования для сохранения данных базы данных в локальном каталоге database.  
Служба postgres всегда перезапускается, если она падает.

Таким образом, этот Docker-файл позволяет вам запускать ваше Java-приложение вместе с базой данных PostgreSQL в Docker.

### Установка Docker
Для развертывания приложения на платформе Docker потребуется сервер Ubuntu 22.04 (LTS) AMD x64 .
Установка Docker на Ubuntu 22.04 LTS может быть выполнена следующим образом:
Настраиваем apt репозиторий Docker.
```shell
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
```
Для установки последней версии docker запустим команду:
```shell
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

Для проверки функционирования docker запустим контейнер из образа hello-world:
```shell
sudo docker run hello-world
```
По умолчанию, доступ к Docker daemon есть только у пользователя root. Чтобы другие пользователи могли работать с Docker, их необходимо добавить в специальную группу под названием "docker".
Для этого нужно выполнить определенную команду, будучи обычным пользователем:
```shell
sudo usermod -aG docker $USER
```
Устанавливаем Docker-compose
```shell
sudo apt install docker-compose
```
### Запуск приложения
Необходимо подготовить каталог приложения, где будет храниться исходная скомпилированная версия приложения и каталог с базой данных.

Для этого создадим каталог ```/var/app``` и поместим в него каталог с скомпилированными файлами приложения ```/target```,  
каталог с файлами базы данных ```/database``` (при отсутствии каталога баз данных при первом запуске этот каталог будет создан автоматически), файл Dockerfile для создания образа с приложением и docker-compose.yml файл для запуска контейнера приложения совместно с контейнером базы данных PostgreSQL.
Для запуска приложения даем команду:
```shell
docker-compose up -d --build
```
Будет выполнена сборка образа с приложением на основе ```openjdk:17-jdk-alpine```, сборка образа c СУБД PostgreSQL на основе образа ```postgres:latest```,  
выполнен запуск контейнеров базы данных и приложения.

Так как в файле ``docker-compose`` указаны параметры служб ```restart: always``` при падении сервиса или при перезапуске сервера приложение будет запущено автоматически.

Так же рекомендуется установить PGAdmin в отдельном контейнере, для возможностей администрирования базы данных. 

## Резервное копирование
Резервное копирование производится средствами операционной системы хост-сервера.  
Процесс резервного копирования условно можно разделить на четыре подзадачи:
* создать копии файлов приложения App.JAR, Dockerfile, docker-compose.yml и скриптов *.sh
* выполнять резервное копирование базы данных PostgreSQL;
* упаковать копии файлов и базы данных в один архив с отметкой о времени выполнения;
* переместить полученный архив в место хранения.

Обе задачи решаются запуском скрипта через планировщика cron.  
В каталоге ```./backup``` создаем скрипт, который снимает дамп с нашей базы данных со следующим содержимым:
```shell
#!/bin/bash
#!/bin/sh
PATH=/etc:/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/bin:/usr/local/sbin
PGPASSWORD=postgres
export PGPASSWORD
pathB=/backup/sql
dbUser=postgres
database=mgpDB
find $pathB \( -name "*-1[^5].*" -o -name "*-[023]?.*" \) -ctime +61 -delete
pg_dump -U $dbUser $database | gzip > $pathB/pgsql_$(date "+%Y-%m-%d").sql.gz
unset PGPASSWORD
```
этот скрипт создает в каталоге ```./backup/sql``` дамп нашей базы с именем ```pgsql_ГГГГ-ММ-ДД.sql.gz```,  где ГГГГ- год, ММ-месяц, ДД-день месяца.

Скрипт запускается из хост-сервера с через команду:
```shell
docker exec -t appmon_postgres_1 /backup/sql-backup.sh
```
где ```appmon_postgres_1``` – имя контейнера с postgresql.

Общий файл для резервного копирования будет выглядеть так:
```shell
#!/bin/bash
cd /var/app/appMon
cp ./Dockerfile ./backup/files/
cp ./docker-compose.yml ./backup/files/
cp ./*.sh ./backup/files/
cp -dr ./target ./backup/files
docker exec -t appmon_postgres_1 /backup/sql-backup.sh
tar -czvf /backup/appMon/backup_`date +"%Y-%m-%d_%H-%M-%S"`.tar.gz ./backup
rm -rf ./backup/sql/* ./backup/files/* 
```
этот скрипт скопирует файлы приложения ```./target/*,  ./Dockerfile, ./docker-compose.yml, ./*.sh``` в каталог ```./backup/files```,  
запустит создание копии базы данных в каталоге ```./backup/sql```,
поместит каталог ```./backup``` со всем содержимым в tar архив с именем ```backup_ГГГГ-ММ-ДД_чч-мм-сс.tar.gz``` в каталоге сервера ```/backup/appMon```
каталоги ```./backup/sql``` и ```./backup/files``` очищаются

Полученные архивы `*.tar.gz` можно переместить на внешнее хранилище, сетевую папку, ftp-сервер или WebDav-хранилище.