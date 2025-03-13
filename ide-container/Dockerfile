# ✅ 더 가벼운 Ubuntu 기반
FROM ubuntu:20.04

# ✅ 환경 변수 설정 (대화형 설치 방지)
ARG DEBIAN_FRONTEND=noninteractive

# ✅ 패키지 설치 (최신 OpenJDK 포함)
RUN sed -i 's|http://archive.ubuntu.com|http://mirror.kakao.com|g' /etc/apt/sources.list \
    && apt-get update && apt-get install -y --no-install-recommends \
    curl \
    git \
    nodejs \
    npm \
    openjdk-21-jdk \  
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# ✅ 작업 디렉토리 설정
WORKDIR /workspace

# ✅ 기본 실행 명령어
CMD ["tail", "-f", "/dev/null"]