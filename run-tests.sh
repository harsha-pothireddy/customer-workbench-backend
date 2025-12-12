#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
echo "Running backend tests in ${ROOT_DIR}"
cd "${ROOT_DIR}"

if [ "${1:-}" = "integration" ]; then
  echo "Running integration test (requires Docker): UploadIntegrationTest"
  mvn -DskipTests=false -Dtest=UploadIntegrationTest test
  exit $?
fi

if [ "${1:-}" = "unit" ]; then
  echo "Running unit tests only: CustomerInteractionServiceTest"
  mvn -DskipTests=false -Dtest=CustomerInteractionServiceTest test
  exit $?
fi

echo "Running all tests (unit + integration). Make sure Docker is running for integration tests."
mvn test
