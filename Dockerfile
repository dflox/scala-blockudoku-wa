FROM sbtscala/scala-sbt:eclipse-temurin-17.0.13_11_1.10.7_3.6.2 AS build

ENV NODE_VERSION=16.13.0
RUN apt install -y curl
RUN curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.3/install.sh | bash
ENV NVM_DIR=/root/.nvm
RUN . "$NVM_DIR/nvm.sh" && nvm install ${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm use v${NODE_VERSION}
RUN . "$NVM_DIR/nvm.sh" && nvm alias default v${NODE_VERSION}
ENV PATH="/root/.nvm/versions/node/v${NODE_VERSION}/bin/:${PATH}"

WORKDIR /build

COPY . /build/blockudoku-wa

ENV VERSION=0.1
ENV CI_RELEASE=true

RUN git clone https://github.com/Freeeezee/scala-blockudoku.git /build/scala-blockudoku

WORKDIR /build/blockudoku-wa
RUN sbt clean stage

FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=build /build/blockudoku-wa/target/universal/stage /app

ENTRYPOINT ["bin/blockudoku-wa"]