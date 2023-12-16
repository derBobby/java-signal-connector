# java-signal-connector
This spring boot library allows you to send messages via Signal messenger using the docker container [signal-cli-rest-api](https://github.com/bbernhard/signal-cli-rest-api)
There is an [official library](https://github.com/signalapp/libsignal-service-java) from signal, you could also use that without the container.

## Features
* Send message to list of configured recipients
* Send message to configured admin
* Basic auth for containers e.g. behind Traefik reverse proxy

## Usage
Add Maven dependency
```xml
        <dependency>
            <groupId>eu.planlos</groupId>
            <artifactId>java-signal-connector</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
```

Add Configuration class
```java
@Configuration
@ComponentScan(basePackages = "eu.planlos.javasignalconnector")
public class SignalConfig {}
```

## Properties

| Property                    | Type    | Description                               |
|-----------------------------|---------|-------------------------------------------|
| `signal.api.active`         | Boolean | Enable/Disable usage of API               |
| `signal.api.address`        | String  | URL of the signal-cli-rest-api container  |
| `signal.api.user`           | String  | Basic auth username                       | 
| `signal.api.password`       | String  | Basic auth password                       | 
| `signal.api.phone-sender`   | String  | Phone number of message sender (+49xxxxx) | 
| `signal.api.phone-receiver` | String  | Comma seperated values of phone numbers   | 
| `signal.api.phone-admin`    | String  | Phone number of admin (+49xxxxx)          | 
| `signal.api.message-prefix` | String  | Prefix to be used for signal messages     | 
| `signal.api.retry-count`    | Integer | Retry count in case of exception          | 
| `signal.api.retry-interval` | Integer | Interval for retries in case of exception | 

## Status

[![Merge Dependabot PR](https://github.com/derBobby/java-nextcloud-connector/actions/workflows/dependabot-automerge.yml/badge.svg)](https://github.com/derBobby/java-nextcloud-connector/actions/workflows/dependabot-automerge.yml)

[![CD](https://github.com/derBobby/java-nextcloud-connector/actions/workflows/test-and-publish.yml/badge.svg)](https://github.com/derBobby/java-nextcloud-connector/actions/workflows/test-and-publish.yml)
