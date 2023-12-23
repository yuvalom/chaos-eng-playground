# Use an official Go runtime as the base image
FROM golang:1.20

# Install necessary utilities
RUN apt-get update && apt-get install -y iproute2

# Set the working directory inside the container
WORKDIR /usr/src/app

# pre-copy/cache go.mod for pre-downloading dependencies and only redownloading them in subsequent builds if they change
COPY go.mod ./
COPY go.sum ./
RUN go mod download && go mod verify

COPY src/ ./src
COPY infra ./infra

WORKDIR ./src

# Build the Go application inside the container
RUN go build -o network-chaos

# Use the host's network namespace
# This allows the container to directly affect the host's network interfaces using tc
# Warning: Using host networking mode can have security implications and port conflicts
# Use with caution in production environments
ENTRYPOINT ["./network-chaos"]
