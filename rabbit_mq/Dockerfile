FROM rabbitmq:4.0-management

COPY config/rabbitmq.conf /etc/rabbitmq
COPY definitions/rabbitmq_definitions.json /etc/rabbitmq/definitions/

RUN cat /etc/rabbitmq/rabbitmq.conf