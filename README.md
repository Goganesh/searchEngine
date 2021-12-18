# searchEngine

##Создание БД в Docker
docker pull postgres
docker run --rm --name=myContainer --env="POSTGRES_PASSWORD=postgres1" --publish 5432:5432 -d postgres
docker exec -it myContainer bash
su postgres
psql
CREATE DATABASE search_engine;