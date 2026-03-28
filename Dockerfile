FROM amazoncorretto:17-al2023

ENV USER_UID=1001 \
    USER_NAME=app-external-context-service

#hadolint ignore=DL3041
RUN dnf update -y --security \
    && dnf install -y shadow-utils passwd \
    && groupadd --gid ${USER_UID} ${USER_NAME} \
    && useradd --uid ${USER_UID} --gid ${USER_NAME} --shell /sbin/nologin --no-create-home ${USER_NAME} \
    && dnf remove -y shadow-utils \
    && dnf clean all \
    && rm -rf /var/cache/dnf

#hadolint ignore=DL3020
ADD target/*.jar app.jar

USER ${USER_NAME}

ENTRYPOINT ["/bin/sh", "-c", "java $JAVA_OPTS -jar /app.jar"]
