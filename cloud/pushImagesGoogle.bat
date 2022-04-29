rem push images to google
echo on
docker tag pierreyvesmonnet/togh:2.0.0 gcr.io/intricate-gamma-325323/togh:2.0.0
docker push gcr.io/intricate-gamma-325323/togh:2.0.0

docker tag pierreyvesmonnet/frontendtogh:2.0.0 gcr.io/intricate-gamma-325323/frontendtogh:2.0.0
docker push gcr.io/intricate-gamma-325323/frontendtogh:2.0.0
