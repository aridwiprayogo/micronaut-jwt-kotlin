FROM oracle/graalvm-ce:20.2.0-java11 as graalvm
RUN gu install native-image

COPY . /home/app/micronaut-jwt-kotlin
WORKDIR /home/app/micronaut-jwt-kotlin

RUN native-image -cp target/micronaut-jwt-kotlin-*.jar

FROM frolvlad/alpine-glibc
RUN apk update && apk add libstdc++
EXPOSE 8080
COPY --from=graalvm /home/app/micronaut-jwt-kotlin/micronaut-jwt-kotlin /app/micronaut-jwt-kotlin
ENTRYPOINT ["/app/micronaut-jwt-kotlin"]
