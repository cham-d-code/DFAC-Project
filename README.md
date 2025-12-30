# DFAC-Project

A distributed system project with two modules:
- `dfac-replica-manager` (RM)
- `dfac-worker` (Client)

## Prerequisites
- Java JDK 11 or higher
- Gradle installed (since the wrapper could not be generated automatically)

## Build
Run the following command in the root directory:
```bash
gradle build
```

## Project Structure
- `dfac-replica-manager`: Contains the Replica Manager code.
- `dfac-worker`: Contains the Worker/Client code.
- `src/main/proto`: Place your `.proto` files here in each module.

## Dependencies
The project is configured with:
- gRPC (Netty Shaded, Protobuf, Stub)
- Protocol Buffers
- Apache Curator (Framework, Recipes)
