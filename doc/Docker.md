# Cloud Google & Docker

https://container-registry.oracle.com
docker pull container-registry.oracle.com/java/openjdk:latest

## 1. Create Back End Togh Docker image

mvn install build and push the docker image on docker image cloud (but we need the image in gcr.io).

You must be connected via Docker Destop

```
> mvn install
```

To do it manually:

```
> docker build -t pierreyvesmonnet/togh:1.0.0 .
> docker push pierreyvesmonnet/togh:1.0.0
```

### Tag the release

Done automatically

### Option Create Front End Togh Docker image


```
> cd npm
> docker build -t pierreyvesmonnet/frontendtogh:1.0.0 .
> docker push pierreyvesmonnet/frontendtogh:1.0.0
```

### Option execute Locally Docker image

```
> docker run --name togh -h localhost -e SPRING_DATASOURCE_URL=jdbc:postgresql://<HOST> -e SPRING_DATASOURCE_USERNAME=<USERNAME> -e SPRING_DATASOURCE_PASSWORD=<Password> --network="host" pierreyvesmonnet/togh:1.0.0 

> docker run --name frontendtogh -h localhost -d -p 3000:3000 -p 80:80 pierreyvesmonnet/frontendtogh:1.0.0
```

Docker compose does not works... but it should be:
docker-compose up

## 2. Push image to gcr.io

First time, the PC must be log on grc:

```
gcloud auth login
```

After, this command works

```
> cd cloud
> pushImagesGoogle.bat
```

or

```
> docker tag pierreyvesmonnet/togh:1.0.0 gcr.io/intricate-gamma-325323/togh:1.0.0
> docker push gcr.io/intricate-gamma-325323/togh:1.0.0

> docker tag pierreyvesmonnet/frontendtogh:1.0.0 gcr.io/intricate-gamma-325323/frontendtogh:1.0.0
> docker push gcr.io/intricate-gamma-325323/frontendtogh:1.0.0
> 
```

Check on https://console.cloud.google.com/gcr/images/intricate-gamma-325323/global/togh?project=intricate-gamma-325323

## 3. Google Cloud instance

Connect on the VM Instance Download the docker image:
1. Connect to https://console.cloud.google.com/
2. Switch to the Togh user
3. Access Compute Engine / VM Instance
4. Machine is here
```
> docker pull gcr.io/intricate-gamma-325323/togh:2.0.0

> docker pull gcr.io/intricate-gamma-325323/frontendtogh:2.0.0
```

Here is the complete procedure:

```
$ cat updateTogh.sh

$ docker container stop togh frontendtogh
$ docker container rm togh frontendtogh
$ docker image ls -a | grep togh | awk '{ print $3 " " $1}'
$ docker image rm XXX


$ docker pull gcr.io/intricate-gamma-325323/togh:2.0.0
$ docker pull gcr.io/intricate-gamma-325323/frontendtogh:2.0.0
$ docker run --name togh \
-e SPRING_DATASOURCE_URL=jdbc:postgresql://0.0.0.0:5432/togh \
-e SPRING_DATASOURCE_USERNAME=<USERNAME> \
-e SPRING_DATASOURCE_PASSWORD=<PASSWORD> \
--network="host" --log-driver=gcplogs -d gcr.io/intricate-gamma-325323/togh:2.0.0

$ docker run --name frontendtogh  --network="host"  --log-driver=gcplogs \
-d gcr.io/intricate-gamma-325323/frontendtogh:1.0.0

docker compose
$ curl http://34.125.198.71:7080/togh/ping
$ curl http://34.125.198.71:3000
```

### troubleshoot

Remove a container after it was stop but still here?

```
docker container ls --all
docker container prune

```

### Option : run docker compose

export GCP_KEY_PATH=/home/toghnow/intricate-gamma-325323-ContainerRegistry.json docker pull
gcr.io/intricate-gamma-325323/togh:1.0.0

docker run --rm \
-v /var/run/docker.sock:/var/run/docker.sock \
-v "$PWD:$PWD" \
-p 5432:5432  \
-w="$PWD" \
-e GOOGLE_APPLICATION_CREDENTIALS=/tmp/keys/keyfile.json \
-v /home/toghnow/intricate-gamma-325323-ContainerRegistry.json:/tmp/keys/keyfile.json:ro \
docker/compose:1.24.0 up &

     -p 5432:5432  \
    -p 7080:7080 \
    -p 3000:3000 \

## SQL Database production update (sql update)

Done automatically.