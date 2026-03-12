#!/usr/bin/env bash

BOOT_VERSION=3.5.4
JAVA_VERSION=17
DEPENDENCIES=web,validation,webflux
GROUP_ID=com.konate.music_application

echo "=============================================="
echo " Creating Country Music Platform Services"
echo "=============================================="

# ================================
# User Service
# ================================
spring init \
  --boot-version=$BOOT_VERSION \
  --build=gradle \
  --type=gradle-project \
  --java-version=$JAVA_VERSION \
  --packaging=jar \
  --name=user-service \
  --package-name=$GROUP_ID.usersubdomain \
  --groupId=$GROUP_ID.usersubdomain \
  --dependencies=$DEPENDENCIES \
  --version=1.0.0-SNAPSHOT \
  user-service


# ================================
# Artist Service
# ================================
spring init \
  --boot-version=$BOOT_VERSION \
  --build=gradle \
    --type=gradle-project \
  --java-version=$JAVA_VERSION \
  --packaging=jar \
  --name=artist-service \
  --package-name=$GROUP_ID.artistsubdomain \
  --groupId=$GROUP_ID.artistsubdomain \
  --dependencies=$DEPENDENCIES \
  --version=1.0.0-SNAPSHOT \
  artist-service


# ================================
# Catalog Service (Albums & Songs)
# ================================
spring init \
  --boot-version=$BOOT_VERSION \
  --build=gradle \
    --type=gradle-project \
  --java-version=$JAVA_VERSION \
  --packaging=jar \
  --name=catalog-service \
  --package-name=$GROUP_ID.catalogsubdomain \
  --groupId=$GROUP_ID.catalogsubdomain \
  --dependencies=$DEPENDENCIES \
  --version=1.0.0-SNAPSHOT \
  catalog-service


# ================================
# Podcast Service
# ================================
spring init \
  --boot-version=$BOOT_VERSION \
  --build=gradle \
    --type=gradle-project \
  --java-version=$JAVA_VERSION \
  --packaging=jar \
  --name=podcast-service \
  --package-name=$GROUP_ID.podcastsubdomain \
  --groupId=$GROUP_ID.podcastsubdomain \
  --dependencies=$DEPENDENCIES \
  --version=1.0.0-SNAPSHOT \
  podcast-service


# ================================
# Order Service (Aggregate Root)
# ================================
spring init \
  --boot-version=$BOOT_VERSION \
  --build=gradle \
    --type=gradle-project \
  --java-version=$JAVA_VERSION \
  --packaging=jar \
  --name=order-service \
  --package-name=$GROUP_ID.ordersubdomain \
  --groupId=$GROUP_ID.ordersubdomain \
  --dependencies=$DEPENDENCIES \
  --version=1.0.0-SNAPSHOT \
  order-service


# ================================
# Ad Campaign Service (Aggregate Root)
# ================================
spring init \
  --boot-version=$BOOT_VERSION \
  --build=gradle \
    --type=gradle-project \
  --java-version=$JAVA_VERSION \
  --packaging=jar \
  --name=ad-service \
  --package-name=$GROUP_ID.adsubdomain \
  --groupId=$GROUP_ID.adsubdomain \
  --dependencies=$DEPENDENCIES \
  --version=1.0.0-SNAPSHOT \
  ad-service


spring init \
  --boot-version=$BOOT_VERSION \
  --build=gradle \
  --type=gradle-project \
  --java-version=$JAVA_VERSION \
  --packaging=jar \
  --name=api-gateway \
  --package-name=$GROUP_ID.apigateway \
  --groupId=$GROUP_ID.apigateway \
  --dependencies=$DEPENDENCIES \
  --version=1.0.0-SNAPSHOT \
  api-gateway


echo "=============================================="
echo " ✔ All DDD Services successfully created!"
echo "=============================================="