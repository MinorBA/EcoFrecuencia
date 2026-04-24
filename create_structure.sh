#!/bin/bash
# Script alternativo en Bash para crear estructura de directorios
# (Ejecutar en caso de preferir bash en lugar de PowerShell)

BASE_PATH="app/src/main/java/com/example/ecofrecuencia"

mkdir -p "$BASE_PATH/data/remote"
mkdir -p "$BASE_PATH/data/repository"
mkdir -p "$BASE_PATH/domain/model"
mkdir -p "$BASE_PATH/domain/usecase"
mkdir -p "$BASE_PATH/domain/repository"
mkdir -p "$BASE_PATH/presentation/ui"
mkdir -p "$BASE_PATH/presentation/viewmodel"
mkdir -p "$BASE_PATH/di"

echo "✅ Estructura de directorios creada"

