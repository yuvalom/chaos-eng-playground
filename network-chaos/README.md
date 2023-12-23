# Build this Dockerfile using:
docker build -t network-chaos .

# Run the Docker container interactively in host networking mode with runtime arguments:
docker run --name chaos-network-app --cap-add NET_ADMIN --cap-add NET_RAW --network host -ti --entrypoint /bin/bash network-chaos

# Running the tool

## Latency attack
./network-chaos -a latency -i ens5 -h 10.21.1.170 -l 15000 -d 30000

## Latency attack on subnet
./network-chaos -a latency -i ens5 -h 192.168.2.0/24 -l 15000 -d 30000

## Packet loss attack
./network-chaos -a drop -i ens5 -h 10.21.1.170 -pl 30 -d 30000
