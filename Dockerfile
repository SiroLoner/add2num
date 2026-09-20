# ---------------------------------------------------------------------------
# Build stage
# ---------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

# Dependencies are resolved before the sources are copied, so that editing a
# source file does not invalidate the cached dependency layer.
COPY pom.xml .
COPY add2num-core/pom.xml add2num-core/
COPY add2num-web/pom.xml add2num-web/
RUN mvn -B -ntp -q dependency:go-offline

COPY add2num-core/src add2num-core/src
COPY add2num-web/src add2num-web/src
RUN mvn -B -ntp clean package

# ---------------------------------------------------------------------------
# Runtime stage
# ---------------------------------------------------------------------------
FROM eclipse-temurin:17-jre AS runtime

# curl is here only so that HEALTHCHECK below can actually call the health
# endpoint. A health check that does not touch the application is decoration.
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

# An unprivileged user: nothing in this application needs root.
RUN groupadd --system --gid 1001 add2num \
 && useradd --system --uid 1001 --gid add2num --create-home add2num

WORKDIR /app
COPY --from=build --chown=add2num:add2num /build/add2num-web/target/add2num-web.jar app.jar

USER add2num
EXPOSE 8080

# MaxRAMPercentage lets the JVM respect the container memory limit instead of
# guessing from the host, which is what makes a container get OOM killed.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseSerialGC"

HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl --fail --silent http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
